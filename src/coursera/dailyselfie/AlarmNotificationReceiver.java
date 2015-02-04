package coursera.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class AlarmNotificationReceiver extends BroadcastReceiver {
	
	private static final String TAG = "Proyect-DailySelfie";
	private static final String tickerText = "Hey!";
	private static final int MY_NOTIFICATION_ID = 11151990;
	
	@Override
	public void onReceive(Context context, Intent intent) {
					
		Log.i(TAG, "New notification");
		final Intent restartMainActivityIntent = new Intent(context, SelfieViewActivity.class);
		restartMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Create a PendingIntent and set its flags to FLAG_UPDATE_CURRENT
		PendingIntent mContentIntent = PendingIntent.getActivity(context, 0, 
				restartMainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Uses R.layout.custom_notification for the layout of the notification View.
		RemoteViews mContentView = new RemoteViews(context.getPackageName(),
				R.layout.custom_notification);

		// Set the notification View's text 
		String tmp = context.getResources().getString(R.string.notification);
		mContentView.setTextViewText(R.id.text, tmp);

		// Use the Notification.Builder class to create the Notification.
		Notification.Builder notificationBuilder = new Notification.Builder(context)
			.setSmallIcon(android.R.drawable.ic_menu_camera)
			.setTicker(tickerText)
			.setAutoCancel(true)
			.setContentIntent(mContentIntent)
			.setContent(mContentView);

		// Send the notification
		NotificationManager mNotificationManager = (NotificationManager) 
				context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MY_NOTIFICATION_ID,	notificationBuilder.build());
					
	}
}
