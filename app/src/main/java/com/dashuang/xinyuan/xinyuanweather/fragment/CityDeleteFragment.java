package com.dashuang.xinyuan.xinyuanweather.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dashuang.xinyuan.xinyuanweather.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityDeleteFragment extends Fragment {

    private ListView lvDeleteCity;

    public CityDeleteFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_delete, container, false);
        lvDeleteCity = (ListView) view.findViewById(R.id.lv_delete_city);
        return view;
    }



}
