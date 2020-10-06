package com.example.hackfrenzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Messages extends AppCompatActivity {
    private BottomNavigationView bottom_nav;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView user_recycler;
    private User_Adapter user_adapter;
    private List<User_Model> list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    private void searchUsers(final String query) {
        firebaseFirestore.collection("users").addSnapshotListener(Messages.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                if(value!=null){
                    for(DocumentSnapshot doc:value.getDocuments()){
                        User_Model user_model=doc.toObject(User_Model.class);
                        if(!user_model.getEmail().equals(firebaseAuth.getCurrentUser().getEmail())){
                            if(user_model.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                            user_model.getName().toLowerCase().contains(query.toLowerCase())){
                                list.add(user_model);
                            }
                        }
                        user_adapter = new User_Adapter(Messages.this,list);
                        user_adapter.notifyDataSetChanged();
                        user_recycler.setAdapter(user_adapter);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:{
                return true;
            }
            case R.id.contact:{
                return true;
            }
            case R.id.notifications:{
                Intent i=new Intent(Messages.this,Notifications.class);
                startActivity(i);
                return true;
            }
            case R.id.search:{
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Messages");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        bottom_nav=findViewById(R.id.bottom_nav);
        bottom_nav.setSelectedItemId(R.id.messages);
        user_recycler=findViewById(R.id.message_recycle);
        user_recycler.setHasFixedSize(true);
        user_recycler.setLayoutManager(new LinearLayoutManager(Messages.this));

        list=new ArrayList<>();

        getAllUsers();


        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dash:{
                        Intent intent=new Intent(Messages.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.status:{
                        Intent intent=new Intent(Messages.this,Status_Videos.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.messages:{
                        return true;
                    }
                    case R.id.profile:{
                        Intent intent=new Intent(Messages.this,Profile.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.add_post:{
                        Intent intent=new Intent(Messages.this,Upload_Post.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void getAllUsers() {
        firebaseFirestore.collection("users").addSnapshotListener(Messages.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                if(value!=null){
                    for(DocumentSnapshot doc:value.getDocuments()){
                        User_Model user_model=doc.toObject(User_Model.class);
                        if(!user_model.getEmail().equals(firebaseAuth.getCurrentUser().getEmail())){
                            list.add(user_model);
                        }
                        user_adapter = new User_Adapter(Messages.this,list);
                        user_adapter.notifyDataSetChanged();
                        user_recycler.setAdapter(user_adapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Messages.this, MainActivity.class);
        startActivity(intent);
        finish();
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
