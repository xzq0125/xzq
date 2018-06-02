package com.xiezhenqi.base.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.xiezhenqi.permission.MPermission;
import com.xiezhenqi.permission.annotation.OnMPermissionDenied;
import com.xiezhenqi.permission.annotation.OnMPermissionGranted;
import com.xiezhenqi.permission.util.PermissionRequestCode;
import com.xiezhenqi.permission.util.RequestUtil;
import com.xiezhenqi.utils.LogUtils;
import com.xiezhenqi.utils.ToastUtils;

import java.util.List;

/**
 * Android 6.0(Marshmallow-棉花糖) 以上动态权限基类
 * 危险权限需要动态授权
 * 也就是运行时提示用户授权
 * 只有用户给予授权后才能进行一些跟权限相关的操作
 * Created by Wesley on 2018/6/2.
 */
@SuppressWarnings("all")
public class MPermissionActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private String[] permissions = null;

    public void needPermission(final int requestCode, final String... permissions) {
        if (permissions == null || permissions.length == 0)
            return;
        this.permissions = permissions;
        MPermission.needPermission(this, requestCode, permissions);
    }

    public void needPermission(final int requestCode, final List<String> permissionsList) {
        if (permissionsList == null || permissionsList.isEmpty())
            return;
        needPermission(requestCode, permissionsList.toArray(new String[permissionsList.size()]));
    }

    //请求日历权限
    public void needCalendarPermission() {
        needPermission(PermissionRequestCode.CALENDAR, Manifest.permission.WRITE_CALENDAR);
    }

    //请求相机权限
    public void needCameraPermission() {
        needPermission(PermissionRequestCode.CAMERA, Manifest.permission.CAMERA);
    }

    //请求通讯录权限
    public void needContactsPermission() {
        needPermission(PermissionRequestCode.CONTACTS, Manifest.permission.WRITE_CONTACTS);
    }

    //请求定位权限
    public void needLocationPermission() {
        needPermission(PermissionRequestCode.LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    //请求麦克风权限
    public void needMicroPermission() {
        needPermission(PermissionRequestCode.MICRO, Manifest.permission.RECORD_AUDIO);
    }

    //请求电话权限
    public void needPhonePermission() {
        needPermission(PermissionRequestCode.PHONE, Manifest.permission.CALL_PHONE);
    }

    //请求传感器权限
    public void needSensorsPermission() {
        needPermission(PermissionRequestCode.SENSORS, Manifest.permission.BODY_SENSORS);
    }

    //请求短信权限
    public void needSmsPermission() {
        needPermission(PermissionRequestCode.SMS, Manifest.permission.SEND_SMS);
    }

    //请求存储权限
    public void needStoragePermission() {
        needPermission(PermissionRequestCode.STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 请求授权失败
     *
     * @param requestCode 请求码
     */
    @OnMPermissionDenied()
    protected void requestPermissionDenied(final int requestCode) {
        LogUtils.debug("XZQ", "授权拒绝，请求码 requestCode = " + requestCode);
        final String title = RequestUtil.getDeniedPermissionsTitle(this, permissions);
        final String message = RequestUtil.getDeniedPermissionsDefaultMessage(this, permissions);
        showPermissionAlertDialog(title, message);
    }

    /**
     * 请求授权成功
     *
     * @param requestCode 请求码
     */
    @OnMPermissionGranted({
            PermissionRequestCode.CALENDAR,
            PermissionRequestCode.CAMERA,
            PermissionRequestCode.CONTACTS,
            PermissionRequestCode.LOCATION,
            PermissionRequestCode.MICRO,
            PermissionRequestCode.PHONE,
            PermissionRequestCode.SENSORS,
            PermissionRequestCode.SMS,
            PermissionRequestCode.STORAGE
    })
    protected void requestPermissionSuccess(final int requestCode) {
        LogUtils.debug("XZQ", "授权成功，请求码 requestCode = " + requestCode);
        String msg = null;
        if (requestCode == PermissionRequestCode.CALENDAR) {
            msg = "日历权限申请成功";
        } else if (requestCode == PermissionRequestCode.CAMERA) {
            msg = "相机权限申请成功";
        } else if (requestCode == PermissionRequestCode.CONTACTS) {
            msg = "通讯录权限申请成功";
        } else if (requestCode == PermissionRequestCode.LOCATION) {
            msg = "定位权限申请成功";
        } else if (requestCode == PermissionRequestCode.MICRO) {
            msg = "麦克风权限申请成功";
        } else if (requestCode == PermissionRequestCode.PHONE) {
            msg = "电话权限申请成功";
        } else if (requestCode == PermissionRequestCode.SENSORS) {
            msg = "身体传感器权限申请成功";
        } else if (requestCode == PermissionRequestCode.SMS) {
            msg = "短信权限申请成功";
        } else if (requestCode == PermissionRequestCode.STORAGE) {
            msg = "存储权限申请成功";
        }
        ToastUtils.showToast(msg);
    }

    protected void onCalendarPermissionGot() {
    }

    protected void onCameraPermissionGot() {
    }

    protected void onContactsPermissionGot() {
    }

    protected void onLocationPermissionGot() {
    }

    protected void onMicroPermissionGot() {
    }

    protected void onPhonePermissionGot() {
    }

    protected void onSensorsPermissionGot() {
    }

    protected void onSmsPermissionGot() {
    }

    protected void onStoragePermissionGot() {
    }

    private AlertDialog mPermissionAlertDialog = null;

    private void showPermissionAlertDialog(String title, String message) {
        if (mPermissionAlertDialog == null) {
            mPermissionAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionAlertDialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            onPermissionDialogPositiveClick();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionAlertDialog.dismiss();
                            onPermissionDialogNegativeClick();
                        }
                    })
                    .show();
        } else {
            mPermissionAlertDialog.setTitle(title);
            mPermissionAlertDialog.setMessage(message);
        }
        if (!mPermissionAlertDialog.isShowing())
            mPermissionAlertDialog.show();
    }

    protected void onPermissionDialogPositiveClick() {
    }

    protected void onPermissionDialogNegativeClick() {
    }

}