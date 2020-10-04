package com.github.microkibaco.track_sdk.wrapper;

import android.view.View;
import android.widget.AdapterView;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 14 55 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkAdapterViewOnItemClick implements AdapterView.OnItemClickListener {
    private AdapterView.OnItemClickListener source;

    public MkAdapterViewOnItemClick(AdapterView.OnItemClickListener source) {
        this.source = source;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (source != null) {
            source.onItemClick(adapterView, view, position, id);
        }

        SensorsDataManager.trackAdapterView(adapterView, view, position);
    }
}
