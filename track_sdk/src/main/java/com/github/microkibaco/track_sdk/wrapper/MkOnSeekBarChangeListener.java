package com.github.microkibaco.track_sdk.wrapper;

import android.widget.SeekBar;

import com.github.microkibaco.track_sdk.SensorsDataManager;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 12 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private SeekBar.OnSeekBarChangeListener source;

    public MkOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener source) {
        this.source = source;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (source != null) {
            source.onStopTrackingTouch(seekBar);
        }

        SensorsDataManager.trackViewOnClick(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (source != null) {
            source.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (source != null) {
            source.onProgressChanged(seekBar, i, b);
        }
    }
}
