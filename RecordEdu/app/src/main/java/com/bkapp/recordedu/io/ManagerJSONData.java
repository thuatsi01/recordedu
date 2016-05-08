package com.bkapp.recordedu.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.bkapp.recordedu.json.JSONArrayRemove;

public class ManagerJSONData {

	private final String PLAY_LIST = "playlists";
	private final String ECHO_LIST = "echolist";
	private final String TITLE = "title";
	private final String pathElemnet = "/";

	public JSONObject dataEcho;

	public ManagerJSONData(JSONObject dataEcho) {
		this.dataEcho = dataEcho;
	}

	public JSONObject setPlayListNull(String nameFile) {
		try {
			JSONObject objectPlayList = new JSONObject();
			JSONArray arrayEchoList = new JSONArray();
			objectPlayList.put(PLAY_LIST, arrayEchoList);
			JSONObject objectEchoList = new JSONObject();
			arrayEchoList.put(objectEchoList);
			JSONArray arrayEcho = new JSONArray();
			objectEchoList.put(ECHO_LIST, arrayEcho);
			objectEchoList.put(TITLE, nameFile);
			dataEcho = objectPlayList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject setPlayList(String nameFile) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			JSONObject objectEchoList = new JSONObject();
			arrayEchoList.put(objectEchoList);
			JSONArray arrayEcho = new JSONArray();
			objectEchoList.put(ECHO_LIST, arrayEcho);
			objectEchoList.put(TITLE, nameFile);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject addEcho(int rating, String url, String nameEcho,
			String nameEchoList) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoList)) {
					JSONArray arrayEcho = objectEchoList
							.getJSONArray(ECHO_LIST);
					JSONObject objectEcho = new JSONObject();
					objectEcho.put("url", url);
					objectEcho.put("rating", rating);
					objectEcho.put("name", nameEcho);
					arrayEcho.put(objectEcho);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject changeDataEcho(int rating, String nameEcho,
			String nameEchoList) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoList)) {
					JSONArray arrayEcho = objectEchoList
							.getJSONArray(ECHO_LIST);
					for (int j = 0; j < arrayEcho.length(); j++) {
						JSONObject objectEcho = arrayEcho.getJSONObject(j);
						if (objectEcho.getString("name").equals(nameEcho)) {
							objectEcho.put("rating", rating);
							Log.i("EchoEdu", Integer.toString(rating));
							break;
						}
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("EchoEdu", dataEcho.toString());
		return dataEcho;
	}

	public JSONObject changeDataEcho(String nameEchoOld, String nameEchoNew,
			String nameEchoList, String url) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoList)) {
					JSONArray arrayEcho = objectEchoList
							.getJSONArray(ECHO_LIST);
					for (int j = 0; j < arrayEcho.length(); j++) {
						JSONObject objectEcho = arrayEcho.getJSONObject(j);
						if (objectEcho.getString("name").equals(nameEchoOld)) {
							objectEcho.put("name", nameEchoNew);
							objectEcho.put("url", url);
							break;
						}
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject changeDataEcho(String nameEchoFileOld,
			String nameEchoFileNew) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoFileOld)) {
					objectEchoList.put(TITLE, nameEchoFileNew);
					JSONArray arrayEcho = objectEchoList
							.getJSONArray(ECHO_LIST);
					for (int j = 0; j < arrayEcho.length(); j++) {
						JSONObject objectEcho = arrayEcho.getJSONObject(j);
						String urlOld = objectEcho.getString("url");
						String nameEcho = objectEcho.getString("name");
						int lengthUrl = urlOld.length();
						int lengthName = nameEcho.length() + 4;
						String urlNew = urlOld.substring(0, lengthUrl - 2
								- nameEchoFileOld.length() - lengthName)
								+ pathElemnet
								+ nameEchoFileNew
								+ pathElemnet
								+ nameEcho + ".mp3";
						objectEcho.put("url", urlNew);
					}
					objectEchoList.put(TITLE, nameEchoFileNew);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject deleteDataEcho(String nameEchoList, String nameEcho) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoList)) {
					JSONArray arrayEcho = objectEchoList.getJSONArray(ECHO_LIST);
					for (int j = 0; j < arrayEcho.length(); j++) {
						JSONObject objectEcho = arrayEcho.getJSONObject(j);
						if (objectEcho.getString("name").equals(nameEcho)) {
							JSONArray arrayTemp = new JSONArray();
							arrayTemp = JSONArrayRemove.remove(j, arrayEcho);
							objectEchoList.remove(ECHO_LIST);
							objectEchoList.put(ECHO_LIST, arrayTemp);
							break;
						}
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject deleteDataEcho(String nameEchoList) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoList)) {
					objectEchoList.remove(TITLE);
					objectEchoList.remove(ECHO_LIST);
					JSONArray arrayTemp = new JSONArray();
					arrayTemp = JSONArrayRemove.remove(i, arrayEchoList);
					dataEcho.remove(PLAY_LIST);
					dataEcho.put(PLAY_LIST, arrayTemp);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject echoDragDrop(int positionStart, int positionEnd,
			String nameEchoList) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				if (objectEchoList.getString(TITLE).equals(nameEchoList)) {
					JSONArray arrayEcho = objectEchoList
							.getJSONArray(ECHO_LIST);
					JSONObject objectTemp = arrayEcho
							.getJSONObject(positionStart);
					if (positionStart < positionEnd)
						for (int j = positionStart; j < positionEnd; ++j) {
							arrayEcho.put(j, arrayEcho.getJSONObject(j + 1));
						}
					else if (positionEnd < positionStart)
						for (int j = positionStart; j > positionEnd; --j) {
							arrayEcho.put(j, arrayEcho.getJSONObject(j - 1));
						}
					arrayEcho.put(positionEnd, objectTemp);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}

	public JSONObject echoDragDrop(int positionStart, int positionEnd) {
		try {
			JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
			JSONObject objectTemp = arrayEchoList.getJSONObject(positionStart);
			if (positionStart < positionEnd)
				for (int j = positionStart; j < positionEnd; ++j) {
					arrayEchoList.put(j, arrayEchoList.getJSONObject(j + 1));
				}
			else if (positionEnd < positionStart)
				for (int j = positionStart; j > positionEnd; --j) {
					arrayEchoList.put(j, arrayEchoList.getJSONObject(j - 1));
				}
			arrayEchoList.put(positionEnd, objectTemp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataEcho;
	}
}
