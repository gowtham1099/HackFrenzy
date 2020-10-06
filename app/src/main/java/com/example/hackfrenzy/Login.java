package com.example.hackfrenzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class Login extends AppCompatActivity {
    private EditText email;
    private TextInputLayout password;
    private Button login;
    private TextView forgot_pwd,signup_page;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.enter_email);
        password=findViewById(R.id.enter_pwd);
        login=findViewById(R.id.login);
        forgot_pwd=findViewById(R.id.forgot);
        signup_page=findViewById(R.id.click_here);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(Login.this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(firebaseAuth.getCurrentUser()!=null && firebaseAuth.getCurrentUser().isEmailVerified()){
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        signup_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,Signup.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_id=email.getText().toString();
                String pwd=password.getEditText().getText().toString();
                progressDialog.setMessage("Logging in....Please wait");
                if(email_id.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(Login.this,"Invalid details",Toast.LENGTH_LONG).show();
                }
                else{
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email_id,pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                    Intent intent=new Intent(Login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                }
                                else {
                                    progressDialog.dismiss();
                                    android.app.AlertDialog alertDialog=new android.app.AlertDialog.Builder(Login.this).create();
                                    alertDialog.setTitle("Email not Verified");
                                    alertDialog.setMessage("if you not received a email.....click resend....already received a email....click cancel");
                                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                    alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Resend", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(firebaseAuth!=null) {
                                                if (firebaseAuth.getCurrentUser() != null) {
                                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(Login.this, "Email sent...cheeck your inbox", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                    alertDialog.show();
                                }
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        forgot_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog alertDialog=new android.app.AlertDialog.Builder(Login.this).create();
                alertDialog.setTitle("Forgot Password");
                final EditText resetmail=new EditText(Login.this);
                resetmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                resetmail.setHint("Enter email");
                alertDialog.setView(resetmail);
                alertDialog.setMessage("Enter email address to receive a password reset link");
                alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail=resetmail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Login.this,"Password reset link send to your registered email address",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
            }
        });

    }
}