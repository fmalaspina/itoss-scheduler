package com.frsi.itoss.mgr.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.controllers.ToolService;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.CtStatusRepo;
import com.frsi.itoss.model.repository.ToolRepo;
import com.frsi.itoss.shared.Mail;
import com.frsi.itoss.shared.ManagerAction;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class MailListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    CtStatusRepo ctStatusRepo;
    @Autowired
    CtRepo ctRepo;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    ToolRepo toolRepo;
    @Autowired
    ToolService toolController;
    @Value("${itoss.smtp.from}")
    private String mailFrom;

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'SEND_MAIL'")
    public void listenSendMail(ManagerAction event) {

        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        Mail mail = mapper.convertValue(event.getActionObject(), Mail.class);
        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "send_mail");
        this.statisticService.inc("processing", "send_mail");
        String htmlMsg = "";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // StringBuilder mailAddress = new StringBuilder();
        Optional<Ct> optionalCt = ctRepo.findById(mail.metricPayloadData().getCtId());


        if (optionalCt.isPresent()) {

            Ct realCt = optionalCt.get();
            try {
                for (String destination : mail.destinations()) {
                    Address recipient = null;
                    // log.info("MAIL SMTP: preparing to send to:" + destination + " mail request:"
                    // + mail.toString());
                    switch (destination) {
                        case "MANAGER":
                            try {
                                String email = realCt.getWorkgroup().getWorkgroupManager().getEmail().trim();
                                if (!email.isBlank()) recipient = new InternetAddress(email);
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)

                                mimeMessage.addRecipient(MimeMessage.RecipientType.TO, recipient);

                            break;
                        case "SUPPORTUSER":

                            try {
                                String email = realCt.getSupportUser().getEmail().trim();
                                if (!email.isBlank()) recipient = new InternetAddress(email);
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)
                                mimeMessage.addRecipient(MimeMessage.RecipientType.TO, recipient);
                            break;
                        case "WORKGROUP":
                            try {
                                String email = realCt.getWorkgroup().getEmail().trim();

                                if (!email.isBlank()) recipient = new InternetAddress(email);
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)
                                mimeMessage.addRecipient(MimeMessage.RecipientType.TO, recipient);

                            break;
                        case "CONTACT":

                            try {
                                String email = realCt.getContact().getEmail().trim();

                                if (!email.isBlank()) recipient = new InternetAddress(email);
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)

                                mimeMessage.addRecipient(MimeMessage.RecipientType.TO, recipient);

                    }

                }

                if (mimeMessage.getAllRecipients() != null && mimeMessage.getAllRecipients().length > 0) {

                    mimeMessage.setFrom(mailFrom);

                    mimeMessage.setSubject(mail.message());

                    if (mail.toolId() != null) {
                        try {
                            htmlMsg = toolController.formatedToolResponse(mail.toolId(),
                                    mail.metricPayloadData().getCtId());
                        } catch (Exception e) {
                            htmlMsg = e.getMessage();
                        }

                    }
                    mimeMessage.setContent(htmlMsg, "text/html");

                    mailSender.send(mimeMessage);
                    log.info("Successfuly sent mail: " + mimeMessage.toString());
                    Long end = System.currentTimeMillis();
                    Long duration = end - start;
                    this.statisticService.dec("processing", "send_mail");
                    this.statisticService.inc("finished", "send_mail");
                    this.statisticService.setDuration("send_mail", duration);
                }
            } catch (Exception e) {
                if (log.isLoggable(Level.SEVERE))
                    log.severe("Unable to notify users. Error:" + e.getMessage());
                this.statisticService.dec("processing", "send_mail");
                this.statisticService.inc("with_error", "send_mail");
            }
        }

    }

    private void mailError(Exception e) {
        if (log.isLoggable(Level.SEVERE))
            log.severe("Unable to send mail " + e.getMessage());
    }

}

