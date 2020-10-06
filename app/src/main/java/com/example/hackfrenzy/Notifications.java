package com.example.hackfrenzy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity {
    private RecyclerView notificationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Notification_Model> list;
    private Notification_Adapter notification_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationView=findViewById(R.id.notificationView);
        notificationView.setHasFixedSize(true);
        notificationView.setLayoutManager(new LinearLayoutManager(Notifications.this));
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        list=new ArrayList<>();

        notification_adapter=new Notification_Adapter(list,Notifications.this);

        firebaseFirestore.collection("Notifications").orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                if(value!=null){
                    for(DocumentSnapshot doc:value.getDocuments()){
                        list.add(doc.toObject(Notification_Model.class));
                        notification_adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        notificationView.setAdapter(notification_adapter);
    }
}