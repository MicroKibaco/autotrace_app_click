package com.github.microkibaco.track_sdk.wrapper;

import android.widget.RadioGroup;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 22 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkRadioGroupOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup.OnCheckedChangeListener source;

    public MkRadioGroupOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener source) {
        this.source = source;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        //调用原有的 OnClickListener
        try {
            if (source != null) {
                source.onCheckedChanged(radioGroup, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //插入埋点代码
        SensorsDataManager.trackViewOnClick(radioGroup);
    }
}