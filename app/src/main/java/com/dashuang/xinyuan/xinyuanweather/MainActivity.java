package com.dashuang.xinyuan.xinyuanweather;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dashuang.xinyuan.xinyuanweather.Global.ConstantURL;
import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.City;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;
import com.dashuang.xinyuan.xinyuanweather.db.County;
import com.dashuang.xinyuan.xinyuanweather.db.Province;
import com.dashuang.xinyuan.xinyuanweather.fragment.ChooseAreaFragment;
import com.dashuang.xinyuan.xinyuanweather.util.HttpUtil;
import com.dashuang.xinyuan.xinyuanweather.util.JsonUtility;
import com.dashuang.xinyuan.xinyuanweather.util.PermissionUtil;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;
import com.dashuang.xinyuan.xinyuanweather.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private StringBuilder currentLocation = new StringBuilder();
    private LocationClient mLocationClient;
    private ProgressDialog progressDialog;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    //是否是更新当前位置
    public boolean isUpdateLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<CityManager> list = CityManagerDao.queryAll();
        //更新位置信息传递的Intent
        Intent updateIntent = getIntent();
        isUpdateLocation = updateIntent.getBooleanExtra("update_location",false);
        if (list.size() > 0 && !isUpdateLocation){
            //如果有选择好的城市直接进入,数据库缓存
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }else {
            mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.registerLocationListener(new MyLocationListener());
            configPermission();
        }

    }

    private void configPermission() {
        List<String> permissionList = PermissionUtil.getPermissionList(MainActivity.this);
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            getLocation();
        }
    }
    public void getLocation(){
        initLocation();
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String province = bdLocation.getProvince();
            String city = bdLocation.getCity();
            currentLocation.append(bdLocation.getCountry())
                    .append(",").append(province.substring(0,province.length()-1))
                    .append(",").append(city.substring(0,city.length()-1))
                    .append(",").append(city.substring(0,city.length()-1));
            Log.e(TAG, "onReceiveLocation: "+bdLocation.getAddrStr());
            Log.e(TAG, "onReceiveLocation: "+bdLocation.getLocType());
            final String lo = bdLocation.getAddrStr();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (lo != null) {
                        showDialog();
                    }else {
                        ToastUtil.show(MainActivity.this,"无法获取当前位置");
                        startFragment();
                    }
                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }
    public void showDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("选择当前位置");
        dialog.setMessage("是否选择当前位置："+currentLocation);
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //弹出加载框
                showProgressDialog();
                //把本地信息存入缓存
                PrefUtil.putString(MainActivity.this, PrefConstantKey.CURRENT_LOCATION,
                        currentLocation.toString());
                //开始获取本地天气码
                queryFromServer(ConstantURL.CITY_URL,"province");
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startFragment();
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void startFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container,new ChooseAreaFragment());
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            ToastUtil.show(this,"获取权限失败");
                            return;
                        }
                    }
                    getLocation();
                }else {
                    ToastUtil.show(this,"手机权限错误");
                    finish();
                }
                break;
        }
    }
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(MainActivity.this);
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



    private void queryFromServer(String path,final String type){

        HttpUtil.sendOkHttpRequest(path, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result =JsonUtility.parseProvinceJson(responseText);
                }else if ("city".equals(type)){
                   result = JsonUtility.parseCityJson(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = JsonUtility.parseCountyJson(responseText,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] location = currentLocation.toString().split(",");
                            if ("province".equals(type)){
                                queryProvince(location[1]);
                            }else if ("city".equals(type)){
                                queryCity(location[2]);
                            }else if ("county".equals(type)){
                                queryCounty(location[3]);
                            }
                        }
                    });
                }
            }
        });

    }

    private void queryCity(String cityName) {
        selectedCity = DataSupport
                .where("cityName = ?",cityName).findFirst(City.class);
        if (selectedCity != null){
            String path = ConstantURL.CITY_URL+"/"+selectedProvince.getProvinceCode()
                    +"/"+selectedCity.getCityCode();
            queryFromServer(path,"county");
        }
    }

    private void queryCounty(String countyName) {
        selectedCounty = DataSupport
                .where("countyName = ?",countyName).findFirst(County.class);
        if (selectedCounty != null){
            closeProgressDialog();
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            intent.putExtra("weather_id", selectedCounty.getWeatherId());
            //存入数据库
            CityManagerDao.addCity(selectedCounty);
            startActivity(intent);
            finish();
        }
    }

    private void queryProvince(String provinceName) {

        selectedProvince = DataSupport
                .where("provinceName = ?",provinceName).findFirst(Province.class);
        if (selectedProvince != null) {
            String path = ConstantURL.CITY_URL + "/" + selectedProvince.getProvinceCode();
            queryFromServer(path, "city");
        }
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setScanSpan(0);
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient != null) {
            mLocationClient.stop();
        }
    }
}
