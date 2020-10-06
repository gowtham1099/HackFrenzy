package com.example.hackfrenzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private BottomNavigationView bottom_nav;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private RewardedAd rewardedAd;
    private RecyclerView recyclerView;
    private ArrayList<Post_Model> arrayList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
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
                Intent i=new Intent(MainActivity.this,Notifications.class);
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
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Hack Frenzy");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        rewardedAd = new RewardedAd(this, "ca-app-pub-3902078744336950/5861010857");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        bottom_nav = findViewById(R.id.bottom_nav);
        bottom_nav.setSelectedItemId(R.id.dash);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerView = findViewById(R.id.post_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        arrayList = new ArrayList<Post_Model>();
        final Post_Adapter post_adapter = new Post_Adapter(arrayList, MainActivity.this);

        firebaseFirestore.collection("posts").orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(MainActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                arrayList.clear();
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        arrayList.add(doc.toObject(Post_Model.class));
                        post_adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        recyclerView.setAdapter(post_adapter);

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dash: {
                        return true;
                    }
                    case R.id.status: {
                        Intent intent = new Intent(MainActivity.this, Status_Videos.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.messages: {
                        if (rewardedAd.isLoaded()) {
                            Activity activityContext = MainActivity.this;
                            RewardedAdCallback adCallback = new RewardedAdCallback() {
                                @Override
                                public void onRewardedAdOpened() {
                                    // Ad opened.
                                }

                                @Override
                                public void onRewardedAdClosed() {
                                    // Ad closed.
                                    Intent intent = new Intent(MainActivity.this, Messages.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {

                                }

                                @Override
                                public void onRewardedAdFailedToShow(AdError adError) {
                                    // Ad failed to display.
                                    Intent intent = new Intent(MainActivity.this, Messages.class);
                                    startActivity(intent);
                                    finish();
                                }
                            };
                            rewardedAd.show(activityContext, adCallback);
                        } else {
                            Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                        }
                        return true;
                    }
                    case R.id.profile: {
                        Intent intent = new Intent(MainActivity.this, Profile.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.add_post:{
                        Intent intent=new Intent(MainActivity.this,Upload_Post.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser==null){
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
            finish();
        }
    }

}
