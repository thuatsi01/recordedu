package com.bkapp.recordedu.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONArrayRemove {

	public static JSONArray remove(final int idx, final JSONArray from) {
		int size = from.length();

		final JSONArray ja = new JSONArray();
		for (int i = 0; i < size; i++) {
			JSONObject objs;
			try {
				objs = from.getJSONObject(i);
				if (i != idx) {
					Log.i("EchoEdu", objs.toString());
					ja.put(objs);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return ja;
	}
}