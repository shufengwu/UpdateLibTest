package com.delta.updatelibs;

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
import com.delta.updatelibs.service.UpdateService;
import com.delta.updatelibs.ui.dialog.DownloadProgressDialog;
import com.delta.updatelibs.ui.dialog.ExistUpdateDialog;
import com.delta.updatelibs.ui.dialog.NetFailedDialog;
import com.delta.updatelibs.ui.update.DownloadService;
import com.delta.updatelibs.utils.PkgInfoUtils;
import com.delta.updatelibs.utils.StringUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shufeng.Wu on 2017/4/10.
 */

public class UpdateUtils {

    private static String downloadStr = null;
    private static Context mContext;

    private static final int TOME_OUT = 10;
    private static Retrofit retrofitUpdateInfo;
    private static UpdateService updateInfoService;
    private static final Map<String, String> mapUpdateInfo = new HashMap<>();

    static Update update = null;
    static String finalAuthority;

    public static final String TAG = "UpdateUtils";

    public static void init(){
        retrofitUpdateInfo = new Retrofit.Builder()
                .baseUrl(Constant.BASE_UPDATE_URL)//域名
                .client(new OkHttpClient.Builder()
                        .connectTimeout(TOME_OUT, TimeUnit.SECONDS)
                        .readTimeout(TOME_OUT, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .build();
        updateInfoService = retrofitUpdateInfo.create(UpdateService.class);
        final retrofit2.Call<Update> call = updateInfoService.getUpdate1();

        new Thread() {
            @Override
            public void run() {
                try {
                    Response<Update> response = call.execute(); // 同步
                    Update update = response.body();
                    mapUpdateInfo.put("version", update.getVersion());
                    mapUpdateInfo.put("versionCode", update.getVersionCode());
                    mapUpdateInfo.put("description", update.getDescription());
                    mapUpdateInfo.put("url", update.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                    mapUpdateInfo.put("version", "网络请求失败！");
                    mapUpdateInfo.put("versionCode", "0");
                    mapUpdateInfo.put("description", "");
                    mapUpdateInfo.put("url", "");
                }
            }
        }.start();
    }

    /*public static Map<String, String> getUpdateInfoMapAtStart(Context mContext, String authority, int label) {
        return mapUpdateInfo;
    }*/

    public static void checkUpdateInfoMap(final Context mContext, final String authority, final int label) {
        retrofitUpdateInfo = new Retrofit.Builder()
                .baseUrl(Constant.BASE_UPDATE_URL)//域名
                .client(new OkHttpClient.Builder()
                        .connectTimeout(TOME_OUT, TimeUnit.SECONDS)
                        .readTimeout(TOME_OUT, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .build();
        updateInfoService = retrofitUpdateInfo.create(UpdateService.class);
        final retrofit2.Call<Update> call = updateInfoService.getUpdate1();

        new Thread() {
            @Override
            public void run() {
                try {
                    Response<Update> response = call.execute(); // 同步
                    Update update = response.body();
                    mapUpdateInfo.put("version", update.getVersion());
                    mapUpdateInfo.put("versionCode", update.getVersionCode());
                    mapUpdateInfo.put("description", update.getDescription());
                    mapUpdateInfo.put("url", update.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                    mapUpdateInfo.put("version", "网络请求失败！");
                    mapUpdateInfo.put("versionCode", "0");
                    mapUpdateInfo.put("description", "");
                    mapUpdateInfo.put("url", "");
                }

                getUpdateInfo(mContext, authority, label);
            }
        }.start();

        /*return mapUpdateInfo;*/
    }

    public static void getUpdateInfoAtStart(Context mContext, String authority, int label){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(mapUpdateInfo);
        update = gson.fromJson(jsonStr,Update.class);
        openCheckDialog(mContext, authority, label);
    }

    public static void getUpdateInfo(Context mContext, String authority, int label){

        Gson gson = new Gson();
        String jsonStr = gson.toJson(mapUpdateInfo);
        update = gson.fromJson(jsonStr,Update.class);
        openCheckDialog(mContext, authority, label);
    }

    public static void openCheckDialog(Context mContext, String authority, int label){
        try{
            if("网络请求失败！".equals(update.getVersion())){
                if(label==1){
                    Log.i(TAG, "openCheckDialog: "+"网络请求失败！");
                    Intent intent = new Intent(mContext, NetFailedDialog.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);

                }
            }else{
                if(Integer.parseInt(update.getVersionCode())> PkgInfoUtils.getVersionCode(mContext)){
                    Update finalUpdate = update;
                    finalAuthority = authority;
                    Intent intent = new Intent(mContext, ExistUpdateDialog.class);
                    intent.putExtra("update_title","发现新版本 "+finalUpdate.getVersion());
                    intent.putExtra("update_content",finalUpdate.getDescription());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }else{
                    if(label==0){
                    }else if(label==1){
                        Toast.makeText(mContext, "未发现新版本！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void checkUpdateInfo(Context mContext, String authority, int label){
        UpdateUtils.mContext = mContext;

        try{
            if(label==0){
                getUpdateInfoAtStart(mContext, authority, label);
            }else{
                checkUpdateInfoMap(mContext, authority, label);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void checkUpdateInfoOK(){
        downloadStr = update.getUrl();
        Intent intent = new Intent(mContext, DownloadProgressDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        download(mContext, downloadStr, finalAuthority);
    }

    public static void checkUpdateInfoCancel(){
        DownloadService.isUpdating = true;
    }


    public static void download(Context context, String urlStr,String authority){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("urlStr", urlStr);
        intent.putExtra("authority",authority);
        context.startService(intent);
    }


}
