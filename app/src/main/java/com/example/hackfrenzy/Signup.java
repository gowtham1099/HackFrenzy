package com.example.hackfrenzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.Single;

public class Signup extends AppCompatActivity {
    private EditText email,username,phone_no;
    private TextInputLayout password;
    private Button signup;
    private TextView click_here,privacy_view,terms_view;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CheckBox privacy,terms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email=findViewById(R.id.enter_email);
        password=findViewById(R.id.enter_pwd);
        username=findViewById(R.id.enter_username);
        privacy=findViewById(R.id.privacy);
        terms=findViewById(R.id.terms);
        privacy_view=findViewById(R.id.privacy_onclick);
        terms_view=findViewById(R.id.terms_onclick);
        phone_no=findViewById(R.id.enter_phone);
        click_here=findViewById(R.id.click_here);
        signup=findViewById(R.id.login);
        progressDialog=new ProgressDialog(Signup.this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Signup.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email_id=email.getText().toString();
                String pwd=password.getEditText().getText().toString();
                final String user_name=username.getText().toString();
                final String phone_number=phone_no.getText().toString();

                if(email_id.isEmpty() && pwd.isEmpty() && user_name.isEmpty() && phone_number.isEmpty()){
                    Toast.makeText(Signup.this,"Please provide valid details",Toast.LENGTH_LONG).show();
                }
                else {
                    if(privacy.isChecked() && terms.isChecked()){
                        progressDialog.setMessage("Signing Up Please Wait....");
                        progressDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(email_id,pwd).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                User_Model details=new User_Model(email_id,null,"online",phone_number,null,user_name);
                                                firebaseFirestore.collection("users").document(email_id).set(details)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(Signup.this,"verification email send to your account, please verify before you login.",Toast.LENGTH_LONG).show();
                                                                    Intent intent=new Intent(Signup.this,Login.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(Signup.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Signup.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(Signup.this,"check 'terms and conditions' and 'Privacy policy'",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        terms_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1= LayoutInflater.from(Signup.this).inflate(R.layout.terms_dialog,null);
                WebView webView=view1.findViewById(R.id.terms);
                webView.loadUrl("file:///android_asset/terms.html");
                webView.getSettings().setJavaScriptEnabled(true);
                AlertDialog alertDialog=new AlertDialog.Builder(Signup.this).create();
                alertDialog.setTitle("Terms and Conditions");
                alertDialog.setView(view1);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        privacy_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1= LayoutInflater.from(Signup.this).inflate(R.layout.terms_dialog,null);
                WebView webView=view1.findViewById(R.id.terms);
                webView.loadUrl("file:///android_asset/privacy.html");
                webView.getSettings().setJavaScriptEnabled(true);
                AlertDialog alertDialog=new AlertDialog.Builder(Signup.this).create();
                alertDialog.setTitle("Privacy policy");
                alertDialog.setView(view1);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
}