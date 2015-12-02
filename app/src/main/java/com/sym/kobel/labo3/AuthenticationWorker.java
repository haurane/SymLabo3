package com.sym.kobel.labo3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by haurane on 12/1/15.
 */
public class AuthenticationWorker {


    /* Hard Coded credentials */
    private final String hcLogin = "abc@def.ch";
    private final String hcPasswd = "123abc";
    private final String hcNfcLogin = "nicolas";
    private final String hcNfcPasswd = "1234";

    /* Variables for checks */
    private String login, passwd;
    private String nfcLogin, nfcPasswd;

    private AuthentListener listener;

    private int currentAccred;



    private static final String TAG = AuthenticationWorker.class.getName();

    public void registerListener(AuthentListener l){
        listener = l;
    }

    public boolean authentPasswd(String login, String passwd){
        new Authenticator().execute(login, passwd);
        return true;
    }

    public boolean authentNFC(String login, String passwd, String nfcLogin, String nfcPasswd){
        new Authenticator().execute(login,passwd,nfcLogin,nfcPasswd);
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
                currentAccred = MainActivity.MED_ACCRED_LEVEL;
                if(params.length == 4 && nfcLogin.equals(hcNfcLogin) && nfcPasswd.equals(hcNfcPasswd)){
                    currentAccred = MainActivity.MAX_ACCRED_LEVEL;
                }
            }



            while (currentAccred >= MainActivity.MIN_ACCRED_LEVEL){
                try {
                    publishProgress(currentAccred);
                    Thread.sleep(2000,0);
                    currentAccred --;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return MainActivity.MIN_ACCRED_LEVEL;
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
