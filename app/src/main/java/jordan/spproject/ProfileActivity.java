package jordan.spproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import jordan.spproject.R;
import jordan.spproject.UserInfo;
import jordan.spproject.reference.GlobalVariable;

/**
 * Created by hyungiko on 8/8/18.
 */

public class ProfileActivity extends Activity implements View.OnClickListener {
    private String TAG = "ProfileActivity";

    private RadioGroup radioGroup;
    private int index1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        initiate();
    }

    private void initiate() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                index1 = radioGroup.indexOfChild(radioButton);
            }
        });

        findViewById(R.id.btn_proceed).setOnClickListener(this);
    }


    private void sendPreventorInfor() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPreventor)
                .child(GlobalVariable.keyList);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Map<String, Object> hopperUpdates = new HashMap<>();
        final String email = account.getEmail().replace('@', '_').replace('.', '_');

        hopperUpdates.put(email, new UserInfo(account.getEmail(), account.getDisplayName()));

        databaseReference.updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "PreventorInfo could not be saved " + databaseError.getMessage());
                } else {
                    Log.e(TAG, "PreventorInfo saved successfully.");
                    GlobalVariable.isJustSignedUp = false;
                    GlobalVariable.saveStringPreferences(getApplicationContext(), GlobalVariable.keyUserType, GlobalVariable.keyPreventor);
                    GlobalVariable.saveStringPreferences(getApplicationContext(), GlobalVariable.keyOnline, GlobalVariable.keyFalse);

                    finish();
                }
            }
        });
    }

    private void sendPatientInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPatient)
                .child(GlobalVariable.keyList);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Map<String, Object> hopperUpdates = new HashMap<>();
        final String email = account.getEmail().replace('@', '_').replace('.', '_');

        hopperUpdates.put(email, new UserInfo(account.getEmail(), account.getDisplayName()));

        databaseReference.updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "PatientInfo could not be saved " + databaseError.getMessage());
                } else {
                    Log.e(TAG, "PatientInfo saved successfully.");
                    GlobalVariable.isJustSignedUp = false;
                    GlobalVariable.saveStringPreferences(getApplicationContext(), GlobalVariable.keyUserType, GlobalVariable.keyPatient);
                    finish();
                }
            }
        });
    }

    private void sendInfo(String userType) {
        if(userType.equals(GlobalVariable.keyPreventor)) {
            sendPreventorInfor();
        } else {
            sendPatientInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_proceed:
                if(index1 == 0)
                    sendInfo(GlobalVariable.keyPatient);
                else
                    sendInfo(GlobalVariable.keyPreventor);
                break;
//            case R.id.btn_patient:
//                sendInfo(GlobalVariable.keyPatient);
//                break;
//            case R.id.btn_preventor:
//                sendInfo(GlobalVariable.keyPreventor);
//                break;
        }
    }
}