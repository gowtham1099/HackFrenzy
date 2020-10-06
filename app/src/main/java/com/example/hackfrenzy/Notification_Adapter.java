package com.example.hackfrenzy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.viewHolder> {
    List<Notification_Model> list;
    Context context;

    public Notification_Adapter(List<Notification_Model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_notification_template,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Notification_Model model=list.get(position);
        final String web_url = model.getUrl();

        Picasso.get().load(model.getImage()).into(holder.image);
        holder.desc.setText(model.getDescription());
        holder.tittle.setText(model.getTittle());
        holder.time.setText(model.getTimeStamp());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,Notification_View.class);
                i.putExtra("url",web_url);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView tittle,desc,time;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.notify_image);
            tittle=itemView.findViewById(R.id.notify_tittle);
            desc=itemView.findViewById(R.id.notify_desc);
            time=itemView.findViewById(R.id.notify_time);
        }
    }
}
