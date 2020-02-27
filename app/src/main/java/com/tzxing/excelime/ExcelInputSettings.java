package com.tzxing.excelime;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.appcompat.app.AppCompatActivity;

public class ExcelInputSettings extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTitle(R.string.settings_name);
        setContentView(R.layout.ime_settings);

        String permission = Manifest.permission.RECORD_AUDIO;
        //检查权限是否已授权
        int hasPermission = checkSelfPermission(permission);

        //如果没有授权
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            //请求权限，此方法会弹出权限请求对话框，让用户授权，并回调 onRequestPermissionsResult 来告知授权结果
            requestPermissions(new String[]{permission},1);
        }else {//已经授权过
            //做一些你想做的事情，即原来不需要动态授权时做的操作

        }

    }

}
