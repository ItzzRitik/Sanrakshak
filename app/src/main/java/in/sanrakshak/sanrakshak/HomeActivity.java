package in.sanrakshak.sanrakshak;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.rm.rmswitch.RMSwitch;
import com.tomergoldst.tooltips.ToolTipsManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout logo_div,splash_cover,diagnosis;
    ImageView ico_splash,menu,done,dob_chooser;
    EditText dob;
    RMSwitch gender;
    float fabX,fabY;
    TextView page_tag,symptoms_tag,gender_tag,diag_results,disResult,appNameSplash;
    Animator animator;
    CardView data_div;
    ObjectAnimator startAnim;
    Point screenSize;
    ToolTipsManager toolTip;
    RecyclerView display;
    double diagonal;
    OkHttpClient client;
    SwipeRefreshLayout refresh;
    FloatingActionButton add;
    String r="",Email="",Gender="",DOB="";
    NachoTextView symptom_edit;
    String symptoms[],sym_id[];
    int key=0;
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
        Email=getIntent().getStringExtra("email");

        page_tag=findViewById(R.id.page_tag);
        page_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        appNameSplash=findViewById(R.id.appNameSplash);
        appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vdub.ttf"));
        setMargins(appNameSplash,0,0,0,dptopx(30) + getHeightStatusNav(1));

        ico_splash=findViewById(R.id.ico_splash);
        ico_splash.setScaleType(ImageView.ScaleType.CENTER);

        menu=findViewById(R.id.menu);
        menu.setOnClickListener(v -> {
            if(key==3){
                int colorFrom = getResources().getColor(R.color.colorPrimary);
                int colorTo = getResources().getColor(R.color.colorAccent);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(300);
                colorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                colorAnimation.addUpdateListener(animator -> diagnosis.setBackgroundColor((int) animator.getAnimatedValue()));

                int cx = data_div.getWidth()/2;
                int cy = data_div.getHeight()/2;
                int finalRadius = Math.max(data_div.getWidth(), data_div.getHeight());
                animator=ViewAnimationUtils.createCircularReveal(diagnosis, cx, cy,finalRadius, add.getWidth());
                animator.setDuration(300);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {}
                    @Override public void onAnimationCancel(Animator animator) {}
                    @Override public void onAnimationRepeat(Animator animator) {}
                    @Override public void onAnimationEnd(Animator animator) {
                        diagnosis.setVisibility(View.GONE);
                        page_tag.setText(R.string.home);
                        refresh.setEnabled(true);
                        key=0;
                        done.setImageDrawable(getDrawable(R.drawable.key));
                        menu.setImageDrawable(getDrawable(R.drawable.menu));
                        float CurrentX = add.getX();
                        float CurrentY = add.getY();
                        Path path = new Path();
                        path.moveTo(CurrentX, CurrentY);
                        path.quadTo(CurrentX*3/7, (CurrentY+fabY)*4/6, fabX, fabY);
                        startAnim = ObjectAnimator.ofFloat(add, View.X, View.Y, path);
                        startAnim.setDuration(300);
                        startAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                        startAnim.start();
                    }
                });
                animator.start();
                colorAnimation.start();
            }
        });
        done=findViewById(R.id.done);
        done.setOnClickListener(v -> {
            if(key==3)
            {
                String ID="";
                for (Chip chip : symptom_edit.getAllChips()) {
                    ID=ID+sym_id[getIndex((chip.getText().toString()),symptoms)]+",";
                }
                ID=ID.substring(0,ID.length()-1);
                String date=dob.getText().toString();
                date=date.substring(date.length()-4,date.length());
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://medisyst-adityabhardwaj.c9users.io/diagnosis").newBuilder();
                urlBuilder.addQueryParameter("ID",ID);
                urlBuilder.addQueryParameter("gender",gender_tag.getText().toString());
                urlBuilder.addQueryParameter("DOB",date);
                Request request = new Request.Builder().url(urlBuilder.build().toString()).get()
                        .addHeader("Content-Type", "application/json").build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.w("failure", e.getMessage());
                        call.cancel();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        assert response.body() != null;
                        String mMessage = Objects.requireNonNull(response.body()).string();
                        refresh.setRefreshing(false);
                        if (response.isSuccessful()){
                            try {
                                JSONArray postsArray = new JSONArray(mMessage);
                                Log.e("diag", postsArray.toString() );
                                r=r+"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
                                for (int i = 0; i < postsArray.length(); i++) {
                                    JSONObject res = postsArray.getJSONObject(i);
                                    r=r+"Disease : "+res.getString("name")+"\n";
                                    r=r+"Professional Name : "+res.getString("profname")+"\n";
                                    r=r+"Prediction Accuracy : "+res.getString("accuracy")+"%\n";
                                    r=r+"Specialisation : "+res.getString("specialisation")+"\n";
                                    r=r+"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
                                }
                                Log.e("diag", r );
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        disResult.setText(r);
                                    }
                                });
                            }
                            catch (JSONException e) {
                                Log.w("error", e.toString());
                            }
                        }
                    }
                });
            }
            else if(key==0)
            {
                done.setImageDrawable(getDrawable(R.drawable.close));key=1;
                listRefresh();
                page_tag.setText(R.string.request);
            }
            else if(key==1)
            {
                done.setImageDrawable(getDrawable(R.drawable.key));key=0;
                listRefresh();
                page_tag.setText(R.string.home);
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                    }
                }
        );

        dob=findViewById(R.id.dob);
        dob.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        dob_chooser=findViewById(R.id.dob_chooser);
        dob_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(20);
                DatePickerDialog dd = new DatePickerDialog(HomeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                try {
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String dateInString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                    Date date = formatter.parse(dateInString);
                                    formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    dob.setText(formatter.format(date));
                                } catch (Exception ex) {}
                            }
                        }, 2000,  Calendar.getInstance().get(Calendar.MONTH),  Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dd.show();
            }
        });
        symptoms_tag=findViewById(R.id.symptoms_tag);
        symptoms_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        gender_tag=findViewById(R.id.gender_tag);
        gender_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        gender=findViewById(R.id.gender);
        gender.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                if(isChecked){
                    gender_tag.setText(R.string.male);
                }
                else{
                    gender_tag.setText(R.string.female);
                }
            }
        });

        diagnosis=findViewById(R.id.diagnosis);
        diag_results=findViewById(R.id.diag_results);
        diag_results.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        disResult=findViewById(R.id.disResult);
        disResult.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        symptom_edit=findViewById(R.id.symptom_edit);
        symptom_edit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        symptom_edit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(symptom_edit.getAllChips().size()!=0 || !symptom_edit.getText().toString().equals("")){
                    symptoms_tag.setVisibility(View.VISIBLE);
                }
                else {
                    symptoms_tag.setVisibility(View.GONE);
                }
            }
        });
        add = findViewById(R.id.add);
        setMargins(add,0,0,0,dptopx(30) + getHeightStatusNav(1));
        add.setOnClickListener(view -> {
            float CurrentX = add.getX();
            float CurrentY = add.getY();
            fabX=CurrentX;
            fabY=CurrentY;
            float FinalX = (data_div.getWidth()/2)-(add.getWidth()/2);
            float FinalY = (data_div.getHeight()/2)-(add.getHeight()/2);
            Path path = new Path();
            path.moveTo(CurrentX, CurrentY);
            path.quadTo(CurrentX*4/3, (CurrentY+FinalY)*2/5, FinalX, FinalY);
            startAnim = ObjectAnimator.ofFloat(add, View.X, View.Y, path);
            startAnim.setDuration(300);
            startAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            startAnim.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animator) {}
                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
                @Override public void onAnimationEnd(Animator animator) {
                    page_tag.setText(R.string.diagnosis);
                    refresh.setEnabled(false);
                    key=3;
                    done.setImageDrawable(getDrawable(R.drawable.tick_mono));
                    menu.setImageDrawable(getDrawable(R.drawable.back));
                    diagnosis.setVisibility(View.VISIBLE);
                    dob.setText(DOB);
                    if(Gender.equals(gender_tag.getText().toString())){
                        gender.performClick();
                    }
                    int cx = data_div.getWidth()/2;
                    int cy = data_div.getHeight()/2;
                    int finalRadius = Math.max(data_div.getWidth(), data_div.getHeight());
                    animator=ViewAnimationUtils.createCircularReveal(diagnosis, cx, cy, add.getWidth(), finalRadius);
                    animator.setDuration(300);
                    animator.start();

                    int colorFrom = getResources().getColor(R.color.colorAccent);
                    int colorTo = getResources().getColor(R.color.colorPrimary);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(300);
                    colorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                    colorAnimation.addUpdateListener(animator1 -> diagnosis.setBackgroundColor((int) animator1.getAnimatedValue()));
                    colorAnimation.start();
                }
            });
            startAnim.start();
        });

        display=findViewById(R.id.display);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
        display.setLayoutManager(mLayoutManager);
        display.addItemDecoration(new GridSpacingItemDecoration(1,dptopx(10),true));
        display.setItemAnimator(new DefaultItemAnimator());

        if(getIntent().getBooleanExtra("isProfile",false))
        {
            splash_cover.setVisibility(View.GONE);
            logo_div.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            page_tag.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
            scaleY(data_div,getIntent().getIntExtra("divHeight",0),0,new AccelerateDecelerateInterpolator());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlphaAnimation anims = new AlphaAnimation(0,1);anims.setDuration(1000);
                    display.setVisibility(View.VISIBLE);display.startAnimation(anims);
                }},800);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

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

                    int colorFrom = getResources().getColor(R.color.colorPrimary);
                    int colorTo = getResources().getColor(R.color.colorAccentLight);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(1000);
                    colorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            logo_div.setBackgroundColor((int) animator.getAnimatedValue());
                        }
                    });
                    colorAnimation.start();
                    startAnim.start();
                    ico_splash.animate().scaleX(0f).scaleY(0f).setDuration(1000).start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scaleY(data_div,pxtodp(splash_cover.getHeight())-85,800,new AccelerateDecelerateInterpolator());
                            AlphaAnimation anims = new AlphaAnimation(1,0);anims.setDuration(700);anims.setFillAfter(true);
                            ico_splash.startAnimation(anims);
                        }},10);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AlphaAnimation anims = new AlphaAnimation(0,1);anims.setDuration(400);
                            page_tag.setVisibility(View.VISIBLE);page_tag.startAnimation(anims);
                            menu.setVisibility(View.VISIBLE);menu.startAnimation(anims);
                            done.setVisibility(View.VISIBLE);done.startAnimation(anims);
                            setLightTheme(false,true);
                        }},400);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AlphaAnimation anims = new AlphaAnimation(0,1);anims.setDuration(1000);
                            display.setVisibility(View.VISIBLE);display.startAnimation(anims);
                        }},800);

                }},1500);
        }
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
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(t);anim.start();
    }
    public void scaleY(final View view,int y,int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(),dptopx(y));anim.setInterpolator(interpolator);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
                view.setLayoutParams(layoutParams);view.invalidate();
            }
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
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
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