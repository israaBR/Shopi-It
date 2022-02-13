package com.example.shop_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class resetPasswordQuestionsActivity extends AppCompatActivity {

    Spinner firstQuestionTxt, secondQuestionTxt;
    EditText firstAnswerTxt, secondAnswerTxt;
    Button savebtn;
    databaseHelper dbh;
    Cursor questionsCursor;
    int customer_id;
    ArrayAdapter<String> questionsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpasswordquestions);

        firstQuestionTxt = (Spinner) findViewById(R.id.question1Spn);
        secondQuestionTxt = (Spinner) findViewById(R.id.question2Spn);
        firstAnswerTxt = (EditText) findViewById(R.id.answer1);
        secondAnswerTxt = (EditText) findViewById(R.id.answer2);
        savebtn = (Button) findViewById(R.id.questionSaveBtn);
        customer_id = getIntent().getExtras().getInt("customer_id");
        dbh = new databaseHelper(this);
        questionsCursor = dbh.get_security_questions();
        String[] questions = new String[questionsCursor.getCount()];
        for(int i = 0; i<questionsCursor.getCount(); i++) {
            questions[i] = questionsCursor.getString(1);
            questionsCursor.moveToNext();
        }
        questionsArr = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, questions);
        questionsArr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstQuestionTxt.setAdapter(questionsArr);
        secondQuestionTxt.setAdapter(questionsArr);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstAns = firstAnswerTxt.getText().toString();
                String secondAns = secondAnswerTxt.getText().toString();
                String firstQue = firstQuestionTxt.getSelectedItem().toString();
                String secondQue = secondQuestionTxt.getSelectedItem().toString();
                int firstQueID = 0;
                int secondQueID = 0;

                questionsCursor.moveToFirst();
                for(int i = 0; i<questionsCursor.getCount(); i++) {
                    if(questionsCursor.getString(1).equals(firstQue))
                        firstQueID = Integer.parseInt(questionsCursor.getString(0));
                    else if(questionsCursor.getString(1).equals(secondQue))
                        secondQueID = Integer.parseInt(questionsCursor.getString(0));
                    questionsCursor.moveToNext();
                    }
                dbh.add_customer_security_questions(customer_id, firstQueID, firstAns, secondQueID, secondAns);
                Toast.makeText(getApplicationContext(), "Security questions updated succesfuly", Toast.LENGTH_LONG);
                Intent intent = new Intent(resetPasswordQuestionsActivity.this, homeActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });


    }
}
