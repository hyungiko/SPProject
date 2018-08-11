package jordan.spproject;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import reference.GlobalVariable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        final Context context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                String name = account.getDisplayName();

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("chat1")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                name)
                        );

                // Clear the input
                input.setText("");
            }
        });


//        hideChatting(true);
//        hidePatientLayout(true);
//        hidePreventorLayout(false);
        displayChatMessages();
    }

    private void initiate() {
        findViewById(R.id.btn_onOff).setOnClickListener(this);
        String userType = GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType);
        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
            hidePatientLayout(true);
            hideChatting(true);
            hidePreventorLayout(false);

            if(isOnline()) {
                ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
                ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
            } else {
                ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
                ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
            }
        } else if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
            hidePatientLayout(false);
            hideChatting(true);
            hidePreventorLayout(true);
//            ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
//            ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
        }
    }

    private void hidePatientLayout(boolean isHide) {
        if(isHide) {
            findViewById(R.id.patientLayout).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.patientLayout).setVisibility(View.VISIBLE);
        }
    }


    private void hidePreventorLayout(boolean isHide) {
        if(isHide) {
            findViewById(R.id.preventorLayout).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.preventorLayout).setVisibility(View.VISIBLE);
        }
    }

    private void hideChatting(boolean isHide) {
        if(isHide) {
            findViewById(R.id.chattingLayout).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.chattingLayout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        checkSignedIn();
        initiate();
    }

    private void checkSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            // signed-in
            if(GlobalVariable.isJustSignedUp) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);
            }
        } else {
            // signed-out
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
        }
    }

    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("chat1")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    private boolean isOnline() {
        String isOnline = GlobalVariable.loadPreferences(this, GlobalVariable.keyOnline);
        if(isOnline != null && isOnline.equals(GlobalVariable.keyTrue)) {
            return true;
        } else if(isOnline != null && isOnline.equals(GlobalVariable.keyFalse)) {
            return false;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onOff:
                String userType = GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType);
                if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
                    if(isOnline()) {
                        ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
                        ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
                        GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyFalse);
                        sendUpdatePreventorStatus(false);
                    } else {
                        ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
                        ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
                        GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyTrue);
                        sendUpdatePreventorStatus(true);
                    }
                } else {
                    ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
                    ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
                    GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyTrue);
                }
                break;
        }
    }

    private void sendUpdatePreventorStatus(boolean isOnline) {
        if(isOnline) {
            // insert online
            insertOnline();
        } else {
            // remove online
            removeOnline();
        }
    }

    private void insertOnline() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String email = account.getEmail().replace('@', '_').replace('.', '_');

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
                    Log.e(TAG, "Availability saved successfully.");
                }
            }
        });
    }

    private void removeOnline() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String email = account.getEmail().replace('@', '_').replace('.', '_');

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(GlobalVariable.keyPreventor)
                .child(GlobalVariable.keyOnline)
                .child(email);

        databaseReference.removeValue();
    }
}
