package com.github.microkibaco.track_sdk.wrapper;

import android.widget.RatingBar;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 23 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnRatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {
    private RatingBar.OnRatingBarChangeListener source;

    public MkOnRatingBarChangeListener(RatingBar.OnRatingBarChangeListener source) {
        this.source = source;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        //调用原有的 OnClickListener
        try {
            if (source != null) {
                source.onRatingChanged(ratingBar, v, b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //插入埋点代码
        SensorsDataManager.trackViewOnClick(ratingBar);
    }
}