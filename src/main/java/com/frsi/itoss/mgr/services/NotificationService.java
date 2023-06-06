package com.frsi.itoss.mgr.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.news.News;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.NewsAcknowledgeRepo;
import com.frsi.itoss.model.repository.NewsRepo;
import com.frsi.itoss.model.repository.TennantRepo;
import com.frsi.itoss.shared.ManagerAction;
import com.frsi.itoss.shared.Notification;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Level;

@Service

@Log

public class NotificationService {

//	@Value("${itoss.notification.url:http://localhost:8089}")
//	private String notificationUrl;

    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    TennantRepo tennantRepo;

    @Autowired
    CtRepo ctRepo;
    @Autowired
    NewsRepo newsRepo;
    @Autowired
    NewsAcknowledgeRepo newsAckRepo;

    @EventListener
    public void onEventInfo(Notification notification) throws Exception {

//		Set<Long> userIds = new HashSet<>();
//		try {
//
//			if (notification.getPayload() != null && notification.getPayload().containsKey("ctId")) {
//
//				Long ctId = Long.valueOf(notification.getPayload().get("ctId"));
////				if (notification.getType().equals("status"))
////					userIds.addAll(tennantRepo.findUserIdsByCtId(ctId));
//
//				Optional<Ct> optionalCt = ctRepo.findById(ctId);
//				if (notification.getDestinations() != null && optionalCt.isPresent()) {
//					Ct ct = optionalCt.get();
//					for (String destination : notification.getDestinations()) {
//						// log.info("MAIL SMTP: preparing to send to:" + destination + " mail request:"
//						// + mail.toString());
//						switch (destination) {
//						case "MANAGER":
//							try {
//								userIds.add(ct.getWorkgroup().getWorkgroupManager().getId());
//							} catch (Exception e) {
//
//							}
//							break;
//						case "SUPPORTUSER":
//
//							try {
//								userIds.add(ct.getSupportUser().getId());
//							} catch (Exception e) {
//
//							}
//
//							break;
//						case "WORKGROUP":
//							try {
//								userIds.add(ct.getWorkgroup().getId());
//							} catch (Exception e) {
//
//							}
//							break;
//						case "CONTACT":
//
//							try {
//								userIds.add(ct.getContact().getId());
//							} catch (Exception e) {
//
//							}
//
//						}
//
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//			if (log.isLoggable(Level.SEVERE)) log.severe("Error getting notification destinations " + e.getMessage());
//		}
//
//		try {
//			notification.setUserIds(userIds);

        try {
            persistNew(notification);


//			WebClient client = WebClient.create();
//			Mono<String> result = client.post().uri("http://localhost:8089/notifications/send")
//					.accept(MediaType.APPLICATION_JSON).bodyValue(notification).exchangeToMono(response -> {
//						return response.bodyToMono(String.class);
//
//					});
//
//			String stringResult = result.block();

        } catch (Exception e) {
            if (log.isLoggable(Level.SEVERE)) log.severe("Unable to save notification.");
        }
    }

    @Transactional
    void persistNew(Notification notification) throws Exception {
        News news = new News();
        news.setPayload(notification.getPayload());
        news.setTags(notification.getTags());
        news.setTimestamp(notification.getTimestamp());
        news.setTitle(notification.getTitle());
        news.setType(notification.getType());
        news.setCtId(notification.getCtId());
        news.setDestinations(notification.getDestinations());
        newsRepo.save(news);

    }

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'SEND_NOTIFICATION'")
    public void listenSendNotificationEvent(ManagerAction event) throws Exception {

        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        Notification notification = mapper.convertValue(event.getActionObject(), Notification.class);
        //Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "send_notification");
        persistNew(notification);
        this.statisticService.inc("processing", "send_notification");


    }


}