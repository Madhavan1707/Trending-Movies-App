package com.example.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<CastMember> castList;

    public CastAdapter(List<CastMember> castList) {
        this.castList = castList;
    }

    public void updateCast(List<CastMember> newCast) {
        this.castList = newCast;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        CastMember castMember = castList.get(position);
        holder.textViewName.setText(castMember.name);
        String profileUrl = "https://image.tmdb.org/t/p/w185" + castMember.profilePath;
        Glide.with(holder.itemView.getContext()).load(profileUrl).into(holder.imageViewProfile);
    }

    @Override
    public int getItemCount() {
        return castList != null ? castList.size() : 0;
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
        TextView textViewName;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewName = itemView.findViewById(R.id.textViewCastName);
        }
    }
}
