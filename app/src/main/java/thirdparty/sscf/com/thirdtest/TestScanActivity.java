package thirdparty.sscf.com.thirdtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class TestScanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        TestScanFragment2 testScanFragment = new TestScanFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt("capture_mode",1);
        testScanFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_layout,testScanFragment)
                .commit();
    }
}
