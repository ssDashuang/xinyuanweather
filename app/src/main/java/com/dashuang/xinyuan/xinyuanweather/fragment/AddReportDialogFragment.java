package com.dashuang.xinyuan.xinyuanweather.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.dashuang.xinyuan.xinyuanweather.R;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by chx on 2017/6/7.
 */

public class AddReportDialogFragment extends DialogFragment{

    private static final String CITY_NAME="CITY_NAME";
    private static final String WEATHER_ID="WEATHER_ID";
    private static final String TAG = "AddReportDialogFragment";
    private   CityManager reportCity ;
    private String cityName;
    private String weatherId;
    private TimePicker timePicker;
    private long time;
    private String timeInfo;
    private OnSaveClickListener listener;

    public static AddReportDialogFragment newInstance(String cityName,String weatherId) {
        AddReportDialogFragment fragment = new AddReportDialogFragment();
        Bundle args = new Bundle();
        args.putString(CITY_NAME,cityName);
        args.putString(WEATHER_ID, weatherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(CITY_NAME);
            weatherId = getArguments().getString(WEATHER_ID);
            reportCity =   DataSupport.where("weatherId = ?",weatherId).findFirst(CityManager.class);
        }
    }

    public void setOnSaveClickListener(OnSaveClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(),R.layout.layout_add_report,null);
        builder.setView(view);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        if (reportCity != null){
            time = reportCity.getTime();
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int h, int m) {
               time = h * 60 * 60 * 1000 + m * 60 * 1000;
                timeInfo = h+":"+m;
                Log.e(TAG, "onTimeChanged: "+h+","+m );
            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reportCity.setCountyName(cityName);
                reportCity.setWeatherId(weatherId);
                reportCity.setTime(time);
                reportCity.setTimeInfo(timeInfo);
                reportCity.setReport(true);
                reportCity.updateAll("weatherId = ?", weatherId);
                Log.e(TAG, "onClick: save 成功");
                List<CityManager> list = DataSupport.findAll(CityManager.class);
                for (CityManager c : list){
                    Log.e(TAG, "onClick: "+c.toString() );
                }
                if (listener != null){
                    listener.onSave();
                }
                Intent intent = new Intent("action.setting.changed");
                getActivity().sendBroadcast(intent);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();

    }

    public interface OnSaveClickListener{
        void onSave();
    }
}
