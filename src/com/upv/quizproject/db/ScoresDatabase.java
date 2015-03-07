package com.upv.quizproject.db;

import java.util.ArrayList;
import java.util.List;

import com.upv.quizproject.model.HighScore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScoresDatabase extends SQLiteOpenHelper {
	
	private final String TABLE_SCORES = "CREATE TABLE IF NOT EXISTS scores (" +
														"id		integer	primary key autoincrement," +
														"name	text	not null," +
														"score	integer	not null)";
	
	public ScoresDatabase(Context context) {
		super(context, "scores.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		try {
			if(db.isOpen()){
				// Create tables
				db.execSQL(TABLE_SCORES);
			}
		} catch (Exception e){
			Log.d("ScoresDatabase.onCreate", e.getMessage());
		}
		
	}
	
	public List<HighScore> getAllScores() {
		List<HighScore> scoresList = new ArrayList<HighScore>();

		String selectQuery = "SELECT * FROM scores GROUP BY name, score ORDER BY score DESC LIMIT 0,10";

		SQLiteDatabase db = this.getReadableDatabase();
		try {
			if (db.isOpen()){
				Cursor cursor = db.rawQuery(selectQuery, null);

				if (cursor.moveToFirst()){
					do {
						HighScore score = new HighScore(cursor.getString(1),
												Integer.parseInt(cursor.getString(2)));

						scoresList.add(score);
					} while (cursor.moveToNext());
				}
			}
		} catch(SQLiteException e){
			Log.e("ScoreDatabase.getAllScores",e.getMessage());
		} finally {
			db.close();
		}

		return scoresList;
	}
	
	public boolean deleteAllScores() throws Exception {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			if(db.isOpen()){
				db.delete("scores", null, null);
			} else {
				return false;
			}
		} catch(SQLiteException e){
			Log.e("ScoreDatabase.deleteAllScores",e.getMessage());
			throw new Exception();
		} finally {
			db.close();
		}
		
		return false;
	}
	
	public boolean saveScore(HighScore score){
		ContentValues mValues;
		SQLiteDatabase db = this.getWritableDatabase();
		
		try {

			if (db.isOpen()) {
				mValues = new ContentValues();

				mValues.put("name", score.getName());
				mValues.put("score", score.getScoring());
				
				db.insert("scores", "", mValues);

				return true;
			} else {
				return false;
			}
			
		} catch (SQLiteException e){
			Log.e("ScoreDatabase.saveScore",e.getMessage());
			
			return false;
		} finally {
			db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
