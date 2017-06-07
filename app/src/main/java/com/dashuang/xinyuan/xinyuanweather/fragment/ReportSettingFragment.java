package com.dashuang.xinyuan.xinyuanweather.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuang.xinyuan.xinyuanweather.R;
import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;

import java.util.Calendar;
import java.util.List;

public class ReportSettingFragment extends Fragment {


    private static final String TAG = "ReportSettingFragment";
    private ListView lvCity;
    private  List<CityManager> citys;
    private MyAdapter myAdapter;


    public ReportSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_setting, container, false);
        lvCity = (ListView) view.findViewById(R.id.lv_report_city);
        initData();
        initEvent();
        return view;
    }

    private void initData() {
       citys = CityManagerDao.queryAll();
        if (citys != null){
           myAdapter = new MyAdapter();
            lvCity.setAdapter(myAdapter);
        }
    }

    private void initEvent() {
        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AddReportDialogFragment dialog = AddReportDialogFragment
                        .newInstance(citys.get(i).getCountyName(),citys.get(i).getWeatherId());
                dialog.show(getFragmentManager(),"ReportTimeDialog");
                dialog.setOnSaveClickListener(new AddReportDialogFragment.OnSaveClickListener() {
                    @Override
                    public void onSave() {
                        citys = CityManagerDao.queryAll();
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return citys.size();
        }

        @Override
        public Object getItem(int i) {
            return citys.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = View.inflate(getActivity(),R.layout.layout_setting_report_item,null);
            }
            TextView tvCityName = (TextView) view.findViewById(R.id.tv_report_city_name);
            TextView tvTime = (TextView) view.findViewById(R.id.tv_report_time);
            SwitchCompat switchOpenReport = (SwitchCompat) view.findViewById(R.id.switch_open_report);
            tvCityName.setText(citys.get(i).getCountyName());
            long time = citys.get(i).getTime();
            if (time == -1){
                tvTime.setText("未设置");
            }else {
                tvTime.setText(citys.get(i).getTimeInfo());
            }
            final int pos = i;
            switchOpenReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    CityManagerDao.updateCity(citys.get(pos).getWeatherId(),b);
                    Log.e(TAG, "onCheckedChanged: ----------->check" );
                }
            });
            return view;
        }


    }

}
