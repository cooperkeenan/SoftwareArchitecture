package napier.destore.notification.sender;

import napier.destore.notification.domain.Notification;


public interface NotificationSender {

   
    boolean send(Notification notification);

    boolean supports(Notification.NotificationChannel channel);
}