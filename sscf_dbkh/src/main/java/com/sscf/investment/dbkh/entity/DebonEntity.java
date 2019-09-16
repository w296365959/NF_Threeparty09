package com.sscf.investment.dbkh.entity;

/**
 * (Hangzhou) <br/>
 *
 * @author: wzm <br/>
 * @date :  2019/8/13 17:24 <br/>
 * Summary:
 */
public class DebonEntity {
    private String action;
    private String rspId;
    private ParamInfo param;

    public static class ParamInfo {
        private String clientId;
        private String recognizeId;
        private String step;
        private String errorNo;
        private String errorInfo;
        private String anychatIp;
        private int anychatPort;
        private String loginName;
        private String loginPwd;
        private String roomName;
        private int roomId;
        private String roomPwd;
        private String showInfo;
        private String anychatRsp;

        public String getAnychatRsp() {
            return anychatRsp;
        }

        public void setAnychatRsp(String anychatRsp) {
            this.anychatRsp = anychatRsp;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getRecognizeId() {
            return recognizeId;
        }

        public void setRecognizeId(String recognizeId) {
            this.recognizeId = recognizeId;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public String getErrorNo() {
            return errorNo;
        }

        public void setErrorNo(String errorNo) {
            this.errorNo = errorNo;
        }

        public String getErrorInfo() {
            return errorInfo;
        }

        public void setErrorInfo(String errorInfo) {
            this.errorInfo = errorInfo;
        }

        public String getAnychatIp() {
            return anychatIp;
        }

        public void setAnychatIp(String anychatIp) {
            this.anychatIp = anychatIp;
        }

        public int getAnychatPort() {
            return anychatPort;
        }

        public void setAnychatPort(int anychatPort) {
            this.anychatPort = anychatPort;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getLoginPwd() {
            return loginPwd;
        }

        public void setLoginPwd(String loginPwd) {
            this.loginPwd = loginPwd;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public String getRoomPwd() {
            return roomPwd;
        }

        public void setRoomPwd(String roomPwd) {
            this.roomPwd = roomPwd;
        }

        public String getShowInfo() {
            return showInfo;
        }

        public void setShowInfo(String showInfo) {
            this.showInfo = showInfo;
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRspId() {
        return rspId;
    }

    public void setRspId(String rspId) {
        this.rspId = rspId;
    }

    public ParamInfo getParam() {
        return param;
    }

    public void setParam(ParamInfo param) {
        this.param = param;
    }
}
