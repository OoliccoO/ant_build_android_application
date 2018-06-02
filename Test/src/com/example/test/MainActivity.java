package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;


@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements OnTouchListener {

    Button[] mAllButton;
    ViewGroup mViewGroup;
    JSONObject mJson;
    static int num = 9;
    static int status_height = 0;
    static DisplayMetrics dm = null;
    MediaPlayer mMediaPlayer;
    MyHandler mHandler;
    View mView;
    TextView mt;
    boolean isdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(getMainLooper());
        status_height = ((MyApplication) getApplication()).status_height;
        dm = getScreenSize();
        mViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        mViewGroup.setBackgroundResource(R.drawable.xx45);
        setButton();
        setContentView(mViewGroup);
    }

    public class MyHandler extends Handler {

        public MyHandler(Looper mLooper) {
            super(mLooper);
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(1, Color.BLACK);
            drawable.setColor(0x00FFFFFF);
            mView.setBackground(drawable);
            if (null != mMediaPlayer) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mt.setVisibility(View.GONE);
            isdisplay = false;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mView = v;
        if (1 == v.getId()) {
            mt.setVisibility(View.VISIBLE);
            isdisplay = true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN && 1 == v.getId()) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(1, Color.RED);
            drawable.setColor(0x22FFFF00);
            v.setBackground(drawable);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && 1 == v.getId()) {
            playRecording();
            return true;
        } else
            return false;
    }

    public String readJson(File file) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                data.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }

    public DisplayMetrics getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public void setButton() {
        int i = 1;
        mAllButton = new Button[num];
        File file = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "json"
                        + File.separator
                        + "json.json");

        try {
            mJson = new JSONObject(readJson(file));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        for (Button m : mAllButton) {
            String name = "dialog_";
            if (i < 10)
                name += "0" + i;
            else
                name += i;
            try {
                int image_x = mJson.getJSONObject(name).getInt("X");
                int image_y = mJson.getJSONObject(name).getInt("Y");
                int image_width = mJson.getJSONObject(name).getInt("widthlenght");
                int image_height = mJson.getJSONObject(name).getInt("heightlenghe");
                int m_X = (dm.widthPixels * image_x) / 601;
                int m_Y = ((dm.heightPixels - status_height) * image_y) / 941;
                int m_width = (dm.widthPixels * image_width) / 601;
                int m_height = ((dm.heightPixels - status_height) * image_height) / 941;
                m = new Button(this);
                m.setX(m_X);
                m.setY(m_Y);
                m.setWidth(m_width);
                m.setHeight(m_height);
                m.setId(i);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(1, Color.BLACK);
                drawable.setColor(0x00FFFFFF);
                m.setBackground(drawable);
                m.setOnTouchListener(this);
                mViewGroup.addView(m);
                if (i == 1) {
                    mt = new TextView(this);
                    mt.setX(m_X);
                    mt.setY(m_Y + m_height + 1);
                    mt.setWidth(m_width + 10);
                    mt.setHeight(m_height + 10);
                    GradientDrawable text_drawable_01 = new GradientDrawable();
                    text_drawable_01.setShape(GradientDrawable.RECTANGLE);
                    text_drawable_01.setStroke(2, Color.GRAY);
                    text_drawable_01.setColor(0xBB7A7B7B);
                    mt.setBackground(text_drawable_01);
                    mt.setTextColor(0xFFFFFFFF);
                    mt.setPadding(2, 2, 2, 2);
                    mt.setGravity(Gravity.CENTER_VERTICAL);
                    mt.setText("Chinese translation");
                    mt.setVisibility(View.INVISIBLE);
                    mViewGroup.addView(mt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    public void playRecording() {
        new Thread() {

            @Override
            public void run() {
                try {
                    mMediaPlayer = new MediaPlayer();
                    AssetManager assetManager = getAssets();
                    AssetFileDescriptor fileDescriptor = assetManager.openFd("audio/88138898e18da6aa322eb9cb355c802c.mp3");
                    mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mHandler.sendEmptyMessage(100);
                        }

                    });
                    mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}
