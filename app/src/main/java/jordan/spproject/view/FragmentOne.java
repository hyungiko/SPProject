package jordan.spproject.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import jordan.spproject.ChatMessage;
import jordan.spproject.ProfileActivity;
import jordan.spproject.R;
import jordan.spproject.SignIn;
import jordan.spproject.process.FirebaseProcessor;
import jordan.spproject.reference.GlobalVariable;

/**
 * Created by hyungiko on 8/17/18.
 */

public class FragmentOne extends Fragment implements View.OnClickListener{
    private String TAG = "MainActivity";
    private FirebaseListAdapter<ChatMessage> adapter;
    private LocalBroadcastManager bManager;
    private BroadcastReceiver broadcastReceiver;
    private FloatingActionButton fab;
    private ListView listOfMessages;
    private TextView tvPreventorStatus;
    private Button btnOnOff, btnCheckOnline;
    private Handler handler;
    private EditText input;
    private AlertDialog mAlertDialog;
    private String gPreventorId;
    private String gPatientId;
    private ProgressDialog progressDialog;
    private RelativeLayout chattingLatout;

    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        initiate(rootView);

        return rootView;
    }

    @SuppressLint("HandlerLeak")
    private void initiate(final View view) {
        // init chatting list
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        listOfMessages = (ListView)view.findViewById(R.id.list_of_messages);
        tvPreventorStatus = (TextView) view.findViewById(R.id.preventorStatus);
        chattingLatout = (RelativeLayout) view.findViewById(R.id.chattingLayout);

        input = (EditText)view.findViewById(R.id.input);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(GlobalVariable.REQUEST_MSG);

        String userType = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyUserType);
        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
            loadPreventorView(view);
        } else if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
            loadPatientView(view);
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(GlobalVariable.ONLINE_PREVENTOR_UPDATE)) {
                        Object tmp = intent.getParcelableExtra(GlobalVariable.LIST_PREVENTOR);
                        Bundle bndl = (Bundle) tmp;

                        JSONArray preventorList = new JSONArray(bndl.getString(GlobalVariable.LIST_PREVENTOR));
                        Log.e(TAG, "preventorList: "+preventorList);

                        if(preventorList.length() == 0) {
                            if(progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                btnCheckOnline.setText(getResources().getString(R.string.matching_preventor));
                                makeDialogForNoti(getResources().getString(R.string.no_matching_result));
                            }
                        } else
                            initChattingViewForPatient(preventorList.getString(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        bManager = LocalBroadcastManager.getInstance(getActivity());
        bManager.registerReceiver(broadcastReceiver, new IntentFilter(GlobalVariable.ONLINE_PREVENTOR_UPDATE));

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1000) {
                    setOffline(view);
                } else if (msg.what == 2000) {
                    setOnline(view);
                } else if (msg.what == 3000) {
                    // Preventor rejects the chatting.
                    (new FirebaseProcessor()).sendMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                            gPreventorId,
                            GlobalVariable.REJECT_MSG,
                            gPatientId);
                } else if (msg.what == 4000) {
                    // Preventor accepts the chatting.
                    (new FirebaseProcessor()).sendMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                            gPreventorId,
                            GlobalVariable.ACCEPT_MSG,
                            gPatientId);

                    setChattingViewForPreventor(gPreventorId, gPatientId, view);
                } else if(msg.what == 5000) {
                    initChattingViewForPreventor(gPreventorId);
                } else if(msg.what == 6000) {
                    adapter = null;
                    listOfMessages.setAdapter(adapter);
                }
            }
        };
    }

    private void loadPreventorView(View view) {
        btnOnOff = (Button) view.findViewById(R.id.btn_onOff);
        btnOnOff.setOnClickListener(this);

        hidePatientLayout(true, view);
        hideChatting(true);
        hidePreventorLayout(false, view);

        if(isOnline()) {
            tvPreventorStatus.setText("You are online.");
            btnOnOff.setText("OFFLINE");

            sendUpdatePreventorStatus(true);
            String preventorId = GoogleSignIn.getLastSignedInAccount(getContext()).getEmail().replace('@', '_').replace('.', '_');
            initChattingViewForPreventor(preventorId);
        } else {
            tvPreventorStatus.setText("You are offline.");
            btnOnOff.setText("ONLINE");
        }
    }

    private void loadPatientView(View view) {
        btnCheckOnline = (Button) view.findViewById(R.id.btn_chkOnline);
        btnCheckOnline.setOnClickListener(this);

        hidePatientLayout(false, view);
        hideChatting(true);
        hidePreventorLayout(true, view);
    }

    private void setOnline(View view) {
        tvPreventorStatus.setText("You are online.");
        btnOnOff.setText("OFFLINE");
        GlobalVariable.saveStringPreferences(getContext(), GlobalVariable.keyOnline, GlobalVariable.keyTrue);
        sendUpdatePreventorStatus(true);
        String preventorId = GoogleSignIn.getLastSignedInAccount(getContext()).getEmail().replace('@', '_').replace('.', '_');
        initChattingViewForPreventor(preventorId);
    }

    private void setOffline(View view) {
        (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                gPreventorId,
                GlobalVariable.PREVENTOR_EXIT_MSG,
                gPatientId);

        tvPreventorStatus.setText("You are offline.");
        btnOnOff.setText("ONLINE");
        GlobalVariable.saveStringPreferences(getContext(), GlobalVariable.keyOnline, GlobalVariable.keyFalse);
        sendUpdatePreventorStatus(false);
        String preventorId = GoogleSignIn.getLastSignedInAccount(getContext()).getEmail().replace('@', '_').replace('.', '_');
        (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
        hideChatting(true);
    }

    private void hidePatientLayout(boolean isHide, View view) {
        if(isHide) {
            view.findViewById(R.id.patientLayout).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.patientLayout).setVisibility(View.VISIBLE);
        }
    }

    private void hidePreventorLayout(boolean isHide, View view) {
        if(isHide) {
            view.findViewById(R.id.preventorLayout).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.preventorLayout).setVisibility(View.VISIBLE);
        }
    }

    private void hideChatting(boolean isHide) {
        if(isHide) {
            chattingLatout.setVisibility(View.INVISIBLE);
        } else {
            chattingLatout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        checkSignedIn();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        String userType = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyUserType);
        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
            if(chattingLatout.getVisibility() == View.VISIBLE) {
                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                        gPreventorId,
                        GlobalVariable.PREVENTOR_EXIT_MSG,
                        gPatientId);
            }

            GlobalVariable.saveStringPreferences(getContext(), GlobalVariable.keyOnline, GlobalVariable.keyFalse);
            sendUpdatePreventorStatus(false);
            String preventorId = GoogleSignIn.getLastSignedInAccount(getContext()).getEmail().replace('@', '_').replace('.', '_');
            (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
        } else if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
            if(btnCheckOnline.getText().toString().equals(getResources().getString(R.string.exist_chatroom))) {
                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                        gPreventorId,
                        GlobalVariable.EXIT_MSG,
                        gPatientId);
            }
        }

        bManager.unregisterReceiver(broadcastReceiver);
    }


    private void checkSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        updateUI(account);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            // signed-in
            if(GlobalVariable.isJustSignedUp) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
//                getContext().overridePendingTransition(0, 0);
            }
        } else {
            // signed-out
            Intent intent = new Intent(getContext(), SignIn.class);
            startActivity(intent);
//            this.overridePendingTransition(0, 0);
        }
    }

    private void initChattingViewForPatient(final String preventorId) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        final String patientId = account.getEmail().replace('@', '_').replace('.', '_');
        final String displayName = account.getDisplayName();

        gPreventorId = preventorId;
        gPatientId = patientId;

        adapter = null;
        adapter = new FirebaseListAdapter<ChatMessage>((Activity) getContext(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId).
                child("dummy")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                try {
                    // Get chatting request from patient
                    if(model != null && model.getMessageText().equals(GlobalVariable.ACCEPT_MSG)) {
                        (new FirebaseProcessor()).insertPreventorList(patientId, preventorId);
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        // Read the input field and push a new instance
                        // of ChatMessage to the Firebase database
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child(GlobalVariable.keyChatRoom)
                                .child(preventorId)
                                .child(patientId)
                                .push()
                                .setValue(new ChatMessage(GlobalVariable.GREETING_MSG,
                                                displayName,
                                                patientId,"aa")
                                        , new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                setChattingViewForPatient(preventorId, patientId);
                                            }
                                        });

                    } else if(model != null && model.getMessageText().equals(GlobalVariable.REJECT_MSG)) {
                        (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
                        progressDialog.dismiss();
                        makeDialogForNoti(getResources().getString(R.string.no_matching_result));
                        btnCheckOnline.setText(getResources().getString(R.string.matching_preventor));
                    }

                    Log.e(TAG, "messageText: "+model.getMessageText());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        };

        listOfMessages.setAdapter(adapter);


        (new FirebaseProcessor()).sendMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                preventorId,
                GlobalVariable.START_MSG,
                patientId);
    }

    private void setChattingViewForPatient(final String preventorId, final String patientId) {
        hideChatting(false);

        fab.setOnClickListener(null);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                        preventorId,
                        input.getText().toString(),
                        patientId);

                // Clear the input
                input.setText("");
            }
        });

        adapter = null;
        adapter = new FirebaseListAdapter<ChatMessage>((Activity) getContext(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId)
                .child(patientId)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                if(model.getMessageText().equals(GlobalVariable.PREVENTOR_EXIT_MSG)) {
                    (new FirebaseProcessor()).removeExitMsg(preventorId, patientId, adapter.getRef(position).getKey());
                    btnCheckOnline.setText(getResources().getString(R.string.matching_preventor));
                    makeDialogForNoti(getResources().getString(R.string.preventor_exit));
                    hideChatting(true);
                    handler.sendEmptyMessageAtTime(6000, 1000);
                } else {
                    // Get references to the views of message.xml
                    TextView messageText = (TextView)v.findViewById(R.id.message_text);
                    TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                    TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                    // Set their text
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());

                    Log.e(TAG, "messageText: "+model.getMessageText());
                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));

                    final int count = listOfMessages.getAdapter().getCount()-1;

                    listOfMessages.clearFocus();
                    listOfMessages.post(new Runnable() {
                        @Override
                        public void run() {
                            listOfMessages.setSelection(count);
                        }
                    });
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    private void initChattingViewForPreventor(final String preventorId) {
        adapter = null;
        adapter = new FirebaseListAdapter<ChatMessage>((Activity) getContext(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId).
                child("dummy")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                try {
                    // Get chatting request from patient
                    if(model != null && model.getMessageText().equals(GlobalVariable.START_MSG)) {
                        (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
                        makeDialogForChattingRequest(preventorId, model.getEmail());
                    }
                    Log.e(TAG, "initChattingViewForPreventor: "+model.getMessageText());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        };

        listOfMessages.setAdapter(adapter);
    }

    private void setChattingViewForPreventor(final String preventorId, final String patientId, View view) {
        (new FirebaseProcessor()).removeDummyChatRoom(preventorId);
        hideChatting(false);

        fab.setOnClickListener(null);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                        preventorId,
                        input.getText().toString(),
                        patientId);

                // Clear the input
                input.setText("");
            }
        });

        adapter = null;
        adapter = new FirebaseListAdapter<ChatMessage>((Activity) getContext(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(GlobalVariable.keyChatRoom).child(preventorId)
                .child(patientId)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                if(model.getMessageText().equals(GlobalVariable.EXIT_MSG)) {
                    (new FirebaseProcessor()).removeExitMsg(preventorId, patientId, adapter.getRef(position).getKey());
                    makeDialogForNoti(getResources().getString(R.string.patient_exit));
                    hideChatting(true);
                    handler.sendEmptyMessageAtTime(5000, 1000);
                } else {
                    TextView messageText = (TextView)v.findViewById(R.id.message_text);
                    TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                    TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                    // Set their text
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());

                    Log.e(TAG, "messageText: "+model.getMessageText());
                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));

                    final int count = listOfMessages.getAdapter().getCount()-1;

                    listOfMessages.clearFocus();
                    listOfMessages.post(new Runnable() {
                        @Override
                        public void run() {
                            listOfMessages.setSelection(count);
                        }
                    });
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    private boolean isOnline() {
        String isOnline = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyOnline);
        if(isOnline != null && isOnline.equals(GlobalVariable.keyTrue)) {
            return true;
        } else if(isOnline != null && isOnline.equals(GlobalVariable.keyFalse)) {
            return false;
        }

        return false;
    }

    private void sendUpdatePreventorStatus(boolean isOnline) {
        if(isOnline) {
            // insert online
            (new FirebaseProcessor()).insertOnline(GoogleSignIn.getLastSignedInAccount(getContext()));
        } else {
            // remove online
            (new FirebaseProcessor()).removeOnline(GoogleSignIn.getLastSignedInAccount(getContext()));
        }
    }

    private void makeDialogForChattingRequest(final String preventorId, final String patientId) {
        mAlertDialog = null;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }

        builder.setTitle("Chatting Request")
                .setMessage("Do you accept "+patientId+"'s chatting request?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        gPreventorId = preventorId;
                        gPatientId = patientId;
                        handler.sendEmptyMessageAtTime(4000, 0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // send reject message
                        gPreventorId = preventorId;
                        gPatientId = patientId;
                        handler.sendEmptyMessageAtTime(3000, 0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void makeDialogForNoti(String msg) {
        mAlertDialog = null;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }

        builder.setTitle("Notification")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onOff:
                String userType = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyUserType);
                if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
                    if(isOnline()) {
                        // make offline
                        handler.sendEmptyMessageAtTime(1000,0);
                    } else {
                        // make online
                        handler.sendEmptyMessageAtTime(2000,0);
                    }
                }
                break;
            case R.id.btn_chkOnline:
                if(btnCheckOnline.getText().toString().equals(getResources().getString(R.string.matching_preventor))) {
                    progressDialog.show();
                    btnCheckOnline.setText(getResources().getString(R.string.exist_chatroom));
                    (new FirebaseProcessor()).getOnlinePreventors(getContext());
                } else {
                    (new FirebaseProcessor()).sendChattingMessage(GoogleSignIn.getLastSignedInAccount(getContext()),
                            gPreventorId,
                            GlobalVariable.EXIT_MSG,
                            gPatientId);

                    hideChatting(true);
                    adapter = null;
                    listOfMessages.setAdapter(adapter);
                    btnCheckOnline.setText(getResources().getString(R.string.matching_preventor));
                }
                break;
        }
    }
}
