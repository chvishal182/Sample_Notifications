package com.example.vdev_notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView toReg,wel;
    EditText userEmailId,logPass;
    Button logButton,logForgot;
    String logMail,logpass;
    FirebaseDatabase inst;
    DatabaseReference ref;
    ProgressBar logProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wel=findViewById(R.id.wel);
        toReg=findViewById(R.id.toReg);
        userEmailId=findViewById(R.id.userEmailId);
        logPass=findViewById(R.id.logPass);
        logButton=findViewById(R.id.logButton);
        logForgot=findViewById(R.id.forgot);
        logProg= findViewById(R.id.logProg);
        logProg.setVisibility(View.GONE);

        mAuth=FirebaseAuth.getInstance();
        inst=FirebaseDatabase.getInstance();
        logForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,com.example.vdev_notify.Forgot_Password.class));
            }
        });
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                logUser();
            }
        });

        toReg.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Login.this,com.example.vdev_notify.Register.class));
                finish();
            }
        }));


    }

    private void logUser()
    {
        logMail=userEmailId.getText().toString().trim();
        logpass=logPass.getText().toString().trim();
        if(logMail.isEmpty()&& TextUtils.isEmpty(logpass))
        {
            userEmailId.setError("Email Should be Filled");
            userEmailId.requestFocus();
            logPass.setError("Password  Should be Filled");
            logPass.requestFocus();
            return;
        }
        else if(logMail.isEmpty()/*TextUtils.isEmpty(mail)*/)
        {
            userEmailId.setError("Email Should be Filled");
            userEmailId.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(logMail).matches())
        {
            userEmailId.setError("Email Should be Filled in the CORRECT FORMAT");
            userEmailId.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(logpass))
        {
            logPass.setError("Email Should be Filled");
            logPass.requestFocus();
            return;
        }
        else if(logPass.length()<6)
        {
            logPass.setError("The minimum length of a Password must be 7 units");
            logPass.requestFocus();
            return;
        }
        else
        {
            logProg.setVisibility(View.VISIBLE);
        }



        mAuth.signInWithEmailAndPassword(logMail,logpass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult)
            {
                ref=inst.getReference("users").child(mAuth.getCurrentUser().getUid()).child("Password");
                if(mAuth.getCurrentUser().isEmailVerified())
                {



                    logProg.setVisibility(View.INVISIBLE);
                    ref.setValue(logpass);
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this,com.example.vdev_notify.MainActivity.class));
                    finish();


//                         else
//                         {
//                             logProg.setVisibility(View.INVISIBLE);
//                             Toast.makeText(Login.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                         }
                }
                else
                {
                    Toast.makeText(Login.this, "Please Verify Your Email-ID for for Authenticity", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                logProg.setVisibility(View.INVISIBLE);
                Toast.makeText(Login.this,e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}