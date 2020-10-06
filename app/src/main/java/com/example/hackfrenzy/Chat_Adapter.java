package com.example.hackfrenzy;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;


public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.viewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat_Model> chatList;

    public Chat_Adapter(Context context, List<Chat_Model> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Chat_Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_msg,parent,false);
            return new viewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_msg,parent,false);
            return new viewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Chat_Adapter.viewHolder holder, final int position) {
        String msg = chatList.get(position).getMessage();
        String timeStp = chatList.get(position).getDate();
        String times = chatList.get(position).getTime();

        holder.message.setText(msg);
        holder.timeStamp.setText(timeStp);
        holder.time.setText(times);

        if(position==chatList.size()-1){
            if(chatList.get(position).isSeen()) {
                holder.isSee.setText("Seen");
            }
            else {
                holder.isSee.setText("Delivered");
                holder.isSee.setVisibility(View.GONE);
            }
        }
        else{
            holder.isSee.setVisibility(View.GONE);
        }

        holder.messageLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog alertDialog=new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Are you sure to delete this message?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }

    private void deleteMessage(int position) {
        final String SenderId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        String msgdate=chatList.get(position).getDate();
        String msgtime=chatList.get(position).getTime();
        Query query = FirebaseFirestore.getInstance().collection("chats").whereEqualTo("date",msgdate).whereEqualTo("time",msgtime);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot doc:value.getDocuments()){
                    if(doc.getString("sender").equals(SenderId)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("message", "This message was deleted...");
                        doc.getReference().update(map);
                    }
                    else{
                        Toast.makeText(context, "You can delete only your message...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public int getItemViewType(int position){
        if(chatList.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView timeStamp,time,message,isSee;
        CardView messageLay;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            timeStamp=itemView.findViewById(R.id.timestamp);
            isSee=itemView.findViewById(R.id.status);
            message=itemView.findViewById(R.id.message);
            time=itemView.findViewById(R.id.time);
            messageLay=itemView.findViewById(R.id.messageLayout);
        }
    }
}
