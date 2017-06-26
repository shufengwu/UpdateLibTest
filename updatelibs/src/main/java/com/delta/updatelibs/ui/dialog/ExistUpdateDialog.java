package com.delta.updatelibs.ui.dialog;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.delta.updatelibs.R;
import com.delta.updatelibs.UpdateUtils;

/**
 * Created by Shufeng.Wu on 2017/4/10.
 */

public class ExistUpdateDialog extends AppCompatActivity implements View.OnClickListener{

    TextView title;
    TextView content;
    Button cancel;
    Button ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.exist_update_dialog);

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


        title = (TextView)findViewById(R.id.update_info_title);
        content = (TextView)findViewById(R.id.update_info_details);
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(this);
        Intent intent = getIntent();
        title.setText(intent.getStringExtra("update_title"));
        content.setText(intent.getStringExtra("update_content"));
        this.setFinishOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.cancel){
            UpdateUtils.checkUpdateInfoCancel();
            finish();
        }else if(v.getId()==R.id.ok){
            UpdateUtils.checkUpdateInfoOK();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {
            //监控/拦截菜单键
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_HOME) {
            //由于Home键为系统键，此处不能捕获，需要重写onAttachedToWindow()
        }
        return super.onKeyDown(keyCode, event);
    }
}
