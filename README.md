# AndroidX
Android开发框架（Android development integration tools）
注意：2021-01-01开始的是2.0版本不向下兼容1.0-2.0之间的版本，旧版本请使用2.0以下版本，2.0以下版本是兼容1.0-2.0之间的版本,2.0以上版本进行了代码重构，在2.0以后版本完全兼容2.0以上版本。
## Maven
1.build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:AndroidX:2.0.0'
}
```

## Application
CoreApplication为AndroidX核心Application,开发者需要继承CoreApplication,然后初始化Http工具.
```
public class App extends CoreApplication {

    @Override
    public void onCreate() {
         super.onCreate();
         //初始化Http,设置调试模式为true;
         initHttp(true);
     }

}
```

## AndroidManifest.xml

1.常用权限配置，根据项目而定。
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA"></uses-permission>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
```

2.Provider配置，主要用于文件处理，图片选择。采取了Android10沙盒分区处理。
```
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:grantUriPermissions="true"
    android:exported="false">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/filepaths" />
</provider>
```

## Style.xml

1.没有ActionBar+背景白色的主题
```
Android.Theme.Light.NoActionBar
```

2.没有ActionBar+背景透明的主题
```
Android.Theme.Transparent.NoActionBar
```

3.没有ActionBar+状态栏半透明的主题
```
Android.Theme.Light.NoActionBa.StatusBar.Translucent
```

4.没有覆盖背景的Dialog
```
Android.Theme.Dialog.Transparent.Background
```

5.有覆盖背景的Dialog
```
Android.Theme.Dialog.Translucent.Background
```

6.横向ProgressBar
```
Android_ProgressBar_Horizontal
```

7.Dialog缩放动画
```
Android_Window_Animation_Scale
```

8.Dialog底部动画
```
Android_Window_Animation_Bottom
```

## View findViewById

1.注解
```
@ViewInject(R.id.et_user)
private EditText et_user;
```
2.类型强转
```
findViewById(EditText.class,R.id.et_user).setText("user");
```

## Toast
以下方法直接在Activity extends CoreActivity和Fragment extends CoreFragment可直接调用

1.页面底部显示
```
showToast("加载成功");
```
2.页面中间带状态图标显示
```
showStatus(Toast.Status.SUCCESS, "支付成功");
showStatus(Toast.Status.WARNING, "支付失败");
showStatus(Toast.Status.WIRELESS, "联网失败");
```
## RSA
RSA加密解密工具

1.公钥配置
```
RSA.PUBLIC_KEY = "xxx";
```

2.密钥配置
```
RSA.PRIVATE_KEY = "xxx";
```

3.加密
```
String encode = RSA.encrypt("xxx");
```

4.解密
```
String encode = RSA.decrypt("xxx");
```

## Http
在使用Http之前一定需要在Application里面初始化，如果继承了CoreApplication
只需要调用CoreApplication 里面的initHttp方法即可初始化Http工具。
AndroidX中Activity和Fragment已经实现了OnHttpListener接口，只需要重写
public void onHttpRequest()；页面进入会进入自动调用。
public void onHttpFailure(ResponseBody responseBody)；数据请求失败。
public void onHttpSucceed(ResponseBody responseBody)；数据请求请求成功。

1.初始化,如果继承了CoreApplication可忽略此操作，与CoreApplication里面方法initHttp(true)作用等同。
默认采用的Json数据传输格式，如需修改对应传递方式，支持全局修改：options.contentType(Header.CONTENT_JSON);
如果单个接口请求方式修改：params.addHeader(Header.CONTENT_TYPE,Header.CONTENT_FORM);
```
RequestOptions options = new RequestOptions(this);
options.debug(debug);
options.cache(true);
options.contentType(Header.CONTENT_JSON);
options.type(RequestOptions.OK_HTTP);
Http.init(options);
```

2.常规使用

2.1接口类
```
import com.androidx.net.Http;
import com.androidx.net.OnHttpListener;
import com.androidx.net.RequestParams;

public class MainApi {

    /**
     * 登录
     *
     * @param accountNumber 账号
     * @param pwd           密码
     * @param listener
     */
    public void accountAndPwdLogin(String accountNumber, String pwd, OnHttpListener listener) {
        RequestParams params = new RequestParams();
        params.add("accountNumber", accountNumber);
        params.add("pwd", pwd);
        Http.post(Constants.BASE_URL + "/storeApi/login/accountAndPwdLogin", params, listener);
    }

}
```
2.2接口使用(CoreActivity|CoreFragment)

```
private MainApi mainApi;

@Override
protected void onCreate(Bundle bundle, NavigationBar bar) {
    mainApi = new MainApi();
}

@Override
public void onHttpRequest() {
    super.onHttpRequest();
    showLoading();
    mainApi.accountAndPwdLogin("account","password",this);
}

@Override
public void onHttpSucceed(ResponseBody responseBody) {
    dismissLoading();
    //Json解析,Body为公共数据实体类。
    Body body = Json.parseJSONObject(Body.class, responseBody.body());
    if (body.getCode().equals("0")) {
        if (response.url().contains("accountAndPwdLogin")) {
            Map<String, String> map = Json.parseJSONObject(body.getData());
            String token = map.get("token");
            //全局添加token.
            Http.options().header().add("token", token);
            showToast(Json.parseObject(body));
         }
    } else {
        showToast(body.getMsg());
    }
}
```

## StatusBar
支持对沉浸状态设置、状态栏颜色、字体颜色修改（深色、浅色）

1.设置状态栏颜色
```
StatusBar.setColor(this, Color.parseColor("#3C3F41"));
```
2.设置状态栏文字是否黑色
```
StatusBar.setTextColor(this,false);
```
3.状态栏高度
```
int height = StatusBar.height(this);
```
4.显示状态栏
```
StatusBar.show(this);
```
5.隐藏状态栏
```
StatusBar.hide(this);
```

## NavigationBar
Activity extends CoreActivity | Fragment extends CoreFragment
这2个类里面都有自定义的NavigationBar,只需要重写onCreate(Bundle bundle, NavigationBar bar)

1.设置背景颜色
```
//方式一
navigationBar.setBackgroundColor(Color.parseColor("#3C3F41"));
//方式二
navigationBar.setBackgroundResource(R.color.colorPrimary);
//只修改NavigationBar背景颜色，不修改StatusBar颜色。
navigationBar.setBackgroundColor(Color.parseColor("#3C3F41"),false);
navigationBar.setBackgroundResource(R.color.colorPrimary,false);
```

2.设置标题
```
navigationBar.setTitle("标题");
navigationBar.setTitleTextColor(Color.WHITE);
navigationBar.setTitleTextSize(18, TypedValue.COMPLEX_UNIT_SP);
```

3.设置返回
```
navigationBar.setBackText("返回");
navigationBar.setBackResource(R.mipmap.xxx);
```

4.设置菜单
```
navigationBar.setMenuText("xxxx");
navigationBar.setMenuResource(R.mipmap.xxx);
```

5.设置监听
```
navigationBar.setOnNavigationBarClickListener(new NavigationBar.OnNavigationBarClickListener() {
    @Override
    public void onNavigationBarClick(View v, int operate) {
       if (operate == NavigationBar.BACK_TEXT) {

       }
       f (operate == NavigationBar.BACK_IMAGE) {

       }
       if (operate == NavigationBar.MENU_TEXT) {

       }
       if (operate == NavigationBar.MENU_IMAGE) {

       }
    }
});
```

6.显示
```
navigationBar.show();
```

7.隐藏
```
navigationBar.hide();
```

### Activity

Activity页面只需要继承CoreActivity,如果是TV研发需要继承TVCoreActivity
```
public class MainActivity extends CoreActivity implements NavigationBar.OnNavigationBarClickListener {

    @ViewInject(R.id.et_user)
    private EditText et_user;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle bundle, NavigationBar bar) {
        bar.setBackText("✖");
        bar.setBackgroundColor(Color.parseColor("#3C3F41"));
        bar.setTitleTextColor(Color.WHITE);
        bar.setOnNavigationBarClickListener(this);
        StatusBar.setColor(this, Color.parseColor("#3C3F41"));
    }

    @Override
    public void onNavigationBarClick(View v, int operate) {
        if (operate == NavigationBar.BACK_IMAGE) {

        }
    }

    @Override
    public void onHttpRequest() {
        super.onHttpRequest();

    }

    @Override
    public void onHttpSucceed(ResponseBody responseBody) {
        super.onHttpSucceed(responseBody);
    }

    @Override
    public void onHttpFailure(ResponseBody responseBody) {
        super.onHttpFailure(responseBody);
    }
```

## Fragment

Fragment页面只需要继承CoreFragment,如果是TV研发需要继承TVCoreFragment
```
public class MainFragment extends CoreFragment {

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        bar.setTitle("CoreFragment");
        bar.setBackgroundResource(R.color.colorAccent,false);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void setParameters(Object params) {
        super.setParameters(params);
    }

    @Override
    public void onHttpRequest() {
        super.onHttpRequest();
    }

    @Override
    public void onHttpSucceed(ResponseBody responseBody) {
        super.onHttpSucceed(responseBody);
    }

    @Override
    public void onHttpFailure(ResponseBody responseBody) {
        super.onHttpFailure(responseBody);
    }
}

```
## SwipeRequestLayout

刷新加载控件，支持上拉加载更多，下拉刷新。

1.xml
```
<?xml version="1.0" encoding="utf-8"?>
<com.androidx.widget.SwipeRequestLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.recyclerview.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </androidx.recyclerview.widget.RecyclerView>

</com.androidx.widget.SwipeRequestLayout>
```

2.设置监听
```
swipeRequestLayout.setOnSwipeRefreshListener(new SwipeLayout.OnSwipeRefreshListener() {
    @Override
    public void onSwipeRefresh() {
        //1.刷新释放会进入；
        //2.setRefreshing(true)会触发
    }
});
swipeRequestLayout.setOnSwipeLoadListener(new SwipeLayout.OnSwipeLoadListener() {
    @Override
    public void onSwipeLoad() {
        //1.上拉释放进入
        //2.setLoading(true)会触发
    }
});
```

3.进入页面开始获取数据,setRefreshing(true),会调用onSwipeRefresh();方法。
```
swipeRequestLayout.setRefreshing(true);
```

4.停止刷新
```
swipeRequestLayout.setRefreshing(false);
```

5.停止加载
```
swipeRequestLayout.setLoading(false);
```

6.设置禁用刷新
```
swipeRequestLayout.setRefreshable(false);
```

7.设置禁用加载
```
swipeRequestLayout.setLoadable(false);
```

## ShapeButton
支持圆角自定义，可调整状态显示（Button、Text -> androidx:state="button"）
```
<com.androidx.view.ShapeButton
     android:layout_width="match_parent"
     android:layout_height="45dp"
     android:layout_marginLeft="10dp"
     android:layout_marginTop="10dp"
     android:layout_marginRight="10dp"
     android:onClick="onItemSelector"
     androidx:state="button"
     android:text="ItemSelector"
     android:textColor="@color/colorWhite"
     androidx:radius="5dp"
     androidx:solid="@color/colorBlack41" />
```
## TextGroupView

ImageView + TextView + TextView + TextView + ImageView + EditText组合控件
多用于横排布局，左边图标，中间文字或者输入框，右边箭头类似的布局。
详细：https://github.com/RelinRan/TextGroupView
```
    <com.android.view.TextGroupView
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginLeft="20dp"
        android:layout_marginRight="20dp"
        android:layout_marginTop="20dp"
        app:left_imageMarginLeft="10dp"
        app:left_imageSrc="@drawable/text_group_view_ic_head"
        app:left_imageWidth="50dp"
        app:left_textPaddingLeft="10dp"
        app:radius="8dp"
        app:right_imagePaddingRight="10dp"
        app:right_imageSrc="@drawable/text_group_view_ic_arrow"
        app:right_text="更换头像"
        app:right_textColor="#FFFFFF"
        app:solid="#161538"></com.android.view.TextGroupView>
```
##  BannerPager
轮播图,支持指示器位置自定义，显示自定义。
```
<com.androidx.view.BannerPager
     android:id="@+id/banner"
     android:visibility="gone"
     android:layout_marginTop="10dp"
     android:layout_marginLeft="10dp"
     androidx:isAutoPlay="true"
     android:layout_marginRight="10dp"
     android:layout_width="match_parent"
     android:layout_height="200dp"></com.androidx.view.BannerPager>
```

1.设置数据
```
    private BannerPager banner;
    private BannerListAdapter bannerListAdapter;

    public void initBanner() {
        if (banner == null) {
            banner = findViewById(R.id.banner);
            bannerListAdapter = new BannerListAdapter(this);
            bannerListAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener<Integer>() {
                @Override
                public void onItemClick(BannerAdapter<Integer> adapter, View view, Integer item, int position) {
                    Log.i("RRL", "->onItemClick -1- item=" + item + ",position=" + position);
                    int realPosition = adapter.getRealPosition(position);
                }
            });
        }
        int resIds[] = {R.mipmap.ic_banner_0, R.mipmap.ic_banner_1, R.mipmap.ic_banner_2, R.mipmap.ic_banner_3};
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < resIds.length; i++) {
            list.add(resIds[i]);
        }
        banner.setAdapter(bannerListAdapter);
        bannerListAdapter.setItems(list);
    }

    private class BannerListAdapter extends BannerAdapter<Integer> {

        public BannerListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onItemBindViewHolder(ViewHolder holder, Integer item, int position) {
            ImageView imageView = holder.find(R.id.banner_image);
            holder.find(ImageView.class, R.id.banner_image).setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(item);
        }

    }

```

2.轮播持续时间
```
androidx:duration="3000"
```

3.指示器显示位置
```
androidx:indicatorGravity="bottom|center_horizontal"
```

4.指示器显示位置
```
androidx:indicatorGravity="bottom|center_horizontal"
```

5.指示器选中背景
```
androidx:indicatorSelected="@drawable/android_indicator_selected"
```

6.指示器未选中背景
```
androidx:indicatorUnSelected="@drawable/android_indicator_unselected"
```
6.自动轮播
```
androidx:isAutoPlay="true"
```

## ClearEditTextView
快速清除内容的EditTextView
```
<com.androidx.view.ClearEditTextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:drawableRight="@drawable/android_ic_clear_text">
</com.androidx.view.ClearEditTextView>
```
1.清除按钮图标
```
android:drawableRight="@drawable/android_ic_clear_text"
```

## CircleTimerView
圆圈计时器
```
<com.androidx.view.CircleTimerView
     android:layout_width="100dp"
     android:layout_height="100dp"
     android:layout_gravity="center"
     android:layout_marginTop="20dp"
     androidx:button="false"
     androidx:duration="1000"
     androidx:strokeWidth="15dp"
     androidx:insideColor="#3A8681"
     androidx:millisInFuture="250"
     androidx:outsideColor="#C4C4C4">
</com.androidx.view.CircleTimerView>
```

1.持续时长
```
androidx:duration="1000"
```

2.当前时长
```
androidx:millisInFuture="250"
```

3.线条宽度
```
androidx:strokeWidth="15dp"
```

4.外部颜色
```
androidx:outsideColor="#C4C4C4"
```

5.内部颜色
```
androidx:insideColor="#C4C4C4"
```

6.是否是按钮
```
androidx:button="false"
```

## CircleProgressView
圆圈进度

```
<com.androidx.view.CircleProgressView
     android:layout_width="100dp"
     android:layout_height="100dp"
     android:layout_gravity="center">
</com.androidx.view.CircleProgressView>
```

1.进度背景颜色
```
androidx:progressBackgroundColor="#DEDEDE"
```

2.进度颜色
```
androidx:progressColor="#DEDEDE"
```

3.文字是否可见
```
androidx:progressTextVisibility="visible"
```

4.文字颜色
```
androidx:progressTextColor="@color/colorPrimary"
```

5.文字大小
```
androidx:progressTextSize="14sp"
```

## AlphabetView
字母列表
```
<com.androidx.view.AlphabetView
   android:layout_width="wrap_content"
   android:layout_height="wrap_content">
</com.androidx.view.AlphabetView>
```

1.文字默认颜色
```
android:textColor="@color/colorPrimary"
```

2.文字选中颜色
```
app:checkColor="@color/colorPrimary"
```

3.文字大小
```
android:textSize="14sp"
```

## StatusView
状态（成功、警告、网络）
```
<com.androidx.view.StatusView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:status="success">
</com.androidx.view.StatusView>
```

1.状态类型
```
app:status="success"
```

2.颜色
```
android:color="@color/colorPrimary"
```

3.圆圈线条宽度，成功|警告类型使用
```
app:circleStrokeWidth="1dp"
```

4.是否使用动画
```
app:isAnimator="false"
```

## PhotoView

## RecyclerAdapter
RecyclerView使用的Adapter,可快速绑定View对应数据。
```
    private class RecyclerListAdapter extends RecyclerAdapter<String> {

        public RecyclerListAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutResId() {
            return R.layout.item_text;
        }

        @Override
        protected void onItemBindViewHolder(ViewHolder holder, String item, int position) {
            holder.find(TextView.class, R.id.tv_label).setText(item);
            holder.addItemClick(R.id.tv_label);
        }
    }
```

## BasisAdapter

实用于ListView、GridView、FlowListView，在BaseAdapter基础上升级封装，可快速构建列表。
```
    private class DebugAdapter extends BasisAdapter<ResponseBody> {

        public DebugAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutResId() {
            return R.layout.android_debug_item;
        }

        @Override
        public void onItemBindViewHolder(ViewHolder holder, ResponseBody item, int position) {
            holder.find(TextView.class, R.id.debug_row_page_value).setText(item.page());
            holder.find(TextView.class, R.id.debug_row_time_value).setText(item.time());
            holder.find(TextView.class, R.id.debug_row_url_value).setText(item.url());
            holder.find(TextView.class, R.id.debug_row_header_value).setText(item.requestParams().header().toString());
            holder.find(TextView.class, R.id.debug_row_params_value).setText(item.requestParams().params().toString());
            holder.find(TextView.class, R.id.debug_row_result_value).setText(item.body());
        }

    }
```

## RecyclerView 分割线/间隔设置
```
recyclerView.addItemDecoration(new DividerItemDecoration(LinearLayoutManager.HORIZONTAL, ContextCompat.getColor(getContext(), R.color.colorDivider), 2));

recyclerView.addItemDecoration(new SpaceItemDecoration(LinearLayoutManager.HORIZONTAL, 2));
```
## DataStorage
数据缓存，目前支持int string double float long Set Map<String,String> List<Map<String,String>>数据类型

1.存数据
```
DataStorage.with(context).put("username","xxxx");
```

2.取数据
```
String username = DataStorage.with(context).getString("username","");
```

## UseCache
常用缓存,主要用户登录页面或者主页存储token和文件前缀地址。

1.存token
```
UseCache.token("xxx");
```

2.取token
```
String token = UseCache.token();
```

3.存url
```
UseCache.url("xxx");
```
4.取url
```
String url = UseCache.url();
```

5.拼接完整url
```
String endUrl = "xxx";
String completeUrl = UseCache.joinUrl(endUrl);
```

## Time
时间工具类
1.现在时间
```
String now = Time.now(Time.DATE_FORMAT_YYYY_MM_DD);
```
2.时间戳转时间
```
String time = Time.parseFromTimestamp(timestamp);
String time = Time.parseFromTimestamp(timestamp,Time.DATE_FORMAT_YYYY_MM_DD);
```
3.时间字符串转时间对象
```
Date date = Time.parse("2019-09-10 09:00:10");
Date date = Time.parse("2019-09-10 09:00:10",Time.DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS);
```

## Log
日志，使用方法跟系统的一致，只是为了打印长字符的时候能够打印完全做了换行打印。
1.一般格式打印
```
Log.i("tag","content");
```
2.带标题格式打印
```
Log.i("tag","header","content");
```

## Decimal
小数位数处理，通常用于价格保留2位小数；四舍五入功能使用

1.保留2位小数
```
String value = Decimal.format(2.351F,2);
```

2.保留2位小数,五入
```
String value = Decimal.format("2.351",2,Decimal.ROUND_HALF_UP);
```

3.保留2位小数,四舍
```
String value = Decimal.format("2.351",2,Decimal.ROUND_HALF_DOWN);
```

3.EditText限制输入2位数,最大输入10位
```
EditText etPrice = findViewById(R.id.et_price);
Decimal.format(etPrice,2,10);
```

## Null
为空判断和处理

1.是否为空，包含null,"null","",3中为空情况
```
boolean isNull = Null.isNull("xxx");
```

2.空值处理为""
```
String value = Null.value("xxx");
```

## Number
兼容性数字转换
```
//为空自动转为0
int intValue = Number.parseInt("null");
//Float自动加".00"
float floatValue = Number.parseFloat("2");
//Double自动加".00"
double doubleValue = Number.parseDouble("34");
```

## Language
语言工具

1.获取系统语言
```
Locale systemLanguage = Language.getSystem();
```

2.获取应用语言
```
Locale applicationLanguage = Language.getApplication(context);
```

3.修改语言
```
Language.update(context,Locale.US);
```

4.判断语言是否相同
```
boolean isSame =  Language.compare(source,target);
```

4.判断是否是中文
```
Locale locale = Language.getSystem();
boolean isChinese =  Language.isChinese(locale);
```

## WebLoader
网页加载器

1.加载URL
```
WebLoader.Builder builder = new WebLoader.Builder(webView);
builder.url("https://xxxxxxx");
builder.build();
```

2.加载Html数据
```
WebLoader.Builder builder = new WebLoader.Builder(webView);
builder.data("Html data");
builder.build();
```

3.加载Html数据,设置图片点击事件
```
WebLoader.Builder builder = new WebLoader.Builder(webView);
builder.data("Html data");
builder.imageClickListener(new WebLoader.OnWebImageClickListener() {
@Override
public void onWebImageClick(String url) {

}
});
builder.build();
```

4.加载Html数据,设置图片适应屏幕宽度
```
WebLoader.Builder builder = new WebLoader.Builder(webView);
builder.data("Html data");
builder.imageFit(true);
builder.build();
```

## ActivityManager
项目有时候需要在一个页面finish的时候杀死之前的页面，那么此时就需要这个类，
注意如需要单个使用这个类在自己框架，需要在自己BaseActivity中使用方法ActivityManager.getInstance().add(xxx);

1.添加页面
```
ActivityManager.getInstance().add(MainActivity.class);
```
2.清除所有页面，包含当前页面
```
ActivityManager.getInstance().removeAll();
```
3.清除单个页面
```
ActivityManager.getInstance().remove(MainActivity.class);
```
4.退出程序
```
ActivityManager.getInstance().exit(context);
```

## Bug
异常捕捉
注意：使用这个类需要提前申请文件写入、读取权限，在Android 6.0需要动态申请权限。
```
Bug.Builder builder = new Bug.Builder(this);
builder.name(Time.now()+".txt");
builder.listener(new OnBugListener() {
    @Override
    public void onBug(File file, String bug) {

    }
});
builder.build();
```
## Badge
APP桌面角标-红色圆点
主要是显示红色圆点，但是不支持所有手机类型，目前支持小米、华为、三星、索尼。
Badge已经做了缓存处理，同时去区别了多个项目在一个手机的缓存区别。

1.增加数量
```
Badge.add(cntext);
```
2.重置数量
```
Badge.reset(context);
```
3.设置数量
```
Badge.setNumber(context,number);
```
4.获取数量
```
int number = Badge.number(context);
```
## CoreDialog
快速自定义对话框
```
CoreDialog.Builder builder = new CoreDialog.Builder(context);
builder.layoutResId(R.layout.xxxx);
builder.width(LinearLayout.LayoutParams.MATCH_PARENT);
builder.width(LinearLayout.LayoutParams.WRAP_CONTENT);
builder.cancelable(false);
builder.canceledOnTouchOutside(false);
builder.animResId(CoreDialog.ANIM_BOTTOM);
builder.themeResId(CoreDialog.THEME_TRANSLUCENT);
builder.gravity(Gravity.BOTTOM);
CoreDialog dialog = builder.build();
dialog.show();
```
## AlterDialog
1.单按钮
```
    new AlertDialog.Builder(AndroidKit.this).msg("你是在测试我吗？").cancel("取消").confirm("确认").listener(null).build().show();
```
2.双按钮
```
new AlertDialog.Builder(AndroidKit.this).msg("你是在测试我吗？").confirm("确认").listener(null).build().show();
```
## AddressSelector
地址选择器
```
new AddressSelector.Builder(AndroidKit.this).listener(new OnAddressSelectListener() {
@Override
public void onAddressSelected(String province, String city, String district, String provinceId, String cityId, String districtId) {
        Log.e("Relin", province + city + district + "-" + provinceId + "," + cityId + "," + districtId);
        showToast(province + city + district + "-" + provinceId + "," + cityId + "," + districtId);
    }
}).build().show();

```
## DateSelector
日期选择器
```
new DateSelector.Builder(AndroidKit.this).type(DateSelector.TYPE_DATE).listener(new OnDateSelectListener() {
    @Override
    public void onDateSelected(String date) {
        showToast(date);
    }
}).year(1992).month(12).day(24).build().show();
```
## DocumentSelector
文件选择器
```
private DocumentSelector documentSelector;
public void startDocumentSelector(){
    DocumentSelector.Builder builder = new DocumentSelector.Builder(this);
    builder.mode(DocumentSelector.MODE_IMAGE_CAPTURE);
    builder.listener(new OnDocumentSelectListener() {
        @Override
        public void onDocumentSelect(DocumentSelector selector, Uri uri, String path) {
            int code = getContentResolver().delete(uri,null,null);
            Log.i("RRL","->onDocumentSelect path="+path+" , code = "+code);
        }
    });
    documentSelector = builder.build();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    documentSelector.onActivityResult(requestCode,resultCode,data);
}
```
## ItemDialog
选择弹框,工具可以单个字段列表数据选择
```
List<ItemDialogBody> bodies = new ArrayList<>();
String names[] = new String[]{"重庆邮电大学", "重庆大学", "重庆科技大学", "重庆交通大学"};
for (int i = 0; i < names.length; i++) {
    ItemDialogBody body = new ItemDialogBody();
    body.setName(names[i]);
    bodies.add(body);
}
ItemDialog.Builder itemBuilder = new ItemDialog.Builder(AndroidKit.this);
itemBuilder.title("选择大学");
itemBuilder.bodies(bodies);
itemBuilder.listener(new ItemDialog.OnItemDialogClickListener() {
    @Override
    public void onItemDialogClick(Dialog dialog, List<ItemDialogBody> bodies, int position) {
        showToast(bodies.get(position).getName());
    }
});
itemBuilder.build();
```

## ItemSelector
底部列表选择器
```
ItemSelector.Builder builder = new ItemSelector.Builder(context);
builder.items(new String[]{"A", "B", "C"});
builder.listener(new OnItemSelectListener() {
@Override
public void onItemSelect(String content, int position) {
        showToast(content + " - " + position);
    }
});
builder.build();
```
## Update
应用更新，支持进度条显示。
```
Update.show(context, R.drawable.ic_launcher, "项目名称", "http://192.168.1.33:8080/app/SG_201906171.apk", "您有有新版本是否更新？", true);
```
## Downloader
下载器，HucDownloader主要采用的是HttpUrlConnection、Downloader只要采用的是OkHttp，但是两者使用方法一致，都是采用Builder模式。

```
Downloader.Builder builder = new Downloader.Builder(context);
builder.isBreakpoint(false);
builder.url("https://xxx");
builder.listener(new OnDownloadListener() {
    @Override
    public void onDownloading(long total, long progress, int percent) {

    }

    @Override
    public void onDownloadCompleted(File file) {

    }

    @Override
    public void onDownloadFailed(Exception e) {

    }
});
builder.build();
```
## Uploader
文件上传工具
```
Uploader.Builder builder = new Uploader.Builder();
builder.url(Constants.BASE_URL + "/appApi/file/uploadImage");
UploadParams params = new UploadParams();
params.addHeader("token", Token.value());
params.add("file", file);
builder.listener(listener);
builder.params(params);
builder.mediaType(Uploader.MEDIA_TYPE_FORM);
builder.build();
```
## SQLite
数据库操作

1.创建表
```
//方式1
SQLite.with(context).createTable("table_name",new String[]{"column_name_a","column_name_b"});
//方式2
User user = new User();
SQLite.with(context).createTable(user);
```
2.删除表格数据（不删除表）
```
//方式1
SQLite.with(context).deleteTable("table_name");
//方式2
SQLite.with(context).deleteTable(User.class);
//方式3
SQLite.with(context).dropTable("table_name");//删除表
```
3.删除数据
```
//方式1
SQLite.with(context).delete("sql");
//方式2
SQLite.with(context).delete(User.class,"user_id=?",new String[]{"1"});
//方式3
SQLite.with(context).delete("table_name","user_id=?",new String[]{"1"});
```
4.查询
```
//方式1
List<Map<String, String>> list = SQLite.with(context).query("sql");
//方式2
List<User> list = SQLite.with(context).query(User.class,"sql");
```
5.插入数据
```
//方式1
User user = new User();
SQLite.with(context).insert(user);
//方式2
SQLite.with(context).insert("sql");
//方式3
ContentValues values = new ContentValues();
values.put("user_id","1");
values.put("user_name","name");
SQLite.with(context).insert("table_name",values);
```
6.更新数据
```
//方式1
SQLite.with(context).update("sql");
//方式2
User user = new User();
user.setUserName("Name");
ContentValues values = new ContentValues();
values.put("user_id","1");
values.put("user_name","name");
SQLite.with(context).update(user,values,"user_id=?",new String[]{"1"});
```

## VideoRecordAty
视频录制
1.AndroidManifest.xml配置
```
<activity android:name="com.androidx.video.VideoRecordAty"></activity>
```
2.跳转页面
```
Bundle bundle = new Bundle();
//限制录制多少秒，如果不限制就传0
bundle.putLong(VideoRecordAty.VIDEO_DURATION,60*1000);
startActivityForResult(VideoRecordAty.class,520,bundle);
```
3.处理结果
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode==RESULT_OK&&requestCode==520){
        //视频信息
        String path = data.getStringExtra(VideoRecordAty.VIDEO_PATH);
        String width = data.getStringExtra(VideoRecordAty.VIDEO_WIDTH);
        String height = data.getStringExtra(VideoRecordAty.VIDEO_HEIGHT);
        String duration = data.getStringExtra(VideoRecordAty.VIDEO_DURATION);
    }
}
```

## ImageProvider
图片提供者，创建图片文件、File和Bitmap转换、图片压缩

1.创建图片
```
File file = ImageProvider.buildFile(context,Environment.DIRECTORY_PICTURES,".jpg");
```

2.Bitmap转byte[]
```
byte[] data = ImageProvider.getBytes(bitmap);
```

3.获取图片角度
```
int angle = ImageProvider.angle(path);
```

4.旋转图片
```
Bitmap bitmap = ImageProvider.rotate(String pathName, int angle, int outWidth, int outHeight);
```

5.压缩图片到1024kb以下
```
ByteArrayOutputStream bos = compress(bitmap, Bitmap.CompressFormat.PNG, 1024);
```

6.压缩图片到500kb以下，获取文件
```
File file = ImageProvider.buildFile(context,Environment.DIRECTORY_PICTURES,".jpg");
String outPutPath = file.getAbsolutePath();
File file = decodeBitmap(bitmap, Bitmap.CompressFormat.PNG, 500, outPutPath);
```

7.是否是图片
```
boolean isImage = ImageProvider.isImage(path);
```

8.图片转Base64
```
String base64 = encodeBase64(file);
```

9.图片转Base64,不URLEncode
```
String base64 = encodeBase64(file,false);
```

10.Base64转图片,不URLDecode
```
String base64 = decodeBase64(base64String,path,false);
```

11.Uri转Bitmap
```
Bitmap bitmap = decodeUri(context,uri);
```

## IntentProvider
常用Intent提供者

1.打开类型选择器
```
String mineType = "image/*";
//方式1
IntentProvider.pick(Activity activity,mineType);
//方式2
IntentProvider.pick(Fragment fragment, String mineType);
```

2.系统拍照
```
//方式1
IntentProvider.imageCapture(Activity activity, Uri outPutUri);
//方式2
IntentProvider.imageCapture(Fragment fragment, Uri outPutUri);
```

3.打开文件
```
IntentProvider.openDocument(Context context, String path);
```

## IOProvider
文件操作提供者

1.获取挂载的缓存文件夹
```
String path = IOProvider.getExternalCacheDir(Context context);
```

2.创建文件
```
File file = IOProvider.createFile(Context context, String dir, String name);
```

3.创建新文件夹
```
String path = IOProvider.makeDirs(Context context, String dir) ;
```

4.删除文件
```
boolean result = IOProvider.delete(File file);
```

5.计算文件大小
```
long length = IOProvider.length(File file);
```

6.获取文件后缀
```
String suffix = IOProvider.getSuffix(String path);
```

7.获取文件类型
```
String mineType = IOProvider.getMimeType(String path);
```

8.根据URL生产文件名
```
String name =  IOProvider.buildNameByUrl(String url)
```

9.获取Assets文件内容
```
String content = IOProvider.readAssets(Context context, String fileName);
```

10.获取文件内容
```
String content = IOProvider.read(File file);
```

11.写入文件（传入文件名，内容）
```
IOProvider.write(Context context, String name, String content);
```

12.文件流转文件
```
File file = IOProvider.decodeInputStream(InputStream inputStream, String path);
```

13.文件转byte[]
```
byte[] data = IOProvider.decodeFile(File file);
```

14.byte[]转文件
```
File file = IOProvider.decodeBytes(byte[] bytes, String path)
```

15.通过文件名获取资源id
```
int resId = findResId("android_ic_close", R.drawable.class);
```

## Validator
验证器

1.自定义正则
```
Validator.REGEX_PHONE = "xxx";
```

2.验证手机号
```
Validator.isPhone(String number);
```

3.验证身份证号（粗略的校验）
```
Validator.isIdCard(String number);
```

4.验证微信号
```
Validator.isWeChat(String number);
```

5.验证密码
默认正则，密码长度为8到20位,必须包含字母和数字，字母区分大小写
```
Validator.isPassword(String password);
```

6.验证数字
```
Validator.isNumeric(String number);
```

7.验证邮箱
```
Validator.isEmail(String mail);
```

8.验证QQ
```
Validator.isQQ(String number);
```

