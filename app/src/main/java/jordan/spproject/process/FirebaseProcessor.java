package jordan.spproject.process;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jordan.spproject.ChatMessage;
import jordan.spproject.reference.GlobalVariable;

/**
 * Created by hyungiko on 8/11/18.
 */

public class FirebaseProcessor {
    private String TAG = "FirebaseProcessor";
    public FirebaseProcessor() {

    }

    public void getOnlinePreventors(final Context context) {
        FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPreventor)
                .child(GlobalVariable.keyOnline).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                JSONArray jsonArrayPreventor = new JSONArray();

                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    String email = singleSnapshot.getKey();
                                    jsonArrayPreventor.put(email);
                                }

                                Log.e(TAG, "online preventors: "+jsonArrayPreventor);

                                // notify main activity
                                Intent i = new Intent(GlobalVariable.ONLINE_PREVENTOR_UPDATE);

                                Bundle mBundle = new Bundle();
                                mBundle.putString(GlobalVariable.LIST_PREVENTOR, jsonArrayPreventor.toString());
                                i.putExtra(GlobalVariable.LIST_PREVENTOR, mBundle);
                                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
                                manager.sendBroadcast(i);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        }
                );
    }

    public void insertOnline(final GoogleSignInAccount account) {
        final String email = account.getEmail().replace('@', '_').replace('.', '_');

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPreventor)
                .child(GlobalVariable.keyOnline);

        Map<String, Object> hopperUpdates = new HashMap<>();

        hopperUpdates.put(email, "dummy");

        databaseReference.updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Availability could not be saved " + databaseError.getMessage());
                } else {
                    openChatRoom(account, email);
                    Log.e(TAG, "Availability saved successfully.");
                }
            }
        });
    }

    public void insertPreventorList(String patientId, String preventorId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPatient)
                .child(GlobalVariable.keyList)
                .child(patientId)
                .child(GlobalVariable.keyPreventor);

        Map<String, Object> hopperUpdates = new HashMap<>();

        hopperUpdates.put(preventorId, preventorId);


//        for(int i = 0; i < jsonArrayYesterday.length(); i++) {
//            JSONObject newJsonObjectSegment = jsonArrayYesterday.getJSONObject(i);
//
//            String seedTimeWindow = newJsonObjectSegment.getString(GlobalVariable.keyTimeWindow);
//            hopperUpdates.put(seedTimeWindow,
//                    new Segment(
//                            context,
//                            newJsonObjectSegment,
//                            newJsonObjectSegment.isNull(GlobalVariable.keyStepCount) ? "0" : newJsonObjectSegment.getString(GlobalVariable.keyStepCount)
//                    )
//            );
//        }


        databaseReference.updateChildren(hopperUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Preventor List could not be saved " + databaseError.getMessage());
                } else {
                    Log.e(TAG, "Preventor List saved successfully.");
                }
            }
        });
    }

    public void sendChattingMessage(GoogleSignInAccount account, String preventorId, String text, String patientId) {
        String name = account.getDisplayName();

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance()
                .getReference()
                .child(GlobalVariable.keyChatRoom)
                .child(preventorId)
                .child(patientId)
                .push()
                .setValue(new ChatMessage(text,
                        name,
                        patientId, "aa")
                );
    }

    public void sendMessage(GoogleSignInAccount account, String preventorId, String text, String patientId) {
        String name = account.getDisplayName();

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance()
                .getReference()
                .child(GlobalVariable.keyChatRoom)
                .child(preventorId)
                .child("dummy")
                .push()
                .setValue(new ChatMessage(text,
                        name,
                        patientId, "aa")
                );
    }

    private void openChatRoom(GoogleSignInAccount account, String preventorId) {
        sendMessage(account, preventorId, GlobalVariable.GREETING_MSG, "dummy");
    }

    public void removeOnline(GoogleSignInAccount account) {
        String email = account.getEmail().replace('@', '_').replace('.', '_');

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPreventor)
                .child(GlobalVariable.keyOnline)
                .child(email);

        databaseReference.removeValue();
    }

    public void removeDummyChatRoom(String preventorId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyChatRoom)
                .child(preventorId)
                .child("dummy");

        databaseReference.removeValue();
    }

    public void removeExitMsg(String preventorId, String patientId, String key) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyChatRoom)
                .child(preventorId)
                .child(patientId)
                .child(key);

        databaseReference.removeValue();
    }
}
