////package jordan.spproject;
////
////import android.app.Activity;
////import android.content.BroadcastReceiver;
////import android.content.Context;
////import android.content.Intent;
////import android.content.IntentFilter;
////import android.os.StrictMode;
////import android.support.annotation.Nullable;
////import android.support.design.widget.FloatingActionButton;
////import android.support.design.widget.TabLayout;
////import android.support.v4.app.Fragment;
////import android.support.v4.app.FragmentManager;
////import android.support.v4.app.FragmentPagerAdapter;
////import android.support.v4.content.LocalBroadcastManager;
////import android.support.v4.view.ViewPager;
////import android.support.v7.app.AppCompatActivity;
////import android.os.Bundle;
////import android.text.format.DateFormat;
////import android.util.Log;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.ListView;
////import android.widget.TextView;
////
////import com.firebase.ui.database.FirebaseListAdapter;
////import com.google.android.gms.auth.api.signin.GoogleSignIn;
////import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
////import com.google.firebase.FirebaseError;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////
////import org.json.JSONArray;
////
////
////import java.util.ArrayList;
////import java.util.List;
////
////import jordan.spproject.process.FirebaseProcessor;
////import jordan.spproject.reference.GlobalVariable;
////import jordan.spproject.view.FragmentOne;
////import jordan.spproject.view.HistoryView;
////
////
////public class MainActivity extends AppCompatActivity {
////    private String TAG = "MainActivity";
////    private FirebaseListAdapter<ChatMessage> adapter;
////    private LocalBroadcastManager bManager;
////    private BroadcastReceiver broadcastReceiver;
////    private FloatingActionButton fab;
////    private ListView listOfMessages;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////
////        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
////        StrictMode.setThreadPolicy(policy);
////
//
////
//////        initiate();
////    }
////
////    // Adapter for the viewpager using FragmentPagerAdapter
////    class ViewPagerAdapter extends FragmentPagerAdapter {
////        private final List<Fragment> mFragmentList = new ArrayList<>();
////        private final List<String> mFragmentTitleList = new ArrayList<>();
////
////        public ViewPagerAdapter(FragmentManager manager) {
////            super(manager);
////        }
////
////        @Override
////        public Fragment getItem(int position) {
////            return mFragmentList.get(position);
////        }
////
////        @Override
////        public int getCount() {
////            return mFragmentList.size();
////        }
////
////        public void addFragment(Fragment fragment, String title) {
////            mFragmentList.add(fragment);
////            mFragmentTitleList.add(title);
////        }
////
////        @Override
////        public CharSequence getPageTitle(int position) {
////            return mFragmentTitleList.get(position);
////        }
////    }
////
//////    private void initiate() {
//////        // init chatting list
//////        fab = (FloatingActionButton)findViewById(R.id.fab);
//////        listOfMessages = (ListView)findViewById(R.id.list_of_messages);
//////
//////        findViewById(R.id.btn_onOff).setOnClickListener(this);
//////        String userType = GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType);
//////        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
//////            loadPreventorView();
//////        } else if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
//////            loadPatientView();
//////        }
//////
//////        broadcastReceiver = new BroadcastReceiver() {
//////            @Override
//////            public void onReceive(Context context, Intent intent) {
//////                try {
//////                    if (intent.getAction().equals(GlobalVariable.ONLINE_PREVENTOR_UPDATE)) {
//////                        Object tmp = intent.getParcelableExtra(GlobalVariable.LIST_PREVENTOR);
//////                        Bundle bndl = (Bundle) tmp;
//////
//////                        JSONArray preventorList = new JSONArray(bndl.getString(GlobalVariable.LIST_PREVENTOR));
//////                        Log.e(TAG, "preventorList: "+preventorList);
//////
//////                        initChattingViewForPatient(preventorList.getString(0));
//////                    }
//////                } catch (Exception e) {
//////                    e.printStackTrace();
//////                }
//////            }
//////        };
//////
//////        bManager = LocalBroadcastManager.getInstance(this);
//////        bManager.registerReceiver(broadcastReceiver, new IntentFilter(GlobalVariable.ONLINE_PREVENTOR_UPDATE));
//////
//////    }
//////
//////    private void loadPreventorView() {
//////        hidePatientLayout(true);
//////        hideChatting(true);
//////        hidePreventorLayout(false);
//////
//////        if(isOnline()) {
//////            ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
//////            ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
//////
//////            sendUpdatePreventorStatus(true);
//////            String preventorId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getEmail().replace('@', '_').replace('.', '_');
//////            initChattingViewForPreventor(preventorId);
//////        } else {
//////            ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
//////            ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
//////        }
//////    }
//////
//////    private void loadPatientView() {
//////        hidePatientLayout(false);
//////        hideChatting(true);
//////        hidePreventorLayout(true);
//////
//////        findViewById(R.id.btn_chkOnline).setOnClickListener(this);
//////    }
//////
//////    private void hidePatientLayout(boolean isHide) {
//////        if(isHide) {
//////            findViewById(R.id.patientLayout).setVisibility(View.INVISIBLE);
//////        } else {
//////            findViewById(R.id.patientLayout).setVisibility(View.VISIBLE);
//////        }
//////    }
//////
//////
//////    private void hidePreventorLayout(boolean isHide) {
//////        if(isHide) {
//////            findViewById(R.id.preventorLayout).setVisibility(View.INVISIBLE);
//////        } else {
//////            findViewById(R.id.preventorLayout).setVisibility(View.VISIBLE);
//////        }
//////    }
//////
//////    private void hideChatting(boolean isHide) {
//////        if(isHide) {
//////            findViewById(R.id.chattingLayout).setVisibility(View.INVISIBLE);
//////        } else {
//////            findViewById(R.id.chattingLayout).setVisibility(View.VISIBLE);
//////        }
//////    }
//////
//////    @Override
//////    public void onStart() {
//////        super.onStart();
//////
//////        checkSignedIn();
//////    }
//////
//////    @Override
//////    protected void onDestroy() {
//////        super.onDestroy();
//////
//////        bManager.unregisterReceiver(broadcastReceiver);
//////    }
//////
//////
//////    private void checkSignedIn() {
//////        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//////        updateUI(account);
//////    }
//////
//////    private void updateUI(@Nullable GoogleSignInAccount account) {
//////        if (account != null) {
//////            // signed-in
//////            if(GlobalVariable.isJustSignedUp) {
//////                Intent intent = new Intent(this, ProfileActivity.class);
//////                startActivity(intent);
//////                this.overridePendingTransition(0, 0);
//////            }
//////        } else {
//////            // signed-out
//////            Intent intent = new Intent(this, SignIn.class);
//////            startActivity(intent);
//////            this.overridePendingTransition(0, 0);
//////        }
//////    }
//////
//////    private void initChattingViewForPatient(final String preventorId) {
//////        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//////        final String patientId = account.getEmail().replace('@', '_').replace('.', '_');
//////
//////        // Read the input field and push a new instance
//////        // of ChatMessage to the Firebase database
//////        FirebaseDatabase.getInstance()
//////                .getReference()
//////                .child(GlobalVariable.keyChatRoom)
//////                .child(preventorId)
//////                .child(patientId)
//////                .push()
//////                .setValue(new ChatMessage(GlobalVariable.GREETING_MSG,
//////                                account.getDisplayName(),
//////                                patientId,"aa")
//////                        , new DatabaseReference.CompletionListener() {
//////                            @Override
//////                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//////                                setChattingViewForPatient(preventorId, patientId);
//////                            }
//////                        });
//////
//////        (new FirebaseProcessor()).sendMessage(GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
//////                preventorId,
//////                GlobalVariable.START_MSG,
//////                patientId);
//////    }
//////
//////    private void setChattingViewForPatient(final String preventorId, final String patientId) {
//////        hideChatting(false);
//////
//////        fab.setOnClickListener(null);
//////        fab.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////                EditText input = (EditText)findViewById(R.id.input);
//////                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
//////                        preventorId,
//////                        input.getText().toString(),
//////                        patientId);
//////
//////                // Clear the input
//////                input.setText("");
//////            }
//////        });
//////
//////        adapter = null;
//////        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
//////                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId)
//////                .child(patientId)) {
//////            @Override
//////            protected void populateView(View v, ChatMessage model, int position) {
//////                // Get references to the views of message.xml
//////                TextView messageText = (TextView)v.findViewById(R.id.message_text);
//////                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//////                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
//////
//////                // Set their text
//////                messageText.setText(model.getMessageText());
//////                messageUser.setText(model.getMessageUser());
//////
//////                Log.e(TAG, "messageText: "+model.getMessageText());
//////                // Format the date before showing it
//////                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//////                        model.getMessageTime()));
//////
//////                final int count = listOfMessages.getAdapter().getCount()-1;
//////
//////                listOfMessages.clearFocus();
//////                listOfMessages.post(new Runnable() {
//////                    @Override
//////                    public void run() {
//////                        listOfMessages.setSelection(count);
//////                    }
//////                });
//////
//////            }
//////        };
//////
//////        listOfMessages.setAdapter(adapter);
//////    }
//////
//////    private void initChattingViewForPreventor(final String preventorId) {
//////        adapter = null;
//////        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
//////                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId).
//////                child("dummy")) {
//////            @Override
//////            protected void populateView(View v, ChatMessage model, int position) {
//////                try {
//////                    if(model != null && model.getMessageText().equals(GlobalVariable.START_MSG)) {
//////                        setChattingViewForPreventor(preventorId, model.getEmail());
//////                    }
//////                    Log.e(TAG, "messageText: "+model.getMessageText());
//////                } catch (NullPointerException e) {
//////                    e.printStackTrace();
//////                }
//////
//////            }
//////        };
//////
//////        listOfMessages.setAdapter(adapter);
//////    }
//////
//////    private void setChattingViewForPreventor(final String preventorId, final String patientId) {
//////        (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
//////        hideChatting(false);
//////
//////        fab.setOnClickListener(null);
//////        fab.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////                EditText input = (EditText)findViewById(R.id.input);
//////                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
//////                        preventorId,
//////                        input.getText().toString(),
//////                        patientId);
//////
//////                // Clear the input
//////                input.setText("");
//////            }
//////        });
//////
//////        adapter = null;
//////        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
//////                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId)
//////                .child(patientId)) {
//////            @Override
//////            protected void populateView(View v, ChatMessage model, int position) {
//////                // Get references to the views of message.xml
//////                TextView messageText = (TextView)v.findViewById(R.id.message_text);
//////                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//////                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
//////
//////                // Set their text
//////                messageText.setText(model.getMessageText());
//////                messageUser.setText(model.getMessageUser());
//////
//////                Log.e(TAG, "messageText: "+model.getMessageText());
//////                // Format the date before showing it
//////                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//////                        model.getMessageTime()));
//////
//////                final int count = listOfMessages.getAdapter().getCount()-1;
//////
//////                listOfMessages.clearFocus();
//////                listOfMessages.post(new Runnable() {
//////                    @Override
//////                    public void run() {
//////                        listOfMessages.setSelection(count);
//////                    }
//////                });
//////
//////            }
//////        };
//////
//////        listOfMessages.setAdapter(adapter);
//////    }
//////
//////    private boolean isOnline() {
//////        String isOnline = GlobalVariable.loadPreferences(this, GlobalVariable.keyOnline);
//////        if(isOnline != null && isOnline.equals(GlobalVariable.keyTrue)) {
//////            return true;
//////        } else if(isOnline != null && isOnline.equals(GlobalVariable.keyFalse)) {
//////            return false;
//////        }
//////
//////        return false;
//////    }
//////
//////    private void sendUpdatePreventorStatus(boolean isOnline) {
//////        if(isOnline) {
//////            // insert online
//////            (new FirebaseProcessor()).insertOnline(GoogleSignIn.getLastSignedInAccount(this));
//////        } else {
//////            // remove online
//////            (new FirebaseProcessor()).removeOnline(GoogleSignIn.getLastSignedInAccount(this));
//////        }
//////    }
//////
//////    @Override
//////    public void onClick(View v) {
//////        switch (v.getId()) {
//////            case R.id.btn_onOff:
//////                String userType = GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType);
//////                if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
//////                    if(isOnline()) {
//////                        ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
//////                        ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
//////                        GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyFalse);
//////                        sendUpdatePreventorStatus(false);
//////                        hideChatting(true);
//////                    } else {
//////                        ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
//////                        ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
//////                        GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyTrue);
//////                        sendUpdatePreventorStatus(true);
//////                        String preventorId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getEmail().replace('@', '_').replace('.', '_');
//////                        initChattingViewForPreventor(preventorId);
//////                    }
//////                }
//////                break;
//////            case R.id.btn_chkOnline:
//////                (new FirebaseProcessor()).getOnlinePreventors(this);
//////                break;
//////        }
//////    }
////}
//
//
//package jordan.spproject;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.StrictMode;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.format.DateFormat;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.firebase.ui.database.FirebaseListAdapter;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.firebase.FirebaseError;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import org.json.JSONArray;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import jordan.spproject.process.FirebaseProcessor;
//import jordan.spproject.reference.GlobalVariable;
//import jordan.spproject.view.FragmentOne;
//import jordan.spproject.view.HistoryView;
//
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    private String TAG = "MainActivity";
//    private FirebaseListAdapter<ChatMessage> adapter;
//    private LocalBroadcastManager bManager;
//    private BroadcastReceiver broadcastReceiver;
//    private FloatingActionButton fab;
//    private ListView listOfMessages;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
////        initiateVariables();
//
//        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        FragmentManager manager = getSupportFragmentManager();
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//
//        // Add Fragments to adapter one by one
////        adapter.addFragment(new FragmentOne(), "FRAG1");
//        adapter.addFragment(new FragmentOne(), "FRAG2");
//        adapter.addFragment(new HistoryView(), "FRAG2");
//
////        adapter.addFragment(new FragmentThree(), "FRAG3");
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        checkSignedIn();
////        initiateUI();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        bManager.unregisterReceiver(broadcastReceiver);
//    }
//
//    private void initiateUI() {
//        String userType = GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType);
//        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
//            loadPreventorView();
//        } else if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
//            loadPatientView();
//        }
//    }
//
//    private void initiateVariables() {
//        // init chatting list
//        fab = (FloatingActionButton)findViewById(R.id.fab);
//        listOfMessages = (ListView)findViewById(R.id.list_of_messages);
//
//        findViewById(R.id.btn_onOff).setOnClickListener(this);
//
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                try {
//                    if (intent.getAction().equals(GlobalVariable.ONLINE_PREVENTOR_UPDATE)) {
//                        Object tmp = intent.getParcelableExtra(GlobalVariable.LIST_PREVENTOR);
//                        Bundle bndl = (Bundle) tmp;
//
//                        JSONArray preventorList = new JSONArray(bndl.getString(GlobalVariable.LIST_PREVENTOR));
//                        Log.e(TAG, "preventorList: "+preventorList);
//
//                        initChattingViewForPatient(preventorList.getString(0));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        bManager = LocalBroadcastManager.getInstance(this);
//        bManager.registerReceiver(broadcastReceiver, new IntentFilter(GlobalVariable.ONLINE_PREVENTOR_UPDATE));
//
//    }
//
//    private void loadPreventorView() {
//        hidePatientLayout(true);
////        hideChatting(true);
//        hidePreventorLayout(false);
//
//        if(isOnline()) {
//            ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
//            ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
//
//            sendUpdatePreventorStatus(true);
//            String preventorId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getEmail().replace('@', '_').replace('.', '_');
//            initChattingViewForPreventor(preventorId);
//        } else {
//            ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
//            ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
//        }
//    }
//
//    private void loadPatientView() {
//        hidePatientLayout(false);
//        hideChatting(true);
//        hidePreventorLayout(true);
//
//        findViewById(R.id.btn_chkOnline).setOnClickListener(this);
//    }
//
//    private void hidePatientLayout(boolean isHide) {
//        if(isHide) {
//            findViewById(R.id.patientLayout).setVisibility(View.INVISIBLE);
//        } else {
//            findViewById(R.id.patientLayout).setVisibility(View.VISIBLE);
//        }
//    }
//
//
//    private void hidePreventorLayout(boolean isHide) {
//        if(isHide) {
//            findViewById(R.id.preventorLayout).setVisibility(View.INVISIBLE);
//        } else {
//            findViewById(R.id.preventorLayout).setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void hideChatting(boolean isHide) {
//        if(isHide) {
//            findViewById(R.id.chattingLayout).setVisibility(View.INVISIBLE);
//        } else {
//            findViewById(R.id.chattingLayout).setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void checkSignedIn() {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
//    }
//
//    private void updateUI(@Nullable GoogleSignInAccount account) {
//        if (account != null) {
//            // signed-in
//            if(GlobalVariable.isJustSignedUp) {
//                Intent intent = new Intent(this, ProfileActivity.class);
//                startActivity(intent);
//                this.overridePendingTransition(0, 0);
//            }
//        } else {
//            // signed-out
//            Intent intent = new Intent(this, SignIn.class);
//            startActivity(intent);
//            this.overridePendingTransition(0, 0);
//        }
//    }
//
//    private void initChattingViewForPatient(final String preventorId) {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        final String patientId = account.getEmail().replace('@', '_').replace('.', '_');
//
//        // Read the input field and push a new instance
//        // of ChatMessage to the Firebase database
//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child(GlobalVariable.keyChatRoom)
//                .child(preventorId)
//                .child(patientId)
//                .push()
//                .setValue(new ChatMessage(GlobalVariable.GREETING_MSG,
//                                account.getDisplayName(),
//                                patientId,"aa")
//                        , new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                setChattingViewForPatient(preventorId, patientId);
//                            }
//                        });
//
//        (new FirebaseProcessor()).sendMessage(GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
//                preventorId,
//                GlobalVariable.START_MSG,
//                patientId);
//    }
//
//    private void setChattingViewForPatient(final String preventorId, final String patientId) {
//        hideChatting(false);
//
//        fab.setOnClickListener(null);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditText input = (EditText)findViewById(R.id.input);
//                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
//                        preventorId,
//                        input.getText().toString(),
//                        patientId);
//
//                // Clear the input
//                input.setText("");
//            }
//        });
//
//        adapter = null;
//        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
//                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId)
//                .child(patientId)) {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//                // Get references to the views of message.xml
//                TextView messageText = (TextView)v.findViewById(R.id.message_text);
//                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
//
//                // Set their text
//                messageText.setText(model.getMessageText());
//                messageUser.setText(model.getMessageUser());
//
//                Log.e(TAG, "messageText: "+model.getMessageText());
//                // Format the date before showing it
//                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                        model.getMessageTime()));
//
//                final int count = listOfMessages.getAdapter().getCount()-1;
//
//                listOfMessages.clearFocus();
//                listOfMessages.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        listOfMessages.setSelection(count);
//                    }
//                });
//
//            }
//        };
//
//        listOfMessages.setAdapter(adapter);
//    }
//
//    private void initChattingViewForPreventor(final String preventorId) {
//        adapter = null;
//        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
//                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId).
//                child("dummy")) {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//                try {
//                    if(model != null && model.getMessageText().equals(GlobalVariable.START_MSG)) {
//                        setChattingViewForPreventor(preventorId, model.getEmail());
//                    }
//                    Log.e(TAG, "messageText: "+model.getMessageText());
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//
//        listOfMessages.setAdapter(adapter);
//    }
//
//    private void setChattingViewForPreventor(final String preventorId, final String patientId) {
//        (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
//        hideChatting(false);
//
//        fab.setOnClickListener(null);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditText input = (EditText)findViewById(R.id.input);
//                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
//                        preventorId,
//                        input.getText().toString(),
//                        patientId);
//
//                // Clear the input
//                input.setText("");
//            }
//        });
//
//        adapter = null;
//        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
//                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId)
//                .child(patientId)) {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//                // Get references to the views of message.xml
//                TextView messageText = (TextView)v.findViewById(R.id.message_text);
//                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
//
//                // Set their text
//                messageText.setText(model.getMessageText());
//                messageUser.setText(model.getMessageUser());
//
//                Log.e(TAG, "messageText: "+model.getMessageText());
//                // Format the date before showing it
//                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                        model.getMessageTime()));
//
//                final int count = listOfMessages.getAdapter().getCount()-1;
//
//                listOfMessages.clearFocus();
//                listOfMessages.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        listOfMessages.setSelection(count);
//                    }
//                });
//
//            }
//        };
//
//        listOfMessages.setAdapter(adapter);
//    }
//
//    private boolean isOnline() {
//        String isOnline = GlobalVariable.loadPreferences(this, GlobalVariable.keyOnline);
//        if(isOnline != null && isOnline.equals(GlobalVariable.keyTrue)) {
//            return true;
//        } else if(isOnline != null && isOnline.equals(GlobalVariable.keyFalse)) {
//            return false;
//        }
//
//        return false;
//    }
//
//    private void sendUpdatePreventorStatus(boolean isOnline) {
//        if(isOnline) {
//            // insert online
//            (new FirebaseProcessor()).insertOnline(GoogleSignIn.getLastSignedInAccount(this));
//        } else {
//            // remove online
//            (new FirebaseProcessor()).removeOnline(GoogleSignIn.getLastSignedInAccount(this));
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_onOff:
//                String userType = GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType);
//                if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
//                    if(isOnline()) {
//                        ((TextView) findViewById(R.id.preventorStatus)).setText("You are offline.");
//                        ((Button) findViewById(R.id.btn_onOff)).setText("ONLINE");
//                        GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyFalse);
//                        sendUpdatePreventorStatus(false);
//                        hideChatting(true);
//                    } else {
//                        ((TextView) findViewById(R.id.preventorStatus)).setText("You are online.");
//                        ((Button) findViewById(R.id.btn_onOff)).setText("OFFLINE");
//                        GlobalVariable.saveStringPreferences(this, GlobalVariable.keyOnline, GlobalVariable.keyTrue);
//                        sendUpdatePreventorStatus(true);
//                        String preventorId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getEmail().replace('@', '_').replace('.', '_');
//                        initChattingViewForPreventor(preventorId);
//                    }
//                }
//                break;
//            case R.id.btn_chkOnline:
//                (new FirebaseProcessor()).getOnlinePreventors(this);
//                break;
//        }
//    }
//
//    // Adapter for the viewpager using FragmentPagerAdapter
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
//}
