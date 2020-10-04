package com.github.microkibaco.autotraceappclick;

import android.app.Application;

import com.github.microkibaco.track_sdk.SensorsDataAPI;

/**
 * @author 杨正友(小木箱)于 2020/10/5 00 08 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class AutoTraceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSensorsDataApi(this);
    }

    /**
     * 初始化sdk
     *
     * @param traceApplication Application
     */
    private void initSensorsDataApi(AutoTraceApplication traceApplication) {


        SensorsDataAPI.init(traceApplication);


    }
}
