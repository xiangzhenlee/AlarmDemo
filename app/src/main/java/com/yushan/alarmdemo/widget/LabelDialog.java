package com.yushan.alarmdemo.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.yushan.alarmdemo.R;
import com.yushan.alarmdemo.activity.AlarmSettingActivity;
import com.yushan.alarmdemo.widget.wheel.AbstractWheelTextAdapter;
import com.yushan.alarmdemo.widget.wheel.WheelView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class LabelDialog extends AbsDialog implements View.OnClickListener {

    private static int theme = R.style.myDialog;// 主题

    private Context mContext;
    // 取消
    private TextView tv_cancel;
    // 确定
    private TextView tv_confirm;

    private SelectLabelInfo selectLabelInfo;

    protected int TOP_COLOR = 0xefFFFFFF;
    protected int CENTER_COLOR = 0xcfFFFFFF;
    protected int BOTTOM_CENTER = 0x3fFFFFFF;

    private WheelView wheelView_label;
    private String[] label;
    private LabelAdapter mLabelAdapter;
    private int oldLabel;
    private int newLabel;
    private String newLabelText;
    private int labelIndex;

    @SuppressLint("InlinedApi")
    public LabelDialog(Activity context, int width, int height, int label) {
        super(context, theme);
        init(context, width, height, label);
    }

    private void init(Activity context, int width, int height, int label) {
        mContext = context;
        labelIndex = label;
        setContentView(R.layout.dialog_label);
        LinearLayout label_select_layout = (LinearLayout) findViewById(R.id.label_select_layout);
        LayoutParams lparams_hours = new LayoutParams(width, height / 3);
        label_select_layout.setLayoutParams(lparams_hours);
    }

    @Override
    protected void initView() {
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_confirm = (TextView) findViewById(R.id.tv_ok);
        tv_cancel.setClickable(true);
        tv_cancel.setFocusable(true);
        tv_confirm.setClickable(true);
        tv_confirm.setFocusable(true);

        wheelView_label = (WheelView) findViewById(R.id.wheelView_label);
    }

    @Override
    protected void initData() {
        getWheelContent(mContext, R.array.alarm_label_range);

        wheelView_label.setShadowColor(TOP_COLOR, CENTER_COLOR, BOTTOM_CENTER);

        mLabelAdapter = new LabelAdapter();
        wheelView_label.setViewAdapter(mLabelAdapter);
        wheelView_label.setCurrentItem(labelIndex);
    }

    /**
     * 根据resId，为不同的修改框填充相应的数据
     *
     * @param context
     */
    public void getWheelContent(Context context, int labelResId) {
        if (labelResId != -1) {
            label = context.getResources().getStringArray(labelResId);
        }
    }

    class LabelAdapter extends AbstractWheelTextAdapter {
        public LabelAdapter() {
            super(mContext, R.layout.item_wheel_text_height);
            setItemTextResource(R.id.question_wheel_textView);
        }

        @Override
        public int getItemsCount() {
            return label != null ? label.length : 0;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return label[index];
        }
    }

    @Override
    protected void setListener() {
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }


    public void show(int labelIndex) {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.wheel_bg);
        window.setGravity(Gravity.BOTTOM);
        show();

        wheelView_label.setCurrentItem(labelIndex, true);
        oldLabel = wheelView_label.getCurrentItem();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                newLabel = wheelView_label.getCurrentItem();
                newLabelText = mLabelAdapter.getItemText(newLabel) + "";
                selectLabelInfo.selectLabel(newLabelText, newLabel);
                dismiss();
                break;
            default:
                break;
        }
    }

    public void selectLabelInfo(SelectLabelInfo info) {
        selectLabelInfo = info;
    }

    public interface SelectLabelInfo {
        void selectLabel(String labelText, int labelIndex);
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
