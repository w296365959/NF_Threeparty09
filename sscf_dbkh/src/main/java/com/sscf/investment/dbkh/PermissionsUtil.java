package com.sscf.investment.dbkh;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * (Hangzhou) <br/>
 *
 * @author: wzm <br/>
 * @date :  2019/9/4 11:26 <br/>
 * Summary:
 */
public class PermissionsUtil {

    public static List<String> checkPermissions(Context context, String... permission) {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            if (!checkPermission(context, permission[i])) {
                permissionList.add(permission[i]);
            }
        }
        return permissionList;
    }


    public static boolean checkPermission(Context context, String permission) {
        boolean result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        } else {
            result = (PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED);
        }
        return result;
    }


    public static void requestPermissions(Activity context, String[] permissions, int code) {
        ActivityCompat.requestPermissions(context, permissions, code);
    }
}
