package com.sscf.investment.dbkh.entity;

/**
 * (Hangzhou) <br/>
 *
 * @author: wzm <br/>
 * @date :  2019/8/15 20:17 <br/>
 * Summary:
 */
public class RoleInfo {
    private String mStrName;
    private String mStrUserID;
    public RoleInfo(){}
    public RoleInfo(String strName, String strUserID) {
        mStrName = strName;
        mStrUserID = strUserID;
    }

    public void setName(String strName) {
        mStrName = strName;
    }

    public String getName() {
        return mStrName;
    }

    public void setUserID(String strUserID) {
        mStrUserID = strUserID;
    }

    public String getUserID() {
        return mStrUserID;
    }


}
