/**
 * File     : Authentication.java
 * Project  : MoapLab
 * Author   : Markus Jaton Jan 2, 2012
 *            IICT / HEIG-VD
 *                                       
 * mailto:markus.jaton@heig-vd.ch
 * 
 * This is a fake authentication, just to show concurrent NFC and login authentication
 * mechanisms. MD5 is showed, but not used (comparison of plain text passwords instead)
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package com.sym.kobel.labo3;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
//import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Markus Jaton
 * Jan 2, 2012
 * 
 */
public class Authentication extends Activity {
	private String passwdAndNfc;
	private String login, md5Hash;

	//Adapter to handle the NFC
    private NfcAdapter nfcAdapter;
    //Pending Intent used for the NFC foreground dispatch
    private PendingIntent mPendingIntent;
    //IntentFilter used for the NFC foreground dispatch
    private IntentFilter[] mFilters;
    //Filter for the NFC tags technologies used for the NFC foreground dispatch
    private String[][] mTechLists;
    
	public static final String NFC_MIME_TYPE = "application/ch.iict.moap";
	public static final String CHARSET = "UTF-16";
	private EditText email, passwd;
	
	private CheckBox cb;

	public static final String TAG = "MoapLabTag";
	private boolean andMode = false;

	/*
	 * Hard coded credentials tags :
	 */
	private String nfcIdTag = "identifier";
	private String nfcCodeTag = "code";
	
	private String nfcAccount = null;
	private String nfcPassWd = null;

	/*
	 * Just for testing, hard-coded fake credentials
	 */
	private String fakeEmail = "markus.jaton@heig-vd.ch";
	private String fakePass = "lesmonts";
	private String fakeNfcIdValue = "markus";
	private String fakeNfcCodeValue = "les monts";
	
	
	private boolean verifyCredentials() {
		boolean loginOk = false;
		boolean nfcOk = false;
		login = email.getText().toString();
		md5Hash = passwd.getText().toString();
		Log.d("Credentials", ""+login+" "+md5Hash);

		Log.d("SYM-LOGIN", ""+login);
		Log.d("SYM-MD5", ""+md5Hash);
		if ((fakeNfcIdValue.equals(nfcAccount)) && (fakeNfcCodeValue.equals(nfcPassWd))) {
			nfcOk = true;
		}
		loginOk = fakeEmail.equals(login) && (fakePass).equals(md5Hash);
		
		if (andMode) {
			return loginOk && nfcOk;
		} else {
			return loginOk || nfcOk;
		}
	}
	
	public String md5(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	        
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
		/*
		* Set up user interface window as you like it...
	    setContentView(...);
		* BLA BLA BLA
		*
		*/
        // NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for MIME based dispatches
        IntentFilter ndef = new IntentFilter();
        ndef.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ndef.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            ndef.addDataType(NFC_MIME_TYPE);
        } catch (MalformedMimeTypeException error) {
            Log.e(TAG, "MalformedMimeTypeException, error: " + error.getMessage());
        }
        
        mFilters = new IntentFilter[] {ndef};

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] {MifareClassic.class.getName() } };

	}


    // When a NFC Tag is scanned, this method is called.
    // The Intent contains various informations about the Tag and it's payloads.
    @Override
    protected void onNewIntent(Intent intent) {
    	if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
	   		Toast.makeText(this, "NFC tag scanned", Toast.LENGTH_SHORT).show();
    		processIntent(intent);
    	}
    	super.onNewIntent(intent);
    }
    
    void processIntent(Intent intent) {
    	
    	Parcelable[] rawMsgs = intent.getParcelableArrayExtra( NfcAdapter.EXTRA_NDEF_MESSAGES );
		
		if(rawMsgs == null) {
			Toast.makeText(this, R.string.empty_tag, Toast.LENGTH_SHORT).show();
			return;
		}

		
		for(int i = 0; i < rawMsgs.length; ++i) {
			 NdefMessage msg = (NdefMessage) rawMsgs[i];
			 NdefRecord[] records = msg.getRecords();
			 
			 for(int j = 0; j < records.length; ++j) {
				 try {
					 if (new String(records[j].getType()).equals(NFC_MIME_TYPE)) {
						 String s = (new String(records[j].getId(), Authentication.CHARSET));
						 if (this.nfcIdTag.equals(s)) {
							 nfcAccount = new String(records[j].getPayload(), Authentication.CHARSET);
						 }
						 if (this.nfcCodeTag.equals(s)) {
							 nfcPassWd = new String(records[j].getPayload(), Authentication.CHARSET);
						 }
						 // Is there something interesting on the tag ?
						 // Code it here...
						 Log.d("NFC TAG", s);
					 }
				 } catch (UnsupportedEncodingException e) {
					 Log.e(TAG, "UnsupportedEncodingException" + e.getMessage());
				 }
			 }
		}

    }

    /*
	* Overriding onResume() and onPause() is VERY important., because NFC should be kept only discoverable 
	* by foreground dispatch tasks. However, whenever you stay in the background, you generally do not
	* stay aware of external sensors, thus you can most often disable sensor dispatching code...
	*/
    @Override
    protected void onResume() {
    	//Enables the NFC foreground dispatch 
    	if(nfcAdapter != null)
    		nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    	
    	super.onResume();
    }

    @Override
    protected void onPause() {
    	//Disables the NFC foreground dispatch
    	if(nfcAdapter != null)
    		nfcAdapter.disableForegroundDispatch(this);
    	
    	super.onPause();
    }

	/*
	* This is the code to save the instance's state on context change...
	*/
	
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
		savedInstanceState.putString("nfcAccount", nfcAccount);
		savedInstanceState.putString("nfcPassWd", nfcPassWd);
		login = email.getText().toString();
		md5Hash = passwd.getText().toString();
    	
    	if (login != null) {
    		savedInstanceState.putString("login", login);
    		savedInstanceState.putString("md5Hash", md5Hash);
    	}
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      CharSequence cs = savedInstanceState.getCharSequence("login");
      if (cs != null) login = cs.toString();
      else login = null;
      cs = savedInstanceState.getCharSequence("md5Hash");
      if (cs != null) md5Hash = cs.toString();
      else md5Hash = null;
      cs = savedInstanceState.getCharSequence("nfcAccount");
      if (cs != null) nfcAccount = cs.toString();
      else nfcAccount = null;
      cs = savedInstanceState.getCharSequence("nfcPassWd");
      if (cs != null) nfcPassWd = cs.toString();
      else nfcPassWd = null;
   }
}
