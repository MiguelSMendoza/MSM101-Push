package es.netrunners.push;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PushActivity extends Activity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	}

	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		Button btnPush;
	    TextView txtPush;
		
		GoogleCloudMessaging gcm;
		String regid;
		String PROJECT_NUMBER = "994972798787";

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_push, container,
					false);
			btnPush = (Button) rootView.findViewById(R.id.btnPush);
	        txtPush = (TextView) rootView.findViewById(R.id.txtPush);

	        btnPush.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getRegId();
				}
			});
			return rootView;
		}
		
		public void getRegId() {
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					String msg = "";
					try {
						if (gcm == null) {
							gcm = GoogleCloudMessaging
									.getInstance(getActivity());
						}
						regid = gcm.register(PROJECT_NUMBER);
						msg = "Device registered, registration ID=" + regid;
						Log.i("GCM", msg);
						registrarUsuario("Miguel", regid);
					} catch (IOException ex) {
						msg = "Error :" + ex.getMessage();
					}
					return msg;
				}

				@Override
				protected void onPostExecute(String msg) {
					txtPush.setText(msg);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG)
							.show();
				}
			}.execute(null, null, null);
		}
		
		private void registrarUsuario(String username, String regId) {
			Log.w("GCM", "registrarUsuario");
			Log.w("GCM", "username: " + username);
			Log.w("GCM", "regID: " + regId);
			try {

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("tag", "usersave"));
				nameValuePairs.add(new BasicNameValuePair("username", username));
				nameValuePairs.add(new BasicNameValuePair("gcmcode", regId));

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://services.netrunners.es/PUSH/index.php");
				if (nameValuePairs != null)
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String res = httpclient.execute(httppost, responseHandler);

				Log.w("GCM", "RES: " + res);

			} catch (Exception e) {
				Log.w("GCM", "ex: " + e.getMessage().toString());
			}
		}
	}

}
