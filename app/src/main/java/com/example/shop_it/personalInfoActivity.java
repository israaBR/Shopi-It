package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class personalInfoActivity extends AppCompatActivity {

    TextView nameTxt, emailTxt, phoneTxt, addressTxt, birthdateTxt;
    Button editbtn;
    databaseHelper dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalinfo);

        nameTxt = (TextView) findViewById(R.id.nameTxtPIV);
        emailTxt = (TextView) findViewById(R.id.emailTxtPIV);
        birthdateTxt = (TextView) findViewById(R.id.birthdateTxtPIV);
        phoneTxt = (TextView) findViewById(R.id.mobileTxtPIV);
       // addressTxt = (TextView) findViewById(R.id.addressTxtPIV);
        editbtn = (Button) findViewById(R.id.editBtnPIV);
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


        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personalInfoActivity.this, personalInfoEditActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });

    }
}
