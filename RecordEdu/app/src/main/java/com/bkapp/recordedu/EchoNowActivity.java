package com.bkapp.recordedu;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bkapp.recordedu.adapter.CustomListEcho;
import com.bkapp.recordedu.adapter.DragNDropSimpleAdapter;
import com.bkapp.recordedu.io.DataEcho;
import com.bkapp.recordedu.io.ManagerJSONData;
import com.bkapp.recordedu.view.CustomListView;

public class EchoNowActivity extends Activity implements CustomListEcho.OnClickNextButton {

    private final String DATA_FILE = "/dataEcho.txt";
    private final String URL_FILE_JSON = "/JSON/";
    private final String ECHOLIST_FOLDER = "/EchoList/";
    private final String EXTERNAL_STORAGE = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

    private CustomListView mLVEchoList;
    private ImageButton mIBPlay, mIBNext, mIBBack, mIBRename, mIBDone,
            mIBDelete, mIBBackEchoList, mIBMakeEcho, mIBRateShuffer,
            mIBShuffer, mIBEdit;
    private TextView ratingOne, ratingTwo, ratingThree, ratingFour, ratingFive;

    private JSONObject dataEcho = null;
    private MediaPlayer mediaRecordPLayer;
    private CustomListEcho adapter;
    private DragNDropSimpleAdapter adapterEdit;
    private DataEcho parseDataEcho;
    private String getDataEcho = "", renameEcho = "";
    private int seekMediaPlayer = 0, positionEchoPlay = -1, positionEchoBegin,
            positionEchoEnd, positionItemChoose = -1, positionPlayShuffer = -1;
    private boolean isPlaying = false, isStop = true, isEdit = false,
            isFound = true, isRateShuffer = false, isShuffer = false;
    private ManagerJSONData managerJSONData;
    private Resources resources;
    private ArrayList<Map<String, Object>> itemEditEcho;
    private FileWriter writeFileJson;
    private ArrayList<String> elementEchoList;
    private ArrayList<Integer> positionEcho;
    private ProgressDialog mPDWating;

    // action good
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echonow);

        resources = getResources();
        try {
            getDataEcho = getIntent().getStringExtra("dataEcho");
            if (!getDataEcho.equals("") && !getDataEcho.equals("{}"))
                dataEcho = new JSONObject(getDataEcho);
            parseDataEcho = new DataEcho(dataEcho);
            managerJSONData = new ManagerJSONData(dataEcho);
            positionEchoBegin = 0;
            positionEchoEnd = parseDataEcho.nameEcho.size();
        } catch (Exception e) {
            Log.e("EchoEdu", "Error Load Data Of JSON In EchoNow");
        }

        mIBNext = (ImageButton) findViewById(R.id.echo_next);
        mIBPlay = (ImageButton) findViewById(R.id.echo_play_pause);
        mIBBack = (ImageButton) findViewById(R.id.echo_back);
        mIBRename = (ImageButton) findViewById(R.id.edit_rename);
        mIBDelete = (ImageButton) findViewById(R.id.edit_delete);
        mIBDone = (ImageButton) findViewById(R.id.edit_done);
        mIBBackEchoList = (ImageButton) findViewById(R.id.back_button);
        mIBMakeEcho = (ImageButton) findViewById(R.id.echo_echonow);
        mIBRateShuffer = (ImageButton) findViewById(R.id.rate_shuffer);
        mIBShuffer = (ImageButton) findViewById(R.id.shuffer);
        mIBEdit = (ImageButton) findViewById(R.id.edit_echo);
        mLVEchoList = (CustomListView) findViewById(R.id.list_echo);
        ratingOne = (TextView) findViewById(R.id.rate_1);
        ratingTwo = (TextView) findViewById(R.id.rate_2);
        ratingThree = (TextView) findViewById(R.id.rate_3);
        ratingFour = (TextView) findViewById(R.id.rate_4);
        ratingFive = (TextView) findViewById(R.id.rate_5);

        mIBDelete.setVisibility(View.INVISIBLE);
        mIBDone.setVisibility(View.INVISIBLE);
        mIBRename.setVisibility(View.INVISIBLE);
        mIBBackEchoList.setVisibility(View.INVISIBLE);

        elementEchoList = new ArrayList<String>();

        adapter = new CustomListEcho(EchoNowActivity.this,
                android.R.layout.simple_list_item_1, parseDataEcho.listEcho,
                true);
        mLVEchoList.setAdapter(adapter);
        mLVEchoList.setItemsCanFocus(false);
        mLVEchoList.setFocusableInTouchMode(false);

        positionEcho = new ArrayList<Integer>();
        ratingOne.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionRating(ratingOne.getId());
            }
        });

        ratingTwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionRating(ratingTwo.getId());
            }
        });

        ratingThree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionRating(ratingThree.getId());
            }
        });

        ratingFour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionRating(ratingFour.getId());
            }
        });

        ratingFive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionRating(ratingFive.getId());
            }
        });

        mIBMakeEcho.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(EchoNowActivity.this,
                        MakeEchoActivity.class);
                isStop = true;
                isPlaying = false;
                String data = "";
                if (dataEcho != null) {
                    data = dataEcho.toString();
                }
                i.putExtra("dataEcho", data);
                startActivity(i);
                overridePendingTransition(R.animator.right_in,
                        R.animator.left_out);
                finish();
            }
        });

        mIBBackEchoList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isEdit) {
                    isEdit();
                    return;
                }
                if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                    callBackListEcho(v);
                }
            }
        });

        mIBPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionPlayPause(v);
            }
        });

        mIBNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionNextMedia(v);
            }
        });

        mIBBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionBackMedia(v);
            }
        });

        mIBRename.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionRename();
            }
        });

        mIBDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DoneEditEcho().execute();
            }
        });

        mIBDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionDelete();
            }
        });

        mIBEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionEdit();
            }
        });

        mLVEchoList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                actionClickEchoItem(parent, view, position, id);
            }
        });

        mLVEchoList.setOnItemDragNDropListener(new CustomListView.OnItemDragNDropListener() {

            @Override
            public void onItemDrop(CustomListView parent, View view,
                                   int startPosition, int endPosition, long id) {
                if (mIBBackEchoList.getVisibility() == View.INVISIBLE)
                    dataEcho = managerJSONData.echoDragDrop(startPosition,
                            endPosition);
                else {
                    int count = 0;
                    for (; count < parseDataEcho.listEcho.size(); count++) {
                        if (parseDataEcho.positionEndEcho.get(count) > positionEchoPlay) {
                            break;
                        }
                    }
                    Log.i("EchoEdu", dataEcho.toString());
                    dataEcho = managerJSONData.echoDragDrop(startPosition,
                            endPosition, parseDataEcho.listEcho.get(count));
                    Log.i("EchoEdu", dataEcho.toString());
                }
                if (startPosition < positionItemChoose
                        && positionItemChoose < endPosition)
                    positionItemChoose -= 1;
                else if (endPosition < positionItemChoose
                        && positionItemChoose < startPosition)
                    positionItemChoose += 1;
                else if (positionItemChoose == startPosition)
                    positionItemChoose = endPosition;
                else if (positionItemChoose == endPosition)
                    positionItemChoose = startPosition;
            }

            @Override
            public void onItemDrag(CustomListView parent, View view,
                                   int position, long id) {

            }
        });

        mIBRateShuffer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isRateShuffer)
                    actionRateShuffer();
                else {
                    isRateShuffer = false;
                    mIBRateShuffer
                            .setImageResource(R.drawable.audio_rateshuffle_but);
                    unSelectShuffer();
                }
            }
        });

        mIBShuffer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isShuffer)
                    actionShuffer();
                else {
                    isShuffer = false;
                    mIBShuffer
                            .setImageResource(R.drawable.audio_mediashuffle_but);
                    unSelectShuffer();
                }
            }
        });

    }

    // action good
    public class PlayRecord extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                isStop = false;
                isPlaying = true;
                mediaRecordPLayer = new MediaPlayer();
                positionEchoPlay = positionEchoPlay > positionEchoBegin - 1 ? positionEchoPlay
                        : positionEchoBegin - 1;

            } catch (Exception e) {
                Log.e("EchoEdu", "Get Data Failed");
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isPlaying = false;
            mediaRecordPLayer.stop();
            mediaRecordPLayer.release();

        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isStop) {
                if (isPlaying) {
                    if (!mediaRecordPLayer.isPlaying()) {
                        checkFilePlay();
                    }
                }
            }
            return null;
        }
    }

    // action good
    private void actionNextMedia(View v) {
        if (isEdit) {
            isEdit();
            return;
        } else if (positionItemChoose < 0) {
            showMessage(R.string.chose_echo);
            return;
        }
        isPlaying = false;
        mediaRecordPLayer.pause();
        checkFilePlay();
    }

    // action good
    private void actionPlayPause(View v) {
        if (isEdit) {
            isEdit();
            return;
        }
        if (mIBDone.getVisibility() != View.VISIBLE) {
            if (!isFound) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        EchoNowActivity.this);
                builder.setTitle(R.string.no_echo);
                builder.setNegativeButton(R.string.alertview_ok_button_title,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            if (!isStop) {
                if (!isPlaying) {
                    isPlaying = true;
                    mediaRecordPLayer.seekTo(seekMediaPlayer);
                    mediaRecordPLayer.start();
                    mIBPlay.setImageResource(R.drawable.audio_pause_but);
                } else {
                    isPlaying = false;
                    mediaRecordPLayer.pause();
                    seekMediaPlayer = mediaRecordPLayer.getCurrentPosition();
                    mIBPlay.setImageResource(R.drawable.audio_play_but);
                }
            } else if (positionItemChoose == -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        EchoNowActivity.this);
                if (mIBBackEchoList.getVisibility() == View.INVISIBLE)
                    builder.setTitle(R.string.chose_play_list);
                else
                    builder.setTitle(R.string.chose_echo);
                builder.setNegativeButton(R.string.alertview_ok_button_title,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                new PlayRecord().execute();
            }
        }
    }

    // action good
    private void actionBackMedia(View v) {
        if (isEdit) {
            isEdit();
            return;
        } else if (positionItemChoose < 0) {
            showMessage(R.string.chose_echo);
            return;
        }
        isPlaying = false;
        mediaRecordPLayer.pause();
        if (!isRateShuffer && !isShuffer) {
            if (positionEchoPlay != positionEchoBegin) {
                positionEchoPlay = positionEchoPlay - 2;
            } else {
                positionEchoPlay = positionEchoEnd - 2;

            }
        } else {
            if (positionPlayShuffer != -1)
                positionPlayShuffer = positionPlayShuffer - 2;
        }
        checkFilePlay();
    }

    private void actionClickEchoItem(AdapterView<?> parent, View view,
                                     int position, long id) {
        setDefaultColor();
        getIsFound(position);
        if (isFound && isPlaying) {
            isPlaying = false;
            mediaRecordPLayer.pause();
        }
        if (!isEdit) {
            positionItemChoose = position;
            if (isPlaying && !isStop) {
                isPlaying = false;
                mediaRecordPLayer.pause();
                mIBPlay.setImageResource(R.drawable.audio_play_but);
            }
            if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                if (!isRateShuffer && !isShuffer)
                    positionEchoPlay = position + positionEchoBegin - 1;
                else {
                    positionPlayShuffer = position - 1;
                }
            } else {
                if (isFound) {
                    if (position == 0)
                        positionEchoBegin = 0;
                    else
                        positionEchoBegin = parseDataEcho.positionEndEcho
                                .get(position - 1);
                    positionEchoEnd = parseDataEcho.positionEndEcho
                            .get(position);
                    if (isRateShuffer) {
                        isRateShuffer = false;
                        actionRateShuffer();
                    } else if (isShuffer) {
                        isShuffer = false;
                        actionShuffer();
                    }
                    Toast.makeText(
                            EchoNowActivity.this,
                            Integer.toString(positionEchoBegin) + " "
                                    + Integer.toString(positionEchoEnd),
                            Toast.LENGTH_LONG).show();
                } else {
                    adapter.selectItem(position);
                    adapter.notifyDataSetChanged();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            EchoNowActivity.this);
                    builder.setTitle(R.string.no_echo);
                    builder.setNegativeButton(
                            R.string.alertview_ok_button_title,
                            new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
            }
            adapter.selectItem(position);
            adapter.notifyDataSetChanged();
            if (isStop && isFound) {
                new PlayRecord().execute();
            } else if (isFound) {
                checkFilePlay();
            }
        }
    }

    // action good
    private void callBackListEcho(View v) {
        if (isEdit) {
            isEdit();
            return;
        }
        adapter = new CustomListEcho(EchoNowActivity.this,
                android.R.layout.simple_list_item_1, parseDataEcho.listEcho,
                true);
        mLVEchoList.setAdapter(adapter);
        mIBBackEchoList.setVisibility(View.INVISIBLE);
        if (positionEchoPlay == -1) {
            adapter.selectItem(-1);
            return;
        }
        Toast.makeText(EchoNowActivity.this, "ABC", Toast.LENGTH_LONG).show();
        if (isFound) {
            int count = 0;
            if (mIBBackEchoList.getVisibility() == View.INVISIBLE) {
                mIBBackEchoList.setVisibility(View.INVISIBLE);
                for (; count < parseDataEcho.listEcho.size(); count++) {
                    if (parseDataEcho.positionEndEcho.get(count) > positionEchoPlay) {
                        break;
                    }
                }
                positionItemChoose = count;
                adapter.selectItem(count);
            }
        } else {
            adapter.selectItem(positionItemChoose);
        }
        adapter.notifyDataSetChanged();
    }

    private void actionRating(int id) {
        if (!isStop) {
            setPositionPlay();
            int rating = 5, count = 0;
            switch (id) {
                case R.id.rate_1:
                    rating = 1;
                    break;
                case R.id.rate_2:
                    rating = 2;
                    break;
                case R.id.rate_3:
                    rating = 3;
                    break;
                case R.id.rate_4:
                    rating = 4;
                    break;
                case R.id.rate_5:
                    rating = 5;
                    break;
            }
            parseDataEcho.ratingEcho.set(positionEchoPlay, rating);
            for (count = 0; count < parseDataEcho.listEcho.size(); count++) {
                if (parseDataEcho.positionEndEcho.get(count) > positionEchoPlay)
                    break;
            }
            dataEcho = managerJSONData.changeDataEcho(rating,
                    parseDataEcho.nameEcho.get(positionEchoPlay),
                    parseDataEcho.listEcho.get(count));
            showRatingEcho(id);
            if (isRateShuffer)
                if (isPlaying) {
                    isPlaying = false;
                    mediaRecordPLayer.pause();
                    sortRating(!isPlaying);
                }
            writeFileJson(EXTERNAL_STORAGE + ECHOLIST_FOLDER + URL_FILE_JSON
                    + DATA_FILE);
        }
    }

    // action good
    private void setDefaultColor() {
        ratingOne.setTextColor(resources.getColor(R.color.red_time));
        ratingTwo.setTextColor(resources.getColor(R.color.red_time));
        ratingThree.setTextColor(resources.getColor(R.color.red_time));
        ratingFour.setTextColor(resources.getColor(R.color.red_time));
        ratingFive.setTextColor(resources.getColor(R.color.red_time));
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

    public void showRatingEcho(int id) {
        setDefaultColor();
        TextView mTVRating = (TextView) findViewById(id);
        mTVRating.setTextColor(resources.getColor(R.color.rate_color));
    }

    @Override
    public void onNextListEcho(int position) {
        int positionHighlight = 0;
        if (isFound)
            if (!isRateShuffer && !isShuffer) {
                elementEchoList.clear();
                if (isFound)
                    for (int i = positionEchoBegin; i < positionEchoEnd; i++) {
                        if (i == positionEchoPlay)
                            positionHighlight = elementEchoList.size();
                        elementEchoList.add(elementEchoList.size(),
                                parseDataEcho.nameEcho.get(i));
                    }
            } else if (isFound) {
                for (int i = 0; i < positionEcho.size(); i++) {
                    if (positionEchoPlay == positionEcho.get(i)) {
                        positionHighlight = i;
                        break;
                    }
                }
            }
        positionItemChoose = !isFound ? positionItemChoose : positionHighlight;
        adapter = new CustomListEcho(EchoNowActivity.this,
                android.R.layout.simple_list_item_1, elementEchoList, false);
        mLVEchoList.setAdapter(adapter);
        adapter.selectItem(positionHighlight);
        adapter.notifyDataSetChanged();
        mLVEchoList.setSelection(positionHighlight);
        mIBBackEchoList.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isStop = true;
            writeFileJson(EXTERNAL_STORAGE + ECHOLIST_FOLDER + URL_FILE_JSON
                    + DATA_FILE);
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

    private void setPositionPlay() {
        if (positionEchoPlay >= positionEchoEnd
                || positionEchoPlay < positionEchoBegin)
            positionEchoPlay = positionEchoBegin;
    }

    public int actionEdit() {
        if (positionItemChoose == -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    EchoNowActivity.this);
            builder.setTitle(R.string.chose_edit);
            builder.setNegativeButton(R.string.alertview_ok_button_title,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialoAlertDialog = builder.create();
            dialoAlertDialog.show();
            return 0;
        } else if (!isFound && mIBBackEchoList.getVisibility() == View.VISIBLE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    EchoNowActivity.this);
            builder.setTitle(R.string.no_echo);
            builder.setNegativeButton(R.string.alertview_ok_button_title,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialoAlertDialog = builder.create();
            dialoAlertDialog.show();
            return 0;
        }
        isEdit = true;
        if (mIBBackEchoList.getVisibility() == View.VISIBLE)
            if (isRateShuffer || isShuffer)
                positionItemChoose = positionEchoPlay - positionEchoBegin;
        if (isPlaying) {
            isPlaying = false;
            mediaRecordPLayer.pause();
        }
        isStop = true;
        mIBRename.setVisibility(View.VISIBLE);
        mIBDelete.setVisibility(View.VISIBLE);
        mIBDone.setVisibility(View.VISIBLE);
        mIBEdit.setVisibility(View.INVISIBLE);
        mIBPlay.setImageResource(R.drawable.audio_play_but);
        setAdapterEdit();
        mLVEchoList.setDragNDropAdapter(adapterEdit);
        Toast.makeText(EchoNowActivity.this,
                Integer.toString(positionItemChoose), Toast.LENGTH_LONG).show();
        mLVEchoList.setSelection(positionItemChoose);
        adapterEdit.selectItem(positionItemChoose);
        return 0;
    }

    // work failed
    class DoneEditEcho extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mPDLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isStop = true;
            mPDLoading = new ProgressDialog(EchoNowActivity.this);
            mPDLoading.setTitle(R.string.save_file);
            mPDLoading.setMessage("Wait For Minute");
            mPDLoading.setCancelable(false);
            mPDLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            writeFileJson(EXTERNAL_STORAGE + ECHOLIST_FOLDER + URL_FILE_JSON
                    + DATA_FILE);
            parseDataEcho.swapEcho(dataEcho);
            if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                if (!isRateShuffer && !isShuffer) {
                    elementEchoList.clear();
                    for (int i = positionEchoBegin; i < positionEchoEnd; i++) {
                        int size = elementEchoList.size();
                        elementEchoList
                                .add(size, parseDataEcho.nameEcho.get(i));
                    }
                } else if (isRateShuffer) {
                    elementEchoList.clear();
                    positionEcho.clear();
                    int rating = 5;
                    while (rating > 0) {
                        for (int i = positionEchoBegin; i < positionEchoEnd; i++)
                            if (parseDataEcho.ratingEcho.get(i) == rating) {
                                int size = elementEchoList.size();
                                positionEcho.add(size, i);
                                elementEchoList.add(size,
                                        parseDataEcho.nameEcho.get(i));
                            }
                        rating--;
                    }
                } else {
                    int sizeOfListEcho = positionEchoEnd - positionEchoBegin;
                    int size = positionEcho.size();
                    if (size != sizeOfListEcho) {
                        for (int i = 0; i < size; i++) {
                            boolean isFound = true;
                            for (int j = positionEchoBegin; j < positionEchoEnd; j++) {
                                if (elementEchoList.get(i).equals(
                                        parseDataEcho.nameEcho.get(j))) {
                                    positionEcho.set(i, j);
                                    isFound = false;
                                    break;
                                }
                            }
                            if (isFound) {
                                elementEchoList.remove(i);
                                positionEcho.remove(i);
                            }
                        }
                    } else {
                        int positionRenameEcho = 0, positionRename = 0;
                        boolean isRename = false;
                        for (int i = 0; i < size; i++) {
                            boolean isCheckRename = true;
                            for (int j = positionEchoBegin; j < positionEchoEnd; j++) {
                                if (elementEchoList.get(i).equals(
                                        parseDataEcho.nameEcho.get(j))) {
                                    Log.e("EchoEdu", Integer.toString(i));
                                    positionEcho.set(i, j);
                                    isCheckRename = false;
                                    break;
                                }
                            }
                            if (isCheckRename) {
                                positionRename = i;
                                isRename = true;
                            }
                        }
                        if (isRename) {
                            for (int i = positionEchoBegin; i < positionEchoEnd; i++) {
                                boolean isCheckRename = true;
                                for (int j = 0; j < size; j++) {
                                    if (parseDataEcho.nameEcho.get(i).equals(
                                            elementEchoList.get(j))) {
                                        isCheckRename = false;
                                        break;
                                    }
                                }
                                if (isCheckRename) {
                                    positionRenameEcho = i;
                                    break;
                                }
                            }
                            elementEchoList.set(positionRename,
                                    parseDataEcho.nameEcho
                                            .get(positionRenameEcho));
                            positionEcho
                                    .set(positionRename, positionRenameEcho);
                        }
                    }
                }
            }
            adapter.selectItem(positionItemChoose);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mIBDelete.setVisibility(View.INVISIBLE);
                    mIBDone.setVisibility(View.INVISIBLE);
                    mIBRename.setVisibility(View.INVISIBLE);
                    mLVEchoList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    mIBEdit.setVisibility(View.VISIBLE);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mPDLoading.dismiss();
            isEdit = false;
            setDefaultColor();
        }
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

    public void isEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                EchoNowActivity.this);
        builder.setTitle(R.string.is_edit);
        builder.setNegativeButton(R.string.alertview_ok_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialogIsEdit = builder.create();
        dialogIsEdit.show();
    }

    public void actionRename() {
        if (positionItemChoose == -1) {
            showMessage(R.string.no_rename);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(
                EchoNowActivity.this);
        String message = "Rename File ";
        renameEcho = "";
        if (mIBBackEchoList.getVisibility() == View.INVISIBLE) {
            message += parseDataEcho.listEcho.get(positionItemChoose);
        } else {
            message += parseDataEcho.nameEcho.get(positionEchoPlay) + ".mp3";
        }
        EditText editName = new EditText(EchoNowActivity.this);
        editName.setHint("Please Name");
        builder.setView(editName);
        builder.setMessage(message);
        editName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence chars, int start,
                                      int before, int count) {
                renameEcho = chars.length() == 0 ? "" : chars.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        builder.setNegativeButton(R.string.alertview_ok_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!renameEcho.equals("")) {
                            if (mIBBackEchoList.getVisibility() == View.INVISIBLE) {
                                File fileOld = new File(EXTERNAL_STORAGE
                                        + ECHOLIST_FOLDER
                                        + parseDataEcho.listEcho
                                        .get(positionItemChoose));
                                File fileNew = new File(EXTERNAL_STORAGE
                                        + ECHOLIST_FOLDER + renameEcho);
                                if (fileOld.exists() && !fileNew.exists()) {
                                    if (fileOld.renameTo(fileNew)) {
                                        String name = parseDataEcho.listEcho
                                                .get(positionItemChoose);
                                        parseDataEcho.listEcho.set(
                                                positionItemChoose, renameEcho);
                                        dataEcho = managerJSONData
                                                .changeDataEcho(name,
                                                        renameEcho);
                                        adapterEdit.setString(renameEcho);
                                    }
                                } else if (fileNew.exists()) {
                                    showMessage(R.string.file_exsist);
                                }
                            } else {
                                int count = 0;
                                for (; count < parseDataEcho.urlEcho.size(); count++) {
                                    if (parseDataEcho.positionEndEcho
                                            .get(count) > positionEchoPlay)
                                        break;
                                }
                                File fileOld = new File(EXTERNAL_STORAGE
                                        + ECHOLIST_FOLDER
                                        + parseDataEcho.listEcho.get(count)
                                        + "/"
                                        + parseDataEcho.nameEcho
                                        .get(positionEchoPlay) + ".mp3");
                                File fileNew = new File(EXTERNAL_STORAGE
                                        + ECHOLIST_FOLDER
                                        + parseDataEcho.listEcho.get(count)
                                        + "/" + renameEcho + ".mp3");
                                if (fileOld.exists() && !fileNew.exists()) {
                                    if (fileOld.renameTo(fileNew)) {
                                        String name = parseDataEcho.nameEcho
                                                .get(positionEchoPlay);
                                        parseDataEcho.nameEcho.set(
                                                positionEchoPlay, renameEcho);
                                        dataEcho = managerJSONData
                                                .changeDataEcho(
                                                        name,
                                                        renameEcho,
                                                        parseDataEcho.listEcho
                                                                .get(count),
                                                        fileNew.getAbsolutePath());
                                        adapterEdit.setString(renameEcho);
                                    }
                                } else if (fileNew.exists()) {
                                    showMessage(R.string.file_exsist);
                                }
                            }
                            Log.i("EchoEdu", dataEcho.toString());
                            adapterEdit.notifyDataSetChanged();
                        } else {
                            Toast.makeText(EchoNowActivity.this, "NoName",
                                    Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(R.string.alertview_cancle_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean getIsFound(int position) {
        isFound = true;
        if (mIBBackEchoList.getVisibility() == View.INVISIBLE)
            if (parseDataEcho.listEcho.size() == 1 || position == 0) {
                if (parseDataEcho.positionEndEcho.get(0) <= 0) {
                    elementEchoList.clear();
                    positionEcho.clear();
                    isFound = false;
                }
            } else {
                if (parseDataEcho.positionEndEcho.get(position)
                        - parseDataEcho.positionEndEcho.get(position - 1) <= 0) {
                    elementEchoList.clear();
                    positionEcho.clear();
                    isFound = false;
                }
            }
        return isFound;
    }

    public void actionDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                EchoNowActivity.this);
        builder.setMessage(R.string.choose_delete);
        builder.setNegativeButton(R.string.alertview_ok_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteEcho().execute();
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(R.string.alertview_cancle_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class DeleteEcho extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mPDLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isStop = true;
            mPDLoading = new ProgressDialog(EchoNowActivity.this);
            mPDLoading.setTitle(R.string.delete_file);
            mPDLoading.setMessage("Wait For Minute");
            mPDLoading.setCancelable(false);
            mPDLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mIBBackEchoList.getVisibility() == View.INVISIBLE
                    && positionItemChoose != -1) {
                File file = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER
                        + parseDataEcho.listEcho.get(positionItemChoose));
                //
                if (file.exists()) {
                    deleteDirectory(file);
                    dataEcho = managerJSONData
                            .deleteDataEcho(parseDataEcho.listEcho
                                    .get(positionItemChoose));
                    int numberEchoDelete = positionItemChoose != 0 ? parseDataEcho.positionEndEcho
                            .get(positionItemChoose)
                            - parseDataEcho.positionEndEcho
                            .get(positionItemChoose - 1)
                            : parseDataEcho.positionEndEcho
                            .get(positionItemChoose);
                    for (int i = positionItemChoose; i < parseDataEcho.positionEndEcho
                            .size() - 1; i++) {
                        int temp = parseDataEcho.positionEndEcho.get(i);
                        parseDataEcho.positionEndEcho.set(i, temp
                                - numberEchoDelete);
                    }
                    int numberEchoList = parseDataEcho.positionEndEcho.size() - 1;
                    parseDataEcho.positionEndEcho.remove(numberEchoList);
                    parseDataEcho.listEcho.remove(positionItemChoose);
                }
            } else if (mIBBackEchoList.getVisibility() == View.VISIBLE
                    && positionEchoPlay != -1) {
                int count;
                for (count = 0; count < parseDataEcho.urlEcho.size(); count++) {
                    if (parseDataEcho.positionEndEcho.get(count) > positionEchoPlay)
                        break;
                }
                File file = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER
                        + parseDataEcho.listEcho.get(count) + "/"
                        + parseDataEcho.nameEcho.get(positionEchoPlay) + ".mp3");
                if (file.exists()) {
                    file.delete();
                    positionEchoEnd = parseDataEcho.positionEndEcho.get(count) - 1;
                    dataEcho = managerJSONData.deleteDataEcho(
                            parseDataEcho.listEcho.get(count),
                            parseDataEcho.nameEcho.get(positionEchoPlay));
                    for (int j = count; j < parseDataEcho.positionEndEcho
                            .size(); j++) {
                        int temp = parseDataEcho.positionEndEcho.get(count);
                        parseDataEcho.positionEndEcho.set(count, temp - 1);
                    }
                    parseDataEcho.nameEcho.remove(positionEchoPlay);
                    parseDataEcho.urlEcho.remove(positionEchoPlay);
                    parseDataEcho.ratingEcho.remove(positionEchoPlay);
                }
            }
            positionItemChoose = positionEchoPlay = -1;
            setAdapterEdit();
            adapterEdit.selectItem(-1);
            // Log.i("EchoEdu", dataEcho.toString());
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mLVEchoList.setDragNDropAdapter(adapterEdit);
                    adapterEdit.notifyDataSetChanged();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mPDLoading.dismiss();
        }

    }

    public void setAdapterEdit() {
        itemEditEcho = new ArrayList<Map<String, Object>>();
        if (mIBBackEchoList.getVisibility() == View.INVISIBLE) {
            for (int i = 0; i < parseDataEcho.listEcho.size(); ++i) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("name", parseDataEcho.listEcho.get(i));
                item.put("_id", i);
                itemEditEcho.add(item);
            }
        } else {
            for (int i = positionEchoBegin; i < positionEchoEnd; ++i) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("name", parseDataEcho.nameEcho.get(i));
                item.put("_id", i - positionEchoBegin);
                itemEditEcho.add(item);
            }
        }
        adapterEdit = new DragNDropSimpleAdapter(EchoNowActivity.this,
                itemEditEcho, R.layout.custom_list_item_edit,
                new String[] { "name" }, new int[] { R.id.edit_item },
                R.id.handler);
    }

    public void showMessage(int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                EchoNowActivity.this);
        builder.setTitle(msg);
        builder.setNegativeButton(R.string.alertview_ok_button_title,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void actionRateShuffer() {
        if (isEdit) {
            isEdit();
            return;
        }
        if (positionItemChoose < 0) {
            showMessage(R.string.chose_echo);
            return;
        } else if (!isFound) {
            showMessage(R.string.no_echo);
            return;
        }
        positionPlayShuffer = -1;
        final boolean checkIsPlaying = isPlaying && !isStop;
        if (isPlaying) {
            isPlaying = false;
            mediaRecordPLayer.pause();
        }
        if (!isRateShuffer) {
            isRateShuffer = true;
            isShuffer = false;
            sortRating(checkIsPlaying);
        }
    }

    public void actionShuffer() {
        if (isEdit) {
            isEdit();
            return;
        }
        if (positionItemChoose < 0) {
            showMessage(R.string.chose_echo);
            return;
        } else if (!isFound) {
            showMessage(R.string.no_echo);
            return;
        }
        positionPlayShuffer = -1;
        final boolean checkIsPlaying = isPlaying && !isStop;
        if (isPlaying) {
            isPlaying = false;
            mediaRecordPLayer.pause();
        }
        if (!isShuffer) {
            isRateShuffer = false;
            isShuffer = true;
            positionEcho.clear();
            mPDWating = new ProgressDialog(EchoNowActivity.this);
            mPDWating.setTitle(R.string.waiting);
            mPDWating.setCancelable(false);
            mPDWating.show();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    elementEchoList.clear();
                    Random random = new Random();
                    int sizeOfList = positionEchoEnd - positionEchoBegin;
                    while (positionEcho.size() < sizeOfList) {
                        int tempPosition = random.nextInt(sizeOfList + 1)
                                % sizeOfList + positionEchoBegin;
                        if (positionEcho.size() == 0) {
                            positionEcho.add(tempPosition);
                            elementEchoList.add(parseDataEcho.nameEcho
                                    .get(tempPosition));
                        } else {
                            boolean isExsist = false;
                            for (int i = 0; i < positionEcho.size(); i++) {
                                if (tempPosition == positionEcho.get(i)) {
                                    isExsist = true;
                                    break;
                                }
                            }
                            if (!isExsist) {
                                positionEcho.add(tempPosition);
                                elementEchoList.add(parseDataEcho.nameEcho
                                        .get(tempPosition));
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mIBRateShuffer
                                    .setImageResource(R.drawable.audio_rateshuffle_but);
                            mIBShuffer
                                    .setImageResource(R.drawable.audio_mediashuffle_but_selected);
                            positionEchoPlay = positionEcho.get(positionEcho
                                    .size() - 1);
                            if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                                adapter.selectItem(0);
                                adapter.notifyDataSetChanged();
                            }
                            if (checkIsPlaying)
                                checkFilePlay();
                            mPDWating.dismiss();
                        }
                    });
                }
            }).start();
        }
        // checkFilePlay();
    }

    private void sortRating(final boolean playing) {
        mPDWating = new ProgressDialog(EchoNowActivity.this);
        mPDWating.setTitle(R.string.waiting);
        mPDWating.setCancelable(false);
        mPDWating.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                positionEcho.clear();
                elementEchoList.clear();
                int rating = 5;
                while (rating > 0) {
                    for (int i = 0; i < positionEchoEnd - positionEchoBegin; i++)
                        if (parseDataEcho.ratingEcho.get(positionEchoBegin + i) == rating) {
                            positionEcho.add(positionEchoBegin + i);
                            elementEchoList.add(parseDataEcho.nameEcho
                                    .get(positionEchoBegin + i));
                        }
                    rating--;
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mIBRateShuffer
                                .setImageResource(R.drawable.audio_rateshuffle_but_selected);
                        mIBShuffer
                                .setImageResource(R.drawable.audio_mediashuffle_but);
                        positionEchoPlay = positionEcho.get(positionEcho.size() - 1);
                        if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                            adapter.selectItem(0);
                            adapter.notifyDataSetChanged();
                        }
                        if (playing)
                            checkFilePlay();
                        mPDWating.dismiss();
                    }
                });
            }
        }).start();

    }

    // action good
    public void checkFilePlay() {
        try {
            if (!isShuffer && !isRateShuffer) {
                positionEchoPlay++;
                setPositionPlay();
                if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                    positionItemChoose = positionEchoPlay - positionEchoBegin;
                    adapter.selectItem(positionItemChoose);
                }
            } else {
                positionPlayShuffer++;
                if (positionPlayShuffer >= positionEcho.size())
                    positionPlayShuffer = 0;
                else if (positionPlayShuffer < 0)
                    positionPlayShuffer = positionEcho.size() - 1;
                positionEchoPlay = positionEcho.get(positionPlayShuffer);
                if (mIBBackEchoList.getVisibility() == View.VISIBLE) {
                    positionItemChoose = positionPlayShuffer;
                    adapter.selectItem(positionPlayShuffer);
                }
            }
            final int position = positionEchoPlay;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIBPlay.setImageResource(R.drawable.audio_pause_but);
                    adapter.notifyDataSetChanged();
                    switch (parseDataEcho.ratingEcho.get(position)) {
                        case 1:
                            showRatingEcho(R.id.rate_1);
                            break;
                        case 2:
                            showRatingEcho(R.id.rate_2);
                            break;
                        case 3:
                            showRatingEcho(R.id.rate_3);
                            break;
                        case 4:
                            showRatingEcho(R.id.rate_4);
                            break;
                        default:
                            showRatingEcho(R.id.rate_5);
                            break;
                    }
                }
            });
            mediaRecordPLayer.reset();
            mediaRecordPLayer.setDataSource(parseDataEcho.urlEcho
                    .get(positionEchoPlay));
            mediaRecordPLayer.prepare();
            mediaRecordPLayer.start();
            isPlaying = true;
        } catch (Exception e) {
            isPlaying = false;
            isStop = true;
        }
    }

    public void unSelectShuffer() {
        final boolean checkPlaying = isPlaying;
        if (isPlaying) {
            isPlaying = false;
            mediaRecordPLayer.pause();
        }
        mPDWating = new ProgressDialog(EchoNowActivity.this);
        mPDWating.setTitle(R.string.waiting);
        mPDWating.setCancelable(false);
        mPDWating.show();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                positionEcho.clear();
                elementEchoList.clear();
                for (int i = positionEchoBegin; i < positionEchoEnd; i++) {
                    int sizeOfList = elementEchoList.size();
                    elementEchoList.add(sizeOfList,
                            parseDataEcho.nameEcho.get(i));
                }
                positionEchoPlay = positionEchoBegin - 1;
                if (mIBBackEchoList.getVisibility() == View.INVISIBLE) {
                    adapter.selectItem(positionItemChoose);
                } else {
                    adapter.selectItem(0);
                }
                adapter.notifyDataSetChanged();
                if (checkPlaying)
                    checkFilePlay();
                mPDWating.dismiss();
            }
        });
    }
}
