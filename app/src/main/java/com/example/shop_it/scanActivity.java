package com.example.shop_it;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class scanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);

        Button scanbtn = (Button) findViewById(R.id.scanbtn);
        int customer_id = getIntent().getExtras().getInt("customer_id");

        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(scanActivity.this);
                intentIntegrator.setCaptureActivity(captureActivity.class);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("Scanning code..");
                intentIntegrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
        if (result.getContents() != null) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        // set title
            Toast.makeText(getApplicationContext(),result.getContents(), Toast.LENGTH_SHORT).show();

            alertDialogBuilder.setTitle("Scanning Result");
        // set dialog message
        alertDialogBuilder
        .setMessage(result.getContents())
        .setCancelable(false)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
         public void onClick(final DialogInterface dialog, final int id) {
        // if this button is clicked, close the dialog box
        dialog.cancel();
        }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show the message
        alertDialog.show();


        //searchBar.setQuery(intentResult.getContents(), true);
        Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_SHORT).show();
        }

    }
 }

}