package es.netrunners.push;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GcmMessageHandler extends IntentService {
	String mes;
	private Handler handler;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		mes = extras.getString("message");
		
		String server = extras.getString("server");

		Intent in = new Intent(getApplicationContext(), PushActivity.class);
		in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(server).setContentText(mes)
				.setAutoCancel(true);

		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, in,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(contentIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
		showToast();
		Log.i("GCM", "Received : (" + messageType + ")  " + mes);

		GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

	public void showToast() {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG)
						.show();
			}
		});

	}
}
