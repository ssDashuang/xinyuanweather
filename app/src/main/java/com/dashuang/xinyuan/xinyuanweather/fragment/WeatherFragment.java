package com.dashuang.xinyuan.xinyuanweather.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dashuang.xinyuan.xinyuanweather.Global.ConstantURL;
import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.R;
import com.dashuang.xinyuan.xinyuanweather.SettingActivity;
import com.dashuang.xinyuan.xinyuanweather.WeatherActivity;
import com.dashuang.xinyuan.xinyuanweather.gson.Forecast;
import com.dashuang.xinyuan.xinyuanweather.gson.Weather;
import com.dashuang.xinyuan.xinyuanweather.util.HttpUtil;
import com.dashuang.xinyuan.xinyuanweather.util.JsonUtility;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;
import com.dashuang.xinyuan.xinyuanweather.util.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "WeatherFragment";
    private static final String WEATHER_ID = "weather_id";

    private String prefKey;

    private ScrollView scrollViewWeather;

    private TextView tvTitleCity;

    private TextView tvUpdateTime;

    private TextView tvDegree;

    private TextView tvWeatherInfo;

    private TextView tvAqi;

    private TextView tvPm25;

    private TextView tvComfort;

    private TextView tvCarWash;

    private TextView tvSport;

    private TextView tvWind;

    private ImageView ivBg;

    private ImageView ivTmpPicture;

    private ImageButton ibSetting;

    private ImageButton ibMore;

    private SwipeRefreshLayout refreshLayout;

    private LinearLayout layoutForecast;

    private String currentWeatherID ;


    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance(String weatherId) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(WEATHER_ID, weatherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentWeatherID = getArguments().getString(WEATHER_ID);
            prefKey =  PrefConstantKey.WEATHER_INFO + currentWeatherID;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        //初始化各控件
        initView(view);
        //有缓存的情况使用缓存
        loadPref();
        //设置监听事件
        initEvent();
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void initEvent() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(currentWeatherID);
            }
        });
        ibSetting.setOnClickListener(this);
        ibMore.setOnClickListener(this);
    }

    private void  loadPref() {
        //加载天气缓存
        String weatherString = PrefUtil.getString(getActivity(), prefKey);
        if (weatherString != null){
            Weather weather = JsonUtility.parseWeatherJson(weatherString);
            currentWeatherID = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务查询天气
            scrollViewWeather.setVisibility(View.INVISIBLE);
            requestWeather(currentWeatherID);
        }
        //加载图片缓存
        String bgPic = PrefUtil.getString(getActivity(),PrefConstantKey.WEATHER_BG);
        if (bgPic != null){
            Glide.with(this).load(bgPic).into(ivBg);
        }else{
            requestImage();
        }
    }


    private void initView(View view) {
        scrollViewWeather = (ScrollView) view.findViewById(R.id.scrollview_weather);
        layoutForecast = (LinearLayout) view.findViewById(R.id.forecast_layout);
        tvAqi = (TextView) view.findViewById(R.id.tv_aqi);
        tvCarWash = (TextView)view.findViewById(R.id.tv_car_wash);
        tvComfort = (TextView) view.findViewById(R.id.tv_comfort);
        tvDegree = (TextView)view.findViewById(R.id.tv_degree_txt);
        tvPm25 = (TextView) view.findViewById(R.id.tv_pm25);
        tvTitleCity = (TextView) view.findViewById(R.id.tv_title_city);
        tvUpdateTime = (TextView) view.findViewById(R.id.tv_title_update_time);
        tvSport = (TextView) view.findViewById(R.id.tv_sport);
        tvWeatherInfo = (TextView) view.findViewById(R.id.tv_weather_info);
        tvWind = (TextView) view.findViewById(R.id.tv_weather_wind);
        ivBg = (ImageView) view.findViewById(R.id.iv_pic_bg);
        ivTmpPicture = (ImageView) view.findViewById(R.id.iv_tmp_picture);
        ibMore = (ImageButton) view.findViewById(R.id.ib_more);
        ibSetting = (ImageButton) view.findViewById(R.id.ib_setting);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_weather_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }


    private void requestImage() {
        HttpUtil.sendOkHttpRequest(ConstantURL.BG_IMG_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //访问失败
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getActivity(),"网络繁忙");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bgPic = response.body().string();
                //缓存到本地
                PrefUtil.putString(getActivity(),PrefConstantKey.WEATHER_BG,bgPic);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(bgPic).into(ivBg);
                    }
                });
            }
        });
    }

    private void requestWeather(final String weatherID){
        //拼接URL
        String url = ConstantURL.WEATHER_URL+weatherID+ConstantURL.WEATHER_API_KEY;
        Log.e(TAG, "requestWeather: ------------->"+url );
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //访问失败
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getActivity(),"网络繁忙");
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e(TAG, "onResponse: "+responseText );
                final Weather weather = JsonUtility.parseWeatherJson(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            PrefUtil.putString(getActivity(), prefKey,responseText);
                            showWeatherInfo(weather);
                        }else {
                            ToastUtil.show(getActivity(),"获取天气信息失败");
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 展示天气
     * @param weather 天气数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime =weather.basic.update.updateTime.split(" ")[1]+" 更新" ;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        String weatherImgUrl = ConstantURL.WEATHER_PICTURE_URL + weather.now.more.code+".png";
        String windInfo = weather.now.wind.direction+"   "+weather.now.wind.grade;
        tvTitleCity.setText(cityName);
        tvUpdateTime.setText(updateTime);
        tvWeatherInfo.setText(weatherInfo);
        tvDegree.setText(degree);
        tvWind.setText(windInfo);
        Log.e(TAG, "showWeatherInfo: --------->"+weatherImgUrl );
        Glide.with(getActivity()).load(weatherImgUrl).into(ivTmpPicture);
        layoutForecast.removeAllViews();
        for (Forecast f : weather.forecastList){
            View view = View.inflate(getActivity(),R.layout.forecast_item,null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            TextView tvDate = (TextView) view.findViewById(R.id.tv_forecast_date);
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_forecast_info);
            TextView tvTmp = (TextView) view.findViewById(R.id.tv_forecast_tmp);
            tvDate.setText(f.date);
            tvInfo.setText(f.more.info);
            tvTmp.setText(f.temperature.max+"°"+"/"+f.temperature.min+"°");
            view.setLayoutParams(layoutParams);
            layoutForecast.addView(view);
        }
        if (weather.aqi != null){
            Log.e(TAG, "showWeatherInfo:----------> "+weather.aqi.city.aqi );
            tvAqi.setText(weather.aqi.city.aqi);
            tvPm25.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车建议:" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;
        tvComfort.setText(comfort);
        tvCarWash.setText(carWash);
        tvSport.setText(sport);
        scrollViewWeather.setVisibility(View.VISIBLE);
    }

    public String getWeatherId(){
        return currentWeatherID;
    }

    public void swipeRefresh(String weatherId){
        refreshLayout.setRefreshing(true);
        requestWeather(weatherId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_setting:
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.ib_more:
                WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                weatherActivity.openDrawer();
                break;
        }
    }
}
