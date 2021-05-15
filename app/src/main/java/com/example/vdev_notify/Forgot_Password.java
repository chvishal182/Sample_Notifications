package com.example.vdev_notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class Forgot_Password extends AppCompatActivity {

    FirebaseAuth forgotAuth;
    TextView forgotWel;
    EditText forgotMail;
    Button resetPassword;
    ProgressBar progForget;
    String getMail,userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);

        forgotWel=findViewById(R.id.forgotwel);
        forgotMail=findViewById(R.id.fogotmail);
        resetPassword=findViewById(R.id.forgetButton);
        progForget=findViewById(R.id.progForget);
        progForget.setVisibility(View.INVISIBLE);
        forgotAuth=FirebaseAuth.getInstance();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPwd();
            }
        });

//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//            @Override
//            public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                if(task.isSuccessful())
//                {
//                    userToken = task.getResult().getToken().trim();
//
//                }else
//                {
//                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void resetPwd()
    {
        getMail=forgotMail.getText().toString().trim();

        if(getMail.isEmpty())
        {
            forgotMail.setError("Email should be Filled");
            forgotMail.requestFocus();
            return ;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(getMail).matches())
        {
            forgotMail.setError("Please Enter the Email-Id in the CORRECT FORMAT!!!!");
            forgotMail.requestFocus();
            return ;
        }

        progForget.setVisibility(View.VISIBLE);

        forgotAuth.sendPasswordResetEmail(getMail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progForget.setVisibility(View.GONE);
                        Toast.makeText(Forgot_Password.this, "Reset Password Link has been sent to your\nE-mail", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        progForget.setVisibility(View.GONE);
                        Toast.makeText(Forgot_Password.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}