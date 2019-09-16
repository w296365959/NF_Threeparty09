package com.sscf.investment.dbkh;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qihoo360.replugin.RePlugin;
import com.sscf.investment.dbkh.entity.DebonEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * (Hangzhou) <br/>
 *
 * @author: wzm <br/>
 * @date :  2019/8/14 16:28 <br/>
 * Summary:德邦证券 开户
 */
public class VideoAuthenticationActivity extends Activity implements AnyChatObjectEvent, AnyChatBaseEvent, View.OnClickListener, AnyChatTransDataEvent {
    public static final String TAG = VideoAuthenticationActivity.class.getSimpleName();
    public static final int VIDEO_REQUEST_CODE = 1230;
    private AnyChatCoreSDK anychat = null;
    private SurfaceView mSurfaceLocal;
    private SurfaceView mSurfaceRemote;

    private ImageButton mSpeakControl;
    private ImageButton mCameraControl;
    private ImageButton mImgSwichVideo;
    private Button endCallBn;
    private TextView mTipsTv;
    private DebonEntity.ParamInfo mParamInfo;

    private int userselfID;//自己的id
    private int userOtherID;//他人的id
    public static final String EXTRA_VIDEO_AUTHENTICATION_MSG = "extra_video_authentication_msg";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viedo_authentication_layout);
        List<String> permissionlist = PermissionsUtil.checkPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
        if (permissionlist.size() == 0) {
            initAnychat();
        } else {
            PermissionsUtil.requestPermissions(this, permissionlist.toArray(new String[permissionlist.size()]), VIDEO_REQUEST_CODE);
        }
    }
    private void initAnychat(){
        //获取核心类对象
        anychat = AnyChatCoreSDK.getInstance(this);
        // 设置基本回调事件接收
        anychat.SetBaseEvent(this);
        // 设置业务对象回调事件接收
        anychat.SetObjectEvent(this);
        //anychat.SetVideoCallEvent(this);
        //服务端回调监听
        anychat.SetTransDataEvent(this);
        //初始化系统
        anychat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
        //设置本地视频采集随着设备而旋转处理
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, 1);
        Intent intent = getIntent();
        String json = intent.getStringExtra(EXTRA_VIDEO_AUTHENTICATION_MSG);
        if (TextUtils.isEmpty(json)) {
            Toast.makeText(this, "参数错误!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        DebonEntity debonEntity = new Gson().fromJson(json, new TypeToken<DebonEntity>() {
        }.getType());
        if (debonEntity == null || debonEntity.getParam() == null) {
            return;
        }
        mParamInfo = debonEntity.getParam();
        initView();
    }
    private void initView() {
        // 初始化用于显示视频的控件 sufaceview
        mSurfaceLocal = this.findViewById(R.id.surfaceview_local);
        mSurfaceRemote = this.findViewById(R.id.surfaceview_remote);

        mSpeakControl = findViewById(R.id.btn_speakControl);
        mCameraControl = findViewById(R.id.btn_cameraControl);
        mImgSwichVideo = findViewById(R.id.ImgSwichVideo);
        endCallBn = findViewById(R.id.endCall);
        mTipsTv = findViewById(R.id.tips_tv);

        endCallBn.setOnClickListener(this);
        mSpeakControl.setOnClickListener(this);
        mCameraControl.setOnClickListener(this);
        mImgSwichVideo.setOnClickListener(this);
        // 连接服务器,第一个参数为你需要连接的视频服务器地址，如果您部署视频服务器的地址为
        // 192.168.1.8，则传人这个地址，AnyChat提供的demo默认地址是demo.anychat.cn
        /**
         *AnyChat可以连接自主部署的服务器、也可以连接AnyChat视频云平台；
         *连接自主部署服务器的地址为自设的服务器IP地址或域名、端口；
         *连接AnyChat视频云平台的服务器地址为：cloud.anychat.cn；端口为：8906
         */
        anychat.Connect(mParamInfo.getAnychatIp(), mParamInfo.getAnychatPort());
        //anychat.Connect(" 192.168.1.8",8906);
        mTipsTv.setText(mParamInfo.getShowInfo());
        initCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ==="+grantResults.length);
        if (requestCode == VIDEO_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "相机，录音等权限不足!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            //所有权限全部获取后，才初始化
            Log.i(TAG, "onRequestPermissionsResult:== "+Thread.currentThread());
            initAnychat();

        } else {
            Toast.makeText(this, "相机，录音等权限不足!", Toast.LENGTH_LONG).show();
        }
    }

    private void initCamera() {
        // 启动 AnyChat 传感器监听
        anychat.mSensorHelper.InitSensor(this);
        // 初始化 Camera 上下文句柄
        AnyChatCoreSDK.mCameraHelper.SetContext(this);
        //设置 SURFACE_TYPE_PUSH_BUFFERS 模式
        mSurfaceLocal.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 如果是采用Java视频采集，则需要设置Surface的CallBack
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            //打开本地视频预览，开始采集本地视频数据
            mSurfaceLocal.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
        }
        mSurfaceRemote.setBackgroundColor(Color.TRANSPARENT);
        //true 使当前SurfaceView在前面显示
        mSurfaceRemote.setZOrderOnTop(true);
        /***
         * AnyChat支持多种用户身份验证方式，包括更安全的签名登录，
         * 详情请参考：http://bbs.anychat.cn/forum.php?mod=viewthread&tid=2211&highlight=%C7%A9%C3%FB
         *  用户登录(用户名，用户密码)，登录密码（为空表示游客）
         */
        anychat.Login(mParamInfo.getLoginName(), mParamInfo.getLoginPwd());

        // 判断是否显示本地摄像头切换图标
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
                // 默认打开前置摄像头
                AnyChatCoreSDK.mCameraHelper
                        .SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
            }
        } else {
            String[] strVideoCaptures = anychat.EnumVideoCapture();
            if (strVideoCaptures != null && strVideoCaptures.length > 1) {
                // 默认打开前置摄像头
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    String strDevices = strVideoCaptures[i];
                    if (strDevices.indexOf("Front") >= 0) {
                        anychat.SelectVideoCapture(strDevices);
                        break;
                    }
                }
            }
        }

    }

    private void exitVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定退出?")
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //离开指定房间，-1 表示离开当前所在房间
                                anychat.LeaveRoom(-1);
                                sendEventBusEvent("SYS:10002","videoWitness");
                                finish();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * 反射 发送eventBus
     * @param code
     * @param action
     */
    private void sendEventBusEvent(String code,String action){
        ClassLoader hostClassLoader = RePlugin.getHostClassLoader();
        try {
            Log.i(TAG, "sendEventBusEvent: ");
            Class<?> msgEvent = hostClassLoader.loadClass("com.sscf.investment.web.sdk.widget.PostVideoEvent");
            Constructor<?> constructor = msgEvent.getConstructor(String.class, String.class);
            Object videoWitness = constructor.newInstance(code, action);
            Method sendEventbus = msgEvent.getMethod("sendEventbus");
            sendEventbus.invoke(videoWitness);
            Log.i(TAG, "sendEventBusEvent: 發送成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.endCall) {
            exitVideoDialog();
        } else if (v.getId() == R.id.btn_speakControl) {
            //麦克风
            if ((anychat.GetSpeakState(-1) == 1)) {//0关
                Log.i(TAG, "关闭 麦克风");
                mSpeakControl.setImageResource(R.drawable.speak_off);
                anychat.UserSpeakControl(-1, 0);
            } else {//1开
                Log.i(TAG, "打开 麦克风");
                mSpeakControl.setImageResource(R.drawable.speak_on);
                anychat.UserSpeakControl(-1, 1);
            }

        } else if (v.getId() == R.id.btn_cameraControl) {
            //摄像头
            if ((anychat.GetCameraState(-1) == 2)) {
                Log.i(TAG, "关闭 摄像头");
                mCameraControl.setImageResource(R.drawable.camera_off);
                anychat.UserCameraControl(-1, 0);
            } else {//1开 ，0关
                Log.i(TAG, "打开 摄像头");
                mCameraControl.setImageResource(R.drawable.camera_on);
                anychat.UserCameraControl(-1, 1);
            }
        } else if (v.getId() == R.id.ImgSwichVideo) {
            //转换摄像头
            // 如果是采用Java视频采集，则在Java层进行摄像头切换
            if (AnyChatCoreSDK
                    .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
                AnyChatCoreSDK.mCameraHelper.SwitchCamera();
                Log.i(TAG, "java转换摄像头");
                return;
            }
            Log.i(TAG, "转换摄像头");
            String strVideoCaptures[] = anychat.EnumVideoCapture();
            String temp = anychat.GetCurVideoCapture();
            for (int i = 0; i < strVideoCaptures.length; i++) {
                if (!temp.equals(strVideoCaptures[i])) {
                    anychat.UserCameraControl(-1, 0);
                    anychat.SelectVideoCapture(strVideoCaptures[i]);
                    anychat.UserCameraControl(-1, 1);
                    break;
                }
            }
        }
    }

    /**
     * 当客户端连接服务器时被触发
     *
     * @param bSuccess 表示是否连接成功， true连接服务器成功
     */
    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        Log.i(TAG, "OnAnyChatConnectMessage: " + bSuccess);

    }

    /**
     * 用户登录触发(login)
     *
     * @param dwUserId    服务器为客户端分配的唯一标识userid,当 dwErrorCode 为 0 时有效
     * @param dwErrorCode 出错代码，可判断登录是否成功,0登录成功，其他值为登录服务器失败的错误代码
     */
    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        Log.i(TAG, "OnAnyChatLoginMessage: dwUserId=" + dwUserId + ",dwErrorCode=" + dwErrorCode);
        if (dwErrorCode == 0) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            // EnterRoomEx使用 房间名称 进入，EnterRoom使用房间id进入
            anychat.EnterRoomEx(mParamInfo.getRoomName(), mParamInfo.getRoomPwd());
            userselfID = dwUserId;
        } else {
            Toast.makeText(this, "登录失败,dwErrorCode=" + dwErrorCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 进入房间事件, 进入房间触发
     *
     * @param dwRoomId    表示进入的房间 ID 号
     * @param dwErrorCode 出错代码，可判断进入房间是否成功
     */
    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        Log.i(TAG, "OnAnyChatEnterRoomMessage: dwRoomId=" + dwRoomId + ",dwErrorCode=" + dwErrorCode);

    }

    // 当前房间用户离开或者进入房间触发这个回调，dwUserId用户 id," bEnter==true"表示进入房间,反之表示离开房间

    /**
     * 房间用户活动事件
     * 当成功进入房间之后，有新的用户进入房间，或是房间用户离开房间，均会触发该接口
     *
     * @param dwUserId 表示当前房间活动用户的 ID 号
     * @param bEnter   true 表示进入房间，false 表示离开房间
     */
    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        Log.i(TAG, "OnAnyChatUserAtRoomMessage: dwUserId=" + dwUserId + ",bEnter=" + bEnter);
        if (!bEnter) {
            if (dwUserId == userOtherID) {
                Log.i(TAG, "对方离开房间");
                //关闭音视频
                anychat.UserCameraControl(dwUserId, 0);
                anychat.UserSpeakControl(dwUserId, 0);
                userOtherID = 0;
                finish();
            } else if (dwUserId == userselfID) {
                Log.i(TAG, "自己离开房间");
                anychat.UserCameraControl(dwUserId, 0);
                anychat.UserSpeakControl(dwUserId, 0);
                //离开指定房间，-1 表示离开当前所在房间
                anychat.LeaveRoom(-1);
            }
        } else {
            if (userselfID != dwUserId) {
                Log.i(TAG, "有人进入我的房间" + dwUserId);
                if (userOtherID != 0) {
                    userOtherID = dwUserId;
                    // 如果是采用Java视频显示，则需要设置Surface的CallBack
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        int index = anychat.mVideoHelper.bindVideo(mSurfaceRemote.getHolder());
                        Log.i(TAG, "SetVideoUser===" + dwUserId);
                        anychat.mVideoHelper.SetVideoUser(index, userOtherID);
                    }
                }
            } else {
                Log.i(TAG, "我进入我的房间 dwUserId=" + dwUserId);
            }
            /**
             * 打开本地视频, 第一个参数用-1 表示对本地视频进行控制，也可以用本地的真实 userid,第二个参数 1表示打开视频，0表示关闭
             * 对于本地用户，该方法是直接操作用户的摄像头
             * 而对于其它用户，该方法只是向对方发送一个请求（取消）视频流的申请，并不会直接操作对方的摄像头
             */
            anychat.UserCameraControl(dwUserId, 1);

            /**
             * 打开本地音频
             * 用户编号，为-1 表示对本地发言进行控制
             * 否允许用户发言，当 dwUserid=-1 时，1 表示请求发言（拿Mic），0 表示停止发言（放 Mic）
             * 对于本地用户，该方法是直接操作用户的 Mic，而对于其它用户，该方法只是向对方发送一个请求（取消）音频流的申请，并不会直接操作对方的 Mic。
             */
            anychat.UserSpeakControl(dwUserId, 1);
        }
    }


    /**
     * 当前房间在线用户消息，进入房间成功后调用一次。
     * 房间在线用户事件
     * 当前房间在线用户消息，进入房间成功后调用一次。
     * 收到该消息后，便可对房间中的用户进行音视频的相关操作，如请求音频、请求视频等
     *
     * @param dwUserNum 表示当前房间的在线用户数（包含自己）
     * @param dwRoomId  房间编号
     */
    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Log.i(TAG, "OnAnyChatOnlineUserMessage: dwUserNum= " + dwUserNum + ",dwRoomId=" + dwRoomId);

        int[] userID = anychat.GetOnlineUser();//获取当前房间在线用户列表（不包含自己）
        if (userID.length > 0) {
            Log.i(TAG, "userOtherID==" + userID[0]);
            userOtherID = userID[0];
            Log.i(TAG, "打开音视频==" + userID[0]);
            // 如果是采用Java视频显示，则需要设置Surface的CallBack
            if (AnyChatCoreSDK
                    .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                int index = anychat.mVideoHelper.bindVideo(mSurfaceRemote.getHolder());
                Log.i(TAG, "打开对方Java视频显示==" + userID[0]);
                anychat.mVideoHelper.SetVideoUser(index, userID[0]);

                anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, userID[0], 0, 0, 0, null);
                anychat.UserCameraControl(userID[0], 1);
                anychat.UserSpeakControl(userID[0], 1);
            }

            anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, userselfID, 0, 0, 0, null);

            /**
             * GetCameraState 返回值
             * 0 没有摄像头
             * 1 有摄像头但没有打开
             * 2 摄像头已打开
             */
            Log.i(TAG, "GetCameraState==" + anychat.GetCameraState(-1));
            if ((anychat.GetCameraState(-1) != 0)) {
                Log.i(TAG, "打开视频==" + userID[0]);
                mCameraControl.setImageResource(R.drawable.camera_on);
                /**
                 * 打开本地视频, 1表示打开视频，0表示关闭
                 */
                anychat.UserCameraControl(-1, 1);
            }
            //设置对方 音视频
            /**
             * GetSpeakState 返回值
             * 0 音频采集关闭
             * 1 音频采集开启
             */
            if ((anychat.GetSpeakState(-1) == 0)) {
                anychat.UserSpeakControl(-1, 0);//莫名开始没有声音，先关再开
                Log.i(TAG, "打开音频==" + userID[0]);
                /**
                 * 打开本地音频
                 * 1开启，0关闭
                 */
                mSpeakControl.setImageResource(R.drawable.speak_on);
                anychat.UserSpeakControl(-1, 1);
            }
        }
    }


    /**
     * 网络连接关闭事件
     * 跟服务器网络断触发该消息。收到该消息后可以关闭音视频以及做相关提示工作
     * 如果已打开本地摄像头，则上层应用必须在网络连接关闭事件中关闭本地摄像头，否则可能造成异常
     *
     * @param dwErrorCode
     */
    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        Log.i(TAG, "OnAnyChatLinkCloseMessage: dwErrorCode=" + dwErrorCode);

        //关闭本地视频，第一个参数用-1 表示本地，也可以用本地的真实 userid
        anychat.UserCameraControl(-1, 0);
        //关闭本地音频
        anychat.UserSpeakControl(-1, 0);

    }


    /**
     * 业务对象回调事件，调用AnyChatCoreSDk.ObjectControl方法触发这个回调
     *
     * @param dwObjectType 业务对象类型
     * @param dwObjectId   业务对象的 ID
     * @param dwEventType  业务对象事件回调类型
     * @param dwParam1     用户自定义参数,整型
     * @param dwParam2
     * @param dwParam3
     * @param dwParam4
     * @param strParam     用户自定义参数,字符串型
     */
    @Override
    public void OnAnyChatObjectEvent(int dwObjectType, int dwObjectId, int dwEventType, int dwParam1, int dwParam2,
                                     int dwParam3, int dwParam4, String strParam) {
        Log.i(TAG, "OnAnyChatObjectEvent: dwObjectType=" + dwObjectType + ",dwObjectId=" + dwObjectId +
                ",dwEventType=" + dwEventType + ",dwParam1=" + dwParam1 + ",dwParam2=" + dwParam2 +
                ",dwParam3=" + dwParam3 + ",dwParam4=" + dwParam4 + ",strParam=" + strParam);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.i(TAG, "onPointerCaptureChanged: hasCapture=" + hasCapture);
    }


    @Override
    public void OnAnyChatTransFile(int dwUserid, String FileName, String TempFilePath, int dwFileLength, int wParam, int lParam, int dwTaskId) {

        Log.i(TAG, "OnAnyChatTransFile " + dwUserid);
    }

    /**
     * SYS:10000 见证通过
     * SYS:10001 见证不通过
     * SYS:10002 客户端主动退出
     * SYS:10003 复核通过
     * SYS:10004 座席强制退出，需要退出房间
     * SYS:10005 客户端回应座席端确认退出
     * SYS:10006 座席关闭，不踢掉客户，等待座席重连客户
     * USE:0     表示常用语和常用问题，方便进行消息显示和纪录，并区分系统消息
     *
     * @param dwUserid 用户 ID，指示发送用户
     * @param lpBuf    缓冲区地址
     * @param dwLen    缓冲区大小
     */
    @Override
    public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
        String code = new String(lpBuf);
        Log.i(TAG, "OnAnyChatTransBuffer==" + code);

        sendEventBusEvent(code, "videoWitness");
        if ("SYS:10000".equals(code)) {
            Log.i(TAG, "见证通过");
        } else if ("SYS:10001".equals(code)) {
            Log.i(TAG, "见证不通过");
        } else if ("SYS:10003".equals(code)) {
            Log.i(TAG, "复核通过");
        }

        if (code.contains("SYS")) {
            finish();
        }

    }

    @Override
    public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int
            lparam, int taskid) {
        Log.i(TAG, "OnAnyChatTransBufferEx " + dwUserid);
    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {
        Log.i(TAG, "OnAnyChatSDKFilterData");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendEventBusEvent("SYS:10002", "videoWitness");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        anychat.removeEvent(this);
        anychat.mSensorHelper.DestroySensor();
        //断开与服务器的连接
        anychat.Logout();
        //释放资源
        anychat.Release();
    }
}
