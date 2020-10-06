package com.example.hackfrenzy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class User_Adapter extends RecyclerView.Adapter<User_Adapter.viewHolder> {

    Context context;
    List<User_Model> list;

    public User_Adapter(Context context, List<User_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public User_Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_user_template, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull User_Adapter.viewHolder holder, int position) {
        final User_Model user_model=list.get(position);
        String online=user_model.getOnline_Status();
        holder.user_name.setText(user_model.getUsername());
        holder.user_email.setText(user_model.getEmail());
        try {
            Picasso.get().load(user_model.getProfile_Picture()).placeholder(R.drawable.ic_baseline_emoji_emotions_24).into(holder.user_profile);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_baseline_emoji_emotions_24).into(holder.user_profile);
        }

        if(online=="online"){
            holder.online_btn.setVisibility(View.VISIBLE);
        }else{
            holder.online_btn.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,Chating_Page.class);
                i.putExtra("email",user_model.getEmail());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView user_profile,online_btn;
        TextView user_name,user_email;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile=itemView.findViewById(R.id.user_profile);
            user_email=itemView.findViewById(R.id.user_email);
            user_name=itemView.findViewById(R.id.user_name);
            online_btn=itemView.findViewById(R.id.online_btn);
        }
    }
}
