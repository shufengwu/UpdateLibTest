package com.delta.updatelibs;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.delta.updatelibs.base.UpdateBaseApplication;
import com.delta.updatelibs.entity.Download;
import com.delta.updatelibs.entity.Update;
import com.delta.updatelibs.ui.update.DownloadService;
import com.delta.updatelibs.utils.PkgInfoUtils;
import com.delta.updatelibs.utils.StringUtils;
import com.google.gson.Gson;

import javax.inject.Inject;

import rx.Observer;

/**
 * Created by Shufeng.Wu on 2017/3/15.
 */

public class Info {

    private static String downloadStr = null;
    private static ProgressDialog progressDialog = null;
    private static LocalBroadcastManager bManager;
    private static AlertDialog retryAlertDialog = null;
    private static Context mContext;
    private static String authority;

    public Info(){
    }

    public static Update getUpdateInfoAtStart(UpdateBaseApplication application){

        Gson gson = new Gson();
        String jsonStr = gson.toJson(application.getUpdateInfoAtStart());
        Update update = gson.fromJson(jsonStr,Update.class);
        return update;
    }

    public static Update getUpdateInfo(UpdateBaseApplication application){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(application.checkUpdateInfo());
        Update update = gson.fromJson(jsonStr,Update.class);
        return update;
    }

    public static void checkUpdateInfo(final Context context, UpdateBaseApplication application, String authority, int label){
        mContext = context;
        authority = authority;
        Update update = null;
        try{
            if(label==0){
                update = getUpdateInfoAtStart(application);
            }else{
                update = getUpdateInfo(application);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        try{
            if("网络请求失败！".equals(update.getVersion())){
                if(label==1){
                    Toast.makeText(context, "网络请求失败！", Toast.LENGTH_SHORT).show();
                }
            }else{
                if(Integer.parseInt(update.getVersionCode())> PkgInfoUtils.getVersionCode(context)){
                    final Update finalUpdate = update;
                    final String finalAuthority = authority;
                    new AlertDialog.Builder(context)
                            .setTitle("发现新版本 " + update.getVersion())
                            .setMessage(update.getDescription())
                            .setCancelable(false)
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                            downloadStr = finalUpdate.getUrl();
                            //显示ProgerssDialog
                            showProgerssDialog(context);
                            download(context, downloadStr, finalAuthority);
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DownloadService.isUpdating = true;
                                    dialogInterface.dismiss();
                                }
                            })
                            .create()
                            .show();
                }else{
                    if(label==0){

                    }else if(label==1){
                        Toast.makeText(context, "未发现新版本！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //显示更新ProgerssDialog
    private static void showProgerssDialog(Context context) {
        registerReceiver(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("更新");
        progressDialog.setIcon(android.R.drawable.ic_dialog_info);
        progressDialog.setMessage("正在下载更新...");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private static void registerReceiver(Context context) {

        bManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MESSAGE_PROGRESS);
        intentFilter.addAction(Constant.MESSAGE_DIALOG_DISMISS);
        intentFilter.addAction(Constant.MESSAGE_FAILED);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    //更新状态
    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            if (intent.getAction().equals(Constant.MESSAGE_PROGRESS)) {

                Download download = intent.getParcelableExtra("download");
                int progress = download.getProgress();
                if (download.getProgress() == 100) {

                    progressDialog.setMessage("下载成功");
                    progressDialog.setProgress(progress);
                    progressDialog.setProgressNumberFormat(
                            StringUtils.getDataSize(download.getCurrentFileSize())
                                    + "/" +
                                    StringUtils.getDataSize(download.getTotalFileSize()));


                } else {
                    progressDialog.setProgress(progress);
                    progressDialog.setProgressNumberFormat(
                            StringUtils.getDataSize(download.getCurrentFileSize())
                                    + "/" +
                                    StringUtils.getDataSize(download.getTotalFileSize()));

                }
            } else if (intent.getAction().equals(Constant.MESSAGE_DIALOG_DISMISS)) {
                progressDialog.dismiss();
            } else if (intent.getAction().equals(Constant.MESSAGE_FAILED)) {
                progressDialog.setMessage("下载失败");
                progressDialog.setCancelable(true);
                if (retryAlertDialog == null) {
                    retryAlertDialog = new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("下载失败，请重试或取消更新！")
                            .setCancelable(false)
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.dismiss();
                                    //显示ProgerssDialog
                                    showProgerssDialog(mContext);
                                    download(mContext, downloadStr,authority);
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.dismiss();
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                }
                if (!retryAlertDialog.isShowing()) {
                    retryAlertDialog.show();
                }
            }
        }
    };

    public static void download(Context context, String urlStr,String authority){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("urlStr", urlStr);
        intent.putExtra("authority",authority);
        context.startService(intent);
    }

}
