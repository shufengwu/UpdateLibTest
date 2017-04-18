package com.delta.updatelibs.updatelibtest.app;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.delta.updatelibs.UpdateUtils;
import com.delta.updatelibs.base.UpdateBaseApplication;
import com.delta.updatelibs.entity.Update;
import com.delta.updatelibs.ui.dialog.ExistUpdateDialog;
import com.delta.updatelibs.ui.update.DownloadService;

/**
 * Created by Shufeng.Wu on 2017/3/15.
 */

public class MyApp extends /*UpdateBaseApplication */Application{

    AlertDialog dialog;

    @Override
    public void onCreate() {
        super.onCreate();
        UpdateUtils.init();
    }

    public void check(String authority,int label){
        UpdateUtils.checkUpdateInfo(this,authority,label);
    }
}
