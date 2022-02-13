package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.Calendar;

public class personalInfoEditActivity extends AppCompatActivity {

    EditText nameTxt, emailTxt, phoneTxt, addressTxt, birthdateTxt;
    Button savebtn;
    databaseHelper dbh;

    DatePickerDialog.OnDateSetListener dateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalinfoedit);

        nameTxt = (EditText) findViewById(R.id.nameTxtPIE);
        emailTxt = (EditText) findViewById(R.id.emailTxtPIE);
        birthdateTxt = (EditText) findViewById(R.id.birthdateTxtPIE);
        phoneTxt = (EditText) findViewById(R.id.mobileTxtPIE);
       // addressTxt = (EditText) findViewById(R.id.addressTxtPIE);
        savebtn = (Button) findViewById(R.id.saveBtnPIE);
        dbh = new databaseHelper(this);
        int customer_id = getIntent().getExtras().getInt("customer_id");

        Cursor customerInfoCursor = dbh.get_customer_data(customer_id);
        nameTxt.setText(customerInfoCursor.getString(1));//view name
        emailTxt.setText(customerInfoCursor.getString(2));//view email
        if(customerInfoCursor.getString(4) == null)//view phone number
            phoneTxt.setText("");
        else
            phoneTxt.setText(customerInfoCursor.getString(4));
        if(customerInfoCursor.getString(5) == null)//view birthdate
            birthdateTxt.setText("");
        else
            birthdateTxt.setText(customerInfoCursor.getString(5));


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        birthdateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(personalInfoEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);
                        birthdateTxt.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //update in database
                String name = nameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String phone = phoneTxt.getText().toString();
                String birthdate = birthdateTxt.getText().toString();
                dbh.update_personalInfo(customer_id, name, email, phone, birthdate);
                Toast.makeText(getApplicationContext(), "Your personal info is now updated!", Toast.LENGTH_LONG);
                Intent intent = new Intent(personalInfoEditActivity.this, homeActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });
    }
}
