package com.yushan.alarmdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yushan.alarmdemo.R;
import com.yushan.alarmdemo.utils.Number2Text;
import com.yushan.alarmdemo.widget.LabelDialog;
import com.yushan.alarmdemo.widget.RepeatDialog;
import com.yushan.alarmdemo.widget.wheel.AbstractWheelTextAdapter;
import com.yushan.alarmdemo.widget.wheel.WheelView;

import java.util.HashMap;
import java.util.Map;

public class AlarmSettingActivity extends AppCompatActivity implements View.OnClickListener, LabelDialog.SelectLabelInfo, RepeatDialog.SelectRepeatInfo {

    private WheelView wv_hour;
    private WheelView wv_minute;

    protected int TOP_COLOR = 0xefFFFFFF;
    protected int CENTER_COLOR = 0xcfFFFFFF;
    protected int BOTTOM_CENTER = 0x3fFFFFFF;
    private HourAdapter mHourAdapter;
    private MinuteAdapter mMinuteAdapter;
    private String[] hour;
    private String[] minute;
    private TextView tv_right;
    private ImageView back;
    private RelativeLayout rl_select_label;
    private RelativeLayout rl_select_repeat;
    private int Swidth;
    private int Sheight;
    private TextView tv_label;
    private int mLabelIndex;
    private TextView tv_repeat;
    private int mRepeatIndex;
    private String mLabelText = "普通";
    private String mRepeatText = "只响一次";
    private TextView tv_state;
    private String action;
    private String position;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        initView();
        initData();
    }

    private void initView() {
        wv_hour = (WheelView) findViewById(R.id.wv_hour);
        wv_hour.setShadowColor(TOP_COLOR, CENTER_COLOR, BOTTOM_CENTER);
        wv_minute = (WheelView) findViewById(R.id.wv_minute);
        wv_minute.setShadowColor(TOP_COLOR, CENTER_COLOR, BOTTOM_CENTER);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("确定");
        tv_right.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        rl_select_label = (RelativeLayout) findViewById(R.id.rl_select_label);
        rl_select_label.setOnClickListener(this);
        rl_select_repeat = (RelativeLayout) findViewById(R.id.rl_select_repeat);
        rl_select_repeat.setOnClickListener(this);
        tv_label = (TextView) findViewById(R.id.tv_label);
        tv_repeat = (TextView) findViewById(R.id.tv_repeat);
        tv_state = (TextView) findViewById(R.id.tv_state);
    }

    private void initData() {
        getWheelContent(AlarmSettingActivity.this, R.array.alarm_hour_time_range, R.array.alarm_minute_time_range);

        mHourAdapter = new HourAdapter();
        wv_hour.setViewAdapter(mHourAdapter);
        wv_hour.setCurrentItem(0);
        wv_hour.setDrawLineBorder(false);
        mMinuteAdapter = new MinuteAdapter();
        wv_minute.setViewAdapter(mMinuteAdapter);
        wv_minute.setCurrentItem(0);
        wv_minute.setDrawLineBorder(false);

        //获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Swidth = dm.widthPixels;
        Sheight = dm.heightPixels;

        String table = getIntent().getStringExtra("table");
        String time = getIntent().getStringExtra("time");
        String repeat = getIntent().getStringExtra("repeat");
        state = getIntent().getStringExtra("state");
        action = getIntent().getStringExtra("action");
        position = getIntent().getStringExtra("position");

        if (!"".equals(table) && table != null) {
            tv_label.setText(table);
            mLabelText = table;
        }

        if (!"".equals(repeat) && repeat != null) {
            tv_repeat.setText(repeat);
            mRepeatText = repeat;
        }

        if (!"".equals(state) && state != null) {
            if ("open".equals(state)) {
                tv_state.setText("已开启");
            } else {
                tv_state.setText("未开启");
            }
        }

        if (!"".equals(time) && time != null) {
            String[] timeArray = time.split(":");
            wv_hour.setCurrentItem(Integer.parseInt(timeArray[0]));
            wv_minute.setCurrentItem(Integer.parseInt(timeArray[1]));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:

                int hourIndex = wv_hour.getCurrentItem();
                int minuteIndex = wv_minute.getCurrentItem();

                String hour = mHourAdapter.getItemText(hourIndex) + "";
                String minute = mMinuteAdapter.getItemText(minuteIndex) + "";

                Intent mIntent = new Intent();
                mIntent.putExtra("table", mLabelText);
                mIntent.putExtra("time", hour + ":" + minute);
                mIntent.putExtra("repeat", mRepeatText);

                if ("setting".equals(action)) {
                    mIntent.putExtra("state", state);
                    mIntent.putExtra("position", position);
                    mIntent.setAction(AlarmActivity.RETURN_ALARM_SETTING);
                    sendBroadcast(mIntent);
                } else {
                    mIntent.putExtra("state", "open");
                    this.setResult(9, mIntent);
                }
                this.finish();

                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.rl_select_label:
                LabelDialog dialog_label = new LabelDialog(AlarmSettingActivity.this, Swidth, Sheight, mLabelIndex);
                dialog_label.selectLabelInfo(AlarmSettingActivity.this);
                Window windowLabel = dialog_label.getWindow();
                windowLabel.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); // 此处可以设置dialog显示的位置
                windowLabel.setWindowAnimations(R.style.AnimBottom);
                dialog_label.show(mLabelIndex);
                break;
            case R.id.rl_select_repeat:
                RepeatDialog dialog_repeat = new RepeatDialog(AlarmSettingActivity.this, Swidth, Sheight, "自定义");
                dialog_repeat.selectRepeatInfo(AlarmSettingActivity.this);
                Window windowRepeat = dialog_repeat.getWindow();
                windowRepeat.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); // 此处可以设置dialog显示的位置
                windowRepeat.setWindowAnimations(R.style.AnimBottom);
                dialog_repeat.showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void selectLabel(String labelText, int labelIndex) {
        mLabelText = labelText;
        mLabelIndex = labelIndex;

        tv_label.setText(mLabelText);
        Toast.makeText(this, labelText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void selectRepeat(String repeatText, int repeatIndex) {

        if ("自定义".equals(repeatText)) {
            RepeatDialog dialog_repeat = new RepeatDialog(AlarmSettingActivity.this, Swidth, Sheight, mRepeatText);
            dialog_repeat.selectRepeatInfo(AlarmSettingActivity.this);
            Window windowRepeat = dialog_repeat.getWindow();
            windowRepeat.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); // 此处可以设置dialog显示的位置
            windowRepeat.setWindowAnimations(R.style.AnimBottom);
            dialog_repeat.showDialog();
        } else {

            if ("未选择".equals(repeatText)){
                return;
            }
            mRepeatText = repeatText;
            mRepeatIndex = repeatIndex;

            String[] repeatArray = repeatText.split(":");
            String repeat = "";
            if ("a".equals(repeatArray[0])) {
                for (int i = 1; i < repeatArray.length; i++) {
                    String num = Number2Text.formatInteger(Integer.parseInt(repeatArray[i]) + 1);
                    switch (repeatArray[i]) {
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                            if (i == repeatArray.length - 1) {
                                repeat = repeat + "周" + num;
                            } else {
                                repeat = repeat + "周" + num + " ";
                            }
                            break;
                        case "6":
                            repeat = repeat + "周日";
                            break;
                    }
                }

                switch (repeat) {
                    case "周一 周二 周三 周四 周五":
                        mRepeatText = "周一至周五";
                        break;
                    case "周一 周二 周三 周四 周五 周六 周日":
                        mRepeatText = "每天";
                        break;
                    case "周六 周日":
                        mRepeatText = "周六和周日";
                        break;
                    default:
                        mRepeatText = repeat;
                        break;
                }
            }

            tv_repeat.setText(mRepeatText);

        }




        Toast.makeText(this, "" + repeatIndex, Toast.LENGTH_SHORT).show();
    }

    class HourAdapter extends AbstractWheelTextAdapter {
        public HourAdapter() {
            super(AlarmSettingActivity.this, R.layout.item_wheel_text_height);
            setItemTextResource(R.id.question_wheel_textView);
        }

        @Override
        public int getItemsCount() {
            return hour != null ? hour.length : 0;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return hour[index];
        }
    }

    class MinuteAdapter extends AbstractWheelTextAdapter {
        public MinuteAdapter() {
            super(AlarmSettingActivity.this, R.layout.item_wheel_text_height);
            setItemTextResource(R.id.question_wheel_textView);
        }

        @Override
        public int getItemsCount() {
            return minute != null ? minute.length : 0;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return minute[index];
        }
    }

    /**
     * 根据resId，为不同的修改框填充相应的数据
     *
     * @param context
     */
    public void getWheelContent(Context context, int hourResId, int minuteResId) {
        if (hourResId != -1) {
            hour = context.getResources().getStringArray(hourResId);
        } else {
            wv_hour.setVisibility(View.GONE);
        }
        if (minuteResId != -1) {
            minute = context.getResources().getStringArray(minuteResId);
        } else {
            wv_minute.setVisibility(View.GONE);
        }
    }
}
