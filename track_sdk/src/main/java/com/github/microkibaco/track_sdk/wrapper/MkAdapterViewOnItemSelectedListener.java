package com.github.microkibaco.track_sdk.wrapper;

import android.view.View;
import android.widget.AdapterView;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 43 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkAdapterViewOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private AdapterView.OnItemSelectedListener source;

    public MkAdapterViewOnItemSelectedListener(AdapterView.OnItemSelectedListener source) {
        this.source = source;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (source != null) {
            source.onItemSelected(adapterView, view, position, id);
        }

        SensorsDataManager.trackAdapterView(adapterView, view, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if (source != null) {
            source.onNothingSelected(adapterView);
        }
    }

}
