package com.delta.updatelibs.updatelibtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.delta.updatelibs.Info;
import com.delta.updatelibs.UpdateUtils;
import com.delta.updatelibs.updatelibtest.app.MyApp;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialog;
    Button check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //城区启动时申请WRITE_EXTERNAL_STORAGE权限，设置requestPermissions方法参数requestCode
            int requestCode = 5;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        } else {
            UpdateUtils.checkUpdateInfo(getApplication(), this.getPackageName() + ".fileprovider", 0);
        }
        check = (Button) findViewById(R.id.checkUpdate);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //手动更新时申请WRITE_EXTERNAL_STORAGE权限，设置requestPermissions方法参数requestCode
                    int requestCode = 6;
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                } else {
                    UpdateUtils.checkUpdateInfo(getApplication(), MainActivity.this.getPackageName() + ".fileprovider", 1);
                }

            }
        });

    }

    //运行时权限请求回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            //程序启动时请求WRITE_EXTERNAL_STORAGE权限对应回调
            case 5:
                //如果权限请求通过
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //程序启动时检查更新，checkUpdateInfo方法第三个参数设置为0
                    UpdateUtils.checkUpdateInfo(getApplication(), MainActivity.this.getPackageName() + ".fileprovider", 0);
                } else {
                }
                break;
            //手动检查更新时请求WRITE_EXTERNAL_STORAGE权限对应回调
            case 6:
                //如果权限请求通过
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //手动检查更新，checkUpdateInfo方法第三个参数设置为1
                    UpdateUtils.checkUpdateInfo(getApplication(), MainActivity.this.getPackageName() + ".fileprovider", 1);
                } else {
                }
                break;
        }
    }
}
