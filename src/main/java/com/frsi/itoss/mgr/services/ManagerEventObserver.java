package com.frsi.itoss.mgr.services;

import com.frsi.itoss.mgr.controllers.ToolService;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.mail.Mail;
import com.frsi.itoss.model.repository.*;
import com.frsi.itoss.shared.CtStatus;
import com.frsi.itoss.shared.Notification;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Service

@Log
@Configuration
@Deprecated
public class ManagerEventObserver {
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    CtRepo ctRepo;
    @Autowired
    CtStatusRepo ctStatusRepo;
    //	@Autowired
//	DashboardEntryRepo dashboardEntryRepo;
    @Autowired
    EventRuleRepo eventRuleRepo;
    //	@Autowired
//	ContainerRepo containerRepo;
    @Autowired
    CollectorRepo collRepo;
    @Autowired
    CtRelationRepo ctRelationRepo;

    @Autowired
    ToolRepo toolRepo;
    @Autowired
    ToolService toolService;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    CtService ctService;
    @Value("${itoss.smtp.from}")
    private String mailFrom;

//	@Async("taskExecutor")
//	@EventListener
//	@Deprecated
//	public void removeDashboardEntry(DashboardEntryKey dashboardEntryId) {
//
//		Long start = System.currentTimeMillis();
//		this.statisticService.inc("remove_dashboardentries.received");
//		this.statisticService.inc("remove_dashboardentries.processing");
//		Long ctId = dashboardEntryId.getCtId();
//		Long monitorId = dashboardEntryId.getMonitorId();
//
//		try {
//			Optional<DashboardEntry> deFound = null;
//			DashboardEntry de;
//			deFound = dashboardEntryRepo.findById(dashboardEntryId);
//			if (deFound.isPresent()) {
//				de = deFound.get();
//				dashboardEntryRepo.delete(de);
//				log.info("Dashboard entry deleted successfully dashboardEntryId:" + dashboardEntryId);
//			}
//
//			List<DashboardEntry> deFaultFound = dashboardEntryRepo.findByIdCtIdAndIdMonitorIdAndFault(ctId, monitorId,
//					true);
//			if (!deFaultFound.isEmpty()) {
//				dashboardEntryRepo.deleteAll(deFaultFound);
//			}
//			if (log.isLoggable(Level.INFO)) {
//				log.info("Successfully deleted faulted for ctId: " + ctId + " monitorId: " + monitorId);
//			}
//			Long end = System.currentTimeMillis();
//			Long duration = end - start;
//			this.statisticService.dec("remove_dashboardentries.processing");
//			this.statisticService.inc("remove_dashboardentries.finished");
//			this.statisticService.setDuration("remove_dashboardentries", duration);
//		} catch (Exception e) {
//			if (log.isLoggable(Level.SEVERE)) {
//				log.severe("Unable to delete Dashboard Entry:" + dashboardEntryId + " error:" + e.getMessage());
//			}
//			this.statisticService.dec("remove_dashboardentries.processing");
//			this.statisticService.inc("remove_dashboardentries.with_error");
//
//		}
//	}

    @Async("taskExecutor")
    @EventListener
    @Deprecated
    public void persistCtStatus(CtStatus ctStatus) {

        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "save_ct_status");

        this.statisticService.inc("processing", "save_ct_status");
        try {

            CtStatus ctStatusFound = null;
            Optional<CtStatus> ctStatusOptional = null;
            List<String> tags = new ArrayList<>();
            ctStatusOptional = ctStatusRepo.findById(ctStatus.getId());
            Optional<Ct> ct = ctRepo.findById(ctStatus.getId());
            if (ctStatusOptional.isPresent()) {
                ctStatusFound = ctStatusOptional.get();

                if (ctStatusFound.isDown() != ctStatus.isDown()) {
                    tags.add("status_change");
                    if (ctStatus.isDown())
                        tags.add("status_down");
                    Notification notification = new Notification();
                    notification.setTitle("Changed status of "
                            + Optional.ofNullable(ct.get().getName()).orElse("ct not found") + " from "
                            + (ctStatusFound.isDown() ? "down" : "up") + " to " + (ctStatus.isDown() ? "down" : "up"));
                    notification.setType("status");
                    notification.getPayload().put("ctId", ct.get().getId().toString());
                    notification.setCtId(ct.get().getId());
                    notification.setTags(tags);
                    notification.setDestinations(null);
                    this.eventPublisher.publishEvent(notification);
                }
            } else {
                ctStatusFound = new CtStatus();
                tags.add("status_change");
                if (ctStatus.isDown())
                    tags.add("status_down");

                Notification notification = new Notification();
                notification
                        .setTitle("Changed status of " + Optional.ofNullable(ct.get().getName()).orElse("ct not found")
                                + " to " + (ctStatus.isDown() ? "down" : "up"));
                notification.setType("status");
                notification.getPayload().put("ctId", ct.get().getId().toString());
                notification.setCtId(ct.get().getId());
                notification.setTags(tags);
                notification.setDestinations(null);
                this.eventPublisher.publishEvent(notification);
            }

            ctStatusFound.setDown(ctStatus.isDown());
            //ctStatusFound.setLastStatusChange(
            //		(ctStatus.getLastStatusChange() == null) ? new Date() : ctStatus.getLastStatusChange());
            ctStatusFound.setLastStatusChange(ctStatus.getLastStatusChange());
            ctStatusFound.setId(ctStatus.getId());
            ctStatusFound.setModifiedAt(new Date());
            ctStatusFound = ctStatusRepo.saveAndFlush(ctStatusFound);

            log.info("Ct status save successfully ctId:" + ctStatus.getId());
            Long end = System.currentTimeMillis();
            Long duration = end - start;
            this.statisticService.dec("processing", "save_ct_status");
            this.statisticService.inc("finished", "save_ct_status");
            this.statisticService.setDuration("save_ct_status", duration);
        } catch (Exception e) {

            if (log.isLoggable(Level.SEVERE))
                log.severe("Unable to save CtStatus:" + ctStatus.toString() + " error:" + e.getMessage());
            this.statisticService.dec("processing", "save_ct_status");
            this.statisticService.inc("with_error", "save_ct_status");
        }
    }

//	@EventListener
//	@Async("taskExecutor")
//	@Deprecated
//	public void persistDashboardEntry(DashboardEntryPayload dp) {
//
//		Long start = System.currentTimeMillis();
//		this.statisticService.inc("save_dashboardentries.received");
//		this.statisticService.inc("save_dashboardentries.processing");
//		try {
//
//			Optional<DashboardEntry> deFound = null;
//			DashboardEntry de;
//			Long monitorId = dp.getMetricPayloadData().getMonitorId();
//			Long ctId = dp.getMetricPayloadData().getCtId();
//			String object = dp.getMetricPayloadData().getTags().toString();
//			DashboardEntryKey key = new DashboardEntryKey();
//			key.setCtId(ctId);
//			key.setMonitorId(monitorId);
//			key.setObject(object);
//			deFound = dashboardEntryRepo.findById(key);
//			if (deFound.isPresent()) {
//				de = deFound.get();
//				if (!de.getSeverity().equalsIgnoreCase(dp.getSeverity()))
//					de.setCreatedAt(new Date());
//			} else {
//				de = new DashboardEntry();
//				de.setId(key);
//			}
//
//			de.setMetricPayloadData(dp.getMetricPayloadData());
//			de.setRuleDescription(dp.getRuleDescription());
//			de.setSeverity(dp.getSeverity());
//			de.setContainerId(dp.getContainerId());
//			de.setFault(dp.isFault());
//			de.setLastChange(dp.getLastChange());
//			de.setModifiedAt(new Date());
//			de.setScore(dp.getScore());
//			de.setCompanyId(dp.getCompanyId());
//			de = dashboardEntryRepo.saveAndFlush(de);
//
//			if (dp.isFault()) {
//
//				List<DashboardEntry> deNoFaultFound = dashboardEntryRepo.findByIdCtIdAndIdMonitorIdAndFault(ctId,
//						monitorId, false);
//				if (!deNoFaultFound.isEmpty()) {
//					dashboardEntryRepo.deleteAll(deNoFaultFound);
//				}
//			} else {
//
//								List<DashboardEntry> deFaultFound = dashboardEntryRepo.findByIdCtIdAndIdMonitorIdAndFault(ctId,
//						monitorId, true);
//				if (!deFaultFound.isEmpty()) {
//					dashboardEntryRepo.deleteAll(deFaultFound);
//				}
//			}
//
//			log.info("Dashboard entry successfully persisted ctId:" + de.toString());
//			Long end = System.currentTimeMillis();
//			Long duration = end - start;
//			this.statisticService.dec("save_dashboardentries.processing");
//			this.statisticService.inc("save_dashboardentries.finished");
//			this.statisticService.setDuration("save_dashboardentries", duration);
//		} catch (Exception e) {
//			this.statisticService.dec("save_dashboardentries.processing");
//			this.statisticService.inc("save_dashboardentries.with_error");
//			if (log.isLoggable(Level.SEVERE))
//				log.severe("Unable to save Dashboard Entry:" + dp.toString() + " error:" + e.getMessage());
//
//		}
//
//	}

    @Async("taskExecutor")
    @EventListener
    @Deprecated
    public void sendMail(Mail mail) {

        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "send_mail");
        this.statisticService.inc("processing", "send_mail");
        String htmlMsg = "";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // StringBuilder mailAddress = new StringBuilder();
        Optional<Ct> optionalCt = ctRepo.findById(mail.getMetricPayloadData().getCtId());

        Address recipient;
        if (optionalCt.isPresent()) {

            Ct realCt = optionalCt.get();
            try {
                for (String destination : mail.getDestinations()) {
                    // log.info("MAIL SMTP: preparing to send to:" + destination + " mail request:"
                    // + mail.toString());
                    switch (destination) {
                        case "MANAGER":
                            try {
                                recipient = new InternetAddress(realCt.getWorkgroup().getWorkgroupManager().getEmail());
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)

                                mimeMessage.addRecipient(RecipientType.TO, recipient);

                            break;
                        case "SUPPORTUSER":

                            try {
                                recipient = new InternetAddress(realCt.getSupportUser().getEmail());
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)
                                mimeMessage.addRecipient(RecipientType.TO, recipient);
                            break;
                        case "WORKGROUP":
                            try {
                                recipient = new InternetAddress(realCt.getWorkgroup().getEmail());
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)
                                mimeMessage.addRecipient(RecipientType.TO, recipient);

                            break;
                        case "CONTACT":

                            try {
                                recipient = new InternetAddress(realCt.getContact().getEmail());
                            } catch (Exception e) {
                                recipient = null;
                            }
                            if (recipient != null)

                                mimeMessage.addRecipient(RecipientType.TO, recipient);

                    }

                }

                // mimeMessage.setContent(htmlMsg, "text/html"); /* Use this or below line */

                mimeMessage.setFrom(mailFrom);
                // mimeMessage.setRecipients(RecipientType.TO,mailAddress.toString());

                mimeMessage.setSubject(mail.getMessage());

                if (mail.getToolId() != null) {
                    try {
                        htmlMsg = toolService.formatedToolResponse(mail.getToolId(),
                                mail.getMetricPayloadData().getCtId());
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
            } catch (Exception e) {
                if (log.isLoggable(Level.SEVERE))
                    log.severe("Unable to nofify users. Error:" + e.getMessage());
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
