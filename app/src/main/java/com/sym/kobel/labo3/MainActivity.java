package com.sym.kobel.labo3;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

    private static final String APP_TAG = "labo3";
    /* others */

    private static int currAccredLevel;
    /* View related elements */
    private EditText editMail;
    private EditText editPWD;
    private TextView textViewResult;
    private TextView verySecretView;
    private TextView secretView;
    private TextView publicView;
    private Button loginButton;
    /* Stuff for NFC */
    private NfcAdapter nfcAdapter;
    private boolean hasNFC;

    private static final int MAX_ACCRED_LEVEL = 10;
    private static final int MED_ACCRED_LEVEL = 5;
    private static final int MIN_ACCRED_LEVEL = 1;

    private AuthenticationWorker authent;
    private static String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        editMail = (EditText) findViewById(R.id.editMail);
        editPWD = (EditText) findViewById(R.id.editPWD);
        loginButton = (Button) findViewById(R.id.loginButton);
        verySecretView = (TextView) findViewById(R.id.very_secret_view);
        secretView = (TextView) findViewById(R.id.secret_view);
        publicView = (TextView) findViewById(R.id.public_view);
        authent = new AuthenticationWorker();
        currAccredLevel = 0;

        authent.registerListener(new AuthentListener() {
            @Override
            public void handleAuthentification(Integer level) {
                Log.d(TAG, "handleAuthentification " + level);

                if(level == MAX_ACCRED_LEVEL){
                    verySecretView.setVisibility(View.VISIBLE);
                } else {
                    verySecretView.setVisibility(View.INVISIBLE);
                }
                if (level >= MED_ACCRED_LEVEL){
                    secretView.setVisibility(View.VISIBLE);
                } else {
                    secretView.setVisibility(View.INVISIBLE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authent.authent(editMail.getText().toString(), editPWD.getText().toString());
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        /* Check if Nfc is available */
        if(nfcAdapter == null){
            Toast.makeText(this,"No NFC available\n" +
                    "You will not be able to log into highest security", Toast.LENGTH_LONG).show();
            hasNFC = false;
        } else {
            hasNFC = true;
        }
        if(hasNFC){
            if(!nfcAdapter.isEnabled()){
                textViewResult.setText("Ooops nfc is disabled");
            } else {
                textViewResult.setText("We have nfc");
            }
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Toast.makeText(this,"Yeah scanned",Toast.LENGTH_LONG);
        }
        else {
            // Failed to read the damn thing!
            Toast.makeText(this,"Nope",Toast.LENGTH_LONG);
        }
        // rest of the code
    }

    public void checkQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

 /*   private class DecreaseAuthLevelActivity extends AsyncTask<int, int, int>{

        @Override
        protected int doInBackground(int... params) {
            if (true);
            return 0;
        }
    }*/
}
