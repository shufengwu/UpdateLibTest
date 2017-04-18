package com.delta.updatelibs.updatelibtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.delta.updatelibs.Info;
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
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            //Info.checkUpdateInfo(this, (MyApp)getApplication(),"delta.base_update_lib.fileprovider",0);
            ((MyApp)getApplication()).check("delta.base_update_lib.fileprovider",0);
        }
        check = (Button)findViewById(R.id.checkUpdate);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Info.checkUpdateInfo(MainActivity.this, (MyApp)getApplication(),"delta.base_update_lib.fileprovider",1);
                ((MyApp)getApplication()).check("delta.base_update_lib.fileprovider",1);
            }
        });
    }

    //运行时权限请求回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                //如果权限请求通过
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Info.checkUpdateInfo(this, (MyApp)getApplication(),"delta.base_update_lib.fileprovider",0);
                    ((MyApp)getApplication()).check("delta.base_update_lib.fileprovider",0);
                    //如果权限请求不通过
                } else {
                }
            }
        }
    }
}
