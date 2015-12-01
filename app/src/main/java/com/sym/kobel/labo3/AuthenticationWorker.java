package com.sym.kobel.labo3;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by haurane on 12/1/15.
 */
public class AuthenticationWorker {

    private static final int MAX_ACCRED_LEVEL = 10;
    private static final int MED_ACCRED_LEVEL = 5;
    private static final int MIN_ACCRED_LEVEL = 1;

    /* Hard Coded credentials */
    private final String hcLogin = "abc@def.ch";
    private final String hcPasswd = "123abc";
    private final String hcNfcLogin = "AAA";
    private final String hcNfcPasswd = "BBB";

    /* Variables for checks */
    private String login, passwd;
    private String nfcLogin, nfcPasswd;

    private AuthentListener listener;

    private int currentAccred;



    private static final String TAG = AuthenticationWorker.class.getName();

    public void registerListener(AuthentListener l){
        listener = l;
    }

    public boolean authent(String login, String passwd){
        new Authenticator().execute(login, passwd);
        return true;
    }


    private class Authenticator extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Log.d(TAG, "doInBackground ");
            if(params.length == 2){
                login = params[0];
                passwd = params[1];
            }

            if(params.length == 4){
                login = params[0];
                passwd = params[1];
                nfcLogin = params[2];
                nfcPasswd = params[3];
            }

            if( login.equals(hcLogin) && passwd.equals(hcPasswd)){
                currentAccred = MED_ACCRED_LEVEL;
                if(params.length == 4 && nfcLogin.equals(hcNfcLogin) && nfcPasswd.equals(hcNfcPasswd)){
                    currentAccred = MAX_ACCRED_LEVEL;
                }
            }



            while (currentAccred >= MIN_ACCRED_LEVEL){
                try {
                    publishProgress(currentAccred);
                    Thread.sleep(1000,0);
                    currentAccred --;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return MIN_ACCRED_LEVEL;
        }

        protected void onProgressUpdate(Integer... progress){
            Log.d(TAG, "onProgressUpdate " + progress[0]);
            listener.handleAuthentification(progress[0]);

        }

        protected void onPostExecute(Integer i){
            Log.d(TAG, "onPostExecute " + i);
            listener.handleAuthentification(i);
        }
    }
}
