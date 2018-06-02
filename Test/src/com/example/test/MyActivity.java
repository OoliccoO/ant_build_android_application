package com.example.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import licc.cn.jarlibrary.ErrorTestClass;

import test3.licc3.jar3library.More60K;
//import com.licc.test.TestMath;
import com.cn.jar2library.Test;

import com.github.mmin18.methodpool2.A;


//import test3.licc3.jar3library.More60K;

public class MyActivity extends Activity implements OnClickListener {

    private Button mbutton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mymain);

        if(null == findViewById(R.id.mybutton_01)) {
            return;
        }

        mbutton = (Button) findViewById(R.id.mybutton_01);
        mbutton.setOnClickListener(this);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }

        // --------------------- Test ---------------------
        // 1
        mbutton.setText(R.string.button_message);
        // 2
        ErrorTestClass.showToast(MyActivity.this, "3 * 4 = " + Test.multiply(3, 4));
        // 3
        More60K more60k = new More60K();
        more60k.method0(MyActivity.this);
        // 4
        A a = new A();
        a.method_0();
        // --------------------- Test ---------------------
    }

    @Override
    public void onClick(View v) {
        Intent m = new Intent(this, MainActivity.class);
        startActivity(m);

    }

    private JSONObject createJSONObject() {
        JSONObject image_inf = new JSONObject();
        try {
            JSONObject dialog_01 = new JSONObject();
            dialog_01.put("X", 85);
            dialog_01.put("Y", 161);
            dialog_01.put("widthlenght", 144);
            dialog_01.put("heightlenghe", 66);
            image_inf.put("dialog_01", dialog_01);

            JSONObject dialog_02 = new JSONObject();
            dialog_02.put("X", 237);
            dialog_02.put("Y", 324);
            dialog_02.put("widthlenght", 75);
            dialog_02.put("heightlenghe", 40);
            image_inf.put("dialog_02", dialog_02);

            JSONObject dialog_03 = new JSONObject();
            dialog_03.put("X", 138);
            dialog_03.put("Y", 484);
            dialog_03.put("widthlenght", 175);
            dialog_03.put("heightlenghe", 64);
            image_inf.put("dialog_03", dialog_03);

            JSONObject dialog_04 = new JSONObject();
            dialog_04.put("X", 220);
            dialog_04.put("Y", 589);
            dialog_04.put("widthlenght", 163);
            dialog_04.put("heightlenghe", 41);
            image_inf.put("dialog_04", dialog_04);

            JSONObject dialog_05 = new JSONObject();
            dialog_05.put("X", 200);
            dialog_05.put("Y", 753);
            dialog_05.put("widthlenght", 110);
            dialog_05.put("heightlenghe", 60);
            image_inf.put("dialog_05", dialog_05);

            JSONObject dialog_06 = new JSONObject();
            dialog_06.put("X", 428);
            dialog_06.put("Y", 105);
            dialog_06.put("widthlenght", 162);
            dialog_06.put("heightlenghe", 72);
            image_inf.put("dialog_06", dialog_06);

            JSONObject dialog_07 = new JSONObject();
            dialog_07.put("X", 524);
            dialog_07.put("Y", 222);
            dialog_07.put("widthlenght", 62);
            dialog_07.put("heightlenghe", 44);
            image_inf.put("dialog_07", dialog_07);

            JSONObject dialog_08 = new JSONObject();
            dialog_08.put("X", 398);
            dialog_08.put("Y", 624);
            dialog_08.put("widthlenght", 163);
            dialog_08.put("heightlenghe", 64);
            image_inf.put("dialog_08", dialog_08);

            JSONObject dialog_09 = new JSONObject();
            dialog_09.put("X", 471);
            dialog_09.put("Y", 802);
            dialog_09.put("widthlenght", 115);
            dialog_09.put("heightlenghe", 81);
            image_inf.put("dialog_09", dialog_09);

            String mString_01 = image_inf.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return image_inf;
    }

    private void writeJSONObjectToSdCard(JSONObject person) {
        String a = Environment.getExternalStorageDirectory() + File.separator + "json" + File.separator + "json.txt";
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "json" + File.separator + "json.json");
        String t = file.getParentFile() + "";
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        PrintStream outputStream = null;
        try {
            outputStream = new PrintStream(new FileOutputStream(file));
            outputStream.print(person.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
