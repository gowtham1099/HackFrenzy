package com.example.hackfrenzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Upload_Post extends AppCompatActivity {

    private Button choose_image,upload,choose_video,upload_video;
    private EditText enter_desc,tittle,des;
    private ImageView image;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private Uri videoUri,resultUri;
    private StorageReference storageReference;
    private String name,profile_Picture,email;
    private String timeStamp;
    private VideoView videoView;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                upload.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                enter_desc.setVisibility(View.VISIBLE);
                tittle.setVisibility(View.INVISIBLE);
                des.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                upload_video.setVisibility(View.INVISIBLE);
                resultUri = data.getData();
                try{
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                    image.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            tittle.setVisibility(View.VISIBLE);
            des.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            upload_video.setVisibility(View.VISIBLE);
            upload.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            enter_desc.setVisibility(View.INVISIBLE);
            videoUri = data.getData();
            videoView.setVideoURI(data.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Upload_Post.this, "Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Upload_Post.this, "Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__post);

        image=findViewById(R.id.imageView6);
        choose_video=findViewById(R.id.choose_video);
        enter_desc=findViewById(R.id.enter_desc);
        choose_image=findViewById(R.id.choose_image);
        upload=findViewById(R.id.upload);
        storageReference=FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        upload_video=findViewById(R.id.upload_video);
        videoView=findViewById(R.id.videoView);
        tittle=findViewById(R.id.enter_tittle);
        des=findViewById(R.id.descr);

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Upload_Post.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Upload_Post.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                } else {
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select picture"), 100);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description=enter_desc.getText().toString();
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat sf=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
                timeStamp=sf.format(calendar.getTime());
                if(description.isEmpty()){
                    Toast.makeText(Upload_Post.this,"Please enter description",Toast.LENGTH_LONG).show();
                }
                else {
                    if(resultUri!=null){
                        final ProgressDialog progressDialog=new ProgressDialog(Upload_Post.this);
                        progressDialog.setMessage("Uploading.....");
                        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).addSnapshotListener(Upload_Post.this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                name=value.getString("username");
                                profile_Picture=value.getString("profile_Picture");
                                email=value.getString("email");
                            }
                        });
                        progressDialog.show();
                        StorageReference storageReference1=storageReference.child("Users").child(firebaseAuth.getCurrentUser().getEmail()).child("posts").child(System.currentTimeMillis()+"."+getExtension(resultUri));
                        storageReference1.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getMetadata() != null) {
                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final Post_Model post_model=new Post_Model(description,email,uri.toString(),null,profile_Picture,timeStamp,name);
                                                firebaseFirestore.collection("posts").document().set(post_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Upload_Post.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                                        finish();
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
                                Toast.makeText(Upload_Post.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(Upload_Post.this,"Please Select Image",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        choose_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Upload_Post.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Upload_Post.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                } else {
                    Intent i = new Intent();
                    i.setType("videos/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select video"), 1000);
                }
            }
        });

        upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat sf=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
                timeStamp=sf.format(calendar.getTime());
                final String tittle1 = tittle.getText().toString();
                final String des1 = des.getText().toString();
                if (tittle1.isEmpty()) {
                    Toast.makeText(Upload_Post.this, "Please enter tittle", Toast.LENGTH_LONG).show();
                } else {
                    if (videoUri != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(Upload_Post.this);
                        progressDialog.setTitle("Uploading");
                        progressDialog.show();
                        StorageReference storageReference1 = storageReference.child("Users").child(firebaseAuth.getCurrentUser().getEmail()).child("videos").child(System.currentTimeMillis() + "." + getExtension(videoUri));
                        storageReference1.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getMetadata() != null) {
                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final Status_Model status_model = new Status_Model(des1, firebaseAuth.getCurrentUser().getEmail(), tittle1, timeStamp, uri.toString());
                                                firebaseFirestore.collection("videos").document().set(status_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(Upload_Post.this, "uploaded", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(Upload_Post.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }
    private String getExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}