package com.dashuang.xinyuan.xinyuanweather.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chx on 2017/5/31.
 */

public class ToastUtil {

    public static void show(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }
}
