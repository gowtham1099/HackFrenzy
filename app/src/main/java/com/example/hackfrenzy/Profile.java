package com.example.hackfrenzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private BottomNavigationView bottom_nav;
    private CircleImageView profile_pic;
    private TextView profile_name,profile_no,profile_edit,logout,save_name,save_no,profile_mail,profile_share,profile_settings;
    private Uri imageUri,fileUri;
    private ImageView name_change,phone_change,photo_change;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private AdView mAdView;
    private Button save_photo;
    private EditText edit_name,edit_no;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Profile.this, "Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Profile.this, "Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        bottom_nav = findViewById(R.id.bottom_nav);
        bottom_nav.setSelectedItemId(R.id.profile);
        name_change = findViewById(R.id.name_edit);
        phone_change = findViewById(R.id.phone_edit);
        photo_change = findViewById(R.id.image_edit);
        save_photo = findViewById(R.id.save_photo);
        profile_no = findViewById(R.id.profile_no);
        profile_name = findViewById(R.id.profile_name);
        profile_pic = findViewById(R.id.profile_pic);
        profile_edit = findViewById(R.id.profile_edit);
        profile_settings = findViewById(R.id.profile_settings);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        profile_mail = findViewById(R.id.profile_email);
        profile_share = findViewById(R.id.profile_share);
        userId = firebaseAuth.getCurrentUser().getEmail();
        edit_name = findViewById(R.id.edit_name);
        edit_no = findViewById(R.id.edit_no);
        save_name = findViewById(R.id.save_name);
        save_no = findViewById(R.id.save_no);

        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(Profile.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profile_name.setText(value.getString("name"));
                profile_mail.setText(value.getString("email"));
                profile_no.setText(value.getString("phone_Number"));
                Picasso.get().load(value.getString("profile_Picture")).placeholder(R.drawable.ic_baseline_emoji_emotions_24).into(profile_pic);
            }
        });

        name_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_name.setVisibility(View.INVISIBLE);
                name_change.setVisibility(View.INVISIBLE);
                edit_name.setVisibility(View.VISIBLE);
                save_name.setVisibility(View.VISIBLE);
                documentReference.addSnapshotListener(Profile.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        edit_name.setText(value.getString("name"));
                    }
                });
            }
        });
        save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(Profile.this, "Please enter name", Toast.LENGTH_LONG).show();
                } else {
                    profile_name.setVisibility(View.VISIBLE);
                    name_change.setVisibility(View.VISIBLE);
                    edit_name.setVisibility(View.INVISIBLE);
                    save_name.setVisibility(View.INVISIBLE);
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).update(map);
                }
            }
        });

        phone_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_no.setVisibility(View.INVISIBLE);
                phone_change.setVisibility(View.INVISIBLE);
                edit_no.setVisibility(View.VISIBLE);
                save_no.setVisibility(View.VISIBLE);
                documentReference.addSnapshotListener(Profile.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        edit_no.setText(value.getString("phone_Number"));
                    }
                });
            }
        });
        save_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = edit_no.getText().toString();
                if (num.isEmpty()) {
                    Toast.makeText(Profile.this, "Please enter name", Toast.LENGTH_LONG).show();
                } else {
                    profile_no.setVisibility(View.VISIBLE);
                    phone_change.setVisibility(View.VISIBLE);
                    edit_name.setVisibility(View.INVISIBLE);
                    save_no.setVisibility(View.INVISIBLE);
                    Map<String, Object> map = new HashMap<>();
                    map.put("phone_Number", num);
                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).update(map);
                }
            }
        });

        photo_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                } else {
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select picture"), 100);
                }
                photo_change.setVisibility(View.INVISIBLE);
                save_photo.setVisibility(View.VISIBLE);
            }
        });
        save_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(Profile.this);
                    progressDialog.setMessage("Saving.....");
                    progressDialog.show();
                    StorageReference storageReference1 = storageReference.child("Users").child(firebaseAuth.getCurrentUser().getEmail()).child("Profile Pictures").child(firebaseAuth.getCurrentUser().getUid() + "." + getExtension(imageUri));
                    storageReference1.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("profile_Picture", uri.toString());
                                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    photo_change.setVisibility(View.VISIBLE);
                                                    save_photo.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(Profile.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dash: {
                        Intent intent = new Intent(Profile.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.status: {
                        Intent intent = new Intent(Profile.this, Status_Videos.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.messages: {
                        Intent intent = new Intent(Profile.this, Messages.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.profile: {
                        return true;
                    }
                    case R.id.add_post: {
                        Intent intent = new Intent(Profile.this, Upload_Post.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Are you sure u want to Logout");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(Profile.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });

        profile_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Hii");
                String msg = "Let me recommend you this application\n\n";
                msg += "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                i.putExtra(Intent.EXTRA_TEXT, msg);

                startActivity(Intent.createChooser(i, "Choose One...."));
            }
        });

        profile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                alertDialog.setTitle("Settings");
                alertDialog.setMessage("Choose Theme Color...");
                View view2 = LayoutInflater.from(Profile.this).inflate(R.layout.settings_dialog, null);
                final Spinner spinner = view2.findViewById(R.id.spinner);
                alertDialog.setView(view2);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String color = spinner.getSelectedItem().toString();
                        Toast.makeText(Profile.this, color + " color applied", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.show();
            }
        });
    }

    private String getExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

