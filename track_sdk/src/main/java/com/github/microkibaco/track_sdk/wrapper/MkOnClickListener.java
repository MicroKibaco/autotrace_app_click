package com.github.microkibaco.track_sdk.wrapper;

import android.view.View;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 09 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnClickListener implements View.OnClickListener {
    private View.OnClickListener source;

    public MkOnClickListener(View.OnClickListener source) {
        this.source = source;
    }

    @Override
    public void onClick(View view) {
        //调用原有的 OnClickListener
        try {
            if (source != null) {
                source.onClick(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //插入埋点代码
        SensorsDataManager.trackViewOnClick(view);
    }
}

