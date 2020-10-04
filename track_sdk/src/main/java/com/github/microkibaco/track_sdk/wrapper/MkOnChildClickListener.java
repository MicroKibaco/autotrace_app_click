package com.github.microkibaco.track_sdk.wrapper;

import android.view.View;
import android.widget.ExpandableListView;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 21 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnChildClickListener implements ExpandableListView.OnChildClickListener {
    private ExpandableListView.OnChildClickListener source;

    public MkOnChildClickListener(ExpandableListView.OnChildClickListener source) {
        this.source = source;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

        SensorsDataManager.trackAdapterView(expandableListView, view, groupPosition, childPosition);

        if (source != null) {
            return source.onChildClick(expandableListView, view, groupPosition, childPosition, id);
        }

        return false;
    }
}