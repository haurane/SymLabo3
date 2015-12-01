package com.sym.kobel.labo3;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by haurane on 12/1/15.
 */
public class NFCWorker {
    private final String TAG = this.getClass().getName();
    private NFCListener listener;

    public void registerListener(NFCListener listener){
        this.listener = listener;
    }

    public void handleTag(Tag t){
        new NFCHandler().execute(t);
    }

    private class NFCHandler extends AsyncTask <Tag, Void, String[]>{

        @Override
        protected String[] doInBackground(Tag... params) {
            Log.d(TAG, "doInBackground ");
            Tag tag = params[0];
            Ndef ndef = Ndef.get(tag);
            if(ndef == null)
                return null;

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] ndefRecords = ndefMessage.getRecords();
            String[] infos = new String[ndefRecords.length];

            for (int i = 0; i < ndefRecords.length; i++) {
                NdefRecord ndefRecord = ndefRecords[i];
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        infos[i] = readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            for(String s : infos){
                Log.d(TAG, "doInBackground " + s);
            }
            return infos;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            listener.handleNFC(strings);
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
    }

}
