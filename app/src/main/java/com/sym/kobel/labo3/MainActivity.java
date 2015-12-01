package com.sym.kobel.labo3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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

    public static final String MIME_TEXT_PLAIN = "text/plain";

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
    private Button scanQRButton;
    /* Stuff for NFC */
    private NfcAdapter nfcAdapter;
    private boolean hasNFC;
    private NFCWorker nfcWorker;

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
        scanQRButton = (Button) findViewById(R.id.scanQRButton);
        verySecretView = (TextView) findViewById(R.id.very_secret_view);
        secretView = (TextView) findViewById(R.id.secret_view);
        publicView = (TextView) findViewById(R.id.public_view);
        authent = new AuthenticationWorker();
        nfcWorker = new NFCWorker();
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

        nfcWorker.registerListener(new NFCListener() {
            @Override
            public boolean handleNFC(String[] strings) {
                textViewResult.setText(strings[0] + " " + strings[1]);
                return true;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMail.getText().length() == 0 || editPWD.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "Please enter login and password", Toast.LENGTH_LONG);
                    return;
                }
                authent.authent(editMail.getText().toString(), editPWD.getText().toString());
            }
        });

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkQRCode();
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

        handleIntent(getIntent());



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
            Log.d(TAG, "onActivityResult Yeah scanned!!!!");
            textViewResult.setText(scanResult.getContents());
            Toast.makeText(this,"Yeah scanned",Toast.LENGTH_LONG);
        }
        else {
            // Failed to read the damn thing!
            Log.d(TAG, "onActivityResult Nope :(");
            Toast.makeText(this,"Nope",Toast.LENGTH_LONG);
        }
        // rest of the code
    }

    public void checkQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    protected void onResume(){
        super.onResume();
        setupForegroundDispatch(this, nfcAdapter);
    }


    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        Log.d(TAG, "setupForegroundDispatch 1");

        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[2];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);

        filters[1] = new IntentFilter();
        filters[1].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filters[1].addAction(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        try{
            filters[1].addDataType(MIME_TEXT_PLAIN);
        }catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    private void handleIntent(Intent intent){
        String action = intent.getAction();
        Log.d(TAG, "handleIntent " + action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Log.d(TAG, "handleIntent " + tag.toString() + (nfcWorker == null));
                nfcWorker.handleTag(tag);
            } else {
                Log.d(TAG, "handleIntent false MIME");
            }


        }


    }

    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }
}
