package com.dashuang.xinyuan.xinyuanweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
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
import com.iflytek.cloud.SynthesizerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherService extends Service {

    private static final String TAG = "WeatherService";
    private boolean isAutoUpdate = false;

    private boolean isVoiceReport = false;

    public WeatherService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isAutoUpdate = PrefUtil.getBoolean(WeatherService.this,PrefConstantKey.AUTO_UPDATE);
        if (isAutoUpdate){
            setAutoUpdate();
            startUpdate();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setAutoUpdate(){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int intervalTime = PrefUtil.getInt(this, PrefConstantKey.UPDATE_INTERVAL_TIME);
        int milliSecond = intervalTime * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + milliSecond;
        Intent intent = new Intent(this, WeatherService.class);
        PendingIntent pi = PendingIntent.getService(this,0,intent,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }
    private void startUpdate(){
        List<CityManager> cityManagerList = CityManagerDao.queryAll();
        if (cityManagerList != null & cityManagerList.size() > 0){
            //使用数据库中的数据
            List<String> weatherIdList = new ArrayList<>();
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

    private void startVoiceReport(){

    }

    private void voiceSpeak(String msg) {
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
}
