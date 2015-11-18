package com.sym.kobel.labo3;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

    private static final String APP_TAG = "labo3";

    /* View related elements */
    private EditText editMail;
    private EditText editPWD;
    private TextView textViewResult;
    private Button loginButton;

    /* Stuff for NFC */
    private NfcAdapter nfcAdapter;

    /* Variables for checks */
    private String login, passwd;

    /* Hard Coded credentials */
    private String hcLogin = "abc@def.ch";
    private String hcPasswd = "123abc";

    /* others */
    private static final int MAX_ACCRED_LEVEL = 10;
    private static final int MED_ACCRED_LEVEL = 5;
    private static final int MIN_ACCRED_LEVEL = 1;

    private static int currAccredLevel;

    private boolean checkCredentials(){
        login = editMail.getText().toString();
		passwd = editPWD.getText().toString();
        return login.equals(hcLogin) && passwd.equals(hcPasswd);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        editMail = (EditText) findViewById(R.id.editMail);
        editPWD = (EditText) findViewById(R.id.editPWD);
        loginButton = (Button) findViewById(R.id.loginButton);
        currAccredLevel = 0;


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCredentials()){
                    currAccredLevel = MAX_ACCRED_LEVEL;
                    textViewResult.setText("Credential ok ! You have now accreditation level" + currAccredLevel);
                } else {
                    currAccredLevel = 0;
                    textViewResult.setText("Login failed ! You dont have access to this phone");
                }
            }
        });
        //nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        /* Check if Nfc is available */
        /*if(nfcAdapter == null){
            Toast.makeText(this,"No NFC available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(!nfcAdapter.isEnabled()){
            textViewResult.setText("Ooops nfc is disabled");
        } else {
            textViewResult.setText("We have nfc");
        }*/



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
            // handle scan result
        }
        else {
            // Failed to read the damn thing!
        }
        // rest of the code
    }

    public void checkQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    private class DecreaseAuthLevelActivity extends AsyncTask<int, int, int>{

        @Override
        protected int doInBackground(int... params) {
            if (true);
            return 0;
        }
    }
}
