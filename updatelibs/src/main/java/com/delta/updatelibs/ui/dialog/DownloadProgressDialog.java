package com.delta.updatelibs.ui.dialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delta.updatelibs.Constant;
import com.delta.updatelibs.R;
import com.delta.updatelibs.UpdateUtils;
import com.delta.updatelibs.entity.Download;
import com.delta.updatelibs.ui.update.DownloadService;
import com.delta.updatelibs.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.List;


/**
 * Created by Shufeng.Wu on 2017/4/11.
 */

public class DownloadProgressDialog extends AppCompatActivity{

    TextView updateLoadStatus;
    ProgressBar progressBar;
    TextView showPercent;
    TextView showSize;

    private static LocalBroadcastManager bManager;

    boolean back_enabled_status = false;

    private static AlertDialog retryAlertDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_progress_dialog);
        back_enabled_status = true;
        this.setFinishOnTouchOutside(false);

        WindowManager m = getWindowManager();

        // 为获取屏幕宽、高
        Display d = m.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        int width = size.x;
        int height = size.y;

        //设置Dialog宽高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        //p.height = (int) (height * 0.5); // 高度设置为屏幕的0.3
        p.width = width; // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);

        updateLoadStatus = (TextView)this.findViewById(R.id.update_load_status);
        progressBar = (ProgressBar)findViewById(R.id.update_load_progress);
        showPercent = (TextView)findViewById(R.id.show_percent);
        showSize = (TextView)findViewById(R.id.show_download_size);
        registerReceiver(this);
    }

    private void registerReceiver(Context context) {

        bManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MESSAGE_PROGRESS);
        intentFilter.addAction(Constant.MESSAGE_DIALOG_DISMISS);
        intentFilter.addAction(Constant.MESSAGE_FAILED);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    //更新状态
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            if (intent.getAction().equals(Constant.MESSAGE_PROGRESS)) {

                Download download = intent.getParcelableExtra("download");
                int progress = download.getProgress();
                if (download.getProgress() == 100) {

                    updateLoadStatus.setText("下载成功");
                    progressBar.setProgress(progress);
                    DecimalFormat df   =new DecimalFormat("#.0");
                    String p = df.format((double)progressBar.getProgress()*100/progressBar.getMax());
                    showPercent.setText(p+"%");
                    showSize.setText(
                            StringUtils.getDataSize(download.getCurrentFileSize())
                                    + "/" +
                                    StringUtils.getDataSize(download.getTotalFileSize()));


                } else {
                    progressBar.setProgress(progress);
                    DecimalFormat df   =new DecimalFormat("#.0");
                    String p = df.format((double)progressBar.getProgress()*100/progressBar.getMax());
                    showPercent.setText(p+"%");
                    showSize.setText(
                            StringUtils.getDataSize(download.getCurrentFileSize())
                                    + "/" +
                                    StringUtils.getDataSize(download.getTotalFileSize()));

                }
            } else if (intent.getAction().equals(Constant.MESSAGE_DIALOG_DISMISS)) {
                DownloadProgressDialog.this.finish();
            } else if (intent.getAction().equals(Constant.MESSAGE_FAILED)) {
                updateLoadStatus.setText("下载失败");
                DownloadProgressDialog.this.setFinishOnTouchOutside(true);
                back_enabled_status =false;

                Intent intent_retry= new Intent(getApplicationContext(),RetryDialog.class);
                startActivity(intent_retry);
                DownloadProgressDialog.this.finish();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            if(back_enabled_status){
                return true;
            }
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {
            //监控/拦截菜单键
            if(back_enabled_status){
                return true;
            }
        } else if(keyCode == KeyEvent.KEYCODE_HOME) {
            //由于Home键为系统键，此处不能捕获，需要重写onAttachedToWindow()
        }
        return super.onKeyDown(keyCode, event);
    }

    public static boolean isTop(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
        if (appTask.size() > 0 && appTask.get(0).topActivity.equals(intent.getComponent())) {
            return true;
        } else {
            return false;
        }
    }


}