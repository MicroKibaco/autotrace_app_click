package com.github.microkibaco.track_sdk;

/**
 * @author 杨正友(小木箱)于 2020/10/4 23 49 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public interface ITrackClickEvent {
    /**
     * 控件的类型
     */
    String CANONICAL_NAME = "$element_type";
    /**
     * 控件的id,即android:id属性指定的值
     */
    String VIEW_ID = "$element_id";
    /**
     * 控件显示的文本信息
     */
    String ELEMENT_CONTENT = "$element_content";

    /**
     * 当前控件所属的 Activity 页面
     */
    String ACTIVITY_NAME = "$activity";

    /**
     * 点击空间行为事件名称
     */
    String APP_CLICK = "$AppClick";
    /**
     * app版本
     */
    String APP_VERSION = "$app_version";

    String APP_NAME = "$app_name";
    /**
     * 屏幕高度
     */
    String SCREEN_HEIGHT = "$screen_height";

    /**
     * 屏幕宽度
     */
    String SCREEN_WIDTH = "$screen_width";

    String ELEMENT_POSITION = "$element_position";

    String ELEMENT_ID = "$element_id";

    String ELEMENT_ELEMENT = "$element_element";

    String MODEL = "$model";

    String LIB_VERSION = "$lib_version";

    /**
     * 手机类型
     */
    String OS = "$os";

    /**
     * 手机系统版本信息
     */
    String OS_VERSION = "$os_version";

    String MANUFACTURER = "$manufacturer";
    String LIB = "$lib";
    /**
     * 上报时间
     */
    String TIME = "time";

    String PROPERTIES = "properties";
    /**
     * 设备id
     */
    String DEVICE_ID = "device_id";
    String EVENT = "event";

}
