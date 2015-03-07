package com.upv.quizproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class WinGameActivity extends Activity {

	private TextView scoreText;
	Integer score; 
	public String myName;
	public static final String PREFS_NAME = "QuizPreferences";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setContentView(R.layout.activity_wingame);
            
            Bundle bune = getIntent().getExtras();
            scoreText = (TextView) findViewById(R.id.scoreText);
            score = bune.getInt("score"); 
            scoreText.setText(String.valueOf(score));
            
            myName = bune.getString("name");
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    		myName = settings.getString("name", "");
            new SetScore().execute();
            
    }
    
	public void onBackPressed(){
		Intent i = new Intent(this, MainActivity.class);
		finish();
		startActivity(i);
		overridePendingTransition(R.anim.unzoom_in_middle,
				R.anim.unzoom_out_middle);
	}
	
private class SetScore extends AsyncTask<Void, Void, Void> {

		
		private static final String URL_SET_SCORE = "http://wwtbamandroid.appspot.com/rest/highscores";

		protected void onPreExecute() {
			super.onPreExecute();

			

			return;
		}

		protected Void doInBackground(Void... result) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPut request = new HttpPut(URL_SET_SCORE);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", myName));
			params.add(new BasicNameValuePair("score", String.valueOf(score)));
			
			

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
			Toast t = Toast.makeText(getApplicationContext(), "Puntuación guardada en el servidor", Toast.LENGTH_SHORT);
			t.show();
			super.onPostExecute(result);

			
		}

	}

}
