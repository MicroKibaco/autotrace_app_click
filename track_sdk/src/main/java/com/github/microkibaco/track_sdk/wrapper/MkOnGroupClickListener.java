package com.github.microkibaco.track_sdk.wrapper;

import android.view.View;
import android.widget.ExpandableListView;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 22 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnGroupClickListener implements ExpandableListView.OnGroupClickListener {

    private ExpandableListView.OnGroupClickListener source;

    public MkOnGroupClickListener(ExpandableListView.OnGroupClickListener source) {
        this.source = source;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
        SensorsDataManager.trackAdapterView(expandableListView, view, groupPosition, -1);
        if (source != null) {
            source.onGroupClick(expandableListView, view, groupPosition, id);
        }
        return false;
    }
}

