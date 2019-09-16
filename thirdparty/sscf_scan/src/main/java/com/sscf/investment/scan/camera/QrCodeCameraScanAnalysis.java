/*
 * Copyright © Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sscf.investment.scan.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 摄像头的预览回调,调用解码
 * Created by gulei on 2018/4/17.
 */
public class QrCodeCameraScanAnalysis implements Camera.PreviewCallback{

    private ExecutorService executorService;//解码的线程池，这里单线程就够了

    private Handler mHandler;//用于线程切换，传输扫码结果
    
    private QRCodeResultCallBack mCallback;//供主线程使用的解码成功回调
    
    private QRDecoder mQRDecoder;//解码器，解码方法的执行者

    private AtomicBoolean allowAnalysis = new AtomicBoolean(true);

    public QrCodeCameraScanAnalysis() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (mCallback != null){
                    //将解码得到的内容返回到主线程
                    mCallback.handleDecodeResult((String) msg.obj);
                }
            }
        };
    }
    
    /**
     * 设置解码器
     * @param decoder
     */
    public void setDecoder(QRDecoder decoder){
        mQRDecoder = decoder;
    }
    
    /**
     * 设置解码结果回调
     * @param callback
     */
    public void setScanCallback(QRCodeResultCallBack callback) {
        this.mCallback = callback;
    }
    
    /**
     * 使用之前都需要调用此方法来创建线程池
     */
    public void create(){
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 停止扫描
     */
    public void stop() {
        this.allowAnalysis.set(false);
    }
    
    /**
     * 开始扫描
     */
    public void start() {
        this.allowAnalysis.set(true);
    }
    
    /**
     * 销毁线程池
     */
    public void destroy(){
        if(executorService!=null && !executorService.isShutdown()){
            executorService.shutdownNow();
        }
    }
    
    /**
     * 相机预览的相片数据
     * @param data
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (allowAnalysis.get()) {
            if (executorService != null && !executorService.isShutdown()) {
                allowAnalysis.set(false);
                executorService.execute(new AnalysisTask(data, camera));
            }
        }
    }
    
    /**
     * 执行解码方法的线程
     */
    private class AnalysisTask implements Runnable {
        private byte[] mData; //待解码的数据
        private Camera mCamera;//相机
        
        /**
         * 设置待解码的资源
         * @param data
         * @param camera
         */
        public AnalysisTask(byte[] data,Camera camera){
            mData = data;
            mCamera = camera;
        }
        
        @Override
        public void run() {
            try {
                if(mData == null || mCamera == null){
                    return;
                }
                
                String resultStr = null;
                if(mQRDecoder!=null){
                    resultStr = mQRDecoder.decode(mData,mCamera);
                }
                if (!TextUtils.isEmpty(resultStr)) {
                    if(Thread.currentThread().isInterrupted()){
                        //避免出现多线程导致的多次结果返回
                        return;
                    }
                    //将结果返回到主线程
                    Message message = mHandler.obtainMessage();
                    message.obj = resultStr;
                    message.sendToTarget();
                } else {
                    //没有得到解码结果，继续解码
                    allowAnalysis.set(true);
                }
               
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mData = null;
                mCamera = null;
            }
            
        }
    };
}