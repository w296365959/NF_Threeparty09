package com.sscf.investment.scan.decoding;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.sscf.investment.component.scan.R;
import com.sscf.investment.scan.ICapturePage;
import com.sscf.investment.scan.camera.CameraManager;
import com.sscf.investment.scan.camera.PlanarYUVLuminanceSource;

import java.util.Hashtable;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final ICapturePage capturePage;
  private final MultiFormatReader multiFormatReader;

  DecodeHandler(ICapturePage capturePage, Hashtable<DecodeHintType, Object> hints) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.capturePage = capturePage;
  }

  @Override
  public void handleMessage(Message message) {
    final int what = message.what;
    if (what == R.id.decode) {
      //Log.d(TAG, "Got decode message");
      decode((byte[]) message.obj, message.arg1, message.arg2);
    } else if (what == R.id.quit) {
      Looper.myLooper().quit();
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
    Result rawResult = null;

    // TODO davidwei 修改为竖屏
    byte[] rotatedData = new byte[data.length];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
        rotatedData[x * height + height - y - 1] = data[x + y * width];
    }
    int tmp = width; // Here we are swapping, that's the difference to #11
    width = height;
    height = tmp;
    data = rotatedData;

    PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
      rawResult = multiFormatReader.decodeWithState(bitmap);
    } catch (Exception re) {
      re.printStackTrace();
      // continue
    } finally {
      multiFormatReader.reset();
    }

    if (rawResult != null) {
      long end = System.currentTimeMillis();
      Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
      Message message = Message.obtain(capturePage.getHandler(), R.id.decode_succeeded, rawResult);
      Bundle bundle = new Bundle();
      bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
      bundle.putByteArray(DecodeThread.BARCODE_BITMAP_BYTE_ARRAY, data);
      message.setData(bundle);
      message.sendToTarget();
    } else {
      Message message = Message.obtain(capturePage.getHandler(), R.id.decode_failed);
      message.sendToTarget();
    }
  }

}
