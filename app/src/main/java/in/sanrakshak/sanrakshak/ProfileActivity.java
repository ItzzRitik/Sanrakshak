package in.sanrakshak.sanrakshak;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rm.rmswitch.RMSwitch;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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

import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;

public class ProfileActivity extends AppCompatActivity {
    RelativeLayout logo_div,splash_cover,camera_pane,permission_camera,galary,click_pane,profile_menu_cov;
    ConstraintLayout root_view;
    CardView data_div;
    ImageView dp_cover,ico_splash,done,camera_flip,click,flash,dob_chooser;
    Button allow_camera;
    Animation anim;
    TextView page_tag,gender_tag,appNameSplash;
    EditText f_name,l_name,dob,aadhaar;
    RMSwitch gender;
    Point screenSize;
    Animator animator;
    ObjectAnimator startAnim;
    CameraView cameraView;
    UCrop.Options options;
    CircularImageView profile;
    boolean profile_lp=false,camOn=false,galaryOn=false,isDP_added=false;
    String profile_url="",profile_path="";
    ProgressBar loading_profile;
    ToolTipsManager toolTip;
    Bitmap profile_dp=null;
    double diagonal;
    OkHttpClient client;
    @Override
    protected void onPause() {
        super.onPause();
        if(camera_pane.getVisibility()== View.VISIBLE)
        {
            click.setVisibility(View.GONE);
            if(cameraView.isCameraOpened()){cameraView.stop();}
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(camera_pane.getVisibility()== View.VISIBLE)
        {
            if(checkPerm() && !cameraView.isCameraOpened()){cameraView.start();cameraListener();}
            new Handler().postDelayed(new Runnable() {@Override public void run()
            {
                click.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.click_grow);click.startAnimation(anim);
            }},500);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        root_view=findViewById(R.id.root_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            root_view.setPadding(0,getHeightStatusNav(0),0,0);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            setLightTheme(true,true);
        }

        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        diagonal=Math.sqrt((screenSize.x*screenSize.x) + (screenSize.y*screenSize.y));
        splash_cover=findViewById(R.id.splash_cover);
        logo_div=findViewById(R.id.logo_div);
        data_div=findViewById(R.id.data_div);
        dp_cover=findViewById(R.id.dp_cover);
        profile_menu_cov=findViewById(R.id.profile_menu_cov);
        toolTip = new ToolTipsManager();
        client = new OkHttpClient();

        gender_tag=findViewById(R.id.gender_tag);
        gender_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/above.ttf"));

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


        f_name=findViewById(R.id.f_name);
        f_name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        l_name=findViewById(R.id.l_name);
        l_name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        dob=findViewById(R.id.dob);
        dob.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        aadhaar=findViewById(R.id.aadhaar);
        aadhaar.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        page_tag=findViewById(R.id.page_tag);
        page_tag.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));

        appNameSplash=findViewById(R.id.appNameSplash);
        appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vdub.ttf"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dptopx(30) + getHeightStatusNav(1));
        appNameSplash.setLayoutParams(params);

        ico_splash=findViewById(R.id.ico_splash);
        ico_splash.setScaleType(ImageView.ScaleType.CENTER);

        click_pane=findViewById(R.id.click_pane);
        click_pane.getLayoutParams().height = dptopx(140) + getHeightStatusNav(1);

        galary= findViewById(R.id.galary);

        camera_pane=findViewById(R.id.camera_pane);
        loading_profile= findViewById(R.id.loading_profile);
        loading_profile.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.progress), PorterDuff.Mode.MULTIPLY);

        done=findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfile();
            }
        });
        profile=findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                startActivity(intent);
                root_view.setPadding(0,0,0,0);

                setLightTheme(false,false);
                if(profile_lp) {profile_lp=false;}
                else
                {
                    vibrate(20);
                    camera_pane.setVisibility(View.VISIBLE);
                    permission_camera.setVisibility(View.VISIBLE);
                    camOn=true;
                    final Animator animator = ViewAnimationUtils.createCircularReveal(camera_pane,dptopx(98),dptopx(260),0, (float)diagonal);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());animator.setDuration(500);animator.start();
                    if (checkPerm()) {
                        permission_camera.setVisibility(View.GONE);if(!cameraView.isCameraOpened()){cameraView.start();
                            cameraListener();
                        }
                    }
                    new Handler().postDelayed(new Runnable() {@Override public void run()
                    {
                        click.setVisibility(View.VISIBLE);
                        click.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.click_grow));
                    }},500);
                    if(checkPerm())
                    {
                        new Handler().postDelayed(new Runnable() {@Override public void run()
                        {
                            ToolTip.Builder builder = new ToolTip.Builder(ProfileActivity.this, click,camera_pane, getString(R.string.open_galary), ToolTip.POSITION_ABOVE);
                            builder.setBackgroundColor(getResources().getColor(R.color.profile));
                            builder.setTextColor(getResources().getColor(R.color.profile_text));
                            builder.setGravity(ToolTip.GRAVITY_CENTER);
                            builder.setTextSize(15);
                            toolTip.show(builder.build());
                        }},1300);
                        new Handler().postDelayed(new Runnable() {@Override public void run() {toolTip.findAndDismiss(click);}},4000);
                    }
                }
            }
        });
        profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(35);
                return false;
            }
        });

        options=new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridColumnCount(0);
        options.setCropGridRowCount(0);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccentDark));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccentDark));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));

        profile_url=new File(new ContextWrapper(getApplicationContext()).getDir("imageDir", Context.MODE_PRIVATE),"profile.jpg").getAbsolutePath();


        flash=findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(20);
                if(cameraView.getFlash()!=CameraView.FLASH_ON) {
                    cameraView.setFlash(CameraView.FLASH_ON);
                    flash.setImageResource(R.drawable.flash_on);
                }
                else {
                    cameraView.setFlash(CameraView.FLASH_OFF);
                    flash.setImageResource(R.drawable.flash_off);
                }
                Toast.makeText(ProfileActivity.this, cameraView.getFlash()+"", Toast.LENGTH_SHORT).show();
            }
        });
        camera_flip=findViewById(R.id.camera_flip);
        camera_flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(20);
                cameraView.switchCamera();
            }
        });
        click=findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraView.isCameraOpened()){
                    cameraView.takePicture();
                }
            }
        });
        click.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int cx=screenSize.x/2;
                int cy=screenSize.y-((int)(click.getY()));
                galaryOn=true;vibrate(35);
                animator = ViewAnimationUtils.createCircularReveal(galary,cx,cy,0,(float) diagonal);
                animator.setInterpolator(new AccelerateInterpolator());animator.setDuration(300);galary.setVisibility(View.VISIBLE);
                galary.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.fade_out));
                animator.start();
                Intent intent = new Intent();
                intent.setType("image/*");intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                overridePendingTransition(R.anim.fade_in,0);
                cameraView.stop();
                return false;
            }
        });

        permission_camera=findViewById(R.id.permission_camera) ;
        allow_camera =findViewById(R.id.allow_camera);
        allow_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(20);
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });

        dob_chooser=findViewById(R.id.dob_chooser);
        dob_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(20);
                DatePickerDialog dd = new DatePickerDialog(ProfileActivity.this,
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

        cameraView=findViewById(R.id.cam);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // SignUp Animation

                splash_cover.setVisibility(View.GONE);
                logo_div.setVisibility(View.VISIBLE);

                float CurrentX = ico_splash.getX();
                float CurrentY = ico_splash.getY();
                float FinalX = -100;
                float FinalY = -108;
                Path path = new Path();
                path.moveTo(CurrentX, CurrentY);
                path.quadTo(CurrentX*4/3, (CurrentY+FinalY)/4, FinalX, FinalY);

                startAnim = ObjectAnimator.ofFloat(ico_splash, View.X, View.Y, path);
                startAnim.setDuration(800);
                startAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                startAnim.start();
                ico_splash.animate().scaleX(0.22f).scaleY(0.22f).setDuration(1000).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scaleY(data_div,pxtodp(splash_cover.getHeight())-65,800,new AccelerateDecelerateInterpolator());
                    }},10);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlphaAnimation anims = new AlphaAnimation(0,1);
                        anims.setDuration(600);
                        page_tag.setVisibility(View.VISIBLE);page_tag.startAnimation(anims);
                        done.setVisibility(View.VISIBLE);done.startAnimation(anims);
                    }},500);

            }},1500);
    }
    public void cameraListener(){
        cameraView.setOnFocusLockedListener(new CameraViewImpl.OnFocusLockedListener() {
            @Override
            public void onFocusLocked() {
            }
        });
        cameraView.setOnPictureTakenListener(new CameraViewImpl.OnPictureTakenListener() {
            @Override
            public void onPictureTaken(Bitmap result, int rotationDegrees) {
                Log.e("Camera", "onPictureTaken: " );
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                result= Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                vibrate(20);
                profile_path = MediaStore.Images.Media.insertImage(ProfileActivity.this.getContentResolver(), result, "Title", null);
                UCrop.of(Uri.parse(profile_path),Uri.parse(profile_url)).withOptions(options).withAspectRatio(1,1)
                        .withMaxResultSize(maxWidth, maxHeight).start(ProfileActivity.this);
            }
        });
        cameraView.setOnTurnCameraFailListener(new CameraViewImpl.OnTurnCameraFailListener() {
            @Override
            public void onTurnCameraFail(Exception e) {
                Toast.makeText(ProfileActivity.this, "Switch Camera Failed. Does you device has a front camera?",
                        Toast.LENGTH_SHORT).show();
            }
        });
        cameraView.setOnCameraErrorListener(new CameraViewImpl.OnCameraErrorListener() {
            @Override
            public void onCameraError(Exception e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void createProfile(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://medisyst-adityabhardwaj.c9users.io/update").newBuilder();
        urlBuilder.addQueryParameter("email",getIntent().getStringExtra("email"));
        urlBuilder.addQueryParameter("fname",f_name.getText().toString());
        urlBuilder.addQueryParameter("lname",l_name.getText().toString());
        urlBuilder.addQueryParameter("gender",gender_tag.getText().toString());
        urlBuilder.addQueryParameter("dob",dob.getText().toString());
        urlBuilder.addQueryParameter("aadhaar",aadhaar.getText().toString());
        Log.i("sign",urlBuilder.toString());
        Request request = new Request.Builder().url(urlBuilder.build().toString()).get()
                .addHeader("Content-Type", "application/json").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("sign", e.getMessage());
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                if(Integer.parseInt(Objects.requireNonNull(response.body()).string())==1 && response.isSuccessful()){
//                    Intent home=new Intent(ProfileActivity.this,HomeActivity.class);
//                    home.putExtra("isProfile",true);
//                    home.putExtra("divHeight",pxtodp(data_div.getHeight()));
//                    home.putExtra("email",ProfileActivity.this.getIntent().getStringExtra("email"));
//                    ProfileActivity.this.startActivity(home);
//                    ProfileActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    finish();
                }
                else{
                    Toast.makeText(ProfileActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void scaleX(final View view,int x,int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredWidth(),(int)dptopx(x));anim.setInterpolator(interpolator);
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
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(),(int)dptopx(y));anim.setInterpolator(interpolator);
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

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent intent) {
        super.onActivityResult(requestCode, resultcode, intent);
        if (requestCode == 1 && resultcode == RESULT_OK) {
            UCrop.of(Objects.requireNonNull(intent.getData()),Uri.parse(profile_url)).withOptions(options).withAspectRatio(1,1)
                    .withMaxResultSize(maxWidth, maxHeight).start(ProfileActivity.this);
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if(resultcode == RESULT_OK)
            {
                try {
                    final Uri resultUri = UCrop.getOutput(intent);
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), resultUri);
                    profile.setImageBitmap(bitmap);dp_cover.setImageBitmap(bitmap);profile_dp=bitmap;isDP_added=true;
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    getWindow().setStatusBarColor(Color.WHITE);
                    closeCam();
                    new File(getRealPathFromURI(ProfileActivity.this,Uri.parse(profile_path))).delete();
                }
                catch (Exception ignored){}
            }
            else if (resultcode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(intent);
                new File(getRealPathFromURI(ProfileActivity.this,Uri.parse(profile_path))).delete();
            }
        }
    }
    public void closeCam()
    {
        int cy=(int)(profile.getY()+profile.getHeight()/2);
        Animation anim = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.click_shrink);click.startAnimation(anim);
        animator = ViewAnimationUtils.createCircularReveal(camera_pane,ico_splash.getRight()/2,cy, ico_splash.getHeight()*141/100,0);
        animator.setInterpolator(new DecelerateInterpolator());animator.setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {camOn=false;click.setVisibility(View.GONE);}
            @Override
            public void onAnimationEnd(Animator animation) {camera_pane.setVisibility(View.GONE);click.setVisibility(View.GONE);}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        new Handler().postDelayed(new Runnable() {@Override public void run() {animator.start();}},300);

    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPerm())
                    {
                        permission_camera.setVisibility(View.GONE);
                        new Handler().postDelayed(new Runnable() {@Override public void run()
                        {
                            ToolTip.Builder builder = new ToolTip.Builder(ProfileActivity.this, click,camera_pane, getString(R.string.open_galary), ToolTip.POSITION_ABOVE);
                            builder.setBackgroundColor(getResources().getColor(R.color.profile));
                            builder.setTextColor(getResources().getColor(R.color.profile_text));
                            builder.setGravity(ToolTip.GRAVITY_CENTER);
                            builder.setTextSize(15);
                            toolTip.show(builder.build());
                            cameraView.setVisibility(View.GONE);
                            cameraView.setVisibility(View.VISIBLE);
                            if(!cameraView.isCameraOpened()){
                                cameraView.start();cameraListener();
                            }
                        }},1300);
                        new Handler().postDelayed(new Runnable() {@Override public void run() {toolTip.findAndDismiss(click);}},4000);
                    }
                }
            }
        }
    }
    public boolean checkPerm(){
        return (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    public int getHeightStatusNav(int viewid) {
        int result = 0;
        String view=(viewid==0)?"status_bar_height":"navigation_bar_height";
        int resourceId = getResources().getIdentifier(view, "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        if(viewid==1){result *= 5/8;}
        return result;
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
