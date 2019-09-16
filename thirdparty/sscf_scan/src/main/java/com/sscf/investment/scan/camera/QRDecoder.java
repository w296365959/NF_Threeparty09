package com.sscf.investment.scan.camera;

import android.hardware.Camera;

/**
 * 条码，二维码解码的接口
 * Created by gulei on 2018/4/17.
 */

public interface QRDecoder {
    String decode(byte[] data, Camera camera);
}
