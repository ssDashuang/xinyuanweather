package com.dashuang.xinyuan.xinyuanweather.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dashuang.xinyuan.xinyuanweather.R;
import com.dashuang.xinyuan.xinyuanweather.dao.CityManagerDao;
import com.dashuang.xinyuan.xinyuanweather.db.CityManager;
import com.dashuang.xinyuan.xinyuanweather.db.County;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityDeleteFragment extends Fragment {

    private ListView lvDeleteCity;
    private ImageButton ibBack;

    private List<CityManager> list;
    private MyAdapter myAdapter;

    public CityDeleteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_delete, container, false);
        lvDeleteCity = (ListView) view.findViewById(R.id.lv_delete_city);
        ibBack = (ImageButton) view.findViewById(R.id.ib_delete_city_back);
        initData();
        return view;
    }

    private void initData() {
        list = CityManagerDao.queryAll();
        if (list != null && myAdapter == null){
            myAdapter = new MyAdapter();
            lvDeleteCity.setAdapter(myAdapter);
        }
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = View.inflate(getActivity(),R.layout.layout_delete_city_item,null);
            }
            final int pos = i;
            TextView tvName = (TextView) view.findViewById(R.id.tv_item_city_name);
            ImageButton ibDelete = (ImageButton) view.findViewById(R.id.ib_item_delete_city);
            tvName.setText(list.get(i).getCountyName());
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CityManager city = list.get(pos);
                    CityManagerDao.deleteCity(city.getCountyName());
                    list.remove(pos);
                    myAdapter.notifyDataSetChanged();
                }
            });
            return view;
        }
    }

}
