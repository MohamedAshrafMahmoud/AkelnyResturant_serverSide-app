package com.example.mohamed.akelny_serverside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohamed.akelny_serverside.Common.Common;
import com.example.mohamed.akelny_serverside.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn_ser extends AppCompatActivity {

    EditText phone,password;
    Button btnsignin;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        phone =(EditText)findViewById(R.id.editText);
        password =(EditText)findViewById(R.id.editText2);
        btnsignin=(Button)findViewById(R.id.button3);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("User");

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUser(phone.getText().toString(),password.getText().toString());
            }
        });



    }

    private void SignUser(String phone, String password) {

        final ProgressDialog progressDialog=new ProgressDialog(SignIn_ser.this);
        progressDialog.setMessage("please wait ....");
        progressDialog.show();

        final String localphone=phone;
        final String localpassword=password;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(localphone).exists()){
                    progressDialog.dismiss();
                    User user=dataSnapshot.child(localphone).getValue(User.class);
                    user.setPhone(localphone);

                    if (Boolean.parseBoolean(user.getIsStaff())){  //if isStuff is true

                        if (user.getPassword().equals(localpassword)){
                            Intent intent=new Intent(SignIn_ser.this,Home_ser.class);
                            Common.currentUser=user;
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(SignIn_ser.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(SignIn_ser.this, "please login with Stuff Acount", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SignIn_ser.this, "user not exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
