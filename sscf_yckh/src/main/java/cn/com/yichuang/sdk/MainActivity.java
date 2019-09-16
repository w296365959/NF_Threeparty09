package cn.com.yichuang.sdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.com.firstcapital.www.fcscsdklib.FirstCapitalOpenAccountActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openAccount();
    }

    private void openAccount() {
        Intent intent = new Intent(this, FirstCapitalOpenAccountActivity.class);
        String openAccountUrl = "http://183.239.170.39:8008/fcsc/acct4/index.html?color=2593e9&ocr=1";
        intent.putExtra("url", openAccountUrl);
        startActivity(intent);
    }
}
