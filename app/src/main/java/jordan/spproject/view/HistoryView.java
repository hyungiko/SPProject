package jordan.spproject.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import jordan.spproject.R;
import jordan.spproject.helper.DataModelChatting;
import jordan.spproject.helper.HistoryAdapter;
import jordan.spproject.process.FirebaseProcessor;
import jordan.spproject.reference.GlobalVariable;

/**
 * Created by hyungiko on 8/17/18.
 */

public class HistoryView extends Fragment {
    private LocalBroadcastManager bManager;
    private BroadcastReceiver broadcastReceiver;
    private String TAG = "HistoryView";
    private ListView listView;
    private ArrayList<DataModelChatting> dataModels;
    private static HistoryAdapter adapter;
    private String userType;
    private android.support.v7.app.AlertDialog surveyDialog;

    public HistoryView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.history_view, container, false);
        initView(rootView);
        initiate();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        bManager.unregisterReceiver(broadcastReceiver);
    }

    private void initiate() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(GlobalVariable.LIST_HISTORY)) {
                        Object tmp = intent.getParcelableExtra(GlobalVariable.LIST_HISTORY);
                        Bundle bndl = (Bundle) tmp;

                        JSONArray preventorList = new JSONArray(bndl.getString(GlobalVariable.LIST_HISTORY));

                        for(int i = 0; i < preventorList.length(); i++) {
                            JSONObject jsonObject = getLastObject(preventorList.getJSONObject(i));
                            String prevDate = new SimpleDateFormat("MM/dd/yy").format(new Date(jsonObject.getLong("messageTime")));
                            String currDate = new SimpleDateFormat("MM/dd/yy").format(new Date(System.currentTimeMillis()));
                            String dateString = null;

                            String email = jsonObject.getString("email");
                            email = email.replaceFirst("_", "@");
                            email = email.replaceFirst("_", ".");

                            if(prevDate.equals(currDate))
                                dateString = new SimpleDateFormat("HH:MM a").format(new Date(jsonObject.getLong("messageTime")));
                            else
                                dateString = prevDate;

                            if(dataModels.size() < preventorList.length())
                                dataModels.add(new DataModelChatting(email, jsonObject.getString("messageText"), dateString));
                        }

                        adapter.notifyDataSetChanged();
                    } else if(intent.getAction().equals(GlobalVariable.LIST_SURVEY)) {
                        Object tmp = intent.getParcelableExtra(GlobalVariable.LIST_SURVEY);
                        Bundle bndl = (Bundle) tmp;

                        JSONObject jsonObjectSurvey = new JSONObject(bndl.getString(GlobalVariable.LIST_SURVEY));
                        showSurveyDialog(jsonObjectSurvey);

                        Log.e(TAG, "jsonObjectSurvey: "+jsonObjectSurvey);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalVariable.LIST_HISTORY);
        intentFilter.addAction(GlobalVariable.LIST_SURVEY);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

        userType = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyUserType);
        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
            (new FirebaseProcessor()).getPatientList(getContext());
        } else if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
            (new FirebaseProcessor()).getPreventorList(getContext());
        }

    }

    private void initView(View view) {
        listView=(ListView) view.findViewById(R.id.list);

        dataModels= new ArrayList<>();
        adapter= new HistoryAdapter(dataModels, getContext());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                DataModelChatting dataModel= dataModels.get(position);
//                Log.e(TAG, "dataModel: "+dataModel.getChattingId());
            }
        });

        userType = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyUserType);
        if(userType != null && userType.equals(GlobalVariable.keyPreventor)) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    // TODO Auto-generated method stub
                    DataModelChatting dataModel= dataModels.get(position);
                    String patientId = dataModel.getChattingId().substring(0, dataModel.getChattingId().indexOf(' ')).trim();
                    patientId = patientId.replace('@', '_').replace('.', '_');

                    Log.e(TAG, "patientId: "+patientId);

                    (new FirebaseProcessor()).getPatientSurvey(getContext(), patientId);
                    return true;
                }
            });
        }
    }

    private JSONObject getLastObject(JSONObject jsonObject) {
        Iterator iterator = jsonObject.keys();
        JSONObject jsonObjectLastItem = new JSONObject();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            try {
                JSONObject jsonObjectTemp = jsonObject.getJSONObject(key);
                Iterator iterator1 = jsonObjectTemp.keys();
                while(iterator1.hasNext()) {
                    String key1 = iterator1.next().toString();
                    JSONObject jsonObjectTemp1 = jsonObjectTemp.getJSONObject(key1);

                    jsonObjectLastItem.put("messageText", jsonObjectTemp1.getString("messageText"));
                    if(userType != null && userType.equals(GlobalVariable.keyPatient)) {
                        jsonObjectLastItem.put("email", String.format("%s (%s)", key, jsonObjectTemp1.getString("messageUser")));
                    } else {
                        jsonObjectLastItem.put("email", String.format("%s (%s)", jsonObjectTemp1.getString("email"), jsonObjectTemp1.getString("messageUser")));
                    }
                    jsonObjectLastItem.put("messageTime", jsonObjectTemp1.getString("messageTime"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObjectLastItem;
    }

    public void showSurveyDialog(JSONObject jsonObject) {
        surveyDialog = null;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        String message = "";
        try {
            if (jsonObject.isNull(getResources().getString(R.string.mood)))
                message = "There is no survey, today.";
            else
                message = "MOOD: "+jsonObject.getString(getResources().getString(R.string.mood))+", SLEEP: "+jsonObject.getString(getResources().getString(R.string.sleep));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogBuilder.setTitle(getResources().getString(R.string.survey));
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });

        surveyDialog = dialogBuilder.create();
        surveyDialog.show();
    }
}
