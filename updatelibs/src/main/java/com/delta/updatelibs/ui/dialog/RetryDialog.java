package com.delta.updatelibs.ui.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.delta.updatelibs.R;
import com.delta.updatelibs.UpdateUtils;
import com.delta.updatelibs.entity.Update;

/**
 * Created by Shufeng.Wu on 2017/4/11.
 */

public class RetryDialog extends AppCompatActivity implements View.OnClickListener{
    private Button retry;
    private Button cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retry_dialog);

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
        p.width = (int) (width); // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);

        retry = (Button)this.findViewById(R.id.ok);
        retry.setOnClickListener(this);
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ok){
            UpdateUtils.checkUpdateInfoOK();
            this.finish();
        }else if(v.getId()==R.id.cancel){
            this.finish();
        }
    }
}
