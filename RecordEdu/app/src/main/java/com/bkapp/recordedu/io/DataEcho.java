package com.bkapp.recordedu.io;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class DataEcho {

	private final String PLAY_LIST = "playlists";
	private final String ECHO_LIST = "echolist";
	private final String TITLE = "title";

	public ArrayList<String> nameEcho, urlEcho, listEcho;
	public ArrayList<Integer> ratingEcho, positionEndEcho;

	public DataEcho(JSONObject dataEcho) {
		listEcho = new ArrayList<String>();
		nameEcho = new ArrayList<String>();
		urlEcho = new ArrayList<String>();
		ratingEcho = new ArrayList<Integer>();
		positionEndEcho = new ArrayList<Integer>();
		try {
			parseData(dataEcho);
		} catch (Exception e) {
			Log.e("EchoEdu", "Parse Data Error");
		}
	}

	private void parseData(JSONObject dataEcho) throws Exception {
		JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
		int count = 0;
		for (int i = 0; i < arrayEchoList.length(); i++) {
			JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
			JSONArray arrayEcho = objectEchoList.getJSONArray(ECHO_LIST);
			int positionAddEchoList = listEcho.size();
			listEcho.add(positionAddEchoList, objectEchoList.getString(TITLE));
			positionEndEcho.add(positionAddEchoList, count + arrayEcho.length());
			count += arrayEcho.length();
			for (int j = 0; j < arrayEcho.length(); j++) {
				JSONObject objectEcho = arrayEcho.getJSONObject(j);
				int positionAddEcho = nameEcho.size();
				nameEcho.add(positionAddEcho, objectEcho.getString("name"));
				Log.i("EchoEdu", Integer.toString(j) + " " + objectEcho.getString("name"));
				urlEcho.add(positionAddEcho, objectEcho.getString("url"));
				ratingEcho.add(positionAddEcho, objectEcho.getInt("rating"));
			}
		}
	}

	public void swapEcho(JSONObject dataEcho) {
		listEcho.clear();
		nameEcho.clear();
		urlEcho.clear();
		ratingEcho.clear();
		positionEndEcho.clear();
		try {
			parseData(dataEcho);
		} catch (Exception e) {
			Log.e("EchoEdu", "Parse Data Error");
		}
	}
}
