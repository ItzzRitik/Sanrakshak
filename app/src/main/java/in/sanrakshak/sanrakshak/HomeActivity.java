package in.sanrakshak.sanrakshak;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout logo_div,splash_cover;
    ImageView ico_splash,menu,done;
    TextView page_tag,appNameSplash;
    Animator animator;
    CardView data_div;
    ObjectAnimator startAnim;
    Point screenSize;
    ToolTipsManager toolTip;
    RecyclerView home;
    double diagonal;
    OkHttpClient client;
    String Email="";
    SwipeRefreshLayout refresh;
    ProgressBar proSplash;
    RequestBody postBody=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setLightTheme(true,true);

        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        diagonal=Math.sqrt((screenSize.x*screenSize.x) + (screenSize.y*screenSize.y));
        splash_cover=findViewById(R.id.splash_cover);
        logo_div=findViewById(R.id.logo_div);
        data_div=findViewById(R.id.data_div);
        toolTip = new ToolTipsManager();
        client = new OkHttpClient();
        Email="ritik.space@gmail.com";//getIntent().getStringExtra("email");

        page_tag=findViewById(R.id.page_tag);
        page_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        appNameSplash=findViewById(R.id.appNameSplash);
        appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vdub.ttf"));
        setMargins(appNameSplash,0,0,0,dptopx(30) + getHeightStatusNav(1));

        ico_splash=findViewById(R.id.ico_splash);
        ico_splash.setScaleType(ImageView.ScaleType.CENTER);

        menu=findViewById(R.id.menu);
        menu.setOnClickListener(v -> {

        });
        done=findViewById(R.id.done);
        done.setOnClickListener(v -> {

        });
        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(() -> {

            }
        );
        home = findViewById(R.id.home);
        proSplash = findViewById(R.id.proSplash);

        try{
            postBody = new FormBody.Builder()
                    .add("device",new CryptLib().encryptPlainTextWithRandomIV(android.os.Build.MODEL,"sanrakshak")).build();

        }
        catch (Exception e){Log.e("encrypt","Error while encryption");}
        splash(0);

    }
    public void splash(final int iteration){
        Log.i("backend_call", "Connecting - "+iteration);
        Request request = new Request.Builder().url("http://3.16.4.70:8080/connect").post(postBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("backend_call", "Connection Failed - "+e);
                call.cancel();
                serverOffline(iteration);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Log.i("backend_call","Server Response - "+iteration+" => "+response.message());
                    if(response.code()==503)
                    {
                        serverOffline(iteration);
                    }
                    else
                    {
                        appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vdub.ttf"));
                        appNameSplash.setText(getString(R.string.app_name));
                        appNameSplash.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                        proSplash.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            splash_cover.setVisibility(View.GONE);
                            logo_div.setVisibility(View.VISIBLE);

                            float CurrentX = ico_splash.getX();
                            float CurrentY = ico_splash.getY();
                            float FinalX = 0;
                            float FinalY = 35;
                            Path path = new Path();
                            path.moveTo(CurrentX, CurrentY);
                            path.quadTo(CurrentX*4/3, (CurrentY+FinalY)/4, FinalX, FinalY);

                            startAnim = ObjectAnimator.ofFloat(ico_splash, View.X, View.Y, path);
                            startAnim.setDuration(800);
                            startAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                            startAnim.start();
                            ico_splash.animate().scaleX(0f).scaleY(0f).setDuration(1000).start();
                            new Handler().postDelayed(() -> {
                                scaleY(data_div,pxtodp(splash_cover.getHeight())-85,800,new AccelerateDecelerateInterpolator());
                                AlphaAnimation anims = new AlphaAnimation(1,0);anims.setDuration(700);anims.setFillAfter(true);
                                ico_splash.startAnimation(anims);
                            },10);
                            new Handler().postDelayed(() -> {
                                AlphaAnimation anims = new AlphaAnimation(0,1);anims.setDuration(400);
                                page_tag.setVisibility(View.VISIBLE);page_tag.startAnimation(anims);
                                menu.setVisibility(View.VISIBLE);menu.startAnimation(anims);
                                done.setVisibility(View.VISIBLE);done.startAnimation(anims);
                                setLightTheme(true,true);
                            },400);
                            new Handler().postDelayed(() -> {
                                AlphaAnimation anims = new AlphaAnimation(0,1);anims.setDuration(1000);
                                home.setVisibility(View.VISIBLE);home.startAnimation(anims);
                            },800);
                        },1500);
                    }
                });
            }
        });
    }
    public void serverOffline(final int iteration){
        new Handler(Looper.getMainLooper()).post(() -> {
            if(iteration==0){
                new Handler().postDelayed(() -> {
                    appNameSplash.setText(getString(R.string.offline));
                    appNameSplash.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                },1000);
            }
            new Handler().postDelayed(() -> splash(iteration+1),(iteration>20)?10000:iteration*500);
        });
    }
    public int getIndex(String element,String arr[]){
        for(int i=0;i<arr.length;i++){
            if(arr[i].equals(element)){
                return i;
            }
        }
        return -1;
    }
    public void listRefresh(){
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
            }
        });
    }

    public void scaleX(final View view,int x,int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredWidth(),dptopx(x));anim.setInterpolator(interpolator);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
            view.setLayoutParams(layoutParams);
        });
        anim.setDuration(t);anim.start();
    }
    public void scaleY(final View view,int y,int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(),dptopx(y));anim.setInterpolator(interpolator);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
            view.setLayoutParams(layoutParams);view.invalidate();
        });
        anim.setDuration(t);anim.start();
    }
    public int dptopx(float dp)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public int pxtodp(float px)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public void vibrate(int ms)
    {
        ((Vibrator) Objects.requireNonNull(this.getSystemService(Context.VIBRATOR_SERVICE))).vibrate(ms);
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;
        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;
            if (includeEdge){
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
    public int getHeightStatusNav(int viewid) {
        int result = 0;
        String view=(viewid==0)?"status_bar_height":"navigation_bar_height";
        int resourceId = getResources().getIdentifier(view, "dimen", "android");
        if (resourceId > 0) { result = getResources().getDimensionPixelSize(resourceId); }
        if(viewid==1){result = result* 5/8;}
        return result;
    }
    public void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    public void setLightTheme(boolean status,boolean nav){
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        if(status && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        if(nav && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        if(!status && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        if(!nav && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }
}