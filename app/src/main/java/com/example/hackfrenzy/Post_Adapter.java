package com.example.hackfrenzy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.viewHolder> {
    ArrayList<Post_Model> arrayList;
    Context context;

    public Post_Adapter(ArrayList<Post_Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public Post_Adapter() {
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_post_template,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Post_Model post_model=arrayList.get(position);
        String imageUrl=post_model.getImage_Url();
        String description=post_model.getDescription();

        holder.desc.setText(description);
        Picasso.get().load(imageUrl).into(holder.post_image);
        holder.profile_name.setText(post_model.getUsername());
        Picasso.get().load(post_model.getProfile_Picture()).into(holder.profile_pic);
        holder.timeStamp.setText(post_model.getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private ImageView like,share,post_image;
        private CircleImageView profile_pic;
        private TextView profile_name,likes_count,desc,timeStamp;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            post_image=itemView.findViewById(R.id.post_image);
            profile_pic=itemView.findViewById(R.id.profile_pic);
            profile_name=itemView.findViewById(R.id.profile_name);
            likes_count=itemView.findViewById(R.id.likes_count);
            like=itemView.findViewById(R.id.like);
            share=itemView.findViewById(R.id.share);
            desc=itemView.findViewById(R.id.descr);
            timeStamp=itemView.findViewById(R.id.notify_time);
        }
    }
}
