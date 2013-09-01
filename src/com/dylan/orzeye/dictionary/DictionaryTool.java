package com.dylan.orzeye.dictionary;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DictionaryTool {
	public static final String DATABASE_PATH = Environment
			.getExternalStorageDirectory().toString()
			+ "/OrzEye"
			+ "/dictionary/";
	private final String DATABASE_FILENAME = "dictionary.db";
	private SQLiteDatabase database = null;

	public DictionaryTool() {
		database = openDatabase();
	}

	private SQLiteDatabase openDatabase() {
		String databaseFilename = DATABASE_PATH + DATABASE_FILENAME;
		if ((new File(databaseFilename)).exists()) {
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
					null);
		}
		return database;
	}

	public String lookUpDictionary(String recognizedText) {
		String sql = "select chinese from t_words where english=?";
		Cursor cursor = database.rawQuery(sql, new String[] { recognizedText });
		String result = "not found!";

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			result = cursor.getString(cursor.getColumnIndex("chinese"));
		}
		return result;
	}

	public boolean isDictionaryReady() {
		return database == null ? false : true;
	}
}
