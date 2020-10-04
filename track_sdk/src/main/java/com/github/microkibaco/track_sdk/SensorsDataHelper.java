package com.github.microkibaco.track_sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.appcompat.view.menu.ActionMenuItemView;

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 48 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class SensorsDataHelper {

    /**
     * 获取 View 所属 Activity
     *
     * @param view View
     * @return Activity
     */
    public static Activity getActivityFromView(View view) {
        Activity activity = null;
        if (view == null) {
            return null;
        }

        try {
            Context context = view.getContext();
            if (context != null) {
                if (context instanceof Activity) {
                    activity = (Activity) context;
                } else if (context instanceof ContextWrapper) {
                    while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                        context = ((ContextWrapper) context).getBaseContext();
                    }
                    if (context instanceof Activity) {
                        activity = (Activity) context;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }


    /**
     * 获取 view 的 android:id 对应的字符串
     *
     * @param view View
     * @return String
     */
    public static String getViewId(View view) {
        String idString = null;
        try {
            if (view.getId() != View.NO_ID) {
                idString = view.getContext().getResources().getResourceEntryName(view.getId());
            }
        } catch (Exception e) {
            //ignore
        }
        return idString;
    }


    /**
     * 获取 View 上显示的文本
     *
     * @param view View
     * @return String
     */
    public static String getElementContent(View view) {
        if (view == null) {
            return null;
        }

        String text = null;
        if (view instanceof Button) {
            text = ((Button) view).getText().toString();
        } else if (view instanceof ActionMenuItemView) {
            text = ((ActionMenuItemView) view).getText().toString();
        } else if (view instanceof TextView) {
            text = ((TextView) view).getText().toString();
        } else if (view instanceof ImageView) {
            text = view.getContentDescription().toString();
        } else if (view instanceof RadioGroup) {
            try {
                RadioGroup radioGroup = (RadioGroup) view;
                Activity activity = SensorsDataHelper.getActivityFromView(view);
                if (activity != null) {
                    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = activity.findViewById(checkedRadioButtonId);
                    if (radioButton != null) {
                        text = radioButton.getText().toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (view instanceof RatingBar) {
            text = String.valueOf(((RatingBar) view).getRating());
        } else if (view instanceof SeekBar) {
            text = String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof ViewGroup) {
            text = traverseViewContent(new StringBuilder(), view);
        }
        return text;
    }


    public static String traverseViewContent(StringBuilder stringBuilder, View root) {
        try {
            if (root == null) {
                return stringBuilder.toString();
            }

            if (root instanceof ViewGroup) {
                final int childCount = ((ViewGroup) root).getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    final View child = ((ViewGroup) root).getChildAt(i);

                    if (child.getVisibility() != View.VISIBLE) {
                        continue;
                    }
                    if (child instanceof ViewGroup) {
                        traverseViewContent(stringBuilder, child);
                    } else {
                        String viewText = getElementContent(child);
                        if (!TextUtils.isEmpty(viewText)) {
                            stringBuilder.append(viewText);
                        }
                    }
                }
            } else {
                stringBuilder.append(getElementContent(root));
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return stringBuilder.toString();
        }
    }

    /**
     * 获取 CheckBox 设置的 OnCheckedChangeListener
     */
    @SuppressWarnings("all")
    public static CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener(View view) {
        try {
            Class viewClazz = Class.forName("android.widget.CompoundButton");
            Field mOnCheckedChangeListenerField = viewClazz.getDeclaredField("mOnCheckedChangeListener");
            if (!mOnCheckedChangeListenerField.isAccessible()) {
                mOnCheckedChangeListenerField.setAccessible(true);
            }
            return (CompoundButton.OnCheckedChangeListener) mOnCheckedChangeListenerField.get(view);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            Log.getStackTraceString(e);
        }
        return null;
    }


    /**
     * 获取 View 当前设置的 OnClickListener
     * 反射获取会有效率问题
     * @param view View
     * @return View.OnClickListener
     */
    @SuppressWarnings({"all"})
    public static View.OnClickListener getOnClickListener(View view) {
        boolean hasOnClick = view.hasOnClickListeners();
        if (hasOnClick) {
            try {
                Class viewClazz = Class.forName("android.view.View");
                Method listenerInfoMethod = viewClazz.getDeclaredMethod("getListenerInfo");
                if (!listenerInfoMethod.isAccessible()) {
                    listenerInfoMethod.setAccessible(true);
                }
                Object listenerInfoObj = listenerInfoMethod.invoke(view);
                Class listenerInfoClazz = Class.forName("android.view.View$ListenerInfo");
                Field onClickListenerField = listenerInfoClazz.getDeclaredField("mOnClickListener");
                if (!onClickListenerField.isAccessible()) {
                    onClickListenerField.setAccessible(true);
                }
                return (View.OnClickListener) onClickListenerField.get(listenerInfoObj);
            } catch (ClassNotFoundException | NoSuchMethodException |
                    InvocationTargetException | IllegalAccessException
                    | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @SuppressWarnings("all")
    public static RadioGroup.OnCheckedChangeListener getRadioGroupOnCheckedChangeListener(View view) {
        try {
            Class viewClazz = Class.forName("android.widget.RadioGroup");
            Field mOnCheckedChangeListenerField = viewClazz.getDeclaredField("mOnCheckedChangeListener");
            if (!mOnCheckedChangeListenerField.isAccessible()) {
                mOnCheckedChangeListenerField.setAccessible(true);
            }
            return (RadioGroup.OnCheckedChangeListener) mOnCheckedChangeListenerField.get(view);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 Android ID
     *
     * @param mContext Context
     * @return String
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context mContext) {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }


    public static void addIndentBlank(StringBuilder sb, int indent) {
        try {
            for (int i = 0; i < indent; i++) {
                sb.append('\t');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("all")
    public static SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener(View view) {
        try {
            Class viewClazz = Class.forName("android.widget.SeekBar");
            Field mOnCheckedChangeListenerField = viewClazz.getDeclaredField("mOnSeekBarChangeListener");
            if (!mOnCheckedChangeListenerField.isAccessible()) {
                mOnCheckedChangeListenerField.setAccessible(true);
            }
            return (SeekBar.OnSeekBarChangeListener) mOnCheckedChangeListenerField.get(view);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String formatJson(String jsonStr) {
        try {
            if (null == jsonStr || "".equals(jsonStr)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            char last;
            char current = '\0';
            int indent = 0;
            boolean isInQuotationMarks = false;
            for (int i = 0; i < jsonStr.length(); i++) {
                last = current;
                current = jsonStr.charAt(i);
                switch (current) {
                    case '"':
                        if (last != '\\') {
                            isInQuotationMarks = !isInQuotationMarks;
                        }
                        sb.append(current);
                        break;
                    case '{':
                    case '[':
                        sb.append(current);
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent++;
                            addIndentBlank(sb, indent);
                        }
                        break;
                    case '}':
                    case ']':
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent--;
                            addIndentBlank(sb, indent);
                        }
                        sb.append(current);
                        break;
                    case ',':
                        sb.append(current);
                        if (last != '\\' && !isInQuotationMarks) {
                            sb.append('\n');
                            addIndentBlank(sb, indent);
                        }
                        break;
                    default:
                        sb.append(current);
                }
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
