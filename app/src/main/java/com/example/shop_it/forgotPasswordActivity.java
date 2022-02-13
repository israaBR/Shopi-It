package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class forgotPasswordActivity extends AppCompatActivity {

    TextView question1txt, question2txt;
    EditText answer1txt, answer2txt;
    Button continuebtn;
    databaseHelper dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        question1txt = (TextView) findViewById(R.id.question1reset);
        question2txt = (TextView) findViewById(R.id.question2reset);
        answer1txt = (EditText) findViewById(R.id.answer1reset);
        answer2txt = (EditText) findViewById(R.id.answer2reset);
        continuebtn = (Button) findViewById(R.id.continueBtn);
        dbh = new databaseHelper(this);
        int customer_id = getIntent().getExtras().getInt("customer_id");
        //get first question and it's answer
        Cursor questionsCursor = dbh.get_customer_questions(customer_id);
        Cursor question1Cursor = dbh.get_question(questionsCursor.getInt(1));
        String question1answer = questionsCursor.getString(2);
        question1txt.setText(question1Cursor.getString(1));
        //get second question and it's answer
        questionsCursor.moveToNext();
        Cursor question2Cursor = dbh.get_question(questionsCursor.getInt(1));
        String question2answer = questionsCursor.getString(2);
        question2txt.setText(question2Cursor.getString(1));


        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer1 = answer1txt.getText().toString();
                String answer2 = answer2txt.getText().toString();

                if(question1answer.equals(answer1) && question2answer.equals(answer2)){

                    Intent intent = new Intent(forgotPasswordActivity.this, resetPasswordActivity.class);
                    intent.putExtra("customer_id", customer_id);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "You've entered wrong answers", Toast.LENGTH_LONG);
            }
        });

    }
}