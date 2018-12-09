package in.sanrakshak.sanrakshak;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class CrackAdapter extends RecyclerView.Adapter<CrackAdapter.MyViewHolder> {
    private List<Cracks> cracks;
    private HomeActivity home;
    private int cardHeight=0,imgHeight=0;
    private int expand=1;
    private Interpolator interpolator;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,city,date;
        ImageView preview,locate,navigate;
        RelativeLayout navtrigger,cardItem,cardthumb;
        ProgressBar glidepro;
        CardView root_view;
        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            name.setTypeface(Typeface.createFromAsset(home.getAssets(), "fonts/exo2_bold.otf"));
            city = view.findViewById(R.id.city);
            city.setTypeface(Typeface.createFromAsset(home.getAssets(), "fonts/exo2.ttf"));
            date = view.findViewById(R.id.date);
            date.setTypeface(Typeface.createFromAsset(home.getAssets(), "fonts/exo2.ttf"));
            preview = view.findViewById(R.id.thumbnail);
            navtrigger = view.findViewById(R.id.navtrigger);
            locate = view.findViewById(R.id.locate);
            navigate = view.findViewById(R.id.navigate);
            cardItem = view.findViewById(R.id.cardItem);
            glidepro = view.findViewById(R.id.glidepro);
            root_view = view.findViewById(R.id.root_view);
            cardthumb = view.findViewById(R.id.cardthumb);
        }
    }
    CrackAdapter(HomeActivity home, List<Cracks> cracks) {
        this.cracks = cracks;
        this.home = home;
        interpolator = new OvershootInterpolator();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_crack, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Cracks item = cracks.get(position);
        holder.name.setText(item.getName());
        holder.city.setText(item.getCity());
        holder.date.setText(item.getDate());
        Glide.with(home).load(item.getPreview())
                .apply(new RequestOptions()
                        .centerCrop()
                        .override(Target.SIZE_ORIGINAL))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.glidepro.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.preview);
        holder.locate.setOnClickListener(view -> startMap(holder,Uri.parse("geo:"+item.getLatitude()+","+item.getLongitude()+"?q="+item.getLatitude()+","+item.getLongitude() )));
        holder.navigate.setOnClickListener(view -> startMap(holder,Uri.parse("google.navigation:q="+item.getLatitude()+","+item.getLongitude())));

        holder.cardItem.setOnClickListener(view -> {
            if(expand==1){
                expand=-1;
                cardHeight=holder.root_view.getHeight();
                imgHeight=holder.cardthumb.getWidth();

                scaleY(holder.root_view,holder.root_view.getWidth(),200,interpolator);
                scaleY(holder.cardthumb,holder.root_view.getWidth(),200,interpolator);
                scaleX(holder.cardthumb,holder.root_view.getWidth(),200,interpolator);

                AlphaAnimation anims = new AlphaAnimation(0,1);anims.setDuration(200);anims.setInterpolator(interpolator);
                holder.navtrigger.setVisibility(View.VISIBLE);holder.navtrigger.requestFocus();
                anims.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {expand=0;}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });holder.navtrigger.startAnimation(anims);
            }
        });
        holder.navtrigger.setOnClickListener(view -> {
            if(expand==0){
                expand=-1;


                scaleY(holder.root_view,cardHeight,200,interpolator);
                scaleY(holder.cardthumb,imgHeight,200,interpolator);
                scaleX(holder.cardthumb,imgHeight,200,interpolator);

                AlphaAnimation anims = new AlphaAnimation(1,0);anims.setDuration(200);anims.setInterpolator(interpolator);
                anims.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) { holder.navtrigger.setVisibility(View.GONE);holder.preview.requestFocus();expand=1;}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });holder.navtrigger.startAnimation(anims);
            }
        });
    }
    private void startMap(@NonNull final MyViewHolder holder, Uri address){
        holder.navtrigger.performClick();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, address);

        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(home.getPackageManager()) != null) {
            home.startActivity(mapIntent);
            home.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            home.showSheet("Google Maps Required",
                    "In order to use navigation feature, Google maps should to be installed on this device.","DOWNLOAD",200);

        }
    }
    @Override
    public int getItemCount() {
        if(cracks==null){return 0;}
        return cracks.size();
    }
    private void scaleY(final View view, int y, int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(),y);anim.setInterpolator(interpolator);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
            view.setLayoutParams(layoutParams);view.invalidate();
        });
        anim.setDuration(t);anim.start();
    }
    public void scaleX(final View view,int x,int t, Interpolator interpolator)
    {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredWidth(),x);anim.setInterpolator(interpolator);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
            view.setLayoutParams(layoutParams);
        });
        anim.setDuration(t);anim.start();
    }
}