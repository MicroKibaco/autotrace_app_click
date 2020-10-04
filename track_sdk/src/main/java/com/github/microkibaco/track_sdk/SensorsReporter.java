package com.github.microkibaco.track_sdk;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.util.Log;
import android.view.ViewTreeObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

/**
 * @author 杨正友(小木箱)于 2020/10/4 14 01 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description: 埋点SDK主类
 */
@Keep
public class SensorsReporter {
    private final String TAG = this.getClass().getSimpleName();
    public static final String SDK_VERSION = "1.0.0";
    private static final Object LOCK = new Object();
    private volatile static SensorsReporter SENSORS_DATA_API_INSTANCE;
    private static Map<String, Object> mDeviceInfo;
    private String mDeviceId;

    public SensorsReporter(Application application){
        mDeviceId = SensorsDataHelper.getAndroidId(application);
        mDeviceInfo = SensorsDataManager.getDeviceInfo(application);
        SensorsDataManager.registerActivityLifecycleCallbacks(application);
    }

    public static SensorsReporter getSensorsDataApiInstance() {
        return SENSORS_DATA_API_INSTANCE;
    }

    /**
     * 初始化埋点SDK
     *
     * @param application 上下文
     * @return SensorsDataAPI 主类
     */
    @Keep
    public static SensorsReporter init(Application application) {
        if (SENSORS_DATA_API_INSTANCE == null) {
            synchronized (LOCK) {
                if (SENSORS_DATA_API_INSTANCE == null) {
                    SENSORS_DATA_API_INSTANCE = new SensorsReporter(application);
                }
            }

        }
        return SENSORS_DATA_API_INSTANCE;
    }

    /**
     * Trace 事件
     *
     * @param eventName  事件名称
     * @param properties 设备信息
     */
    public void track(String eventName, JSONObject properties) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ITrackClickEvent.EVENT, eventName);
            jsonObject.put(ITrackClickEvent.DEVICE_ID , mDeviceId);
            final JSONObject sendProperties = new JSONObject(mDeviceInfo);

            if (Objects.nonNull(sendProperties)) {
                SensorsDataManager.mergeJsonObject(properties, sendProperties);
            }
            jsonObject.put(ITrackClickEvent.PROPERTIES, sendProperties);
            jsonObject.put(ITrackClickEvent.TIME, System.currentTimeMillis());
            Log.i(TAG, SensorsDataHelper.formatJson(jsonObject.toString()));
        } catch (JSONException e) {
            Log.getStackTraceString(e);
        }
    }


    /**
     * Track Dialog 的点击
     * @param activity Activity
     * @param dialog Dialog
     */
    public void trackDialog(@NonNull final Activity activity, @NonNull final Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    SensorsDataManager.delegateViewsOnClickListener(activity, dialog.getWindow().getDecorView());
                }
            });
        }

    }
}
