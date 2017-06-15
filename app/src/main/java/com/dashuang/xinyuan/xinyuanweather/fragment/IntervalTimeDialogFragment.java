package com.dashuang.xinyuan.xinyuanweather.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.R;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;

/**
 * Created by chx on 2017/6/9.
 */

public class IntervalTimeDialogFragment extends DialogFragment{

    private RadioGroup rgSelectTime;
    private RadioButton rbOne;
    private RadioButton rbFour;
    private RadioButton rbEight;
    private int mIntervalTime;
    private static final String TAG = "IntervalTimeDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.layout_interval_time,null);
        initView(view);
        initEvent();
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefUtil.putInt(getActivity(),PrefConstantKey.UPDATE_INTERVAL_TIME,mIntervalTime);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private void initEvent() {
        rgSelectTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                Log.e(TAG, "onCheckedChanged: " );
                switch (i){
                    case R.id.rb_one_hour:
                        mIntervalTime = 1;
                        break;
                    case R.id.rb_four_hour:
                        mIntervalTime = 4;
                        break;
                    case R.id.rb_eight_hour:
                        mIntervalTime = 8;
                        break;
                }
            }
        });
    }

    private void initView(View view) {
        rgSelectTime = (RadioGroup) view.findViewById(R.id.rg_select_time);
        rbEight = (RadioButton) view.findViewById(R.id.rb_eight_hour);
        rbOne = (RadioButton) view.findViewById(R.id.rb_one_hour);
        rbFour = (RadioButton) view.findViewById(R.id.rb_four_hour);
        int time = PrefUtil.getInt(getActivity(),PrefConstantKey.UPDATE_INTERVAL_TIME);
        if (time == 8){
            rbEight.setChecked(true);
        }else if (time == 4){
            rbFour.setChecked(true);
        }else {
            rbOne.setChecked(true);
            mIntervalTime = 1;
        }
    }
}
