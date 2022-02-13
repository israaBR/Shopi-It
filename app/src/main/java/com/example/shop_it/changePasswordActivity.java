package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class changePasswordActivity extends AppCompatActivity {

    EditText newPasstxt, confirmPasstxt, currentPasstxt;
    Button updatePassBtn;
    TextView forgotPass;
    databaseHelper dbh;
    int customer_id;
    String currentPass, newPass, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        newPasstxt = (EditText) findViewById(R.id.newPassTxtCP);
        confirmPasstxt = (EditText) findViewById(R.id.confirmPassTxtCP);
        currentPasstxt = (EditText) findViewById(R.id.currentPassTxtCP);
        forgotPass = (TextView) findViewById(R.id.forgotPassCP);
        updatePassBtn = (Button) findViewById(R.id.updatePassBtnCP);

        customer_id = getIntent().getExtras().getInt("customer_id");
        dbh = new databaseHelper(this);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(changePasswordActivity.this, resetPasswordActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });

        updatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPass = currentPasstxt.getText().toString();
                newPass = newPasstxt.getText().toString();
                confirmPass = confirmPasstxt.getText().toString();

                if (!newPass.equals(confirmPass))
                    Toast.makeText(getApplicationContext(), "passwords doesn't match", Toast.LENGTH_LONG).show();

                Cursor customer = dbh.get_customer_data(customer_id);
                if (customer.getString(3).equals(currentPass)) {
                    dbh.reset_password(customer_id, newPass);
                    Toast.makeText(getApplicationContext(), "password updated!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(changePasswordActivity.this, homeActivity.class);
                    intent.putExtra("customer_id", customer_id);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "You have entered a wrong password", Toast.LENGTH_LONG).show();

            }
        });


    }
}
