package com.sscf.investment.scan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.sscf.investment.scan.view.ViewfinderView;

public interface ICapturePage {

    Handler getHandler();

    ViewfinderView getViewfinderView();

    Context getContext();

    void drawViewfinder();

    void handleDecode(final Result obj, Bitmap barcode, byte[] sourceData);

    void forResult(int resultCode, Intent intent);
}
