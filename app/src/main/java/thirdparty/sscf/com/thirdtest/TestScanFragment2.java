package thirdparty.sscf.com.thirdtest;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;

import com.sscf.investment.scan.CaptureFragment;
import com.sscf.investment.scan.camera.QRDecoder;

/**
 * (Hangzhou)
 *
 * @author: wzm
 * @date :  2019/2/20 14:27
 * Summary:
 */
public class TestScanFragment2 extends CaptureFragment {
    @Override
    protected void showCameraErrorDialog() {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void setPictureFinishResultOk(Uri uri) {

    }

    @Override
    public void setScanFinishResultOk(String res) {

    }

    @Override
    public void onQrCodeDecodeResult(String result) {

    }

    @Override
    public QRDecoder getQRDecoder() {
        return null;
    }

    @Override
    public void forResult(int resultCode, Intent intent) {

    }
}
