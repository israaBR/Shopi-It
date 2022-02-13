package com.example.shop_it;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;

public class loginActivity extends AppCompatActivity {

    SharedPreferences preference;
    SharedPreferences.Editor prefEditor;
    CheckBox rememberMeBox;
    EditText emailtxt, passtxt;
    Button loginbtn;
    TextView forgotPass, register;
    databaseHelper dbh;
    String email, password;
    int customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailtxt = (EditText) findViewById(R.id.emailTxtLog);
        passtxt = (EditText) findViewById(R.id.passTxtLog);
        loginbtn = (Button) findViewById(R.id.loginBtnLog);
        forgotPass = (TextView) findViewById(R.id.forgetPassLog);
        register = (TextView) findViewById(R.id.registerLog);
        rememberMeBox = (CheckBox) findViewById(R.id.rememberMeLog);
        dbh = new databaseHelper(this);
        preference = getSharedPreferences("myPreferences", MODE_PRIVATE);
        prefEditor = preference.edit();

        email = preference.getString("customer_email", null);
        if(email != null) { //data exists
            password = preference.getString("customer_password", null);
            customer_id = preference.getInt("customer_id", -1);
            emailtxt.setText(email);
            passtxt.setText(password);
            rememberMeBox.setChecked(true);
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailtxt.getText().toString();
                password = passtxt.getText().toString();
                boolean customerExist = dbh.check_customer(email, password);

                if (customerExist) {
                    Toast.makeText(getApplicationContext(), "You're now logged in",Toast.LENGTH_LONG).show();
                    customer_id = dbh.get_customer_id(email);
                    //save customer data
                    if(rememberMeBox.isChecked()) {
                        prefEditor.putString("customer_email", email);
                        prefEditor.putString("customer_password", password);
                        prefEditor.putInt("customer_id", customer_id);
                        prefEditor.apply();
                    }

                    Intent intent = new Intent(loginActivity.this, homeActivity.class);
                    intent.putExtra("customer_id", customer_id);
                    startActivity(intent);
                }
               else
                   Toast.makeText(getApplicationContext(), "You've entered wrong email or password",Toast.LENGTH_LONG).show();

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });
    }

    void showCustomDialog() {
        final Dialog dialog = new Dialog(loginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.emaildialog);
        final EditText emailTxtDialog = dialog.findViewById(R.id.emailEmailDialog);
        Button proceedbtn = dialog.findViewById(R.id.proceedbtn);
        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailTxtDialog.getText().toString();
                int customer_id = dbh.get_customer_id(email);
                if (customer_id == -1)
                    Toast.makeText(getApplicationContext(), "You've entered wrong email", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(loginActivity.this, forgotPasswordActivity.class);
                    intent.putExtra("customer_id", customer_id);
                    startActivity(intent);
                }
            }
        });
        dialog.show();
    }
}