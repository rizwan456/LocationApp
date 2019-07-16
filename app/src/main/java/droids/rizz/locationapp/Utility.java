package droids.rizz.locationapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;

public class Utility {
    public static boolean checkPermissionRequest(Permission permission, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (permission) {
                case LOCATION:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ? false : true;
                case COARSE_LOCATION:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ? false : true;
                case READ_SMS:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ? false : true;
                case WRITE_SMS:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ? false : true;
                case READ_STORAGE:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ? false : true;
                case WRITE_STORAGE:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ? false : true;
                case CAMERA:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ? false : true;
                case RECORD:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ? false : true;
                case READ_CALENDAR:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ? false : true;
                case WRITE_CALENDAR:
                    return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ? false : true;
            }
            return false;
        } else
            return true;
    }

    public static void raisePermissionRequest(Permission permission, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (permission) {
                case COARSE_LOCATION:
                    if (!checkPermissionRequest(Permission.COARSE_LOCATION, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, generateRequestCodes().get("LOCATION_REQUEST"));
                    }
                    break;
                case LOCATION:
                    if (!checkPermissionRequest(Permission.LOCATION, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, generateRequestCodes().get("LOCATION_REQUEST"));
                    }
                    break;

                case READ_STORAGE:
                    if (!checkPermissionRequest(Permission.READ_STORAGE, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, generateRequestCodes().get("READ_STORAGE_REQUEST"));
                    }
                    break;

                case WRITE_STORAGE:
                    if (!checkPermissionRequest(Permission.WRITE_STORAGE, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, generateRequestCodes().get("WRITE_STORAGE_REQUEST"));
                    }
                    break;

                case READ_SMS:
                    if (!checkPermissionRequest(Permission.READ_SMS, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, generateRequestCodes().get("READ_SMS_REQUEST"));
                    }
                    break;
                case WRITE_SMS:
                    if (!checkPermissionRequest(Permission.WRITE_SMS, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, generateRequestCodes().get("WRITE_SMS_REQUEST"));
                    }
                    break;

                case CAMERA:
                    if (!checkPermissionRequest(Permission.CAMERA, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, generateRequestCodes().get("CAMERA_REQUEST"));
                    }
                    break;
                case RECORD:
                    if (!checkPermissionRequest(Permission.RECORD, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, generateRequestCodes().get("RECORD_REQUEST"));
                    }
                    break;
                case READ_CALENDAR:
                    if (!checkPermissionRequest(Permission.READ_CALENDAR, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, generateRequestCodes().get("READ_CALENDAR_REQUEST"));
                    }
                    break;
                case WRITE_CALENDAR:
                    if (!checkPermissionRequest(Permission.WRITE_CALENDAR, activity)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, generateRequestCodes().get("WRITE_CALENDAR_REQUEST"));
                    }
                    break;
            }
        }
    }

    public static HashMap<String, Integer> generateRequestCodes() {
        HashMap<String, Integer> hashMap = new HashMap<>();

        //permissions
        hashMap.put("COARSE_LOCATION_REQUEST", 24000);
        hashMap.put("LOCATION_REQUEST", 24001);
        hashMap.put("WRITE_SMS_REQUEST", 24002);
        hashMap.put("READ_SMS_REQUEST", 24003);
        hashMap.put("READ_STORAGE_REQUEST", 24004);
        hashMap.put("WRITE_STORAGE_REQUEST", 24005);
        hashMap.put("CAMERA_REQUEST", 24006);
        hashMap.put("RECORD_REQUEST", 24007);
        hashMap.put("READ_CALENDAR_REQUEST", 24008);
        hashMap.put("WRITE_CALENDAR_REQUEST", 24009);

        //profile
        hashMap.put("CHANGE_MOBILE_NUMBER_REQUEST", 2000);
        hashMap.put("USER_REGISTER", 2001);
        hashMap.put("EDIT_ADDRESS", 2002);

        //snap from camera
        hashMap.put("SNAP_FROM_CAMERA", 20001);
        hashMap.put("VIDEO_FROM_CAMERA", 20002);

        //pick media from gallery
        hashMap.put("MEDIA_FROM_GALLERY", 19001);

        //google
        hashMap.put("OPEN_PLACES_SEARCH", 4001);
        hashMap.put("GOOGLE_SIGN_IN", 4002);
        hashMap.put("ACCESS_GOOGLE_LOCATION", 4003);


        //image
        hashMap.put("IMAGE_CROP",1101);


        return hashMap;
    }
}
