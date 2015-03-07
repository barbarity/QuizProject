package com.upv.quizproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Add Listeners
		ImageView playImageView = (ImageView) findViewById(R.id.playImageView);
		playImageView.setOnClickListener(this);

		ImageView settingsImageView = (ImageView) findViewById(R.id.settingsImageView);
		settingsImageView.setOnClickListener(this);

		ImageView scoresImageView = (ImageView) findViewById(R.id.scoresImageView);
		scoresImageView.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.playImageView: {
				Intent i = new Intent(this, PlayActivity.class);
				startActivity(i);
				
				overridePendingTransition(R.anim.zoom_in_middle, R.anim.zoom_out_middle);
				break;
			} case R.id.scoresImageView: {
				Intent i = new Intent(this, ScoresActivity.class);
				startActivity(i);
				
				overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
				break;
			} case R.id.settingsImageView: {
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				
				overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
				break;
			} default: {
				Toast t = Toast.makeText(this.getApplicationContext(), "Unimplemented method", Toast.LENGTH_SHORT);
				t.show();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	 @Override public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
         case R.id.action_about:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
         }
         return true; /** true -> consumimos el item, no se propaga*/
}
	
	
}
