package com.upv.quizproject;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.upv.quizproject.db.ScoresDatabase;
import com.upv.quizproject.model.Question;
import com.upv.quizproject.model.HighScore;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

public class PlayActivity extends Activity implements OnClickListener {

	private static final int[] scores = { 0, 100, 200, 300, 500, 1000, 2000,
			4000, 8000, 16000, 32000, 64000, 125000, 250000, 500000, 1000000 };

	private static final String PREFS_NAME = "QuizPreferences";
	private static final int TRANSITION_TIME = 800;
	private static final int AFTER_TRANSITION_TIME = 400;
	
	private static final String STATE_CURRENTQUESTION = "currentQuestion";
	private static final String STATE_CURRENTHELPNUM = "currentHelpNum";
	private static final String STATE_AUDIENCEHELPSTATE = "audienceHelpState";
	private static final String STATE_FIFTYHELPSTATE = "callHelpState";
	private static final String STATE_CALLHELPSTATE = "fiftyHelpState";

	private String userName;

	private int currentQuestion = 1;

	private Question question;

	private XmlPullParserFactory xppFactory;
	private XmlPullParser xpp;
	private InputStream input;

	private TextView title;
	private TextView answer1, answer2, answer3, answer4;
	private TextView questionNumber;
	private TextView scoreNumber;
	
	private ImageView audienceHelp, callHelp, fiftyHelp;

	private int helpsNum;
	private int currentHelpsNum = 0;
	
	
	private boolean audienceState = true;
	private boolean fiftyState = true;
	private boolean callState = true;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		// Get name from preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userName = settings.getString("name", "");
		helpsNum = settings.getInt("helpsNum", 3);

		if (userName.equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			// 2. Chain together various setter methods to set the dialog
			// characteristics
			builder.setMessage(R.string.no_username).setTitle(
					R.string.no_username_title);
			// Add the buttons
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							Intent i = new Intent(getBaseContext(),
									SettingsActivity.class);
							finish();
							startActivity(i);
						}
					});
			// Set other dialog properties
			
			builder.setCancelable(false);

				
			// Create the AlertDialog
			AlertDialog dialog = builder.create();

			dialog.show();
		}

		// Start Xml Parser
		try {
			xppFactory = XmlPullParserFactory.newInstance();
			xppFactory.setNamespaceAware(true);
			xpp = xppFactory.newPullParser();

			input = getResources().openRawResource(R.raw.questions0001);
			xpp.setInput(input, null);
		} catch (Exception e) {
			Log.e("startXmlParser", e.getMessage());
		}

		// Get Views
		title = (TextView) findViewById(R.id.questionTitle);
		answer1 = (TextView) findViewById(R.id.answer1);
		answer2 = (TextView) findViewById(R.id.answer2);
		answer3 = (TextView) findViewById(R.id.answer3);
		answer4 = (TextView) findViewById(R.id.answer4);
		questionNumber = (TextView) findViewById(R.id.questionNumber);
		scoreNumber = (TextView) findViewById(R.id.scoreNumber);
		callHelp = (ImageView) findViewById(R.id.callHelp);
		audienceHelp = (ImageView) findViewById(R.id.audienceHelp);
		fiftyHelp = (ImageView) findViewById(R.id.fiftyHelp);

		// Set Listeners for the answers
		answer1.setOnClickListener(this);
		answer2.setOnClickListener(this);
		answer3.setOnClickListener(this);
		answer4.setOnClickListener(this);
		callHelp.setOnClickListener(this);
		audienceHelp.setOnClickListener(this);
		fiftyHelp.setOnClickListener(this);
		
		if (savedInstanceState != null) {
			currentQuestion = savedInstanceState.getInt(STATE_CURRENTQUESTION);
			
			for (int i = 1; i < currentQuestion; i++){
				getQuestion();
			}
		
			
		
			if(savedInstanceState.getBoolean(STATE_AUDIENCEHELPSTATE) == false){
				audienceHelp.setImageResource(R.drawable.audience_used);
				audienceHelp.setClickable(false);
			}
			
			if(savedInstanceState.getBoolean(STATE_FIFTYHELPSTATE) == false){
				fiftyHelp.setImageResource(R.drawable.fifty_used);
				fiftyHelp.setClickable(false);
			}
			
			if(savedInstanceState.getBoolean(STATE_CALLHELPSTATE) == false){
				callHelp.setImageResource(R.drawable.call_used);
				callHelp.setClickable(false);
			}
			
			currentHelpsNum = savedInstanceState.getInt(STATE_CURRENTHELPNUM);
			
			checkHelpers();
		}
		

		// Get First Question
		question = getQuestion();

		// Update all necessary view
		updateView();

	}
	
	protected void onSaveInstanceState(Bundle outState) {
		// Save the current question
		
		outState.putInt(STATE_CURRENTQUESTION, currentQuestion);
		outState.putInt(STATE_CURRENTHELPNUM, currentHelpsNum);
		outState.putBoolean(STATE_CALLHELPSTATE, callState);
		outState.putBoolean(STATE_AUDIENCEHELPSTATE, audienceState);
		outState.putBoolean(STATE_FIFTYHELPSTATE, fiftyState);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);
	}

	private void updateView(int answerChosenNum) {

		TextView answerChosen = null;
		
		// Select the answer chosen
		switch (answerChosenNum) {
		case 1:
			answerChosen = answer1;
			setBackgroundAndKeepPadding(
					answerChosen,
					getApplicationContext().getResources().getDrawable(
							R.drawable.answer_background_selector));
			break;
		case 2:
			answerChosen = answer2;
			setBackgroundAndKeepPadding(
					answerChosen,
					getApplicationContext().getResources().getDrawable(
							R.drawable.answer_background_selector));
			break;
		case 3:
			answerChosen = answer3;
			setBackgroundAndKeepPadding(
					answerChosen,
					getApplicationContext().getResources().getDrawable(
							R.drawable.answer_background_selector));
			break;
		case 4:
			answerChosen = answer4;
			setBackgroundAndKeepPadding(
					answerChosen,
					getApplicationContext().getResources().getDrawable(
							R.drawable.answer_background_selector));
			break;
		}
		

		setBackgroundAndKeepPadding(
				answerChosen,
				getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_selector));

		

		updateView();
	}

	private void updateView() {
		// Change texts to next question
		title.setText(question.getText());

		answer1.setText(question.getAnswer1());
		answer2.setText(question.getAnswer2());
		answer3.setText(question.getAnswer3());
		answer4.setText(question.getAnswer4());

		questionNumber.setText(Integer.toString(question.getNumber()));
		scoreNumber.setText(String.format("%,d", scores[currentQuestion]));

		// Set everything clickable again
		setClickable(true);

	}

	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.unzoom_in_middle,
				R.anim.unzoom_out_middle);
	}

	private Question getQuestion() {
		Question question = new Question();
		try {
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_TAG) {
				if (eventType == XmlPullParser.START_TAG) {

					if (xpp.getName().equals("question")) {

						// Get answers
						String answer1 = xpp.getAttributeValue(null, "answer1");
						String answer2 = xpp.getAttributeValue(null, "answer2");
						String answer3 = xpp.getAttributeValue(null, "answer3");
						String answer4 = xpp.getAttributeValue(null, "answer4");

						// Get helps
						int audience = Integer.parseInt(xpp.getAttributeValue(
								null, "audience"));
						int fifty1 = Integer.parseInt(xpp.getAttributeValue(
								null, "fifty1"));
						int fifty2 = Integer.parseInt(xpp.getAttributeValue(
								null, "fifty2"));
						int phone = Integer.parseInt(xpp.getAttributeValue(
								null, "phone"));

						// Get question info
						int right = Integer.parseInt(xpp.getAttributeValue(
								null, "right"));
						int number = Integer.parseInt(xpp.getAttributeValue(
								null, "number"));
						String text = xpp.getAttributeValue(null, "text");

						question = new Question(answer1, answer2, answer3,
								answer4, audience, fifty1, fifty2, number,
								phone, right, text);
					}
				}

				eventType = xpp.nextTag();
			}
			eventType = xpp.nextTag();
		} catch (Exception e) {
			Log.e("error", e.getMessage());
		}

		return question;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.answer1:
			checkIfAnswerIsRight(1);
			break;
		case R.id.answer2:
			checkIfAnswerIsRight(2);
			break;
		case R.id.answer3:
			checkIfAnswerIsRight(3);
			break;
		case R.id.answer4:
			checkIfAnswerIsRight(4);
			break;
		
		case R.id.audienceHelp:
			setAudienceHelper();
			break;
		case R.id.callHelp:
			setCallHelper();
			break;
		case R.id.fiftyHelp:
			setFiftyHelper();
			break;
		}

	}

	private void setFiftyHelper() {
		fiftyHelp.setImageResource(R.drawable.fifty_used);
		
		if(question.getFifty1() == 1 || question.getFifty2() == 1){
			answer1.setText("");
			answer1.setClickable(false);
		}
		
		if(question.getFifty1() == 2 || question.getFifty2() == 2){
			answer2.setText("");
			answer2.setClickable(false);
		}
		
		if(question.getFifty1() == 3 || question.getFifty2() == 3){
			answer3.setText("");
			answer3.setClickable(false);
		} 
		
		if(question.getFifty1() == 4 || question.getFifty2() == 4){
			answer4.setText("");
			answer4.setClickable(false);
		}
		
		fiftyHelp.setClickable(false);
		
		fiftyState = false;
		
		checkHelpers();
		
		currentHelpsNum++;

		
	}

	private void checkHelpers() {
		if(currentHelpsNum == helpsNum){
			fiftyHelp.setClickable(false);
			audienceHelp.setClickable(false);
			callHelp.setClickable(false);
			
			callHelp.setImageResource(R.drawable.call_used);
			audienceHelp.setImageResource(R.drawable.audience_used);
			fiftyHelp.setImageResource(R.drawable.fifty_used);
		}
	}

	private void setCallHelper() {
		callHelp.setImageResource(R.drawable.call_used);
		switch(question.getPhone()) {
			case 1:
				setBackgroundAndKeepPadding(answer1, getApplicationContext().getResources().getDrawable(
							R.drawable.answer_background_audience));
				break;
			case 2:
				setBackgroundAndKeepPadding(answer2, getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_audience));
				break;
			case 3:
				setBackgroundAndKeepPadding(answer3, getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_audience));
				break;
			case 4:
				setBackgroundAndKeepPadding(answer4, getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_audience));
				break;
		}
		callHelp.setClickable(false);
		callState = false;
		
		checkHelpers();
		
		currentHelpsNum++;
		
		
		
	}

	private void setAudienceHelper() {
		audienceHelp.setImageResource(R.drawable.audience_used);
		switch(question.getAudience()) {
			case 1:
				setBackgroundAndKeepPadding(answer1, getApplicationContext().getResources().getDrawable(
							R.drawable.answer_background_audience));
				break;
			case 2:
				setBackgroundAndKeepPadding(answer2, getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_audience));
				break;
			case 3:
				setBackgroundAndKeepPadding(answer3, getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_audience));
				break;
			case 4:
				setBackgroundAndKeepPadding(answer4, getApplicationContext().getResources().getDrawable(
						R.drawable.answer_background_audience));
				break;
		}
		audienceHelp.setClickable(false);
		
		audienceState = false;
		
		checkHelpers();
		
		currentHelpsNum++;
		
		
		
	}

	private void setClickable(boolean b) {
		answer1.setClickable(b);
		answer2.setClickable(b);
		answer3.setClickable(b);
		answer4.setClickable(b);

	}

	private void checkIfAnswerIsRight(final int answerChosenNum) {
		setClickable(false);
		
		TextView answerChosen = null;

		// Select the answer chosen
		switch (answerChosenNum) {
		case 1:
			answerChosen = answer1;
			break;
		case 2:
			answerChosen = answer2;
			break;
		case 3:
			answerChosen = answer3;
			break;
		case 4:
			answerChosen = answer4;
			break;
		}

		if (question.getRight() == answerChosenNum) {
			// Set background to correct with transition.
			setBackgroundAndKeepPadding(
					answerChosen,
					getApplicationContext().getResources().getDrawable(
							R.drawable.transition_selected_to_correct));
			TransitionDrawable transition = (TransitionDrawable) answerChosen
					.getBackground();
			transition.startTransition(TRANSITION_TIME);

			// If answer is Right
			if (currentQuestion == 15) {
				// If we are in the last question

				// Get name from preferences
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

				// Create new Score
				final HighScore score = new HighScore(
						settings.getString("name", "undefined"),
						scores[currentQuestion]);

				// Store it in Database
				ScoresDatabase scoreDBHelper = new ScoresDatabase(
						getBaseContext());
				scoreDBHelper.saveScore(score);
				
				// After transition is over, start EndGameActivity
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent winGameIntent = new Intent(getBaseContext(),
								WinGameActivity.class);
						winGameIntent.putExtra("score", score.getScoring());
						finish();
						startActivity(winGameIntent);
						
					}
				}, TRANSITION_TIME + AFTER_TRANSITION_TIME);
			} else {
				
				setBackgroundAndKeepPadding(
						answerChosen,
						getApplicationContext().getResources().getDrawable(
								R.drawable.transition_selected_to_correct));

				TransitionDrawable transition1 = (TransitionDrawable) answerChosen
						.getBackground();
				transition1.startTransition(TRANSITION_TIME);
				
				currentQuestion++;				

				// After transition is over, start next question.
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						question = getQuestion();
						updateView(answerChosenNum);
						
						setBackgroundAndKeepPadding(
								answer1,
								getApplicationContext().getResources().getDrawable(
										R.drawable.answer_background_selector));
						setBackgroundAndKeepPadding(
								answer2,
								getApplicationContext().getResources().getDrawable(
										R.drawable.answer_background_selector));
						setBackgroundAndKeepPadding(
								answer3,
								getApplicationContext().getResources().getDrawable(
										R.drawable.answer_background_selector));
						setBackgroundAndKeepPadding(
								answer4,
								getApplicationContext().getResources().getDrawable(
										R.drawable.answer_background_selector));
					}
				}, TRANSITION_TIME + AFTER_TRANSITION_TIME);
			}

		} else {
			// If answer is Wrong

			// Set background to wrong with transition.
			setBackgroundAndKeepPadding(
					answerChosen,
					getApplicationContext().getResources().getDrawable(
							R.drawable.transition_selected_to_wrong));

			TransitionDrawable transition = (TransitionDrawable) answerChosen
					.getBackground();
			transition.startTransition(TRANSITION_TIME);

			// Create new Score
			final HighScore score = new HighScore(userName,
					scores[((currentQuestion - 1) / 5) * 5]);

			// Store it in Database
			ScoresDatabase scoreDBHelper = new ScoresDatabase(getBaseContext());
			scoreDBHelper.saveScore(score);

			// After transition is over, start EndGameActivity
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent endGameIntent = new Intent(getBaseContext(),
							EndGameActivity.class);
					endGameIntent.putExtra("score", score.getScoring());
					finish();
					startActivity(endGameIntent);
				}
			}, TRANSITION_TIME + AFTER_TRANSITION_TIME);

		}

	}

	@SuppressWarnings("deprecation")
	public static void setBackgroundAndKeepPadding(View view,
			Drawable backgroundDrawable) {
		Rect drawablePadding = new Rect();
		backgroundDrawable.getPadding(drawablePadding);

		int top = view.getPaddingTop() + drawablePadding.top;
		int left = view.getPaddingLeft() + drawablePadding.left;
		int right = view.getPaddingRight() + drawablePadding.right;
		int bottom = view.getPaddingBottom() + drawablePadding.bottom;

		view.setBackgroundDrawable(backgroundDrawable);
		view.setPadding(left, top, right, bottom);
	}
}
