package com.dashuang.xinyuan.xinyuanweather;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dashuang.xinyuan.xinyuanweather.fragment.ChooseAreaFragment;

public class SettingDetailActivity extends AppCompatActivity {

    private static String fragmentKey = "fragmentName";
    public static final String addCity = "addCity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_detail);
        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra(fragmentKey);
        initFragment(fragmentName);
    }

    private void initFragment(String fragmentName) {
        FragmentManager manager= getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        switch (fragmentName){
            case addCity:
                ft.replace(R.id.setting_detail_container,new ChooseAreaFragment());
                ft.commit();
                break;
            case "":
                break;
        }
    }

    public static void startAction(Context context,String fragmentName){
        Intent intent = new Intent(context,SettingDetailActivity.class);
        intent.putExtra(fragmentKey,fragmentName);
        context.startActivity(intent);
    }
}
