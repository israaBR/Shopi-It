package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class resetPasswordActivity extends AppCompatActivity {
    EditText newPassword, confirmPassword;
    Button updatebtn;
    databaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);

        newPassword = (EditText) findViewById(R.id.passTxtRP);
        confirmPassword = (EditText) findViewById(R.id.confirmPassTxtRP);
        updatebtn = (Button) findViewById(R.id.updatePassBtnRP);

        String newPass = newPassword.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        int customer_id = getIntent().getExtras().getInt("customer_id");
        dbh = new databaseHelper(this);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!newPass.equals(confirmPass))
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
                else
                {
                    dbh.reset_password(customer_id, newPass);
                    Toast.makeText(getApplicationContext(), "Password reset! login with your new password", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(resetPasswordActivity.this, loginActivity.class);
                    startActivity(intent);
                }

            }
        });



    }
}