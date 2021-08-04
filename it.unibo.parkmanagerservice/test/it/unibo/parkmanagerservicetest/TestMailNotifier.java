package it.unibo.parkmanagerservicetest;

import it.unibo.parkmanagerservice.bean.User;
import it.unibo.parkmanagerservice.bean.UserState;
import it.unibo.parkmanagerservice.notification.DefaultNotificationFactory;
import it.unibo.parkmanagerservice.notification.NotificationType;
import it.unibo.parkmanagerservice.notification.Notifier;
import it.unibo.parkmanagerservice.notification.SystemNotifier;

public class TestMailNotifier {
	
	@SuppressWarnings("incomplete-switch")
	public static void main(String[] args) {
		Notifier notifier = SystemNotifier.get();
		User user = new User(0, "Luca", "Marchegiani", "luca.marchegiani3@studio.unibo.it", UserState.CREATED, null, null);
		NotificationType[] types = NotificationType.values();
		String[] not_args = new String[2];
	
		for(NotificationType t : types) {
			switch(t) {
			case GENERAL:
				not_args[0] = "General Text";
				break;
				
			case PICKUP:
				not_args[0] = "120";
				break;
				
			case SLOTNUM:
				not_args[0] = "0";
				not_args[1] = "120";
				break;
				
			case TOKEN:
				not_args[0] = "FakeToken";
				break;
				
			}
			
			notifier.sendNotification(DefaultNotificationFactory.createForUser(user, t, not_args));
			System.out.println("TestMailNotifier | Sent notification " + t.toString());
		}
		
		System.out.println("TestMailNotifier | Terminated");
	}

}
