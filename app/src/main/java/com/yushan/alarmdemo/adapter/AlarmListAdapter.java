package com.yushan.alarmdemo.adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yushan.alarmdemo.R;
import com.yushan.alarmdemo.activity.AlarmActivity;
import com.yushan.alarmdemo.activity.AlarmSettingActivity;
import com.yushan.alarmdemo.utils.Number2Text;
import com.yushan.alarmdemo.utils.PreferenceHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import static com.yushan.alarmdemo.utils.PreferenceHelper.SETTING;

public class AlarmListAdapter extends BaseAdapter {

    private static String mWay;
    private Context mContext;
    private ArrayList<Map<String, Object>> mLists;
    private int minusDay;
    private String finalTime;

    public AlarmListAdapter(Context context, ArrayList<Map<String, Object>> list) {
        this.mContext = context;
        this.mLists = list;
    }

    public void refresh(ArrayList<? extends Object> list) {
        mLists = (ArrayList<Map<String, Object>>) list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_list_data, null);

            holder.rl_alarm_info = (RelativeLayout) convertView.findViewById(R.id.rl_alarm_info);
            holder.tv_table = (TextView) convertView.findViewById(R.id.tv_table);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_repeat = (TextView) convertView.findViewById(R.id.tv_repeat);
            holder.tv_last_time = (TextView) convertView.findViewById(R.id.tv_last_time);
            holder.sw_state = (Switch) convertView.findViewById(R.id.sw_state);
            holder.line_two = convertView.findViewById(R.id.line_two);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Random rand = new Random();
        final String random = rand.nextInt(100) + "";

        Map<String, Object> info = mLists.get(position);
        final String table = (String) info.get("table");
        final String time = (String) info.get("time");
        final String repeat = (String) info.get("repeat");
        final String state = (String) info.get("state");
        PreferenceHelper.put(mContext, SETTING, "APosition:" + position + random, state);

        holder.tv_table.setText(table);
        holder.tv_time.setText(time);
        holder.tv_repeat.setText(repeat);
        holder.tv_last_time.setText(state);

        if (position == (mLists.size() - 1)) {
            holder.line_two.setVisibility(View.GONE);
        } else {
            holder.line_two.setVisibility(View.VISIBLE);
        }

        if ("open".equals(state)) {
            holder.sw_state.setChecked(true);
            judgeRepeat(repeat, time);
            holder.tv_last_time.setText(finalTime);
        } else {
            holder.sw_state.setChecked(false);
            holder.tv_last_time.setText("未开启");
        }

        holder.sw_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    judgeRepeat(repeat, time);
                    PreferenceHelper.put(mContext, SETTING, "APosition:" + position + random, "open");
                    holder.tv_last_time.setText(finalTime);
                } else {
                    PreferenceHelper.put(mContext, SETTING, "APosition:" + position + random, "close");
                    holder.tv_last_time.setText("未开启");
                }

                String state = PreferenceHelper.get(mContext, SETTING, "APosition:" + position + random, "close");
                Intent mIntent = new Intent(AlarmActivity.RETURN_ALARM_SETTING);
                mIntent.putExtra("table", table);
                mIntent.putExtra("time", time);
                mIntent.putExtra("repeat", repeat);
                mIntent.putExtra("state", state);
                mIntent.putExtra("position", position + "");
                mContext.sendBroadcast(mIntent);

            }
        });

        holder.rl_alarm_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlarmSettingActivity.class);
                intent.putExtra("table", table);
                intent.putExtra("time", time);
                intent.putExtra("repeat", repeat);
                intent.putExtra("state", state);
                intent.putExtra("action", "setting");
                intent.putExtra("position", position + "");
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private void judgeRepeat(String repeat, String startTime) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String format = formatter.format(calendar.getTime());
        String[] dateArray = format.split(" ");
        String weekday = getWeekday();

        switch (repeat) {
            case "只响一次":
            case "每天":
                finalTime = countTime(startTime, dateArray[1]);
                break;
            case "周一至周五":
                finalTime = countTime(startTime, dateArray[1]);

                switch (weekday) {
                    case "6":
                        finalTime = "" + (3 + minusDay) + "天" + finalTime;
                        break;
                    case "7":
                        finalTime = "" + (2 + minusDay) + "天" + finalTime;
                        break;
                    case "1":
                        if (!((minusDay + 1) == 0)) {
                            finalTime = "" + (1 + minusDay) + "天" + finalTime;
                        }
                        break;
                }
                break;
            case "周六和周日":
                finalTime = countTime(startTime, dateArray[1]);
                switch (weekday) {
                    case "1":
                        finalTime = "" + (6 + minusDay) + "天" + finalTime;
                        break;
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                        finalTime = "" + (7 - Integer.parseInt(weekday) + minusDay) + "天" + finalTime;
                        break;
                    case "7":
                        if (!((minusDay + 1) == 0)) {
                            finalTime = "" + (1 + minusDay) + "天" + finalTime;
                        }
                        break;

                }
                break;
            default:
                finalTime = countTime(startTime, dateArray[1]);
                String[] repeatDay = repeat.split(" ");
                int day = 0;
                switch (weekday) {
                    case "1":
                        day = countCustomDay(6, repeatDay);
                        break;
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                        day = countCustomDay(Integer.parseInt(weekday) - 2, repeatDay);
                        break;
                }

                if (day != 0) {
                    finalTime = "" + day + "天" + finalTime;
                }
                break;
        }
    }

    private int countCustomDay(int weekday, String[] repeatDay) {

        if (weekday - Number2Text.displaceWeekday(repeatDay[repeatDay.length - 1]) > 0) {
            return -(weekday - Number2Text.displaceWeekday(repeatDay[0])) + 7 + minusDay;
        } else if (weekday - Number2Text.displaceWeekday(repeatDay[repeatDay.length - 1]) == 0) {
            if (minusDay == -1) {
                return -(weekday - Number2Text.displaceWeekday(repeatDay[0])) + 7 + minusDay;
            } else {
                return 0;
            }
        } else {
            for (int i = 0; i < repeatDay.length; i++) {
                if (weekday - Number2Text.displaceWeekday(repeatDay[i]) < 0) {
                    return -(weekday - Number2Text.displaceWeekday(repeatDay[i])) + minusDay;
                } else if (-(weekday - Number2Text.displaceWeekday(repeatDay[i])) == 0) {
                    if (minusDay == -1) {
                        return -(weekday - Number2Text.displaceWeekday(repeatDay[i + 1])) + minusDay;
                    } else {
                        return 0;
                    }
                }
            }
        }

        return -1;
    }

    private String countTime(String startTime, String endTime) {
        String[] startArray = startTime.split(":");
        String[] endArray = endTime.split(":");
        minusDay = 0;

        int hour = Integer.parseInt(startArray[0]) - Integer.parseInt(endArray[0]);
        int minute = Integer.parseInt(startArray[1]) - Integer.parseInt(endArray[1]);

        if (minute > 0) {
            if (hour > 0) {
                return "" + hour + "小时" + minute + "分钟后响铃";
            } else if (hour == 0) {
                return "" + minute + "分钟后响铃";
            } else {
                minusDay = -1;
                return "" + (hour + 24) + "小时" + minute + "分钟后响铃";
            }
        } else if (minute == 0) {
            if (hour > 0) {
                return "" + hour + "小时" + minute + "分钟后响铃";
            } else if (hour == 0) {
                minusDay = -1;
                return "" + 23 + "小时" + 59 + "分钟后响铃";
            } else {
                minusDay = -1;
                return "" + (hour + 24) + "小时" + minute + "分钟后响铃";
            }
        } else {
            int newHour = hour - 1;

            if (newHour > 0) {
                return "" + newHour + "小时" + (minute + 60) + "分钟后响铃";
            } else if (newHour == 0) {
                return "" + (minute + 60) + "分钟后响铃";
            } else {
                minusDay = -1;
                return "" + (newHour + 24) + "小时" + (minute + 60) + "分钟后响铃";
            }
        }
    }

    public String getWeekday() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        return mWay;
//        if ("1".equals(mWay)) {
//            mWay = "天";
//        } else if ("2".equals(mWay)) {
//            mWay = "一";
//        } else if ("3".equals(mWay)) {
//            mWay = "二";
//        } else if ("4".equals(mWay)) {
//            mWay = "三";
//        } else if ("5".equals(mWay)) {
//            mWay = "四";
//        } else if ("6".equals(mWay)) {
//            mWay = "五";
//        } else if ("7".equals(mWay)) {
//            mWay = "六";
//        }
//        return "星期" + mWay;
    }

    class ViewHolder {
        private TextView tv_table, tv_time, tv_repeat, tv_last_time;
        private Switch sw_state;
        private RelativeLayout rl_alarm_info;
        private View line_two;
    }
}
