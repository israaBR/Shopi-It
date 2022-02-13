package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class registerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText usernametxt = (EditText) findViewById(R.id.userNameTxtReg);
        EditText emailtxt = (EditText) findViewById(R.id.emailTxtReg);
        EditText passtxt = (EditText) findViewById(R.id.passTxtReg);
        EditText confirmpasstxt = (EditText) findViewById(R.id.confirmPassTxtReg);
        Button registerbtn = (Button) findViewById(R.id.registerBtnReg);
        TextView login = (TextView) findViewById(R.id.loginReg);
        final databaseHelper dbh = new databaseHelper(this);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = usernametxt.getText().toString();
                String email = emailtxt.getText().toString();
                String password = passtxt.getText().toString();
                String confirmPassword = confirmpasstxt.getText().toString();

                if(!password.equals(confirmPassword))
                    Toast.makeText(getApplicationContext(), "Password doesn't match",Toast.LENGTH_LONG).show();

                boolean customer_added = dbh.add_customer(name, email, password);
                if(customer_added)
                {
                    Toast.makeText(getApplicationContext(), "You're now registered",Toast.LENGTH_LONG).show();
                    int customer_id = dbh.get_customer_id(email);
                    dbh.add_customer_cart(customer_id);
                    Intent intent = new Intent(registerActivity.this,homeActivity.class);
                    intent.putExtra("customer_id", customer_id);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "Email already exists",Toast.LENGTH_LONG).show();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }
}