package in.sanrakshak.sanrakshak;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tomergoldst.tooltips.ToolTipsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout logo_div,splash_cover,sheet_pane,sheet_back;
    CardView sheet;
    ImageView ico_splash,menu,done;
    TextView page_tag,appNameSplash,sheet_title,sheet_msg,sheet_action;
    Animator animator;
    CardView data_div;
    ObjectAnimator startAnim;
    Point screenSize;
    ToolTipsManager toolTip;
    RecyclerView home;
    double diagonal;
    OkHttpClient client;
    SwipeRefreshLayout refresh;
    ProgressBar proSplash;
    RequestBody postBody=null;
    SharedPreferences user,crack;
    SharedPreferences.Editor user_edit,crack_edit;
    List<Cracks> cracks;
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

        page_tag=findViewById(R.id.page_tag);
        page_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        appNameSplash=findViewById(R.id.appNameSplash);
        appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        setMargins(appNameSplash,0,0,0,dptopx(30) + getHeightStatusNav(1));

        proSplash = findViewById(R.id.proSplash);
        setMargins(proSplash,0,0,0,(int)(dptopx(60) + getHeightStatusNav(1)));

        ico_splash=findViewById(R.id.ico_splash);
        ico_splash.setScaleType(ImageView.ScaleType.CENTER);

        menu=findViewById(R.id.menu);
        menu.setOnClickListener(v -> {

        });
        done=findViewById(R.id.done);
        done.setOnClickListener(v -> {

        });
        refresh = findViewById(R.id.refresh);
        refresh.setProgressViewOffset(true,dptopx(50),dptopx(100));
        refresh.setNestedScrollingEnabled(true);
        refresh.setSlingshotDistance(dptopx(150));
        refresh.setOnRefreshListener(() -> {
            if(isOnline()){setCrackList();}
            else {Toast.makeText(HomeActivity.this, R.string.unreachable, Toast.LENGTH_SHORT).show();refresh.setRefreshing(false);}
        });

        home = findViewById(R.id.home);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
        home.setLayoutManager(mLayoutManager);
        home.addItemDecoration(new GridSpacingItemDecoration(1,dptopx(10),true));
        home.setItemAnimator(new DefaultItemAnimator());

        sheet=findViewById(R.id.sheet);
        sheet_back=findViewById(R.id.sheet_back);
        sheet_pane=findViewById(R.id.sheet_pane);
        sheet_pane.setPadding(dptopx(20),dptopx(20),dptopx(20),dptopx(20)+getHeightStatusNav(1));
        sheet_title=findViewById(R.id.sheet_title);
        sheet_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2_bold.otf"));
        sheet_msg=findViewById(R.id.sheet_msg);
        sheet_msg.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        sheet_action=findViewById(R.id.sheet_action);
        sheet_action.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        sheet_action.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sheet_action.setBackgroundResource(R.drawable.signin_pressed);sheet_action.setTextColor(Color.parseColor("#ffffff"));
                    break;
                case MotionEvent.ACTION_UP:
                    sheet_action.setBackgroundResource(R.drawable.signin);sheet_action.setTextColor(getResources().getColor(R.color.colorAccent));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps")));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    vibrate(20);
                    break;
            }
            return true;
        });
        sheet_back=findViewById(R.id.sheet_back);
        sheet_back.setOnClickListener(view -> {
            scaleY(sheet,0,400,new AnticipateInterpolator());
            sheet_back.setVisibility(View.GONE);
        });
        user = getSharedPreferences("user", MODE_PRIVATE);
        user_edit = user.edit();
        crack = getSharedPreferences("crack", MODE_PRIVATE);
        crack_edit = crack.edit();
        connect();
    }
    public void connect(){
        splash();
        if(isOnline())
        {
            refresh.setRefreshing(true);
            Log.i("backend_call", "Connecting");
            try{
                postBody = new FormBody.Builder()
                        .add("device",new CryptLib().encryptPlainTextWithRandomIV(android.os.Build.MODEL,"sanrakshak")).build();

            }
            catch (Exception e){Log.e("encrypt","Error while encryption");}
            Request request = new Request.Builder().url("http://3.16.4.70:8080/connect").post(postBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("backend_call", "Connection Failed - "+e);
                    call.cancel();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Log.i("backend_call","Server Response => "+response.message());
                        if(response.code()==503) {}
                        else{cacheData();}
                    });
                }
            });
        }
    }
    public void cacheData(){
        try{
            String enc=new CryptLib().encryptPlainTextWithRandomIV(user.getString("email", "ritik.space@gmail.com"),"sanrakshak");
            postBody = new FormBody.Builder().add("email",enc).build();
        }
        catch (Exception e){Log.e("encrypt","Error while encryption");}
        Request request = new Request.Builder().url("http://3.16.4.70:8080/getprofile").post(postBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("backend_call", "Connection Failed - "+e);
                call.cancel();
                Toast.makeText(HomeActivity.this, R.string.unreachable, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONArray postsArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        for (int i = 0; i < postsArray.length(); i++) {
                            JSONObject pO = postsArray.getJSONObject(i);
                            user_edit.putString("fname", pO.getString("fname"));
                            user_edit.putString("lname", pO.getString("lname"));
                            user_edit.putString("gender", pO.getString("gender"));
                            user_edit.putString("dob", pO.getString("dob"));
                            user_edit.putString("aadhaar", pO.getString("aadhaar"));
                            user_edit.apply();
                        }
                        new Handler(Looper.getMainLooper()).post(() -> {
                            setCrackList();
                        });
                    }
                    catch (JSONException e) {
                        Log.w("error", e.toString());
                    }
                }
            }
        });
    }
    public void setCrackList(){
        try{
            String enc=new CryptLib().encryptPlainTextWithRandomIV(user.getString("email", "ritik.space@gmail.com"),"sanrakshak");
            postBody = new FormBody.Builder().add("email",enc).build();
        }
        catch (Exception e){Log.e("encrypt","Error while encryption");}
        Request request = new Request.Builder().url("http://3.16.4.70:8080/getcrack").post(postBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("backend_call", "Failed - "+e);
                call.cancel();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(HomeActivity.this, R.string.unreachable, Toast.LENGTH_SHORT).show();
                    refresh.setRefreshing(false);
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONArray postsArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        cracks = new ArrayList<>();
                        home.setAdapter(null);
                        for (int i = 0; i < postsArray.length(); i++) {
                            JSONObject obj = postsArray.getJSONObject(i);
                            double lat=Double.parseDouble(obj.getString("x"));
                            double lng=Double.parseDouble(obj.getString("y"));
                            cracks.add(new Cracks(""+lat,""+lng,getPlaceName(lat,lng,0),getPlaceName(lat,lng,1),
                                    obj.getString("y"),"Date (DD/MM/YYYY)",getMapURL(lat,lng,16,data_div.getWidth()*7/17)));
                        }
                        crack_edit.putString("list", new Gson().toJson(cracks));
                        crack_edit.apply();
                        new Handler(Looper.getMainLooper()).post(() -> {
                            home.setAdapter(new CrackAdapter(HomeActivity.this,cracks));
                            refresh.setRefreshing(false);
                        });
                    }
                    catch (JSONException e) {
                        Log.w("error", e.toString());
                    }
                }
                else{
                    cracks=new Gson().fromJson(crack.getString("list", null), new TypeToken<ArrayList<Cracks>>() {}.getType());
                    home.setAdapter(new CrackAdapter(HomeActivity.this,cracks));
                    refresh.setRefreshing(false);
                }
            }
        });
    }
    public String getPlaceName(double latitude, double longitude, int token){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                Address obj = listAddresses.get(0);
                Log.i("backend_call", obj.getLocality()+" - "+obj.getAddressLine(0));
                if(token==0){
                    String name=obj.getAddressLine(0);
                    String[] split = name.split(", ");
                    int index=getIndex(obj.getLocality(),split)-2;
                    index = (index<=0)?0:index;
                    name=split[index];
                    return name;
                }
                else if(token==1){return  obj.getLocality()+", "+obj.getAdminArea();}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getMapURL(double lat, double lng, int zoom, int size){
        try{
            String APIKey="3dd4wgt62K2sMwn/5rzbKmiZOMjvh7s8FvYkmezWkmNW7YzOu99TvSTcyJIBOzl457hHkyulDbBaKWdJccGfc4GajVR4gycz/iFwgBpiHi1dbbHaF9QnqKxO2jh9aaCfUKaFapjLuDoSLfEcZQgEAA==";
            APIKey = new CryptLib().decryptCipherTextWithRandomIV(new CryptLib().decryptCipherTextWithRandomIV(APIKey,"sanrakshak"),"sanrakshak");
            String keyString="46HSOIkf1Y8T9zoxbzIjmjfD+x2T7pe4Gv0MJKDJDqNJxuVsNLYuMzpQJGLUj4pAUv3q7r8PJyuVS/YTA8iktOo1fRG0iJC6oZkuhvNtjDEAQBmYOii9z4tlSsiAGmUY";
            keyString = new CryptLib().decryptCipherTextWithRandomIV(new CryptLib().decryptCipherTextWithRandomIV(keyString,"sanrakshak"),"sanrakshak");

            String resource="https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom="+zoom+"&size="+size+"x"+size+"&maptype=terrain&key="+APIKey;
            URL url=new URL(resource);

            keyString = (keyString.replace('-', '+')).replace('_', '/');
            byte[] key = android.util.Base64.decode(keyString, android.util.Base64.DEFAULT);

            resource = url.getPath() + '?' + url.getQuery();
            SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sha1Key);
            byte[] sigBytes = mac.doFinal(resource.getBytes());
            String signature = android.util.Base64.encodeToString(sigBytes,android.util.Base64.DEFAULT);
            signature = signature.replace('+', '-');
            signature = signature.replace('/', '_');
            return "https://maps.googleapis.com" + resource + "&signature=" + signature;
        }
        catch (Exception e){Log.e("signature","Error occured - "+e);}
        return "";
    }
    public void splash(){
        cracks=new Gson().fromJson(crack.getString("list", null), new TypeToken<ArrayList<Cracks>>() {}.getType());
        home.setAdapter(new CrackAdapter(HomeActivity.this,cracks));

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
                scaleY(data_div,pxtodp(splash_cover.getHeight()),800,new AccelerateDecelerateInterpolator());
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
    public int getIndex(String element,String arr[]){
        for(int i=0;i<arr.length;i++){
            if(arr[i].contains(element)){
                return i;
            }
        }
        return -1;
    }
    public void listRefresh(){
        refresh.post(() -> refresh.setRefreshing(true));
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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
    public void showSheet(String title,String msg,String action,int size){
        sheet_title.setText(title);
        sheet_msg.setText(msg);
        sheet_action.setText(action);
        sheet_back.setVisibility(View.VISIBLE);
        scaleY(sheet,size,500,new OvershootInterpolator());
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