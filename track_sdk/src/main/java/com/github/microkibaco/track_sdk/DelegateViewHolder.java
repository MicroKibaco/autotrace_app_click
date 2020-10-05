package com.github.microkibaco.track_sdk;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.github.microkibaco.track_sdk.wrapper.MkAdapterViewOnItemClick;
import com.github.microkibaco.track_sdk.wrapper.MkAdapterViewOnItemSelectedListener;
import com.github.microkibaco.track_sdk.wrapper.MkOnCheckedChangeListener;
import com.github.microkibaco.track_sdk.wrapper.MkOnChildClickListener;
import com.github.microkibaco.track_sdk.wrapper.MkOnGroupClickListener;
import com.github.microkibaco.track_sdk.wrapper.MkOnRatingBarChangeListener;
import com.github.microkibaco.track_sdk.wrapper.MkOnSeekBarChangeListener;
import com.github.microkibaco.track_sdk.wrapper.MkRadioGroupOnCheckedChangeListener;

import java.lang.reflect.Field;

/**
 * @author 杨正友(小木箱)于 2020/10/4 19 51 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class DelegateViewHolder {
    private static final DelegateViewHolder OURINSTANCE = new DelegateViewHolder();

    static DelegateViewHolder getInstance() {
        return OURINSTANCE;
    }

    private DelegateViewHolder() {
    }

    /**
     * spinner 代理事件
     *
     * @param view spinner代理id
     */
    public void spinnerItemClick(View view) {
        if (view instanceof Spinner){
            AdapterView.OnItemSelectedListener onItemSelectedListener =
                    ((Spinner) view).getOnItemSelectedListener();

            if (onItemSelectedListener != null &&
                    !(onItemSelectedListener instanceof MkAdapterViewOnItemSelectedListener)) {
                ((Spinner) view).setOnItemSelectedListener(
                        new MkAdapterViewOnItemSelectedListener(onItemSelectedListener));
            }
        }

    }

    /**
     * 列表代理事件
     *
     * @param view gridView代理ui
     */
    public void gridViewItemClick(View view) {
        if (view instanceof ListView || view instanceof GridView){
            AdapterView.OnItemClickListener onItemClickListener = ((AdapterView) view).getOnItemClickListener();

            if (onItemClickListener != null && !(onItemClickListener instanceof MkAdapterViewOnItemClick)) {

                ((AdapterView) view).setOnItemClickListener(new MkAdapterViewOnItemClick(onItemClickListener));

            }
        }

    }

    /**
     * CheckBox  代理事件
     *
     * @param view compoundButton代理ui
     */
    public void compoundButtonItemClick(View view) {
        if (view instanceof CompoundButton){
            final CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
                    SensorsDataHelper.getOnCheckedChangeListener(view);
            if (onCheckedChangeListener != null &&
                    !(onCheckedChangeListener instanceof MkOnCheckedChangeListener)) {
                ((CompoundButton) view).setOnCheckedChangeListener(
                        new MkOnCheckedChangeListener(onCheckedChangeListener));
            }
        }

    }

    /**
     * ExpandableListView代理事件
     *
     * @param view ExpandableListView代理view
     */
    public void expandableItemClick(View view) {
       if (view instanceof  ExpandableListView){
           try {

               final Class viewClazz = Class.forName("android.widget.ExpandableListView");

               // ---------------------------------------Child---------------------------------------
               Field mOnChildClickListenerField = viewClazz.getDeclaredField("mOnChildClickListener");

               if (!mOnChildClickListenerField.isAccessible()) {

                   mOnChildClickListenerField.setAccessible(true);

               }
               ExpandableListView.OnChildClickListener onChildClickListener =
                       (ExpandableListView.OnChildClickListener) mOnChildClickListenerField.get(view);

               if (onChildClickListener != null && !(onChildClickListener instanceof MkOnChildClickListener)) {

                   ((ExpandableListView) view).setOnChildClickListener(new MkOnChildClickListener(onChildClickListener));

               }

               // ---------------------------------------Group---------------------------------------
               Field mOnGroupClickListenerField = viewClazz.getDeclaredField("mOnGroupClickListener");

               if (!mOnGroupClickListenerField.isAccessible()) {
                   mOnGroupClickListenerField.setAccessible(true);
               }

               ExpandableListView.OnGroupClickListener onGroupClickListener =
                       (ExpandableListView.OnGroupClickListener) mOnGroupClickListenerField.get(view);
               if (onGroupClickListener != null && !(onGroupClickListener instanceof MkOnGroupClickListener)) {

                   ((ExpandableListView) view).setOnGroupClickListener(new MkOnGroupClickListener(onGroupClickListener));

               }

           } catch (Exception e) {
               Log.getStackTraceString(e);
           }
       }
    }

    /**
     * RadioGroup 代理事件
     *
     * @param view RadioGroup代理view
     */
    public void radioGroupItemClick(View view) {
        if (view instanceof RadioGroup) {
            final RadioGroup.OnCheckedChangeListener radioOnCheckedChangeListener =
                    SensorsDataHelper.getRadioGroupOnCheckedChangeListener(view);
            if (radioOnCheckedChangeListener != null &&
                    !(radioOnCheckedChangeListener instanceof MkRadioGroupOnCheckedChangeListener)) {
                ((RadioGroup) view).setOnCheckedChangeListener(
                        new MkRadioGroupOnCheckedChangeListener(radioOnCheckedChangeListener));
            }
        }
    }

    /**
     * seekBar 代理事件
     *
     * @param view seekBar代理id
     */
    public void seekBarItemClick(View view) {
        if (view instanceof SeekBar) {
            final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
                    SensorsDataHelper.getOnSeekBarChangeListener(view);
            if (onSeekBarChangeListener != null &&
                    !(onSeekBarChangeListener instanceof MkOnSeekBarChangeListener)) {
                ((SeekBar) view).setOnSeekBarChangeListener(
                        new MkOnSeekBarChangeListener(onSeekBarChangeListener));
            }
        }

    }

    /**
     * RatingBar 代理事件
     *
     * @param view RatingBar代理id
     */
    public void ratingBarItemClick(View view) {
        if (view instanceof RatingBar) {
            final RatingBar.OnRatingBarChangeListener onRatingBarChangeListener =
                    ((RatingBar) view).getOnRatingBarChangeListener();
            if (onRatingBarChangeListener != null &&
                    !(onRatingBarChangeListener instanceof MkOnRatingBarChangeListener)) {
                ((RatingBar) view).setOnRatingBarChangeListener(
                        new MkOnRatingBarChangeListener(onRatingBarChangeListener));
            }
        }
    }
}
