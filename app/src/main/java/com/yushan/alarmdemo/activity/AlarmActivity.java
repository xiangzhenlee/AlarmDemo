package com.yushan.alarmdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yushan.alarmdemo.adapter.AlarmListAdapter;
import com.yushan.alarmdemo.R;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_current_time;
    private ListView lv_alarm;
    private ImageView iv_add_alarm;
    private int count;
    private String saveTime = "";
    private Handler mHandler;
    private Runnable mRunnable;
    private ArrayList<Map<String, Object>> alarmData;
    private AlarmListAdapter alarmListAdapter;
    public static final String RETURN_ALARM_SETTING = "alarm_setting";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); // 得到的Action
            if (action.equals(RETURN_ALARM_SETTING)) {
                getAndSetReturnDate(intent);
            } else {
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initView();
        initData();

        refreshTime();

        IntentFilter returnFilter = new IntentFilter(RETURN_ALARM_SETTING);
        registerReceiver(broadcastReceiver, returnFilter);
    }

    private void initView() {
        tv_current_time = (TextView) findViewById(R.id.tv_current_time);
        lv_alarm = (ListView) findViewById(R.id.lv_alarm);
        iv_add_alarm = (ImageView) findViewById(R.id.iv_add_alarm);
        iv_add_alarm.setOnClickListener(this);
    }

    private void initData() {

        alarmData = new ArrayList<>();
        Map<String, Object> firstAlarm = new HashMap<>();
        firstAlarm.put("table", "起床");
        firstAlarm.put("time", "07:00");
        firstAlarm.put("repeat", "只响一次");
        firstAlarm.put("state", "open");

        Map<String, Object> secondAlarm = new HashMap<>();
        secondAlarm.put("table", "运动");
        secondAlarm.put("time", "07:30");
        secondAlarm.put("repeat", "每天");
        secondAlarm.put("state", "close");

        Map<String, Object> thirdAlarm = new HashMap<>();
        thirdAlarm.put("table", "开会");
        thirdAlarm.put("time", "08:30");
        thirdAlarm.put("repeat", "周一至周五");
        thirdAlarm.put("state", "open");

        Map<String, Object> forthAlarm = new HashMap<>();
        forthAlarm.put("table", "测血压");
        forthAlarm.put("time", "08:30");
        forthAlarm.put("repeat", "周六和周日");
        forthAlarm.put("state", "open");

        alarmData.add(firstAlarm);
        alarmData.add(secondAlarm);
        alarmData.add(thirdAlarm);
        alarmData.add(forthAlarm);

        setAlarmData(alarmData);
    }

    private void getAndSetReturnDate(Intent intent){
        String table = intent.getStringExtra("table");
        String time = intent.getStringExtra("time");
        String repeat = intent.getStringExtra("repeat");
        String state = intent.getStringExtra("state");
        String position = intent.getStringExtra("position");

        if (!"".equals(position) && position != null){
            Map<String, Object> setAlarm = new HashMap<>();
            setAlarm.put("table", table);
            setAlarm.put("time", time);
            setAlarm.put("repeat", repeat);
            setAlarm.put("state", state);

            alarmData.set(Integer.parseInt(position),setAlarm);
            alarmListAdapter.refresh(alarmData);
        }
    }

    private void setAlarmData(ArrayList<Map<String, Object>> alarmData) {
        alarmListAdapter = new AlarmListAdapter(this, alarmData);
        lv_alarm.setAdapter(alarmListAdapter);
    }

    private void refreshTime() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                tv_current_time.setText(saveTime);
            }
        };

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getLocalTime();
            }
        };

        //delay为long,period为long：从现在起过delay毫秒以后，每隔period毫秒执行一次。
        timer.schedule(task, 0, 1000);
    }

    private void getLocalTime() {
        URL url = null;//取得资源对象
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String format = formatter.format(calendar.getTime());
            String[] dateArray = format.split(" ");
            if (dateArray.length > 1) {

                if (!saveTime.equals(dateArray[1])) {
                    saveTime = dateArray[1];
                    new Thread() {
                        public void run() {
                            mHandler.post(mRunnable);
                        }
                    }.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_alarm:
                Intent intent = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
                startActivityForResult(intent, 1111);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (resultCode == 9) {
                String table = data.getStringExtra("table");
                String time = data.getStringExtra("time");
                String repeat = data.getStringExtra("repeat");
                String state = data.getStringExtra("state");

                Map<String, Object> returnAlarm = new HashMap<>();
                returnAlarm.put("table", table);
                returnAlarm.put("time", time);
                returnAlarm.put("repeat", repeat);
                returnAlarm.put("state", state);

                alarmData.add(returnAlarm);
                alarmListAdapter.refresh(alarmData);

                if (alarmData.size() >= 10) {
                    iv_add_alarm.setVisibility(View.GONE);
                } else {
                    iv_add_alarm.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
