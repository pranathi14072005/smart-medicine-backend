package com.smartmedicine.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sends push notifications to mobile / browser via Firebase Cloud Messaging.
 */
@Service
public class FcmNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FcmNotificationService.class);

    /**
     * Sends a push notification to the device identified by {@code fcmToken}.
     *
     * @param fcmToken  the target device's FCM registration token
     * @param title     notification title (e.g. "Time to take your medicine!")
     * @param body      notification body (e.g. "Paracetamol 500mg – Morning Dose")
     * @return true if the message was sent, false on any error or if FCM is not initialised
     */
    public boolean sendNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.debug("FCM token is blank – skipping push notification.");
            return false;
        }
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase not initialised – push notification skipped (title={}).", title);
            return false;
        }
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("title", title)
                    .putData("body", body)
                    .setToken(fcmToken)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM push sent successfully. MessageId={}", response);
            return true;
        } catch (Exception e) {
            log.error("Failed to send FCM push notification: {}", e.getMessage());
            return false;
        }
    }
}
