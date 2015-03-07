package com.upv.quizproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {

	private final String[] helpSpinnerData = new String[] { "1", "2", "3" };
	public static final String PREFS_NAME = "QuizPreferences";
	
	private Spinner helpSpinner;
	private EditText nameEditText;
	private EditText friendEditText;
	private Button addFriendButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// Setup helpSpinner
		helpSpinner = (Spinner) findViewById(R.id.helpSpinner);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, helpSpinnerData);

		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		helpSpinner.setAdapter(spinnerAdapter);

		// Setup views
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		friendEditText = (EditText) findViewById(R.id.friendEditText);
		
		addFriendButton = (Button) findViewById(R.id.AddFriendButton);

		// Restore Preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		nameEditText.setText(settings.getString("name", ""));
		if(settings.getInt("helpsNum", 0) != 0){

			helpSpinner.setSelection(settings.getInt("helpsNum", 0)-1);
		} else {
			helpSpinner.setSelection(0);
		}
		// Set Click Listeners
		addFriendButton.setOnClickListener(this);

	}

	protected void onStop() {
		super.onStop();

		// Save Preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("name", nameEditText.getText().toString());
		editor.putInt("helpsNum", helpSpinner.getSelectedItemPosition()+1);

		// Commit preferences
		editor.commit();
	}

	public void onBackPressed() {
		Intent i = new Intent(this, MainActivity.class);
		finish();
		startActivity(i);
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	}

	private class SetFriend extends AsyncTask<Void, Void, Void> {

		private String name;
		private String friendName;
		
		private static final String URL_SET_FRIEND = "http://wwtbamandroid.appspot.com/rest/friends";

		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			// Setup names
			name = nameEditText.getText().toString();
			friendName = friendEditText.getText().toString();
			
			if (name.equals("")) {
				Log.e("SetFriend", "Your name is empty.");
				Toast t = Toast.makeText(getApplicationContext(), "ERROR: Tu nombre está vacio", Toast.LENGTH_SHORT);
				t.show();
				cancel(true);
			}
			
			if (friendName.equals("")){
				Log.e("SetFriend", "Your friend's name is empty.");
				Toast t = Toast.makeText(getApplicationContext(), "ERROR: El nombre de tu amigo está vacio.", Toast.LENGTH_SHORT);
				t.show();
				cancel(true);
			}

			return;
		}

		protected Void doInBackground(Void... result) {

			HttpClient httpClient = new DefaultHttpClient();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("friend_name", friendName));

			HttpPost request = new HttpPost(URL_SET_FRIEND);

			try {
				// Set the parameters to the request
				request.setEntity(new UrlEncodedFormEntity(params));
				
				// Execute the request
				httpClient.execute(request);


			} catch (Exception e) {
				Log.e("SetFriend", e.getLocalizedMessage());
				cancel(true);
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast t = Toast.makeText(getApplicationContext(), "Amigo " + friendName + " " + "añadido", Toast.LENGTH_SHORT);
			t.show();
			

		}
		}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.AddFriendButton:
				new SetFriend().execute();
				break;
		}
		
	}
}
