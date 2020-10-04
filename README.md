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
### 四. SDK风险点介绍
### 五. SDK拓展采集能力
#### **5.0.1** 怎样获取`TextView`的显示文本?
##### 5.0.1.1 
##### 5.0.1.2 
##### 5.0.1.3
#### **5.0.2** 怎样获取`ImageView`的显示文本信息?
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
   @SuppressWarnings("all")
    private static CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener(View view) {
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
#### **5.1.0** 怎样采集 `Dialog` 的点击事件?

### 六. SDK缺陷

