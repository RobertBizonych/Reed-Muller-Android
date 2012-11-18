package com.uzhnu.reedmuller;

import android.app.Activity;
import android.app.ActivityManager;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
    private int mTitleRes;
    private Toast toast;
    private long lastBackPressTime = 0;
    private static final long DELAY = 4000;

    public BaseActivity(int titleRes) {
        mTitleRes = titleRes;
    }

    @Override
    public void setContentView(int layoutResID){
       super.setContentView(layoutResID);
        TextView title = (TextView) findViewById(R.id.textViewTitleBar);
        title.setText(mTitleRes);
    }

    @Override
    public void onBackPressed() {
        if(readyToExit()){
            onDoubleBackPress();
        }else{
            super.onBackPressed();
        }
    }

    private boolean readyToExit() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo taskInfo = manager.getRunningTasks(1).get(0);

        return (taskInfo.numRunning == 1 && taskInfo.numActivities == 1);
    }

    private void onDoubleBackPress() {
        if (this.lastBackPressTime < System.currentTimeMillis() - DELAY) {
            toast = Toast.makeText(this, getString(R.string.prompt_close), (int) DELAY);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
        }
    }
}