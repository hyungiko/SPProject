package jordan.spproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jordan.spproject.process.FirebaseProcessor;
import jordan.spproject.reference.GlobalVariable;
import jordan.spproject.view.HistoryView;
import jordan.spproject.view.MainView;
import jordan.spproject.view.ProfileView;


public class MainActivity extends AppCompatActivity{
    private String TAG = "MainActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LocalBroadcastManager bManager;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(GlobalVariable.PROFILE_UPDATE)) {
                        initViewPager(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalVariable.PROFILE_UPDATE);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();

        checkSignedIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bManager.unregisterReceiver(broadcastReceiver);
    }



    private void checkSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            // signed-in
            Log.e(TAG, "isJustSignedUp: "+GlobalVariable.isJustSignedUp);
            Log.e(TAG, "viewPager: "+viewPager);
            if(GlobalVariable.isJustSignedUp&&
                    viewPager == null) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);
            } else if(GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType) != null &&
                    viewPager == null) {
                String profile = GlobalVariable.loadPreferences(this, getResources().getString(R.string.profile));
                try {
                    if(profile == null) {
                        initViewPager(0);
                    } else {
                        JSONObject jsonObject = new JSONObject(profile);
                        if(jsonObject.getString(getResources().getString(R.string.profile_date_of_birth)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_gender)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_nationality)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_job)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_languages)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_interests)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_hometown)).equals("") ||
                                jsonObject.getString(getResources().getString(R.string.profile_current_city)).equals("")) {
                            initViewPager(0);
                        } else {
                            initViewPager(1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType) == null &&
                    viewPager == null) {
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

    private void initViewPager(int currentItem) {
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager manager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new ProfileView(), "Profile");
        adapter.addFragment(new MainView(), "Main");
        adapter.addFragment(new HistoryView(), "History");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if(currentItem == 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0);
            params.weight = 0.0f;
            tabLayout.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0);
            params2.weight = 1.0f;

            viewPager.setLayoutParams(params2);
            viewPager.beginFakeDrag();
            Toast.makeText(this, "Please make your profile.", Toast.LENGTH_LONG).show();
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0);
            params.weight = 0.1f;
            tabLayout.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0);
            params2.weight = 0.9f;

            viewPager.setLayoutParams(params2);
//            viewPager.endFakeDrag();
        }
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
