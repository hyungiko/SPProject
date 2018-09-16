package jordan.spproject.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import jordan.spproject.R;
import jordan.spproject.helper.DataModelProfile;
import jordan.spproject.helper.ProfileAdapter;
import jordan.spproject.reference.GlobalVariable;

/**
 * Created by hyungiko on 8/31/18.
 */

public class ProfileView extends Fragment implements View.OnClickListener{
    private String TAG = "ProfileView";
    private ListView listView;
    private ArrayList<DataModelProfile> dataModels;
    private ArrayList<String> nationality = new ArrayList<>();
    private ArrayList<String> language = new ArrayList<>();
    private JSONArray jsonArrayLanguage;
    private JSONArray jsonArrayInterest;

    private static ProfileAdapter adapter;
    private RadioGroup radioGroupGender;
    private int index1 = 0;
    private View testView;
    private View genderDialogView;
    private View nationalityDialogView;
    private AlertDialog nationalityDialog;

    private int REQUEST_CODE_AUTOCOMPLETE_HOMETOWN = 1000;
    private int REQUEST_CODE_AUTOCOMPLETE_CUR_CITY = 1001;

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] INTEREST = {"Community Involvement", "Blogging", "Sports", "Art", "Gaming", "Traveling", "Child Care", "Pet Care", "Music", "Cooking", "Collecting", "Reading", "Photography"};

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_HOMETOWN) {
            Place place = PlaceAutocomplete.getPlace(getContext(), data);
            if(place != null) {
                Log.i(TAG, "Place: " + place.getName() +", "+place.getLatLng().latitude+", "+place.getLatLng().longitude+", "+place.getAddress());

                JSONObject jsonObjectHometown = new JSONObject();
                try {
                    jsonObjectHometown.put(getResources().getString(R.string.profile_address), place.getAddress());
                    jsonObjectHometown.put(getResources().getString(R.string.profile_lat), place.getLatLng().latitude);
                    jsonObjectHometown.put(getResources().getString(R.string.profile_lng), place.getLatLng().longitude);

                    jsonObject.put(getResources().getString(R.string.profile_hometown), jsonObjectHometown.toString());
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), jsonObject.toString());

                    resetListView(getResources().getString(R.string.profile_hometown));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if(requestCode == REQUEST_CODE_AUTOCOMPLETE_CUR_CITY) {
            Place place = PlaceAutocomplete.getPlace(getContext(), data);
            if(place != null) {
                Log.i(TAG, "Place: " + place.getName() +", "+place.getLatLng().latitude+", "+place.getLatLng().longitude+", "+place.getAddress());

                JSONObject jsonObjectCurCity = new JSONObject();
                try {
                    jsonObjectCurCity.put(getResources().getString(R.string.profile_address), place.getAddress());
                    jsonObjectCurCity.put(getResources().getString(R.string.profile_lat), place.getLatLng().latitude);
                    jsonObjectCurCity.put(getResources().getString(R.string.profile_lng), place.getLatLng().longitude);

                    jsonObject.put(getResources().getString(R.string.profile_current_city), jsonObjectCurCity.toString());
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), jsonObject.toString());

                    resetListView(getResources().getString(R.string.profile_current_city));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ProfileView() {
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
        View rootView = inflater.inflate(R.layout.profile, container, false);
        testView = rootView;
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        CircleImageView iv = view.findViewById(R.id.profilePic);
        Uri personPhoto = account.getPhotoUrl();
        Picasso.with(getContext()).load(personPhoto).into(iv);

        String name = account.getDisplayName();
        String userType = GlobalVariable.loadPreferences(getContext(), GlobalVariable.keyUserType);

        TextView textView = view.findViewById(R.id.userName);
        textView.setText(String.format("%s (%s)", name, userType));

        view.findViewById(R.id.profile_bio).setOnClickListener(this);

        LayoutInflater inflater = this.getLayoutInflater();
        genderDialogView= inflater.inflate(R.layout.gender_dialog, null);
        nationalityDialogView = inflater.inflate(R.layout.nationality_dialog, null);

        radioGroupGender = (RadioGroup) genderDialogView.findViewById(R.id.radioGroupGender);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                index1 = radioGroup.indexOfChild(radioButton);
                Log.e("TAG", "index1: "+index1);
            }
        });

        listView=(ListView) view.findViewById(R.id.listview);

        dataModels= new ArrayList<>();
        adapter= new ProfileAdapter(dataModels, getContext());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModelProfile dataModel= dataModels.get(position);

                if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_date_of_birth))) {
                    setBirthDate();
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_gender))) {
                    showGenderDialog();
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_nationality))) {
                    showNationalityDialog();
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_job))) {
                    showJobDialog();
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_languages))) {
                    showLanguageDialog();
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_interests))) {
                    showInterestDialog();
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_hometown))) {
                    Intent intent = null;
                    try {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(getActivity());
                        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE_HOMETOWN);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else if(dataModel.getProfileFeature().equals(getResources().getString(R.string.profile_current_city))) {
                    Intent intent = null;
                    try {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(getActivity());
                        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE_CUR_CITY);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
             }
        });

        JSONObject jsonObject = new JSONObject();
        String profile_content = GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile));
        if(profile_content == null) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put(getResources().getString(R.string.profile_bio), getResources().getString(R.string.profile_bio_text));
                jsonObject.put(getResources().getString(R.string.profile_date_of_birth), "");
                jsonObject.put(getResources().getString(R.string.profile_gender), "");
                jsonObject.put(getResources().getString(R.string.profile_nationality), "");
                jsonObject.put(getResources().getString(R.string.profile_job), "");
                jsonObject.put(getResources().getString(R.string.profile_languages), "");
                jsonObject.put(getResources().getString(R.string.profile_interests), "");
                jsonObject.put(getResources().getString(R.string.profile_hometown), (new JSONObject()).toString());
                jsonObject.put(getResources().getString(R.string.profile_current_city), (new JSONObject()).toString());

                GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject = new JSONObject(profile_content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            ((Button) view.findViewById(R.id.profile_bio)).setText(jsonObject.getString(getResources().getString(R.string.profile_bio)));
            dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_date_of_birth), jsonObject.getString(getResources().getString(R.string.profile_date_of_birth))));
            dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_gender), jsonObject.getString(getResources().getString(R.string.profile_gender))));
            dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_nationality), jsonObject.getString(getResources().getString(R.string.profile_nationality))));
            dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_job), jsonObject.getString(getResources().getString(R.string.profile_job))));

            if(!jsonObject.getString(getResources().getString(R.string.profile_languages)).equals("")) {
                String languageList = jsonObject.getString(getResources().getString(R.string.profile_languages));
                JSONArray jsonArrayLanguage = new JSONArray(languageList);
                setLanguageDB();

                StringBuilder sb = new StringBuilder();
                for(int j = 0; j < jsonArrayLanguage.length(); j++) {
                    sb.append(language.get(jsonArrayLanguage.getInt(j)));
                    if(j != jsonArrayLanguage.length()-1)
                        sb.append(", ");
                }

                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_languages), sb.toString()));
            } else {
                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_languages), jsonObject.getString(getResources().getString(R.string.profile_languages))));
            }


            if(!jsonObject.getString(getResources().getString(R.string.profile_interests)).equals("")) {
                String interestList = jsonObject.getString(getResources().getString(R.string.profile_interests));
                JSONArray jsonArrayInterest = new JSONArray(interestList);

                StringBuilder sb = new StringBuilder();
                for(int j = 0; j < jsonArrayInterest.length(); j++) {
                    sb.append(INTEREST[jsonArrayInterest.getInt(j)]);
                    if(j != jsonArrayInterest.length()-1)
                        sb.append(", ");
                }

                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_interests), sb.toString()));
            } else {
                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_interests), jsonObject.getString(getResources().getString(R.string.profile_interests))));
            }

            if((new JSONObject(jsonObject.getString(getResources().getString(R.string.profile_hometown)))).length() == 0)
                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_hometown), ""));
            else {
                String hometown = jsonObject.getString(getResources().getString(R.string.profile_hometown));
                JSONObject jsonObject1 = new JSONObject(hometown);
                String address = jsonObject1.getString(getResources().getString(R.string.profile_address));
                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_hometown), address));
            }

            if((new JSONObject(jsonObject.getString(getResources().getString(R.string.profile_current_city)))).length() == 0)
                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_current_city), ""));
            else {
                String hometown = jsonObject.getString(getResources().getString(R.string.profile_current_city));
                JSONObject jsonObject1 = new JSONObject(hometown);
                String address = jsonObject1.getString(getResources().getString(R.string.profile_address));
                dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_current_city), address));
            }

//            dataModels.add(new DataModelProfile(getResources().getString(R.string.profile_current_city), jsonObject.getString(getResources().getString(R.string.profile_current_city))));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    private void resetListView(String profileFeature) {
        try {
            JSONObject jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
            for(int i = 0; i < dataModels.size(); i++) {
                if(profileFeature.equals(dataModels.get(i).getProfileFeature())) {
                    if(profileFeature.equals(getResources().getString(R.string.profile_languages))) {
                        String languageList = jsonObject.getString(getResources().getString(R.string.profile_languages));
                        JSONArray jsonArray = new JSONArray(languageList);
                        setLanguageDB();

                        StringBuilder sb = new StringBuilder();
                        for(int j = 0; j < jsonArray.length(); j++) {
                            sb.append(language.get(jsonArray.getInt(j)));
                            if(j != jsonArray.length()-1)
                                sb.append(", ");
                        }

                        dataModels.add(i, new DataModelProfile(profileFeature, sb.toString()));
                    } else if(profileFeature.equals(getResources().getString(R.string.profile_interests))) {
                        String interestList = jsonObject.getString(getResources().getString(R.string.profile_interests));
                        JSONArray jsonArray = new JSONArray(interestList);

                        StringBuilder sb = new StringBuilder();
                        for(int j = 0; j < jsonArray.length(); j++) {
                            sb.append(INTEREST[jsonArray.getInt(j)]);
                            if(j != jsonArray.length()-1)
                                sb.append(", ");
                        }

                        dataModels.add(i, new DataModelProfile(profileFeature, sb.toString()));
                    } else if(profileFeature.equals(getResources().getString(R.string.profile_hometown))) {
                        String hometown = jsonObject.getString(profileFeature);
                        JSONObject jsonObject1 = new JSONObject(hometown);
                        String address = jsonObject1.getString(getResources().getString(R.string.profile_address));
                        dataModels.add(i, new DataModelProfile(profileFeature, address));
                    } else if(profileFeature.equals(getResources().getString(R.string.profile_current_city))) {
                        String hometown = jsonObject.getString(profileFeature);
                        JSONObject jsonObject1 = new JSONObject(hometown);
                        String address = jsonObject1.getString(getResources().getString(R.string.profile_address));
                        dataModels.add(i, new DataModelProfile(profileFeature, address));
                    } else {
                        dataModels.add(i, new DataModelProfile(profileFeature, jsonObject.getString(profileFeature)));
                    }
                    dataModels.remove(i+1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

        int count = 0;
        for(int i = 0; i < dataModels.size(); i++) {
            Log.e(TAG, "datemodels: "+dataModels.get(i).getProfileContent());
            if(!dataModels.get(i).getProfileContent().equals(""))
                count++;
        }

        if(count == 8) {
            // notify main activity
            Intent i = new Intent(GlobalVariable.PROFILE_UPDATE);

            Bundle mBundle = new Bundle();
            mBundle.putString(GlobalVariable.LIST_PROFILE, "done");
            i.putExtra(GlobalVariable.LIST_PROFILE, mBundle);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
            manager.sendBroadcast(i);
        }

        Log.e(TAG, "count: "+count);
    }

    private void setBirthDate() {
        DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    JSONObject jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
                    jsonObject.put(getResources().getString(R.string.profile_date_of_birth), String.format("%s %d, %d", MONTHS[monthOfYear], dayOfMonth, year));
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), jsonObject.toString());
                    resetListView(getResources().getString(R.string.profile_date_of_birth));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 1995, 1, 1);

        dpd.show();

    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.bio_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
            if(!jsonObject.getString(getResources().getString(R.string.profile_bio)).equals(""))
                edt.setText(jsonObject.getString(getResources().getString(R.string.profile_bio)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalJsonObject = jsonObject;

        dialogBuilder.setTitle(getResources().getString(R.string.profile_bio));

        dialogBuilder.setMessage(getResources().getString(R.string.profile_bio_text));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                try {
                    finalJsonObject.put(getResources().getString(R.string.profile_bio), edt.getText().toString());
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), finalJsonObject.toString());
                    ((Button) testView.findViewById(R.id.profile_bio)).setText(edt.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    public void showGenderDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(genderDialogView);

        RadioButton radioButtonMale = genderDialogView.findViewById(R.id.radioMale);
        RadioButton radioButtonFemale = genderDialogView.findViewById(R.id.radioFemale);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
            if(!jsonObject.getString(getResources().getString(R.string.profile_gender)).equals("")) {
                if(jsonObject.getString(getResources().getString(R.string.profile_gender)).equals(getResources().getString(R.string.profile_gender_male))) {
                    radioButtonMale.setChecked(true);
                    radioButtonFemale.setChecked(false);
                } else {
                    radioButtonMale.setChecked(false);
                    radioButtonFemale.setChecked(true);
                }
            } else {
                radioButtonMale.setChecked(true);
                radioButtonFemale.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalJsonObject = jsonObject;

        dialogBuilder.setTitle(getResources().getString(R.string.profile_gender));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                try {
                    if(index1 == 1)
                        finalJsonObject.put(getResources().getString(R.string.profile_gender), getResources().getString(R.string.profile_gender_female));
                    else
                        finalJsonObject.put(getResources().getString(R.string.profile_gender), getResources().getString(R.string.profile_gender_male));

                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), finalJsonObject.toString());
                    resetListView(getResources().getString(R.string.profile_gender));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showNationalityDialog() {
        if(nationality.size() == 0) {
            try {

                BufferedReader reader = new BufferedReader( new InputStreamReader(getContext().getAssets().open("nationalities.csv"), "UTF-8"));
                String line = "";
                String cvsSplitBy = ",";

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(cvsSplitBy);
                    for(int i = 0; i < data.length; i++)
                        nationality.add(data[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalJsonObject = jsonObject;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(nationalityDialogView);

        ListView lv = nationalityDialogView.findViewById(R.id.listView_nationality);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    finalJsonObject.put(getResources().getString(R.string.profile_nationality), nationality.get(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), finalJsonObject.toString());
                nationalityDialog.dismiss();
                resetListView(getResources().getString(R.string.profile_nationality));
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, nationality);
        lv.setAdapter(adapter);

        dialogBuilder.setTitle(getResources().getString(R.string.profile_nationality));
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        if(nationalityDialog == null)
            nationalityDialog = dialogBuilder.create();
        nationalityDialog.show();
    }

    public void showJobDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.bio_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
            if(!jsonObject.getString(getResources().getString(R.string.profile_job)).equals(""))
                edt.setText(jsonObject.getString(getResources().getString(R.string.profile_job)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalJsonObject = jsonObject;

        dialogBuilder.setTitle(getResources().getString(R.string.profile_job));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                try {
                    finalJsonObject.put(getResources().getString(R.string.profile_job), edt.getText().toString());
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), finalJsonObject.toString());
                    resetListView(getResources().getString(R.string.profile_job));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setLanguageDB() {
        if(language.size() == 0) {
            try {

                BufferedReader reader = new BufferedReader( new InputStreamReader(getContext().getAssets().open("language.csv"), "UTF-8"));
                String line = "";
                String cvsSplitBy = ",";

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(cvsSplitBy);
                    for(int i = 0; i < data.length; i++)
                        language.add(data[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showLanguageDialog() {
        if(language.size() == 0) {
            try {

                BufferedReader reader = new BufferedReader( new InputStreamReader(getContext().getAssets().open("language.csv"), "UTF-8"));
                String line = "";
                String cvsSplitBy = ",";

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(cvsSplitBy);
                    for(int i = 0; i < data.length; i++)
                        language.add(data[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final CharSequence[] dialogList=  language.toArray(new CharSequence[language.size()]);
        boolean[] test = new boolean[language.size()];

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
            if(jsonObject.getString(getResources().getString(R.string.profile_languages)).equals("")) {
                for(int i = 0; i < test.length; i++) {
                    test[i] = false;
                }
                jsonArrayLanguage = new JSONArray();
            } else {
                String test1 = jsonObject.getString(getResources().getString(R.string.profile_languages));
                jsonArrayLanguage = new JSONArray(test1);
                Set<Integer> langSet = new HashSet<>();
                for(int i = 0; i < jsonArrayLanguage.length(); i++)
                    langSet.add(jsonArrayLanguage.getInt(i));

                for(int i = 0; i < test.length; i++) {
                    if(langSet.contains(i))
                        test[i] = true;
                    else
                        test[i] = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalJsonObject = jsonObject;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
//        dialogBuilder.setView(languageDialogView);
        dialogBuilder.setMultiChoiceItems(dialogList, test, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked) {
                    jsonArrayLanguage.put(which);
                } else {
                    for(int i = 0; i < jsonArrayLanguage.length(); i++) {
                        try {
                            if(jsonArrayLanguage.getInt(i) == which) {
                                jsonArrayLanguage.remove(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.e("TEST", "which: "+jsonArrayLanguage);

            }
        });

        dialogBuilder.setTitle(getResources().getString(R.string.profile_languages));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                try {
                    finalJsonObject.put(getResources().getString(R.string.profile_languages), jsonArrayLanguage.toString());
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), finalJsonObject.toString());
                    resetListView(getResources().getString(R.string.profile_languages));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog nationalityDialog = dialogBuilder.create();
        nationalityDialog.show();
    }

    public void showInterestDialog() {
        boolean[] test = new boolean[INTEREST.length];

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(GlobalVariable.loadPreferences(getContext(), getResources().getString(R.string.profile)));
            if(jsonObject.getString(getResources().getString(R.string.profile_interests)).equals("")) {
                for(int i = 0; i < test.length; i++) {
                    test[i] = false;
                }
                jsonArrayInterest = new JSONArray();
            } else {
                String test1 = jsonObject.getString(getResources().getString(R.string.profile_interests));
                jsonArrayInterest = new JSONArray(test1);
                Set<Integer> langSet = new HashSet<>();
                for(int i = 0; i < jsonArrayInterest.length(); i++)
                    langSet.add(jsonArrayInterest.getInt(i));

                for(int i = 0; i < test.length; i++) {
                    if(langSet.contains(i))
                        test[i] = true;
                    else
                        test[i] = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalJsonObject = jsonObject;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setMultiChoiceItems(INTEREST, test, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked) {
                    jsonArrayInterest.put(which);
                } else {
                    for(int i = 0; i < jsonArrayInterest.length(); i++) {
                        try {
                            if(jsonArrayInterest.getInt(i) == which) {
                                jsonArrayInterest.remove(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.e("TEST", "which: "+jsonArrayInterest);

            }
        });

        dialogBuilder.setTitle(getResources().getString(R.string.profile_interests));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                try {
                    finalJsonObject.put(getResources().getString(R.string.profile_interests), jsonArrayInterest.toString());
                    GlobalVariable.saveStringPreferences(getContext(), getResources().getString(R.string.profile), finalJsonObject.toString());
                    resetListView(getResources().getString(R.string.profile_interests));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog nationalityDialog = dialogBuilder.create();
        nationalityDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_bio:
                showChangeLangDialog();
                break;
        }
    }
}
