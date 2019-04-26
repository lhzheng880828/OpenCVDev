package org.opencv.samples.facedetect;

import android.Manifest;
import android.app.Activity;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:18-10-9
 */
public class PermissionsUtils {

    public final static String[] PERMS_CAMERA ={Manifest.permission.CAMERA};

    public final static int REQ_CAMERA_CODE = 0xff;

    public static final String REQ_CAMERA_TIP = "request carmera permission";

    public static boolean checkPermission(Activity context, String[] perms) {
        return EasyPermissions.hasPermissions(context, perms);
    }


    public static void requestPermission(Activity context,String tip,int requestCode,String[] perms) {
        EasyPermissions.requestPermissions(context, tip,requestCode,perms);
    }

}
