package in.sanrakshak.sanrakshak;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CrackAdapter extends RecyclerView.Adapter<CrackAdapter.MyViewHolder> {
    private List<Cracks> cracks;
    private HomeActivity homeActivity;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,city,date;
        LinearLayout cardItem;
        ImageView preview;
        RelativeLayout navtrigger;
        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            name.setTypeface(Typeface.createFromAsset(homeActivity.getAssets(), "fonts/exo2_bold.otf"));
            city = view.findViewById(R.id.city);
            city.setTypeface(Typeface.createFromAsset(homeActivity.getAssets(), "fonts/exo2.ttf"));
            date = view.findViewById(R.id.date);
            date.setTypeface(Typeface.createFromAsset(homeActivity.getAssets(), "fonts/exo2_bold.otf"));
            preview = view.findViewById(R.id.thumbnail);
            navtrigger = view.findViewById(R.id.navtrigger);
            cardItem = view.findViewById(R.id.cardItem);
        }
    }
    CrackAdapter(HomeActivity homeActivity, List<Cracks> cracks) {
        this.cracks = cracks;
        this.homeActivity = homeActivity;
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
        Glide.with(homeActivity).load(item.getPreview())
                .apply(new RequestOptions().centerCrop())
                .into(holder.preview);
        holder.preview.setOnClickListener(view -> {
            if(holder.navtrigger.getVisibility()==View.VISIBLE)
                holder.navtrigger.setVisibility(View.INVISIBLE);
            else if(holder.navtrigger.getVisibility()==View.INVISIBLE)
                holder.navtrigger.setVisibility(View.VISIBLE);
            holder.navtrigger.requestFocus();
        });
        holder.navtrigger.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                holder.navtrigger.setVisibility(View.INVISIBLE);
            }
        });
        holder.cardItem.setOnClickListener(view -> {
        });
    }
    @Override
    public int getItemCount() {
        if(cracks==null){return 0;}
        return cracks.size();
    }
}