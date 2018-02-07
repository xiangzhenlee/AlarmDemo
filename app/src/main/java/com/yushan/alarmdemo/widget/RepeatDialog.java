package com.yushan.alarmdemo.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yushan.alarmdemo.R;
import com.yushan.alarmdemo.utils.Number2Text;
import com.yushan.alarmdemo.utils.PreferenceHelper;
import com.yushan.alarmdemo.widget.wheel.AbstractWheelTextAdapter;
import com.yushan.alarmdemo.widget.wheel.WheelView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.yushan.alarmdemo.utils.PreferenceHelper.SETTING;
import static java.util.Collections.swap;

public class RepeatDialog extends AbsDialog implements View.OnClickListener {

    private static int theme = R.style.myDialog;// 主题

    private Context mContext;
    // 取消
    private TextView tv_cancel;
    private SelectRepeatInfo selectRepeatInfo;

    private ListView lv_repeat_state;
    private ArrayList<Map<String, String>> repeatData;
    private RepeatAdapter mRepeatAdapter;
    private int mListViewItemId;
    private int mArrayTextId;
    private String mFromWhere;
    private TextView tv_ok;
    private ArrayList<String> customData;

    @SuppressLint("InlinedApi")
    public RepeatDialog(Activity context, int width, int height, String fromWhere) {
        super(context, theme);
        init(context, width, height, fromWhere);
    }

    private void init(Activity context, int width, int height, String fromWhere) {
        mContext = context;
        mFromWhere = fromWhere;
        if (!"自定义".equals(fromWhere)) {
            mListViewItemId = R.layout.item_wheel_custom_text;
            mArrayTextId = R.array.alarm_custom_range;
        } else {
            mListViewItemId = R.layout.item_wheel_text_height;
            mArrayTextId = R.array.alarm_repeat_range;
        }

        setContentView(R.layout.dialog_repeat);
        LinearLayout label_select_layout = (LinearLayout) findViewById(R.id.label_select_layout);
        LayoutParams lparams_hours;
        if (!"自定义".equals(fromWhere)) {
            lparams_hours = new LayoutParams(width, height / 2);
        } else {
            lparams_hours = new LayoutParams(width, height / 3);
        }

        label_select_layout.setLayoutParams(lparams_hours);
    }

    @Override
    protected void initView() {
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setClickable(true);
        tv_cancel.setFocusable(true);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setClickable(true);
        tv_ok.setFocusable(true);

        if (!"自定义".equals(mFromWhere)) {
            tv_ok.setVisibility(View.VISIBLE);
        } else {
            tv_ok.setVisibility(View.INVISIBLE);
        }

        lv_repeat_state = (ListView) findViewById(R.id.lv_repeat_state);
        lv_repeat_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ("自定义".equals(mFromWhere)) {

                    String repeatText = repeatData.get(position).get("weekday");
                    selectRepeatInfo.selectRepeat(repeatText, position);
                    dismiss();
                }
            }
        });
    }

    @Override
    protected void initData() {
        getWheelContent(mContext, mArrayTextId);

        mRepeatAdapter = new RepeatAdapter(repeatData);
        lv_repeat_state.setAdapter(mRepeatAdapter);

        customData = new ArrayList<>();
    }

    private String[] selectedWeekday(String weekday) {
        String[] weekdayArray = {"0", "0", "0", "0", "0", "0", "0"};
        switch (weekday) {
            case "只响一次":
                break;
            case "每天":
                for (int i = 0; i < weekdayArray.length; i++) {
                    weekdayArray[i] = "1";
                }
                break;
            case "周一至周五":
                for (int i = 0; i < weekdayArray.length - 2; i++) {
                    weekdayArray[i] = "1";
                }
                break;
            case "周六和周日":
                for (int i = 5; i < weekdayArray.length; i++) {
                    weekdayArray[i] = "1";
                }
                break;
            default:
                String[] selected = weekday.split(" ");

                for (int i = 0; i < selected.length; i++) {
                    weekdayArray[Number2Text.displaceWeekday(selected[i])] = "1";
                }
                break;
        }
        return weekdayArray;
    }

    /**
     * 根据resId，为不同的修改框填充相应的数据
     *
     * @param context
     */
    public void getWheelContent(Context context, int labelResId) {
        if (labelResId != -1) {
            String[] label = context.getResources().getStringArray(labelResId);
            String[] select = null;
            if (!"自定义".equals(mFromWhere)) {
                select = selectedWeekday(mFromWhere);
            }

            repeatData = new ArrayList<>();
            for (int i = 0; i < label.length; i++) {
                Map<String, String> newMap = new HashMap<>();
                newMap.put("weekday", label[i]);
                if (select != null) {
                    newMap.put("selected", select[i]);
                } else {
                    newMap.put("selected", "0");
                }
                repeatData.add(newMap);
            }
        }
    }

    class RepeatAdapter extends BaseAdapter {
        ArrayList<Map<String, String>> mLists;
        private boolean state;

        public RepeatAdapter(ArrayList<Map<String, String>> list) {
            this.mLists = list;
        }

        @Override
        public int getCount() {
            return repeatData != null ? repeatData.size() : 0;
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
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mListViewItemId, null);
                holder.wheel_textView = (TextView) convertView.findViewById(R.id.question_wheel_textView);
                holder.rl_custom_item = (RelativeLayout) convertView.findViewById(R.id.rl_custom_item);
                if (!"自定义".equals(mFromWhere)) {
                    holder.wheel_textView.setTextSize(16);
                    holder.rb_select = (ImageView) convertView.findViewById(R.id.rb_select);
                }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Random rand = new Random();
            final String random = rand.nextInt(100) + "";

            String info = mLists.get(position).get("weekday");
            String selected = mLists.get(position).get("selected");
            if (!"自定义".equals(mFromWhere)) {

                if ("0".equals(selected)) {
                    holder.rb_select.setImageResource(R.drawable.unselected);
                    PreferenceHelper.put(mContext, SETTING, "position:" + position + random, false);
                } else if ("1".equals(selected)) {
                    if (!checkSelected(position)){
                        customData.add("" + position);
                    }
                    holder.rb_select.setImageResource(R.drawable.selected);
                    PreferenceHelper.put(mContext, SETTING, "position:" + position + random, true);
                }

                holder.rl_custom_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        state = PreferenceHelper.get(mContext, SETTING, "position:" + position + random, false);
                        if (state == false) {
                            state = true;
                            customData.add("" + position);
                            holder.rb_select.setImageResource(R.drawable.selected);
                        } else {
                            state = false;
                            customData.remove("" + position);
                            holder.rb_select.setImageResource(R.drawable.unselected);
                        }
                        sortAscending(customData);
                        PreferenceHelper.put(mContext, SETTING, "position:" + position + random, state);
                    }
                });

            }

            holder.wheel_textView.setText(info);

            return convertView;
        }

        public ArrayList sortAscending(ArrayList<String> arrayList) {                 //升序排序方法
            Collections.sort(arrayList);
            return arrayList;
        }

        public boolean checkSelected(int position){
            for (int i = 0; i < customData.size(); i ++){
                if (position == Integer.parseInt(customData.get(i))){
                    return true;
                }
            }
            return false;
        }

        class ViewHolder {
            private ImageView rb_select;
            private RelativeLayout rl_custom_item;
            private TextView wheel_textView;
        }
    }

    @Override
    protected void setListener() {
        tv_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }


    public void showDialog() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.wheel_bg);
        window.setGravity(Gravity.BOTTOM);
        show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                String text = "a";
                if (customData.size() > 0) {
                    for (int i = 0; i < customData.size(); i++) {
                        text = text + ":" + customData.get(i);
                    }
                } else {
                    text = "未选择";
                }

                selectRepeatInfo.selectRepeat(text, 4);
                dismiss();
                break;
            default:
                break;
        }
    }

    public void selectRepeatInfo(SelectRepeatInfo info) {
        selectRepeatInfo = info;
    }

    public interface SelectRepeatInfo {
        void selectRepeat(String repeatText, int repeatIndex);

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
