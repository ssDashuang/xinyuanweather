package com.dashuang.xinyuan.xinyuanweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.dashuang.xinyuan.xinyuanweather.Global.ConstantURL;
import com.dashuang.xinyuan.xinyuanweather.Global.PrefConstantKey;
import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;
import com.dashuang.xinyuan.xinyuanweather.gson.Weather;
import com.dashuang.xinyuan.xinyuanweather.util.HttpUtil;
import com.dashuang.xinyuan.xinyuanweather.util.JsonUtility;
import com.dashuang.xinyuan.xinyuanweather.util.PrefUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.thirdparty.W;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherService extends Service {

    private static final String TAG = "WeatherService";
    private boolean isAutoUpdate = false;

    private boolean isVoiceReport = false;

    private Timer mTimer;

    private MyBroadCastReceiver myBroadCastReceiver;

    public WeatherService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58d2459f");
        myBroadCastReceiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter("action.setting.changed");
        registerReceiver(myBroadCastReceiver,filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isAutoUpdate = PrefUtil.getBoolean(WeatherService.this,PrefConstantKey.AUTO_UPDATE);
        isVoiceReport = PrefUtil.getBoolean(WeatherService.this,PrefConstantKey.VOICE_REPORT);
        if (isAutoUpdate){
            setAutoUpdate();
            startUpdate();
        }
       if (isVoiceReport && mTimer == null){
            setVoiceReport();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setVoiceReport() {
        final List<CityManager> cityList = CityManagerDao.queryAll();
        if (cityList != null && cityList.size() > 0 ) {
            mTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Log.e(TAG, "setVoiceReport: voiceReport" );
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    int h = c.get(Calendar.HOUR_OF_DAY);
                    int m = c.get(Calendar.MINUTE);
                    Log.e(TAG, "run: "+h+","+m );
                    long currentTime = h * 60 * 60 * 1000 + m * 60 * 1000;
                    for (CityManager city : cityList) {
                        if ( city.getReport() & city.getTime() == currentTime ) {
                            startVoiceReport(city);
                        }
                    }
                }
            };
            mTimer.schedule(task,0,60000);
        }
    }

    private void updateSetting(){
        this.stopSelf();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Intent intent = new Intent(WeatherService.this, WeatherService.class);
        startService(intent);
    }

    private void setAutoUpdate(){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int intervalTime = PrefUtil.getInt(this, PrefConstantKey.UPDATE_INTERVAL_TIME);
        if (intervalTime != 0) {
            int milliSecond = intervalTime * 60 * 60 * 1000;
            long triggerAtTime = SystemClock.elapsedRealtime() + milliSecond;
            Intent intent = new Intent(this, WeatherService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
    }
    private void startUpdate(){
        List<CityManager> cityManagerList = CityManagerDao.queryAll();
        if (cityManagerList != null & cityManagerList.size() > 0){
            //使用数据库中的数据
            for (CityManager cityManager : cityManagerList){
                String url = ConstantURL.WEATHER_URL
                        + cityManager.getWeatherId() + ConstantURL.WEATHER_API_KEY;
                HttpUtil.sendOkHttpRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: 天气数据更新失败" );
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Weather weather = JsonUtility.parseWeatherJson(responseText);
                        String key = PrefConstantKey.WEATHER_INFO + weather.basic.weatherId;
                        if (weather != null && "ok".equals(weather.status)){
                            PrefUtil.putString(WeatherService.this,key,responseText);
                        }
                    }
                });
            }
        }
    }

    private void startVoiceReport(CityManager city){
        String msg = PrefUtil.getString(WeatherService.this,
                PrefConstantKey.WEATHER_INFO+city.getWeatherId());
        Weather weather = JsonUtility.parseWeatherJson(msg);
        String speak = "现在播报"+city.getCountyName()+"的天气状况,"+"当前温度"
                +weather.now.temperature+"度,"+weather.now.more.info+","
                +weather.now.wind.direction+","+weather.now.wind.grade+"级";
        voiceSpeak(speak);
    }

    private void voiceSpeak(String msg) {
        Log.e(TAG, "voiceSpeak: msg" );
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(getApplicationContext(), null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(msg, mSynListener);
        //合成监听器

    }
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {

        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {

        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCastReceiver);
    }

    class MyBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            updateSetting();
            Log.e(TAG, "onReceive: ----------->receiver" );
        }
    }
}
