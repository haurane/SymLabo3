package com.sym.kobel.labo3;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private boolean checkCredentials(){
        login = editMail.getText().toString();
		passwd = editPWD.getText().toString();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        editMail = (EditText) findViewById(R.id.editMail);
        editPWD = (EditText) findViewById(R.id.editPWD);
        loginButton = (Button) findViewById(R.id.loginButton);

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
}
