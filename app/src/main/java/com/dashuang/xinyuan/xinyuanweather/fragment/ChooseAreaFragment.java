package com.dashuang.xinyuan.xinyuanweather.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.MainActivity;
import com.dashuang.xinyuan.xinyuanweather.R;
import com.dashuang.xinyuan.xinyuanweather.SettingDetailActivity;
import com.dashuang.xinyuan.xinyuanweather.WeatherActivity;
import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.City;
import com.dashuang.xinyuan.xinyuanweather.db.County;
import com.dashuang.xinyuan.xinyuanweather.db.Province;
import com.dashuang.xinyuan.xinyuanweather.util.HttpUtil;
import com.dashuang.xinyuan.xinyuanweather.util.JsonUtility;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseAreaFragment extends Fragment {

    private final static int LEVEL_PROVINCE = 0;
    private final static int LEVEL_CITY = 1;
    private final static int LEVEL_COUNTY = 2;
    //当前选中的级别
    private int currentLevel = -1;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<String>();

    private List<City> cityList = new ArrayList<City>();

    private List<Province> provinceList = new ArrayList<Province>();

    private List<County> countyList = new ArrayList<County>();

    private Province selectedProvince;

    private City selectedCity;

    private TextView tvTitle;
    private Button btnBack;
    private ListView lvChooseArea;

    private ProgressDialog progressDialog;

    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_area_title);
        btnBack = (Button) view.findViewById(R.id.btn_choose_area_back);
        lvChooseArea = (ListView) view.findViewById(R.id.lv_choose_area);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        lvChooseArea.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lvChooseArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCity();
                }else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY){
                    County county = countyList.get(i);
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", county.getWeatherId());
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity.isUpdateLocation){
                            //更新位置信息
                            StringBuilder location = new StringBuilder();
                            location.append("中国")
                                    .append(",").append(selectedProvince.getProvinceName())
                                    .append(",").append(selectedCity.getCityName())
                                    .append(",").append(county.getCountyName());
                            PrefUtil.putString(mainActivity, PrefConstantKey.
                                    CURRENT_LOCATION,location.toString());
                        }
                        //存入数据库
                        CityManagerDao.addCity(county);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof SettingDetailActivity){
                        CityManagerDao.addCity(county);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.closeDrawer();
                        WeatherFragment fragment = activity.getCurrentFragment();
                        CityManagerDao.updateCity(fragment.getWeatherId(),county);
                        fragment.swipeRefresh(county.getWeatherId());
                    }
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince(){
        tvTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getProvinceName());
            adapter.notifyDataSetChanged();
            lvChooseArea.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String path = "http://guolin.tech/api/china";
            queryFromServer(path,"province");
        }
    }
    private void queryCity(){
        tvTitle.setText(selectedProvince.getProvinceName());
        btnBack.setVisibility(View.VISIBLE);
        cityList = DataSupport
                .where("provinceid = ?",String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvChooseArea.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String path = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(path,"city");
        }
    }
    private void queryCounty(){
        tvTitle.setText(selectedCity.getCityName());
        btnBack.setVisibility(View.VISIBLE);
        countyList = DataSupport
                .where("cityid = ?",String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvChooseArea.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String path = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(path,"county");
        }
    }
    private void queryFromServer(String path,final String type){
        //弹出加载框
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(path, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = JsonUtility.parseProvinceJson(responseText);
                }else if ("city".equals(type)){
                    result = JsonUtility.parseCityJson(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = JsonUtility.parseCountyJson(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载数据..");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
