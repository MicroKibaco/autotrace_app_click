package com.github.microkibaco.track_sdk.wrapper;

import android.widget.CompoundButton;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 16 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

    private CompoundButton.OnCheckedChangeListener source;

    public MkOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener source) {
        this.source = source;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //调用原有的 OnClickListener
        try {
            if (source != null) {
                source.onCheckedChanged(compoundButton, b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //插入埋点代码
        SensorsDataManager.trackViewOnClick(compoundButton);
    }
}
