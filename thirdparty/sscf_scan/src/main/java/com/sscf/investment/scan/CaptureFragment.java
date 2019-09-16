package com.sscf.investment.scan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.sscf.investment.component.scan.R;
import com.sscf.investment.scan.camera.CameraManager;
import com.sscf.investment.scan.camera.QRDecoder;
import com.sscf.investment.scan.camera.QrCodeCameraScanAnalysis;
import com.sscf.investment.scan.camera.Util;
import com.sscf.investment.scan.decoding.CaptureActivityHandler;
import com.sscf.investment.scan.decoding.InactivityTimer;
import com.sscf.investment.scan.view.ScanCoverView;
import com.sscf.investment.scan.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

public abstract class CaptureFragment extends Fragment implements ICapturePage, Callback, View.OnClickListener,
        DialogInterface.OnCancelListener, Camera.PictureCallback {
    private static final String TAG = CaptureFragment.class.getSimpleName();
    private static final int REQUEST_CODE_GALLERY = 100;
    public static final String EXTRA_CAPTURE_MODE = "capture_mode";
    private int mMode;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceHolder surfaceHolder;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;

    private View root;
    private View mTakePictureButton;
    private View mGallaryButton;

    private ScanCoverView qrCodeScanLayout;
    private View stockImportLayout;
    private View qrCodeScanButton;
    private View stockImportButton;
    private View centerScanLayout;
    private View centerScanView;
    private Animation scanAnim;
    private QrCodeCameraScanAnalysis qrCodeCameraScanAnalysis;

//    private CommonDialog mCameraErrorDialog;

    private OrientationEventListener mOrientationEventListener;

    private int mOrientation;
    private boolean isScanInit=false;

    /**
     * 显示获取摄像头异常Dialog
     */
    protected abstract void showCameraErrorDialog();

    /**
     * 存储抓取到的数据
     *
     * @param data
     * @param camera
     */
    @Override
    public abstract void onPictureTaken(byte[] data, Camera camera);

    public abstract void setPictureFinishResultOk(final Uri uri);

    public abstract void setScanFinishResultOk(final String res);

    public abstract void onQrCodeDecodeResult(String result);

    public abstract QRDecoder getQRDecoder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.activity_setting_scan, null);
        root = parent;
        CameraManager.init(getContext().getApplicationContext());
        Bundle arguments = getArguments();
        int mode=1;
        if (arguments!=null){
            mode = arguments.getInt(EXTRA_CAPTURE_MODE, 1);
        }
        initView(parent,mode);
        return parent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        hasSurface = false;
        inactivityTimer = new InactivityTimer((AppCompatActivity) context);

        mOrientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation != ORIENTATION_UNKNOWN) {
                    mOrientation = Util.roundOrientation(orientation, mOrientation);
                }
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView(View parent,int mode) {
        viewfinderView = (ViewfinderView) parent.findViewById(R.id.viewfinder_view);
        final SurfaceView surfaceView = (SurfaceView) parent.findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();

        centerScanLayout = parent.findViewById(R.id.scan_center_layout);
        centerScanView = parent.findViewById(R.id.center_scan_view);
        mTakePictureButton = parent.findViewById(R.id.takePictureButton);
        mTakePictureButton.setOnClickListener(this);
        mGallaryButton = parent.findViewById(R.id.gallaryButton);
        mGallaryButton.setOnClickListener(this);
        qrCodeScanLayout = parent.findViewById(R.id.scan_cover_layout);
        stockImportLayout = parent.findViewById(R.id.import_stock_layout);
        qrCodeScanButton = parent.findViewById(R.id.bottom_qr_code_layout);
        stockImportButton = parent.findViewById(R.id.bottom_import_stock_layout);
        qrCodeScanButton.setOnClickListener(v -> changeToQrCodeScan());
        stockImportButton.setOnClickListener(v -> changeToImportStock());
        if (mode==1){
            qrCodeScanButton.performClick();
        }else {
            stockImportButton.performClick();
        }

        parent.findViewById(R.id.gallaryButton).setOnClickListener(this);

    }

    private void animScan() {
        centerScanView.post(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation upToDownAnim = new TranslateAnimation(
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE,0,
                        Animation.ABSOLUTE,0f,
                        Animation.ABSOLUTE,centerScanLayout.getHeight() - centerScanView.getHeight());
                TranslateAnimation downToUpAnim = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT,0,
                        Animation.RELATIVE_TO_PARENT,1f,
                        Animation.RELATIVE_TO_PARENT,0f);
                upToDownAnim.setDuration(3000);
                upToDownAnim.setFillEnabled(true);
                upToDownAnim.setFillAfter(true);
                upToDownAnim.setRepeatMode(Animation.REVERSE);
                upToDownAnim.setRepeatCount(Integer.MAX_VALUE);

                downToUpAnim.setDuration(3000);
                downToUpAnim.setStartTime(3000);
                centerScanView.startAnimation(upToDownAnim);
                scanAnim = upToDownAnim;
            }
        });
    }

    private void changeToQrCodeScan() {
        if (qrCodeScanButton.isSelected()) {
            return;
        }
        qrCodeScanButton.setSelected(true);
        stockImportButton.setSelected(false);
        qrCodeScanLayout.setVisibility(View.VISIBLE);
        stockImportLayout.setVisibility(View.GONE);
        if (!isScanInit){
            qrCodeScanLayout.post(new Runnable() {
                @Override
                public void run() {
                    CameraManager.get().setFramingRect(qrCodeScanLayout.getScanValidRect());
                    isScanInit=true;
                }
            });
            animScan();
        }
        if (qrCodeCameraScanAnalysis == null) {
            qrCodeCameraScanAnalysis = createScanAnalysis();
        }
        qrCodeCameraScanAnalysis.start();
    }

    private void changeToImportStock() {
        if (stockImportButton.isSelected()) {
            return;
        }
        qrCodeScanButton.setSelected(false);
        stockImportButton.setSelected(true);
        qrCodeScanLayout.setVisibility(View.GONE);
        stockImportLayout.setVisibility(View.VISIBLE);

        if (qrCodeCameraScanAnalysis != null) {
            qrCodeCameraScanAnalysis.stop();
        }
    }

    private QrCodeCameraScanAnalysis createScanAnalysis() {
        QrCodeCameraScanAnalysis analysis = new QrCodeCameraScanAnalysis();
        analysis.setDecoder(getQRDecoder());
//        analysis.setScanCallback(this::onQrCodeDecodeResult);
        analysis.create();
        CameraManager.get().addPreviewCallback(analysis);
        return analysis;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }

        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        vibrate = true;

        if (qrCodeCameraScanAnalysis != null && qrCodeScanButton.isSelected()) {
            qrCodeCameraScanAnalysis.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }

        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

        if (qrCodeCameraScanAnalysis != null && qrCodeScanButton.isSelected()) {
            qrCodeCameraScanAnalysis.stop();
        }
    }

    @Override
    public void onDestroy() {
        if (scanAnim != null) {
            scanAnim.cancel();
        }
        if(surfaceHolder != null){
            surfaceHolder.removeCallback(this);
            surfaceHolder = null;
        }
        if (qrCodeCameraScanAnalysis != null) {
            qrCodeCameraScanAnalysis.destroy();
        }
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    handleGalleryPic(data);
                    break;
                default:
            }
        }
    }

    private boolean handleGalleryPic(Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            Log.d(TAG, "uri = " + uri);
            if (uri != null) {
                setPictureFinishResultOk(uri);
                return true;
            }
        }
        return false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
            }
        } catch (IOException ioe) {
            showCameraErrorDialog();
            return;
        } catch (RuntimeException e) {
            showCameraErrorDialog();
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }
    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }
    @Override
    public Handler getHandler() {
        return handler;
    }
    @Override
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }
    @Override
    public void handleDecode(final Result obj, Bitmap barcode, byte[] sourceData) {
        inactivityTimer.onActivity();
        vibrate();
        if (stockImportLayout.isSelected()) {
            setScanFinishResultOk(obj.getText());
        } else {
            onQrCodeDecodeResult(obj.getText());
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (handler != null) {
            handler.sendEmptyMessage(R.id.restart_preview);
        }
    }

    private void vibrate() {
        if (vibrate) {
            Vibrator vibrator = (Vibrator) (getContext()).getSystemService(Activity.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.takePictureButton) {
            if (handler != null) {
                handler.removeMessages(R.id.auto_focus);
            }
            CameraManager.get().takePicture(this, mOrientation);
        } else if (id == R.id.gallaryButton) {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        try {
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
