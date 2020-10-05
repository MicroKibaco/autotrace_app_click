### 一. SDK业务背景
&emsp;&emsp;你在开发中是否遇到过这样的场景,当点击同一个`dialog`或者`button`的时候,如果暴击多次,该`dialog`或`button`的被点击行为会被瞬间执行多次,这时候有小伙伴可能要想了,我可以做一个`view`时间戳呀,让它延迟生效。![错误的点击方式](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/57f651c251cf4ee39bef7c394bdb0e9f~tplv-k3u1fbpfcp-zoom-1.image)<br/>&emsp;&emsp;可是你们有木有想过一个问题,这么做?是不是会绑定`view`?如果工程里面有10000个点击事件需要处理,那岂不是要改10000行代码,有没有一种优雅的方式能实现如下需求呢?
 
 
 
 - **1.1** 可以设置全局控制点击行为的开关
 - **1.2** 可以动态控制点击时间戳
 - **1.3** 对`butterknife`,`kotlin`,`lambada`表达式,`dialog`,`xml`点击事件高度支持
 - **1.4** 对不需要防重复点击的事件,可以通过注解进行额外逻辑处理
 - **1.5** 可以借助沪江`aop`思想,也可以自己编写`transform plugin`实现
 - **1.6** 将打点事件带到线上,线上分析用户行为
 
> 小朋友，你是否有很多问号？

本文介绍的内容会详细解释以上问题，并在最后给解答。稳住，别慌~
 
### 二. SDK全局点击控制原理
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2735b31021854affb06c0f9abefbbca8~tplv-k3u1fbpfcp-zoom-1.image)
#### 2.0.1 全局监控`activity`生命周期

- 在app的`onCreate()`方法初始化`SDK`,然后在`ActivityLifeCycleCallBack`注册,这样就能监控`activity`所有生命周期方法了。

#### 2.0.2 获取`activity` 对应的根视图`rootview`

- 我们在该回调事件里面的对应生命周期方法如:`onActivityPaused(Activity activity)`,通过 `activity.findViewById(android.R.id.content)`方法可以拿到整块区域所对应的`rootview`,其实就是`framelayout`

#### 2.0.3 树形递归根视图`rootview`,织入埋点代码
- **2.0.3.1** 自定义`OnClickListener`派生类`WrapperOnClickListener`,实现`OnClickListener`接口
- **2.0.3.2** 逐层递归遍历`rootview`,判断当前`view`是否设置了`mOnClickListener`对象
  - **2.0.3.2.1** 如果已经设置了`mOnClickListener`并且`mOnClickListener`不是我们自定义的`WrapperOnClickListener`类型,则通过`WrapperOnClickListener`代理当前`view`设置的`mOnClickListener`
- **2.0.3.3** 在`WrapperOnClickListener`的`onClick`方法里会先调用`view`的原有`mOnClickListener`处理逻辑
  - 调用埋点代码,实现"插入"埋点,达到自动埋点效果。

### 三. SDK埋点信息介绍

```java
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

    String APP_VERSION = "$app_version";

    String APP_NAME = "$app_name";

    String SCREEN_HEIGHT = "$screen_height";

    String SCREEN_WIDTH = "$screen_width";

    String ELEMENT_POSITION = "$element_position";

    String ELEMENT_ID = "$element_id";

    String ELEMENT_ELEMENT = "$element_element";

    String MODEL = "$model";

    String LIB_VERSION = "$lib_version";

    String OS = "$os";

    String OS_VERSION = "$os_version";

    String MANUFACTURER = "$manufacturer";
    String LIB = "$lib";

}

```

### 四. SDK风险点介绍

#### 4.1 `DataBinding`绑定的函数的点击事件是无法采集的
&emsp;&emsp;`DataBinding`框架给`Button`设置`OnClickListener`对象动作稍微晚于`onActivityResumed` 回调方法,`DataBinding`还没来得及给我们`Button`对象设置`mOnClickListener`对象,我们再遍历`RootView`的时,当前`View`不满足`hasObClickListener`的判断条件,因此没有去代理`mOnClickListener`对象,给出的解决方案是给`DataBinding`框架一点延迟事件处理设置`mOnClickListener`对象操作

```java

 new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        delegateViewsOnClickListener(activity, activity.findViewById(android.R.content));
                    }
                },300);

``` 

#### 4.2 `mOnClickListener`是无法采集`MenuItem`的点击事件

&emsp;&emsp;我们通过`android.R.content`获取的`RootView`不包含`Activity`标题栏,其实也就是不包含`MenuItem`所对应的父容器的,自然当我们遍历`RootView`是无法获取`MenuItem`控件的,因此也无法代理`mOnClickListener`对象,间接导致`MenuItem`点击事件无法触发。我们可以借助`DecorView`来处理,那么什么是`DecorView`呢?

![DecorView](https://tool.oschina.net/uploads/apidocs/android/resources/articles/images/window_background_root.png)


官方解释是这样的: 

> The DecorView is the view that actually holds the window’s background drawable. Calling getWindow().setBackgroundDrawable() from your Activity changes the background of the window by changing the DecorView‘s background drawable. As mentioned before, this setup is very specific to the current implementation of Android and can change in a future version or even on another device.

&emsp;&emsp;我的理解是: `DecorView`是整个`Window`最顶层`View`,他有且只有一个字孩子`LinearLayout`,其实就包括了通知栏,标题栏,内容显示栏,`LinearLayout`里面包括两个`FrameLayout`,第一个是标题栏显示的Title,第二个`FrameLayout`才是我们所说的`android.R.content`。所以针对我么上面提到的无法采集`MenuItem`点击事件的问题,我们只需要将`activity.findViewById(android.R.content)` 换成 `activity.getWindow().getDecorView()`就可以采集到`MenuItem`的点击事件了

```java
       new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        delegateViewsOnClickListener(activity, activity.getWindow().getDecorView());
                    }
                },300);

            }
```

&emsp;&emsp;但是上报的时候,我们发现却没有Button文本信息,这是为什么呢?`element_content`这个字段没问题呀,后面我查了一下,`MenuView.ItemView`的派生类是`ActionMenuItemView`,而非`View`,所以这里需要强转一下

```java

 else if (view instanceof ActionMenuItemView) {
            text = ((ActionMenuItemView) view).getText().toString();
        } 
        
```

#### 4.3  无法采集`Button`点击,在`OnClickListener`里动态创建一个`Button`,然后通过`addView`添加到页面上,这个动态添加的`Button`无法采其点击事件

```java
ViewGroup rootView = findViewById(R.id.rootView);
        AppCompatButton button = new AppCompatButton(this);
        button.setText("动态创建的button");
        button.setOnClickListener(v -> {
            
        });
        rootView.addView(button);
```
当点击这个动态创建的 `rootView` ,当前方案是无法采集点击事件的。为啥会这样呢?这是因为我们在`Activity`的`onResume`之前去遍历整个`rootView` 并代理其`mOnClickListener`对象的,如果在`onResume`动态创建`View`当时肯定无法被遍历到的,后来我没没有再次遍历,所以它的`mOnClickListener`对就没有被代理过,因此点击控件是没有效果的,那么该怎么办呢?用`OnGlobalLayoutListener`来解决,什么是`OnGlobalLayoutListener`?官方解释是这样的:

> Interface definition for a callback to be invoked when the global layout state or the visibility of views within the view tree changes.

我的理解是:  当一个`View`视图树发生改变的时候,我没给当前的`View`设置了`OnGlobalLayoutListener`监听器,就能回调	`onGlobalLayout()`方法,基于这个原理我们可以给我们的`Activity`的`RootView`注册一个这样的监听器,这样就能实时观察视图树布局的变化呢,我们重新遍历一次`RootView`,然后找到那些没有被代理过的`OnGlobalLayoutListener`对象`View`进行代理即可解决上面的问题


```java
            private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

            @Override
            public void onActivityCreated(@NonNull final Activity activity, @Nullable Bundle savedInstanceState) {
                onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        delegateViewsOnClickListener(activity,
                                SensorsDataHelper.getRootViewFromActivity(activity, false));
                    }
                };
            }

       

            @Override
            public void onActivityResumed(@NonNull final Activity activity) {

                SensorsDataHelper
                         .getRootViewFromActivity(activity, true)
                         .getViewTreeObserver()
                         .addOnGlobalLayoutListener(onGlobalLayoutListener);
            }
            
            @Override
            public void onActivityStopped(@NonNull Activity activity) {

                /*
                 * 移除顶层Activity的监听
                 */
                SensorsDataHelper.getRootViewFromActivity(activity, false)
                        .getViewTreeObserver()
                        .removeOnGlobalLayoutListener(onGlobalLayoutListener);

            }
            // -----------------省-------------------------
             }
        
```

### 五. SDK拓展采集能力
#### **5.0.1** 怎样获取`TextView`的显示文本?
 
```java
((TextView) view).getText().toString();
```
 
#### **5.0.2** 怎样获取`ImageView`的显示文本信息?

```java

view.getContentDescription().toString();

```

#### **5.0.3** 怎样采集`CheckBox`的点击事件?

##### 5.0.3.1   自定义WrapperOnCheckedChangeListener并且 织入埋点代码

```java
public class WrapperOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

    private CompoundButton.OnCheckedChangeListener source;

    public WrapperOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener source) {
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
        SensorsDataPrivate.trackViewOnClick(compoundButton);
    }
}

```

##### 5.0.3.2 获取 CheckBox 设置的 OnCheckedChangeListener

```java
    /**
     * CheckBox  代理事件
     *
     * @param view compoundButton代理ui
     */
    public void compoundButtonItemClick(View view) {
        if (view instanceof CheckBox){
            final CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
                    SensorsDataHelper.getOnCheckedChangeListener(view);
            if (onCheckedChangeListener != null &&
                    !(onCheckedChangeListener instanceof MkOnCheckedChangeListener)) {
                ((CompoundButton) view).setOnCheckedChangeListener(
                        new MkOnCheckedChangeListener(onCheckedChangeListener));
            }
        }

    }
```


#### **5.0.4** 怎样采集`RadioGroup`的点击事件?
#### **5.0.5** 怎样采集`RattingBar`的点击事件?
#### **5.0.6** 怎样采集 `SeekBar` 的点击事件?

```java
    public void seekBarItemClick(View view) {
        final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
                SensorsDataHelper.getOnSeekBarChangeListener(view);
        if (onSeekBarChangeListener != null &&
                !(onSeekBarChangeListener instanceof MkOnSeekBarChangeListener)) {
            ((SeekBar) view).setOnSeekBarChangeListener(
                    new MkOnSeekBarChangeListener(onSeekBarChangeListener));
        }
    }
```

#### **5.0.7** 怎样采集 `Spinner` 的点击事件?
#### **5.0.8** 怎样采集 `ListView` ,`GridView` 的点击事件?
#### **5.0.9** 怎样采集 `ExpandableListView`的点击事件?
&emsp;&emsp; `ExpandableListView`是`AdapterView`的子类,同时也是`ListView`的子类,`ListView`的点击事件分为`GroupClick`和`ChildClick`,它设置的监听器也有两种,这里我们就需要增加额外的方式去做处理啦,我们来看看 `ExpandableListView`的点击事件是如何处理的吧

```java
    public static void trackAdapterView(AdapterView<?> adapterView, View view, int groupPosition, int childPosition) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(ITrackClickEvent.CANONICAL_NAME, adapterView.getClass().getCanonicalName());
            jsonObject.put(ITrackClickEvent.ELEMENT_ID, SensorsDataHelper.getViewId(adapterView));
            if (childPosition > -1) {
                jsonObject.put(ITrackClickEvent.ELEMENT_POSITION, String.format(Locale.CHINA, "%d:%d", groupPosition, childPosition));
            } else {
                jsonObject.put(ITrackClickEvent.ELEMENT_POSITION, String.format(Locale.CHINA, "%d", groupPosition));
            }
            StringBuilder stringBuilder = new StringBuilder();
            String viewText = SensorsDataHelper.traverseViewContent(stringBuilder, view);
            if (!TextUtils.isEmpty(viewText)) {
                jsonObject.put(ITrackClickEvent.ELEMENT_ELEMENT, viewText);
            }
            final Activity activity = SensorsDataHelper.getActivityFromView(adapterView);
            if (activity != null) {
                jsonObject.put(ITrackClickEvent.ACTIVITY_NAME, activity.getClass().getCanonicalName());
            }

            SensorsReporter.getSensorsDataApiInstance().track(ITrackClickEvent.APP_CLICK, jsonObject);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }
```
   &emsp;&emsp; 那么我们是如何获取`ExpandableListView`的呢?这里主要体现在 `$element_position`这个参数上,我们需要根据是`group`或者`child`做不同的逻辑处理

```java
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
```
&emsp;&emsp;先通过反射获取`mOnGroupClickListener`对象和`mOnChildClickListener`,如果`listener`不为空,并且不是我们需要的`Listener`类型,那么分别通过自定义的的`MkOnGroupClickListener`和`MkOnChildClickListener`区代理完成。

##### 5.0.9.1 `OnGroupClickListene`源码如下:

```java    
/**
 * @author 杨正友(小木箱)于 2020/10/4 15 22 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnGroupClickListener implements ExpandableListView.OnGroupClickListener {

    private ExpandableListView.OnGroupClickListener source;

    public MkOnGroupClickListener(ExpandableListView.OnGroupClickListener source) {
        this.source = source;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
        SensorsDataManager.trackAdapterView(expandableListView, view, groupPosition, -1);
        if (source != null) {
            source.onGroupClick(expandableListView, view, groupPosition, id);
        }
        return false;
    }
}
```

在其`onGroupClick`方法内部,我们首先调用埋点代码,然后调用原有的`listener`的`OnGroupClickListener`方法,这样即可实现`插入`埋点代码的效果

##### 5.0.9.2 `OnChildClickClickListener`源码如下:

```java

/**
 * @author 杨正友(小木箱)于 2020/10/4 15 21 创建
 * @Email: yzy569015640@gmail.com
 * @Tel: 18390833563
 * @function description:
 */
public class MkOnChildClickListener implements ExpandableListView.OnChildClickListener {
    private ExpandableListView.OnChildClickListener source;

    public MkOnChildClickListener(ExpandableListView.OnChildClickListener source) {
        this.source = source;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

        SensorsDataManager.trackAdapterView(expandableListView, view, groupPosition, childPosition);

        if (source != null) {
            return source.onChildClick(expandableListView, view, groupPosition, childPosition, id);
        }

        return false;
    }
}

```


#### **5.1.0** 怎样采集 `Dialog` 的点击事件?

&emsp;&emsp; 目前这种全埋点的方案是无法采集`activity`上游离的`view`,如: `Dialog` ,因为无法遍历到被点击的`view`。对于这样的`dialog`,我们可以通过如下方式解决

```java
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
```

然后在`Dialog`show之前调用即可

```java     
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setNegativeButton("取消", (dialog, which) -> {})
                .setPositiveButton("确定", (dialog, which) -> {})
                .setTitle("小木箱").setMessage("一定要加油努力哦~").create();

        SensorsReporter.getSensorsDataApiInstance().trackDialog(this,alertDialog);
```        
        
### 六. SDK缺陷

#### 6.1 由于使用反射,效率比较低,对App的整体性能有一定影响,也可能会伴随着一些兼容问题

#### 6.2 无法直接采集游离与`activity`上的`view`的点击,比如`dialog`,`popuWindow`





### 七. 参考资料

- << [Android AutoTrace Solution](https://github.com/fengcunhan/AutoTrace) >>

> 你的 点赞、评论、收藏、转发，是对我的巨大鼓励！

