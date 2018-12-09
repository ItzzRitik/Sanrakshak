package in.sanrakshak.sanrakshak;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity  implements KeyboardHeightObserver  {
    FrameLayout root_view;
    Animation anim;
    RelativeLayout login_div,social_div,bottompadding,logo_div,splash_cover,forget_pass,email_reset,social_google,social_facebook;
    ImageView ico_splash,social_google_logo,social_facebook_logo;
    TextView signin,forget_create;
    EditText email,pass,con_pass;
    int log=0,keyHeight=0;
    String buttonText="NEXT";
    OkHttpClient client;
    ProgressBar nextLoad,proSplash;
    TextView appNameSplash;
    RequestBody postBody=null;
    private KeyboardHeightProvider keyProvider;
    SharedPreferences.Editor user;

    GoogleSignInOptions gso;
    GoogleSignInClient gclient;
    GoogleSignInAccount account;
    @Override
    public void onPause() {
        super.onPause();
        keyProvider.setKeyboardHeightObserver(null);
    }
    @Override
    public void onResume() {
        super.onResume();
        keyProvider.setKeyboardHeightObserver(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        keyProvider.close();
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gclient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(getSharedPreferences("user", MODE_PRIVATE).getString("email", null)!=null){
            Intent home=new Intent(LoginActivity.this, HomeActivity.class);
            LoginActivity.this.startActivity(home);
            finish();
            LoginActivity.this.overridePendingTransition(0, 0);
        }

        setContentView(R.layout.activity_login);
        root_view=findViewById(R.id.root_view);
        keyProvider = new KeyboardHeightProvider(this);
        root_view.post(() -> keyProvider.start());
        setLightTheme(true,true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        setLightTheme(true,true);
        splash_cover=findViewById(R.id.splash_cover);
        ico_splash=findViewById(R.id.ico_splash);
        logo_div=findViewById(R.id.logo_div);
        login_div=findViewById(R.id.login_div);
        social_div=findViewById(R.id.social_div);
        bottompadding=findViewById(R.id.bottompadding);

        client = new OkHttpClient();

        social_google=findViewById(R.id.social_google);
        social_google_logo=findViewById(R.id.social_google_logo);
        social_google.setOnClickListener(view -> {
            scaleX(social_google_logo,(int)pxtodp(social_google.getWidth()),150,new AccelerateInterpolator());
            new Handler().postDelayed(() -> {
                Intent signInIntent = gclient.getSignInIntent();
                startActivityForResult(signInIntent, 0);
            },50);

        });

        appNameSplash=findViewById(R.id.appNameSplash);
        appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        proSplash=findViewById(R.id.proSplash);
        setMargins(appNameSplash,0,0,0,(int)(dptopx(30) + getHeightStatusNav(1)));
        setMargins(proSplash,0,0,0,(int)(dptopx(60) + getHeightStatusNav(1)));

        forget_create=findViewById(R.id.forget_create);
        forget_create.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        email=findViewById(R.id.email);
        email.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(isEmailValid(email.getText().toString()))
                {setButtonEnabled(true);}
                else{setButtonEnabled(false);}
            }
        });
        email.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if(log==0 &&isEmailValid(email.getText().toString())){performSignIn();}
                        return true;
                    default:break;
                }
            }
            return false;
        });

        pass=findViewById(R.id.pass);
        pass.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                con_pass.setText("");
                if(pass.getText().length()>=6)
                {
                    pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_ok,0,0,0);
                    if(log==1){setButtonEnabled(true);}
                    else if(log==2){con_pass.setEnabled(true);}
                }
                else
                {
                    pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_nok,0,0,0);
                    if(log==1){setButtonEnabled(false);}
                    else if(log==2){con_pass.setText("");con_pass.setEnabled(false);}
                }
            }
        });
        pass.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    con_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                    break;
                case MotionEvent.ACTION_UP:
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    con_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                    if(log==2)
                    scaleY(bottompadding,(int)pxtodp(keyHeight-(int)(con_pass.getHeight()-dptopx(5))),200,new AnticipateInterpolator());
                    break;
            }
            return false;
        });
        pass.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT) {
                if(con_pass.isEnabled()){scaleY(bottompadding,(int)pxtodp(keyHeight+(int)(dptopx(7))),200,new OvershootInterpolator());}
                con_pass.requestFocus();
                return true;
            }
            else if(i == EditorInfo.IME_ACTION_DONE){
                performSignIn();
            }
            return false;
        });

        con_pass=findViewById(R.id.con_pass);
        con_pass.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
        con_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(log==2)
                {
                    if(con_pass.getText().toString().equals(pass.getText().toString()) && con_pass.getText().length()>=6)
                    {con_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.con_password_ok,0,0,0);setButtonEnabled(true);}
                    else{con_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.con_password_nok,0,0,0);setButtonEnabled(false);}
                }
            }
        });
        con_pass.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if(log==2 && signin.isEnabled()) {performSignIn();}
                        return true;
                    default:break;
                }
            }
            return false;
        });

        forget_pass = findViewById(R.id.forget_pass);
        forget_pass.setOnClickListener(v -> vibrate(20));
        email_reset=findViewById(R.id.email_reset);
        email_reset.setOnClickListener(v -> {
            showKeyboard(v,false);
            new Handler().postDelayed(() -> {
                scaleY(social_div,80,300,new AccelerateDecelerateInterpolator());
                scaleY(login_div,48,300,new AccelerateDecelerateInterpolator());
                scaleY(forget_pass,0,300,new AccelerateDecelerateInterpolator());
                nextPad( 5,2);
                login_div.setPadding(0,(int)(10 * getResources().getDisplayMetrics().density),0,0);

                email_reset.setVisibility(View.GONE);email.setEnabled(true);
                pass.setText("");con_pass.setText("");
                signin.setText(getString(R.string.next));
                setButtonEnabled(true);vibrate(20);
                email.setVisibility(View.VISIBLE);
                pass.setVisibility(View.GONE);
                con_pass.setVisibility(View.GONE);
                log=0;
            },100);

        });


        signin=findViewById(R.id.signin);
        nextLoad=findViewById(R.id.nextLoad);
        signin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vdub.ttf"));
        signin.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    signin.setBackgroundResource(R.drawable.signin_pressed);signin.setTextColor(Color.parseColor("#ffffff"));
                    break;
                case MotionEvent.ACTION_UP:
                    signin.setBackgroundResource(R.drawable.signin);signin.setTextColor(Color.parseColor("#ff611c"));
                    vibrate(20);
                    email.setEnabled(false);
                    performSignIn();
                    break;
            }
            return true;
        });

        setButtonEnabled(false);logo_div.setVisibility(View.VISIBLE);
        ico_splash.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_initialgrow));

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
                            // Splash Animation
                            new Handler().postDelayed(() -> setLightTheme(true,false),300);
                            appNameSplash.setVisibility(View.GONE);
                            splash_cover.setVisibility(View.GONE);logo_div.setVisibility(View.VISIBLE);
                            logo_div.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_reveal));

                            anim=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_trans);
                            anim.setDuration(550);ico_splash.startAnimation(anim);
                            new Handler().postDelayed(() -> {
                                new Handler().postDelayed(() -> scaleY(login_div,48,400,new OvershootInterpolator()),200);
                                scaleY(social_div,80,280,new AccelerateInterpolator());
                                setLightTheme(false,true);
                            },800);
                        },2000);
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
    public void performSignIn()
    {
        showKeyboard(email,false);
        if(log==0)
        {
            nextLoading(true);
            try{
                postBody = new FormBody.Builder()
                        .add("email",new CryptLib().encryptPlainTextWithRandomIV(email.getText().toString(),"sanrakshak")).build();
            }
            catch (Exception e){Log.e("encrypt","Error while encryption");}
            Request request = new Request.Builder().url("http://3.16.4.70:8080/check").post(postBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("sign", e.getMessage());
                    call.cancel();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    if(response.body().string().equals("1") && response.isSuccessful())
                    {
                        //If Exists then ask password
                        Log.e("sign", "SignIN");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            scaleY(social_div,0,300,new AccelerateDecelerateInterpolator());
                            login_div.setPadding(0,0,0,0);
                            nextPad(8,4);
                            nextLoading(false);
                            //Ask Password
                            pass.setVisibility(View.VISIBLE);
                            con_pass.setVisibility(View.GONE);
                            email_reset.setVisibility(View.VISIBLE);
                            pass.requestFocus();
                            pass.setEnabled(true);
                            pass.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            setButtonEnabled(false);
                            forget_create.setTextSize(13);
                            forget_create.setText(getResources().getString(R.string.forgot_pass));
                            scaleY(forget_pass,27,300,new OvershootInterpolator());
                            scaleY(login_div,98,300,new AccelerateDecelerateInterpolator());
                            log=1;
                        });
                    }
                    else
                    {
                        //If Doesn't exist then ask signup
                        Log.e("sign", "SignUP");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            scaleY(social_div,0,300,new AccelerateDecelerateInterpolator());
                            login_div.setPadding(0,0,0,0);
                            nextPad(8,4);
                            nextLoading(false);

                            //Ask SignUp Details
                            pass.setVisibility(View.VISIBLE);
                            con_pass.setVisibility(View.VISIBLE);
                            email_reset.setVisibility(View.VISIBLE);
                            pass.requestFocus();
                            pass.setEnabled(true);
                            pass.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                            setButtonEnabled(false);
                            forget_create.setTextSize(14);
                            forget_create.setText(getResources().getString(R.string.login_create));
                            scaleY(forget_pass,30,300,new OvershootInterpolator());
                            scaleY(login_div,148,300,new AccelerateDecelerateInterpolator());
                            log=2;
                        });
                    }
                }
            });
        }
        else if(log==1)
        {
            nextLoading(true);
            try{
                postBody = new FormBody.Builder()
                        .add("email",new CryptLib().encryptPlainTextWithRandomIV(email.getText().toString(),"sanrakshak"))
                        .add("pass",new CryptLib().encryptPlainTextWithRandomIV(pass.getText().toString(),"sanrakshak")).build();
            }
            catch (Exception e){Log.e("encrypt","Error while encryption");}
            Request request = new Request.Builder().url("http://3.16.4.70:8080/login").post(postBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("sign", e.getMessage());
                    call.cancel();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    String rs=response.body().string();
                    if (rs.equals("1") && response.isSuccessful())
                    {
                        Log.i("sign", "Login Done");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            //SignIn Initiate
                            newPageAnim(2);
                            nextLoading(false);
                            new Handler().postDelayed(() -> {
                                user = getSharedPreferences("user", MODE_PRIVATE).edit();
                                user.putString("email", email.getText().toString());
                                user.apply();
                                Intent home=new Intent(LoginActivity.this, HomeActivity.class);
                                LoginActivity.this.startActivity(home);
                                finish();
                                LoginActivity.this.overridePendingTransition(0, 0);},1500);
                        });
                    }
                    else if(rs.equals("2") && response.isSuccessful())
                    {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            try{
                                postBody = new FormBody.Builder()
                                        .add("email",new CryptLib().encryptPlainTextWithRandomIV(email.getText().toString(),"sanrakshak")).build();

                            }
                            catch (Exception e){Log.e("encrypt","Error while encryption");return;}
                            newPageAnim(1);
                            nextLoading(false);
                        });
                    }
                    else if(rs.equals("3") && response.isSuccessful())
                    {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            newPageAnim(2);
                            nextLoading(false);
                            new Handler().postDelayed(() -> {
                                Intent home=new Intent(LoginActivity.this, ProfileActivity.class);
                                home.putExtra("email",email.getText().toString());
                                LoginActivity.this.startActivity(home);
                                finish();
                                LoginActivity.this.overridePendingTransition(0, 0);},1500);
                        });
                    }
                    else{
                        Log.i("sign", "Login Failed");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            nextLoading(false);
                            pass.setText("");
                            Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
        else if(log==2)
        {
            //SignUp Initiate
            Log.i("sign", "SignUp Initiate");
            nextLoading(true);
            try{
                postBody = new FormBody.Builder()
                        .add("email",new CryptLib().encryptPlainTextWithRandomIV(email.getText().toString(),"sanrakshak"))
                        .add("pass",new CryptLib().encryptPlainTextWithRandomIV(pass.getText().toString(),"sanrakshak")).build();
            }
            catch (Exception e){Log.e("encrypt","Error while encryption");}
            Request request = new Request.Builder().url("http://3.16.4.70:8080/signup").post(postBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("sign", e.getMessage());
                    call.cancel();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    if(response.body().string().equals("1") && response.isSuccessful()){
                        Log.i("sign","Account Creation Successful");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            try{
                                postBody = new FormBody.Builder()
                                        .add("email",new CryptLib().encryptPlainTextWithRandomIV(email.getText().toString(),"sanrakshak")).build();

                            }
                            catch (Exception e){Log.e("encrypt","Error while encryption");return;}
                            newPageAnim(1);
                            nextLoading(false);
                        });
                    }
                    else{
                        new Handler(Looper.getMainLooper()).post(() -> {
                            nextLoading(false);
                            Log.i("sign","Account Creation Failed");
                            Toast.makeText(LoginActivity.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                            setButtonEnabled(true);
                        });
                    }
                }
            });
        }
    }
    public void verify(final int iteration){
        Log.i("backend_call", "Verification - "+iteration);
        Request request = new Request.Builder().url("http://3.16.4.70:8080/checkverification").post(postBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("backend_call", "Verification Failed - "+e);
                call.cancel();
                verifyFailed(iteration);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                assert response.body() != null;
                if(response.body().string().equals("1") && response.isSuccessful()){
                    Log.i("sign","Account Verified Successful");
                    new Handler(Looper.getMainLooper()).post(() -> new Handler().postDelayed(() -> {
                        Intent profile = new Intent(LoginActivity.this, ProfileActivity.class);
                        profile.putExtra("email",email.getText().toString());
                        LoginActivity.this.startActivity(profile);
                        finish();
                        LoginActivity.this.overridePendingTransition(0, 0);},1500));
                }
                else{
                    verifyFailed(iteration);
                }
            }
        });
    }
    public void verifyFailed(final int iteration){
        new Handler(Looper.getMainLooper()).post(() -> new Handler().postDelayed(() -> verify(iteration+1),(iteration>20)?10000:iteration*500));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    account = task.getResult(ApiException.class);
                    assert account != null;
                    try{
                        postBody = new FormBody.Builder()
                                .add("email",new CryptLib().encryptPlainTextWithRandomIV(account.getEmail(),"sanrakshak")).build();
                    }
                    catch (Exception e){Log.e("encrypt","Error while encryption");}
                    Request request = new Request.Builder().url("http://3.16.4.70:8080/check").post(postBody).build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.i("sign", e.getMessage());
                            call.cancel();
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            assert response.body() != null;
                            if(response.body().string().equals("1") && response.isSuccessful())
                            {
                                //If Exists then Login without password
                                user = getSharedPreferences("user", MODE_PRIVATE).edit();
                                user.putString("email", email.getText().toString());
                                user.apply();
                                Intent home=new Intent(LoginActivity.this, HomeActivity.class);
                                LoginActivity.this.startActivity(home);
                                finish();
                                LoginActivity.this.overridePendingTransition(0, 0);},1500);
                            }
                            else
                            {
                                //If Doesn't exist then ask signup
                                Log.e("sign", "SignUP");
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    scaleY(social_div,0,300,new AccelerateDecelerateInterpolator());
                                    login_div.setPadding(0,0,0,0);
                                    nextPad(8,4);
                                    nextLoading(false);

                                    //Ask SignUp Details
                                    pass.setVisibility(View.VISIBLE);
                                    con_pass.setVisibility(View.VISIBLE);
                                    email_reset.setVisibility(View.VISIBLE);
                                    pass.requestFocus();
                                    pass.setEnabled(true);
                                    pass.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                                    setButtonEnabled(false);
                                    forget_create.setTextSize(14);
                                    forget_create.setText(getResources().getString(R.string.login_create));
                                    scaleY(forget_pass,30,300,new OvershootInterpolator());
                                    scaleY(login_div,148,300,new AccelerateDecelerateInterpolator());
                                    log=2;
                                });
                            }
                        }
                    });
                }
                catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            else {scaleX(social_google_logo,50,100,new AccelerateDecelerateInterpolator());}
        }
    }
    public void newPageAnim(final int type)
    {
        //type=0 --->> Connection lost
        //type=1 --->> Verify Account
        //type=2 --->> Open New Page


        logo_div.setVisibility(View.VISIBLE);
        anim=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_hide);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                scaleY(social_div,0,300,new AccelerateDecelerateInterpolator());
                scaleY(login_div,0,300,new AccelerateDecelerateInterpolator());
                scaleY(forget_pass,0,300,new AccelerateDecelerateInterpolator());
                ico_splash.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_grow));
                setLightTheme(true,true);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if(type==1){
                    appNameSplash.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/exo2.ttf"));
                    appNameSplash.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                    appNameSplash.setText(R.string.email_wait);
                    appNameSplash.setVisibility(View.VISIBLE);
                    proSplash.setVisibility(View.VISIBLE);
                    verify(0);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        logo_div.startAnimation(anim);

    }
    public void nextLoading(Boolean loading)
    {
        if(loading)
        {
            buttonText=signin.getText().toString();signin.setText("");
            scaleX(signin,30,150,new AnticipateInterpolator());
            signin.setBackgroundResource(R.drawable.signin_disabled);
            signin.setTextColor(Color.parseColor("#616161"));
            new Handler().postDelayed(() -> {
                nextLoad.setVisibility(View.VISIBLE);signin.setText("╳");
            },150);
        }
        else
        {
            nextLoad.setVisibility(View.GONE);signin.setText("");
            scaleX(signin,85,300,new OvershootInterpolator());
            new Handler().postDelayed(() -> signin.setText(buttonText),300);
        }
    }
    public void setButtonEnabled(Boolean what)
    {
        if(what) {
            signin.setBackgroundResource(R.drawable.signin);
            signin.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        else {
            signin.setBackgroundResource(R.drawable.signin_disabled);
            signin.setTextColor(getResources().getColor(R.color.disabled));
        }
        signin.setEnabled(what);
    }
    public static boolean isEmailValid(String emailStr)
    {
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE) .matcher(emailStr).find();
    }
    public void scaleX(final View view,int x,int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredWidth(),(int)dptopx(x));anim.setInterpolator(interpolator);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
            view.setLayoutParams(layoutParams);
        });
        anim.setDuration(t);anim.start();
    }
    public void scaleY(final View view,int y,int t, Interpolator interpolator)
    {
        if(view==social_div)
        {
            y=y+(int)pxtodp(getHeightStatusNav(1)*3/2);
            view.setPadding((int)dptopx(10),(int)dptopx(10),(int)dptopx(10),getHeightStatusNav(1)*3/2);
        }
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(),(int)dptopx(y));anim.setInterpolator(interpolator);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
            view.setLayoutParams(layoutParams);view.invalidate();
        });
        anim.setDuration(t);anim.start();
    }
    public float dptopx(float num)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, num, getResources().getDisplayMetrics());
    }
    public float pxtodp(float px)
    {
        return px / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public void vibrate(int ms)
    {
        ((Vibrator) Objects.requireNonNull(this.getSystemService(Context.VIBRATOR_SERVICE))).vibrate(ms);
    }
    public void nextPad(int button,int loader){
        button = (int)(button * getResources().getDisplayMetrics().density);
        loader = (int)(loader * getResources().getDisplayMetrics().density);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) signin.getLayoutParams();
        lp.setMargins(0, 0, 0, button);
        signin.setLayoutParams(lp);
        lp = (RelativeLayout.LayoutParams) nextLoad.getLayoutParams();
        lp.setMargins(0, 0, (int)(-4.5 * getResources().getDisplayMetrics().density), loader);
        nextLoad.setLayoutParams(lp);
    }
    public void showKeyboard(View view,boolean what)
    {
        if(what)
        {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(),InputMethodManager.SHOW_FORCED, 0);
        }
        else
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        //String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        int margin=0;
        if(height>=keyHeight && height>0)
        {
            if(email.isFocused())
            {
                margin=height-(int)(social_div.getHeight()-login_div.getHeight()-dptopx(5));
            }
            else if(pass.isFocused())
            {
                if(log==2)margin=height-(int)(con_pass.getHeight()-dptopx(5));
                else if(log==1)margin=height+(int)(dptopx(7));
            }
            else if(con_pass.isFocused())
            {
                margin=height+(int)(dptopx(7));
            }
        }
        //if(log==1 && height>0)margin=height+(int)(dptopx(7));
        final int fmargin=margin;
        keyHeight=height;
        Log.i("keyboard", "Margin Calculated : "+pxtodp(fmargin));
        scaleY(bottompadding,(int)pxtodp(fmargin),75,new AccelerateDecelerateInterpolator());

    }
    public int getHeightStatusNav(int viewid) {
        int result = 0;
        if(viewid==0){
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) { result = getResources().getDimensionPixelSize(resourceId); }
        }
        else if(viewid==1){ result = getNavigationBarSize(this).y * 5/8; }
        return result;
    }
    public Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }
        return new Point();
    }
    public Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    public Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }
}
