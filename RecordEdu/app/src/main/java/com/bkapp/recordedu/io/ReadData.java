package com.bkapp.recordedu.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.bkapp.recordedu.json.JSONArrayRemove;

public class ReadData {

	// public final String URL_FILE_DATA =
	// "/data/data/com.tranle.echoedu/dataEcho.txt";
	public final String URL_FILE_JSON = "/JSON/";//
	public final String URL_FILE_DATA = "/dataEcho.txt";
	public final String URL_FILE_ECHO_LIST = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/EchoList/";
	private final String PLAY_LIST = "playlists";
	private final String ECHO_LIST = "echolist";
	private final String TITLE = "title";
	// data Echo
	public JSONObject jsonEcho = null;

	public ReadData() {
		jsonEcho = new JSONObject();
	}

	public void LoadJSON() {
		new File(URL_FILE_ECHO_LIST).mkdir();
		File fileDataEcho = new File(URL_FILE_ECHO_LIST + URL_FILE_JSON + URL_FILE_DATA);
		File fileManager = new File(URL_FILE_ECHO_LIST);
		try {
			if (fileDataEcho.length() > 0) {
				BufferedReader bufferedReaderEcho = new BufferedReader(
						new FileReader(URL_FILE_ECHO_LIST + URL_FILE_JSON
								+ URL_FILE_DATA));
				String data = "";
				String tempData;
				while ((tempData = bufferedReaderEcho.readLine()) != null) {
					data += tempData;
				}
				bufferedReaderEcho.close();
				jsonEcho = new JSONObject(data);
				updateFileData();
			} else if (fileDataEcho.exists()) {
				if (fileManager.listFiles().length != 0) {
					deleteDirectory(fileManager);
				}
			} else {
				deleteDirectory(fileManager);
				fileDataEcho.createNewFile();
			}
		} catch (Exception e) {
			Log.e("EchoEdu", "Read File Echo Reate");
		}
	}

	public void updateFileData() {
		try {
			JSONArray arrayEchoList = jsonEcho.getJSONArray(PLAY_LIST);
			for (int i = 0; i < arrayEchoList.length(); i++) {
				JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
				String pathEchoList = URL_FILE_ECHO_LIST
						+ objectEchoList.getString(TITLE);
				File echoList = new File(pathEchoList);
				if (echoList.exists()) {
					JSONArray arrayEcho = objectEchoList
							.getJSONArray(ECHO_LIST);
					for (int j = 0; j < arrayEcho.length(); j++) {
						JSONObject objectEcho = arrayEcho.getJSONObject(j);
						String pathEcho = objectEcho.getString("url");
						File echoFile = new File(pathEcho);
						if (!echoFile.exists()) {
							JSONArray arrayTemp = new JSONArray();
							arrayTemp = JSONArrayRemove.remove(j, arrayEcho);
							objectEchoList.remove(ECHO_LIST);
							objectEchoList.put(ECHO_LIST, arrayTemp);
						}
					}
				} else {
					JSONArray arrayTemp = new JSONArray();
					arrayTemp = JSONArrayRemove.remove(i, arrayEchoList);
					jsonEcho.remove(PLAY_LIST);
					jsonEcho.put(PLAY_LIST, arrayTemp);
				}
			}
			writeFileJson(jsonEcho);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean deleteDirectory(File file) {
		if (file.exists()) {
			File[] temp = file.listFiles();
			if (temp == null) {
				return true;
			}
			for (int i = 0; i < temp.length; i++)
				if (temp[i].isDirectory()) {
					deleteDirectory(temp[i]);
				} else
					temp[i].delete();
		}
		return file.delete();
	}

	public void writeFileJson(JSONObject dataEcho) {
		try {
			String url = URL_FILE_ECHO_LIST + URL_FILE_JSON + URL_FILE_DATA;
			FileWriter writeFileJson = new FileWriter(url);
			writeFileJson.write(dataEcho.toString());
			writeFileJson.flush();
			writeFileJson.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}