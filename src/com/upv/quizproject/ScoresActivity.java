package com.upv.quizproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;




import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.upv.quizproject.db.ScoresDatabase;
import com.upv.quizproject.model.HighScore;
import com.upv.quizproject.model.HighScoreList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;


public class ScoresActivity extends Activity implements OnClickListener {
	
	private static final String PREFS_NAME = "QuizPreferences";
	
	private static final String TAG_LOCAL = "Local";
	private static final String TAG_FRIENDS = "Friends";
	
	private static final String URL_GET_FRIENDS = "http://wwtbamandroid.appspot.com/rest/highscores";
	
	private TabHost mTabHost;
	private TableLayout tableScoresFriends;
	private TableLayout tableScoresLocal;
	private Button resetScoresButton;
	
	private String name;
	
	List<HighScore> scoresList;
	HighScoreList friendsScoreList;
	

    private void setupTabHost() {
            mTabHost = (TabHost) findViewById(android.R.id.tabhost);
            mTabHost.setup();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setContentView(R.layout.activity_scores);
       
            resetScoresButton = (Button) findViewById(R.id.resetScores);
            
            resetScoresButton.setOnClickListener(this);
            
            setupTabHost();
            
            setupTab(getLayoutInflater().inflate(R.layout.tab_local, null), TAG_LOCAL);
            
         // Get name saved in preferences
         // Restore Preferences
         SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
         name = settings.getString("name", "");
         
         if(name.equals("")){
        	 Toast t = Toast.makeText(getApplicationContext(), getResources().getString(R.string.noNameToast), Toast.LENGTH_LONG);
        	 t.show();
         }
    }
    
    public void onResume(){
    	super.onResume();
        
        new GetFriendsScores().execute();
    }
    
	public void onBackPressed(){
		super.onBackPressed();
	    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	}

    private void setupTab(final View view, final String tag) {
            View tabview = createTabView(mTabHost.getContext(), tag);

            TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabContentFactory() {
                    public View createTabContent(String tag) {
                    	if(tag.equals(TAG_LOCAL)){
                    		// Inflate the Score's table
                            tableScoresLocal = (TableLayout) view.findViewById(R.id.tableScores);
                            
                            // Initiate Database Helper
                			ScoresDatabase scoreDBHelper = new ScoresDatabase(view.getContext());
                			
                			// Get all the scores
                			scoresList = scoreDBHelper.getAllScores();
                			
                			// Init textview
                    		TextView tv;
                    		
                    		// Loop for all the scores
                    		for(HighScore score : scoresList){
                    			
                        		TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_score, null);
                    			tv = (TextView) tableRow.findViewById(R.id.name);
                    			tv.setText(score.getName());
                    			
                    			tv = (TextView) tableRow.findViewById(R.id.score);
                    			tv.setText(String.format("%,d", score.getScoring()));
                    			
                    			tableScoresLocal.addView(tableRow);
                    		}
                    	} else if (tag.equals(TAG_FRIENDS)){
                    		// Inflate the Score's table
                            tableScoresFriends = (TableLayout) view.findViewById(R.id.tableScores);
                			
                			// Init textview
                    		TextView tv;
                    		
                    		// Loop for all the scores
                    		for(HighScore score : friendsScoreList.getScores()){
                    			
                        		TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_score, null);
                    			tv = (TextView) tableRow.findViewById(R.id.name);
                    			tv.setText(score.getName());
                    			
                    			tv = (TextView) tableRow.findViewById(R.id.score);
                    			tv.setText(String.format("%,d", score.getScoring()));
                    			
                    			tableScoresFriends.addView(tableRow);
                    		}
                    	}
                    	return view;
                    }
            });
            mTabHost.addTab(setContent);

    }

    private static View createTabView(final Context context, final String text) {
            View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
            TextView tv = (TextView) view.findViewById(R.id.tabsText);
            tv.setText(text);
            return view;
    }
    
    private class GetFriendsScores extends AsyncTask<Void, Void, Void> {

		
		protected Void doInBackground(Void... result) {
			
			if (name.equals("")){
				cancel(true);
				return null;
			}
			
			HttpClient httpClient = new DefaultHttpClient();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			
			HttpGet httpget = new HttpGet(URL_GET_FRIENDS + "?" + URLEncodedUtils.format(params, "utf-8"));
			
			try {
				// Get the response from the server
				HttpResponse response = httpClient.execute(httpget);
				String respStr = EntityUtils.toString(response.getEntity());
				
				JSONObject scoresJSON = new JSONObject(respStr);
				
				GsonBuilder gBuilder = new GsonBuilder();
				Gson gson = gBuilder.create();
				friendsScoreList = gson.fromJson(scoresJSON.toString(), HighScoreList.class);
	            
			} catch (Exception e) {
				Log.e("GetFriendsScore", e.getLocalizedMessage());
			}
			
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			// Initiate Friends Tag
            setupTab(getLayoutInflater().inflate(R.layout.tab_friends, null), TAG_FRIENDS);
		}    	
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.resetScores:
				// Initiate Database Helper
    			ScoresDatabase scoreDBHelper = new ScoresDatabase(v.getContext());
    			
			try {
				// Delete all scores
				scoreDBHelper.deleteAllScores();
				
				// Update the view
				tableScoresLocal.removeAllViews();
				
				Toast t = Toast.makeText(getApplicationContext(), "Puntuaciones borradas!", Toast.LENGTH_SHORT);
				t.show();
			} catch (Exception e) {
				Toast t = Toast.makeText(getApplicationContext(), "Error: Las puntuaciones no han podido ser borradas. Intentalo más tarde.", Toast.LENGTH_SHORT);
				t.show();
			}
				break;
		}
	}
}
