package com.example.hackfrenzy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chating_Page extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView send_button, back_button;
    private EditText edit_message;
    private TextView recv_name, recv_status;
    private CircleImageView recv_profile;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String sender_Email, receiver_Email,timeStamp;

    List<Chat_Model> chatList;
    Chat_Adapter chat_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating__page);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(" ");

        Intent intent = getIntent();
        receiver_Email = intent.getStringExtra("email");


        send_button = findViewById(R.id.send_button);
        back_button = findViewById(R.id.back_button);
        edit_message = findViewById(R.id.edit_message);
        recv_name = findViewById(R.id.recv_name);
        recv_profile = findViewById(R.id.recv_profile);
        recv_status = findViewById(R.id.recv_status);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Chating_Page.this));


        Query query = firebaseFirestore.collection("users").whereEqualTo("email", receiver_Email);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    String name = doc.getString("username");
                    String image = doc.getString("profile_Picture");
                    String onlinestatus = doc.getString("online_Status");

                    recv_name.setText(name);
                    if(onlinestatus=="online"){
                        recv_status.setText(onlinestatus);
                    }else{
                        recv_status.setText("Last seen at: "+onlinestatus);
                    }
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_baseline_emoji_emotions_24).into(recv_profile);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_baseline_emoji_emotions_24).into(recv_profile);
                    }
                }
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edit_message.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(Chating_Page.this, "Can't send empty message", Toast.LENGTH_LONG).show();
                } else {
                    sendMessage(message);
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Chating_Page.this, Messages.class);
                startActivity(i);
                finish();
            }
        });

        readMessages();

        seenMessages();

    }

    private void seenMessages() {
        firebaseFirestore.collection("chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Chat_Model chat = doc.toObject(Chat_Model.class);
                        if (chat.getReceiver().equals(firebaseAuth.getCurrentUser().getEmail()) && chat.getSender().equals(receiver_Email)) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("isSeen", true);
                            doc.getReference().update(map);
                        }
                    }
                }
            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        firebaseFirestore.collection("chats").orderBy("date").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                chatList.clear();
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Chat_Model chat = doc.toObject(Chat_Model.class);
                        if (chat.getReceiver().equals(firebaseAuth.getCurrentUser().getEmail()) && chat.getSender().equals(receiver_Email) ||
                                chat.getReceiver().equals(receiver_Email) && chat.getSender().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            chatList.add(chat);
                        }
                        Chat_Adapter chat_adapter = new Chat_Adapter(Chating_Page.this, chatList);
                        chat_adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(chat_adapter);
                    }
                }
            }
        });
    }

    private void checkOnlineStatus(String status){
        HashMap<String,Object> map=new HashMap<>();
        map.put("online_Status",status);
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).update(map);
    }

    private void sendMessage(String message) {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sf=new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sd=new SimpleDateFormat("dd/MM/yyyy");
        String time=sf.format(calendar.getTime());
        String date=sd.format(calendar.getTime());

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", firebaseAuth.getCurrentUser().getEmail());
        map.put("receiver", receiver_Email);
        map.put("message", message);
        map.put("time", time);
        map.put("date", date);
        map.put("isSeen", false);
        firebaseFirestore.collection("chats").document().set(map);
        edit_message.setText("");
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sf=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        String timestamp=sf.format(calendar.getTime());
        checkOnlineStatus(timestamp);
    }

    @Override
    protected void onPostResume() {
        checkOnlineStatus("online");
        super.onPostResume();
    }
}