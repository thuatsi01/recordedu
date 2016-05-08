package com.bkapp.recordedu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bkapp.recordedu.adapter.CustomDropListEcho;
import com.bkapp.recordedu.io.ManagerJSONData;

public class MakeEchoActivity extends Activity {

    static {
        try {
            System.loadLibrary("mp3lame");
        } catch (UnsatisfiedLinkError e) {
            System.loadLibrary("libmp3lame");
        }
    }

    private final String STORAGE_PATH = "/data/data/";
    private final String PACKAGE_NAME = "com.tranle.echoedu";
    private final String RECORD_NAME_TEMP = "/recorder.mp3";
    private final String ECHOLIST_FOLDER = "/EchoList/";
    private final String PLAY_LIST = "playlists";
    private final String TITLE = "title";

    private final String DATA_FILE = "/dataEcho.txt";
    private final String EXTERNAL_STORAGE = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath();

    private ImageButton mIBMakeEcho, mIBAdd, mIBRecord, mIAddFile, mIBCancle;
    private TextView mTVRecordTimer, mTVNoEcho;
    private Spinner mISListItem;
    private EditText mIEFileName;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listEchoList;
    private File fileManager;
    private MediaPlayer mediaRecordPLayer;
    private OutputStream fileRecordTemp;
    private int echoRecordLimit = 0, sampleRate = 8000, minBufferSize;
    private String mCurEchoName, mCurEchoListName, mEchoName;
    boolean isTextClockRunning, isEchoRecording;
    private AudioRecord echoRecorder;
    private long countTimer;
    private Handler handlerClock;
    private short[] buffer;
    private byte[] mp3Buffer;
    private FileWriter writeFileJson;
    private String getDataEcho = "";
    private JSONObject dataEcho = null;
    private ManagerJSONData managerJSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeecho);
        handlerClock = new Handler();
        try {
            File folderJSON = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER + "JSON/");//
            File fileJSON = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER + "JSON/" + DATA_FILE);
            if (!folderJSON.exists()) {// update
                folderJSON.mkdir();// update
                fileJSON.createNewFile();
            } else {
                if (!fileJSON.exists()) {
                    fileJSON.createNewFile();
                    dataEcho = new JSONObject();
                } else {
                    getDataEcho = getIntent().getStringExtra("dataEcho");
                    dataEcho = new JSONObject(getDataEcho);
                }
            }
            new File(STORAGE_PATH + PACKAGE_NAME + RECORD_NAME_TEMP).createNewFile();
        } catch (Exception e) {
            Log.e("EchoEdu", "Error Create Data In MakeEcho");
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fileManager = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER);
        if (!fileManager.exists())
            fileManager.mkdir();
        mCurEchoListName = "";

        mISListItem = (Spinner) findViewById(R.id.list_item);
        mIBAdd = (ImageButton) findViewById(R.id.add_folder);
        mIBRecord = (ImageButton) findViewById(R.id.manager_record);
        mIAddFile = (ImageButton) findViewById(R.id.add_record);
        mIBMakeEcho = (ImageButton) findViewById(R.id.make_makeecho);
        mIBCancle = (ImageButton) findViewById(R.id.cancle_record);
        mTVRecordTimer = (TextView) findViewById(R.id.time_record);
        mTVNoEcho = (TextView) findViewById(R.id.no_echolist);
        mIEFileName = (EditText) findViewById(R.id.text_inputfile);

        LinearLayout mContentView = (LinearLayout) findViewById(R.id.make_echo);
        mContentView.setBackground(makeScaleDrawable());

        // Set adapter for droplist show list EchoList
        listEchoList = new ArrayList<String>();
        adapter = new CustomDropListEcho(this, android.R.layout.simple_spinner_dropdown_item, listEchoList);
        mISListItem.setAdapter(adapter);

        searchFolderRecord();
        setDefault();

        mIBRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                actionRecordButton(view);
            }
        });

        mIBAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionNewFileButton(v);
            }
        });

        mIAddFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionAddRecord(v);
            }
        });

        mIBCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionCancle(v);
            }
        });

        mTVNoEcho.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listEchoList.size() != 0) {
                    adapter.notifyDataSetChanged();
                    mISListItem.bringToFront();
                    mISListItem.performClick();
                }
            }
        });

        mISListItem.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                adapter.notifyDataSetChanged();
                if (listEchoList.size() == 0
                        && event.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            MakeEchoActivity.this);
                    alert.setTitle(R.string.no_echolist);
                    alert.setMessage(R.string.message_no_echolist);
                    alert.setPositiveButton(R.string.alertview_ok_button_title,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog_alert = alert.create();
                    dialog_alert.show();
                    return true;
                }
                return false;
            }
        });

        mISListItem.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long status) {
                mCurEchoListName = listEchoList.get(position);
                mISListItem.bringToFront();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {

            }
        });

        mIEFileName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSeq, int start, int end,
                                      int count) {
                mEchoName = charSeq.length() == 0 ? "" : charSeq.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence charSeq, int start,
                                          int end, int count) {
            }

            @Override
            public void afterTextChanged(Editable edit) {

            }
        });

        // Navigate to EchoNow Screen
        mIBMakeEcho.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MakeEchoActivity.this, EchoNowActivity.class);
                String sendData = "{}";
                if (dataEcho != null)
                    sendData = dataEcho.toString();
                i.putExtra("dataEcho", sendData);
                startActivity(i);
                overridePendingTransition(R.animator.right_in, R.animator.left_out);
                finish();
            }
        });
    }

    // Search Item Record
    private void searchFolderRecord() {
        if (!getDataEcho.equals("") && !getDataEcho.equals("{}")) {
            try {
                JSONArray arrayEchoList = dataEcho.getJSONArray(PLAY_LIST);
                for (int i = 0; i < arrayEchoList.length(); i++) {
                    JSONObject objectEchoList = arrayEchoList.getJSONObject(i);
                    int positionAddEchoList = listEchoList.size();
                    listEchoList.add(positionAddEchoList, objectEchoList.getString(TITLE));
                }
            } catch (Exception e) {
                Log.e("EchoEdu", "Parse Data Error");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getPackageName() + ".dataEcho");
            Bundle bundle = new Bundle();
            if (dataEcho != null)
                getDataEcho = dataEcho.toString();
            bundle.putString("dataEcho", getDataEcho);
            i.putExtras(bundle);
            sendBroadcast(i);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Runnable Clock = new Runnable() {

        @Override
        public void run() {
            int timeclock = (int) ((SystemClock.uptimeMillis() - countTimer) / 10);
            boolean checktime = timeclock % 100 < 10 ? true : false;
            if (!isEchoRecording && !isTextClockRunning && timeclock >= echoRecordLimit) {
                mediaRecordPLayer.stop();
                handlerClock.removeCallbacks(Clock);
                mIBRecord.setImageResource(R.drawable.makeecho_play_but);
                isTextClockRunning = !isTextClockRunning;
                setTextClock(checktime, echoRecordLimit);
                setEnableImageButton(true);
            } else {
                if (isEchoRecording) {
                    echoRecordLimit = timeclock;
                }
                setTextClock(checktime, timeclock);
                handlerClock.postDelayed(this, 0);
            }
        }
    };

    public Drawable makeScaleDrawable() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        BitmapDrawable BitmapScale = (BitmapDrawable) getResources().getDrawable(R.drawable.screen_bg);
        Bitmap temp = Bitmap.createScaledBitmap(BitmapScale.getBitmap(), displaymetrics.widthPixels, (int) (displaymetrics.heightPixels * 0.9), false);
        return new BitmapDrawable(getResources(), temp);
    }

    public void actionNewFileButton(View v) {
        Context context = MakeEchoActivity.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_new_echolist);
        builder.setMessage(R.string.message_new_echolist);
        EditText mETInputEchoName = new EditText(context);
        builder.setView(mETInputEchoName);

        mETInputEchoName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSeq, int start, int before, int count) {
                mCurEchoName = charSeq.length() == 0 ? "" : charSeq.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                File addFileManager = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER + mCurEchoName);
                if (!addFileManager.mkdir() || mCurEchoName.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MakeEchoActivity.this);
                    alert.setMessage(R.string.message_exist_echoname);
                    alert.setPositiveButton("OK", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog_alert = alert.create();
                    dialog_alert.show();
                } else {
                    try {
                        if (dataEcho != null) managerJSONData.setPlayList(mCurEchoName);
                        else dataEcho = managerJSONData.setPlayListNull(mCurEchoName);

                        writeFileJson(EXTERNAL_STORAGE + ECHOLIST_FOLDER + "JSON/" + DATA_FILE);
                        listEchoList.add(mCurEchoName);
                        adapter.notifyDataSetChanged();
                        mCurEchoName = "";
                    } catch (Exception e) {
                        Log.e("EchoEdu", "Failed Put listEcho");
                    }
                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton("Cancel", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertAddEcho = builder.create();
        alertAddEcho.show();
    }

    public void actionRecordButton(View v) {
        if (mCurEchoListName.equals("")) {
            showMessage(R.string.message_no_echolist, null);
        } else {
            setEnableImageButton(!isTextClockRunning);
            if (isTextClockRunning) {
                if (!isEchoRecording) {
                    try {
                        mediaRecordPLayer = new MediaPlayer();
                        mediaRecordPLayer.reset();
                        mediaRecordPLayer.setDataSource(STORAGE_PATH + PACKAGE_NAME + RECORD_NAME_TEMP);
                        mediaRecordPLayer.prepare();
                        mediaRecordPLayer.start();
                    } catch (Exception e) {
                        Log.e("EchoEdu", "Error: Can't play recorded echo");
                    }
                } else new RecordDelay().execute();

                mIBRecord.setImageResource(R.drawable.makeecho_stop_but);
                countTimer = SystemClock.uptimeMillis();
                handlerClock.postDelayed(Clock, 0);
            } else {
                if (isEchoRecording) {
                    isEchoRecording = !isEchoRecording;
                    Intent i = new Intent(MakeEchoActivity.this, RecordDelay.class);
                    i.putExtra("isFinish", isEchoRecording);
                } else
                    mediaRecordPLayer.stop();
                handlerClock.removeCallbacks(Clock);
                mIBRecord.setImageResource(R.drawable.makeecho_play_but);
            }
            isTextClockRunning = !isTextClockRunning;
        }
    }

    public void actionCancle(View v) {
        writeFileJson(EXTERNAL_STORAGE + ECHOLIST_FOLDER + "JSON/" + DATA_FILE);
        setDefault();
    }

    public void setDefault() {
        // Init record variable
        echoRecordLimit = 0;
        isEchoRecording = true;
        isTextClockRunning = true;
        mCurEchoName = "";
        mEchoName = "";
        buffer = new short[sampleRate * (16 / 8) * 1 * 5];
        mp3Buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];
        managerJSONData = new ManagerJSONData(dataEcho);
        minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // Config record infomation
        mIBRecord.setImageResource(R.drawable.makeecho_record_but);
        mTVRecordTimer.setText("00:00");
        mIEFileName.setText("");
    }

    public void actionAddRecord(View v) {
        try {
            if (!isEchoRecording && !mCurEchoListName.equals("") && !mEchoName.equals("")) {
                File fileEcho = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER + mCurEchoListName + "/" + mEchoName + ".mp3");
                if (fileEcho.exists()) {
                    showMessage(R.string.message_exist_echoname, null);
                } else {
                    managerJSONData.addEcho(5, fileEcho.getAbsolutePath(), mEchoName, mCurEchoListName);
                    fileEcho.createNewFile();
                    new CopyFile().execute(fileEcho.getAbsolutePath());
                }
            } else if (mCurEchoListName.equals("") || mEchoName.equals("")) {
                // Show message if recorded file name not filled
                showMessage(R.string.message_echoname_null, null);
            } else {
                // Show message if not existed recorded file to add
                showMessage(R.string.message_notexisted_record, null);
            }
        } catch (Exception e) {
            Log.e("EchoEdu", "Error: Can't convert recorded echo");
        }
    }

    private class RecordDelay extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SimpleLame.init(sampleRate, 1, sampleRate, 32);
            echoRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);
            try {
                fileRecordTemp = new FileOutputStream(new File(STORAGE_PATH + PACKAGE_NAME + RECORD_NAME_TEMP));
                echoRecorder.startRecording();
                int flushResult = 0;
                while (isEchoRecording) {
                    int readSize = echoRecorder.read(buffer, 0, minBufferSize); // 4
                    if (readSize >= 0) {
                        int encResult = SimpleLame.encode(buffer, buffer, readSize, mp3Buffer); // 1ms
                        if (encResult >= 0)
                            fileRecordTemp.write(mp3Buffer, 0, encResult); // 0ms
                        else {
                            flushResult = SimpleLame.flush(mp3Buffer);
                            if (flushResult != 0)
                                fileRecordTemp.write(mp3Buffer, 0, flushResult);
                        }
                    } else {
                        flushResult = SimpleLame.flush(mp3Buffer);
                        if (flushResult != 0)
                            SimpleLame.close();
                    }
                    Log.i("EchoEdu", Integer.toString((int) ((SystemClock
                            .uptimeMillis() - countTimer) / 10)));
                }
                echoRecorder.stop();
                SimpleLame.close();
                fileRecordTemp.close();

            } catch (Exception e) {
                SimpleLame.close();
                Log.i("EchoEdu",
                        "Have error while converting mp3: " + e.getMessage());
            }
            return null;
        }
    }

    private class CopyFile extends AsyncTask<String, Void, Void> {

        private InputStream in = null;
        private OutputStream out = null;
        private ProgressDialog mPDLoading;
        private int lengthByteCopy = 0;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mPDLoading.dismiss();
            MakeEchoActivity.this.setDefault();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPDLoading = new ProgressDialog(MakeEchoActivity.this);
            mPDLoading.setTitle(R.string.save_file);
            mPDLoading.setMessage("Wait For Minute");
            mPDLoading.setCancelable(false);
            mPDLoading.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            final byte[] temp = new byte[1024];
            try {
                in = new FileInputStream(STORAGE_PATH + PACKAGE_NAME + RECORD_NAME_TEMP);
                out = new FileOutputStream(params[0]);
                writeFileJson(EXTERNAL_STORAGE + ECHOLIST_FOLDER + "JSON/" + DATA_FILE);
                while ((lengthByteCopy = in.read(temp)) > 0)
                    out.write(temp, 0, lengthByteCopy);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void setTextClock(boolean checkLock, int time) {
        mTVRecordTimer.setText(checkLock ? Integer.toString((time / 100) % 60)
                + ":0" + Integer.toString(time % 100) : Integer
                .toString((time / 100) % 60)
                + ":"
                + Integer.toString(time % 100));
    }

    public void setEnableImageButton(boolean isEnable) {
        mIBCancle.setEnabled(isEnable);
        mIAddFile.setEnabled(isEnable);
        mIBAdd.setEnabled(isEnable);
        mIBMakeEcho.setEnabled(isEnable);
    }

    public void writeFileJson(String url) {
        try {
            dataEcho = managerJSONData.dataEcho;
            writeFileJson = new FileWriter(url);
            writeFileJson.write(dataEcho.toString());
            writeFileJson.flush();
            writeFileJson.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(int messStrId, String messStr) {
        // If messStrId = 0 and messStr # null show messStr
        // Else show string by messStrId
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MakeEchoActivity.this);
        if (messStrId == 0 && !TextUtils.isEmpty(messStr))
            builder.setMessage(messStr);
        else
            builder.setMessage(messStrId);
        builder.setNegativeButton(R.string.alertview_ok_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
