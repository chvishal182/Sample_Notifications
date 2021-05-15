package com.example.vdev_notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText fullName,phone,userMail,password;
    TextView welcome,toLog;
    MediaPlayer greet;
    Button regButton;
    String mail,pwd,name,phn;
    ProgressBar progReg;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore mFireStore ;
    DocumentReference documentReference;//-----> this variable is used for referring the  documents created in the FirebaseFirestore under a collection
    FirebaseDatabase rootNode=FirebaseDatabase.getInstance();
    DatabaseReference reference;//sub node
    String userId;
    Map<String,Object> hashMap=new HashMap<>();
    HashMap<String,String>userInfo=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName=findViewById(R.id.full);
        password=findViewById(R.id.sec);
        phone=findViewById(R.id.contact);
        userMail=findViewById(R.id.mail);
        welcome=findViewById(R.id.wel);
        toLog=findViewById(R.id.textView123);
        regButton=findViewById(R.id.regButton);
        progReg=findViewById(R.id.progReg);
        progReg.setVisibility(View.GONE);
        //greet=MediaPlayer.create(this,R.raw.aot);
        //greet.start();
        mAuth=FirebaseAuth.getInstance();
        mFireStore=FirebaseFirestore.getInstance();

        toLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,com.example.vdev_notify.Login.class));
                finish();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userRegister();
            }
        });

        if(mAuth.getCurrentUser()!=null&&mAuth.getCurrentUser().isEmailVerified())
        {
            startActivity(new Intent(Register.this,com.example.vdev_notify.MainActivity.class));
            finish();
        }
//       else if(mAuth.getCurrentUser()!=null&&!mAuth.getCurrentUser().isEmailVerified())
//       {
//           startActivity(new Intent(Register.this,com.example.testauth.Login.class));
//           finish();
//       }

    }

    private void userRegister()
    {
        name=fullName.getText().toString().trim();
        mail=userMail.getText().toString().trim();
        pwd=password.getText().toString().trim();
        phn=phone.getText().toString().trim();
        if(name.isEmpty())
        {
            fullName.setError("Name Should be Filled");
            fullName.requestFocus();
            return;
        }
        else if(mail.isEmpty()/*TextUtils.isEmpty(mail)*/)
        {
            userMail.setError("Email Should be Filled");
            userMail.requestFocus();
            return;

        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches())
        {
            userMail.setError("Email Should be Filled in the CORRECT FORMAT");
            userMail.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(pwd))
        {
            password.setError("Email Should be Filled");
            password.requestFocus();
            return;
        }
        else if(pwd.length()<6)
        {
            password.setError("The minimum length of a Password must be 7 units");
            password.requestFocus();
            return;
        }
        else if(mail.isEmpty()&&TextUtils.isEmpty(pwd))
        {
            userMail.setError("Email Should be Filled");
            userMail.requestFocus();
            password.setError("Email Should be Filled");
            password.requestFocus();
            return;

        }
        else if(phn.isEmpty())
        {
            phone.setError("Phone Number Should be Filled");
            phone.requestFocus();
            return;
        }

        else
        {
            progReg.setVisibility(View.VISIBLE);
        }

        mAuth.createUserWithEmailAndPassword(mail,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Register.this, "An Verification Mail Has Been Sent To Your Registered Mail", Toast.LENGTH_SHORT).show();

                                progReg.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "User Registration has been Successful :) ", Toast.LENGTH_SHORT).show();
                                //saveToFireStore();
                                saveToDatabase();
                                startActivity(new Intent(Register.this,com.example.vdev_notify.Login.class));
                                finish();



                            }
                            else
                            {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

                else
                {
                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progReg.setVisibility(View.GONE);
                }
            }
        });


    }

    private void saveToDatabase()
    {
        userId=mAuth.getCurrentUser().getUid();
        reference = rootNode.getReference("users").child(userId);
        userInfo.put("Name",name);
        userInfo.put("Email",mail);
        userInfo.put("Contact",phn);
        userInfo.put("Password",pwd);
        reference.setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(Register.this, "User-Data has been Updated Successfully :)", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(Register.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFireStore()
    {
        userId=mAuth.getCurrentUser().getUid();
        documentReference= mFireStore.collection( "users").document(userId);
        hashMap.put("Name",name);
        hashMap.put("Email",mail);
        hashMap.put("Contact",phn);

        //inserting the hashMap to the FireStoreFirebase
        documentReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Register.this, "User-Data has been Updated Successfully :)", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this,e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }


}


