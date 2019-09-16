package com.sscf.sscf_changjiang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.thinkive_cj.adf.ui.OpenMainActivity;

public class MainActivity extends AppCompatActivity {

    private final String URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.class.getSimpleName(),"长江插件启动");
        Intent intent = new Intent();
        intent.putExtra(URL,getIntent().getStringExtra(URL));
        intent.setClass(MainActivity.this, OpenMainActivity.class);
        startActivity(intent);
        finish();
    }
}
