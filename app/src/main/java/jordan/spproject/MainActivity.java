package jordan.spproject;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


import java.util.ArrayList;
import java.util.List;

import jordan.spproject.reference.GlobalVariable;
import jordan.spproject.view.HistoryView;
import jordan.spproject.view.MainView;
import jordan.spproject.view.ProfileView;


public class MainActivity extends AppCompatActivity{
    private String TAG = "MainActivity";

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onStart() {
        super.onStart();

        checkSignedIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    private void checkSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            // signed-in
            if(GlobalVariable.isJustSignedUp&&
                    viewPager == null) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                this.overridePendingTransition(0, 0);
            } else if(GlobalVariable.loadPreferences(this, GlobalVariable.keyUserType) != null &&
                    viewPager == null) {
                initViewPager();
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

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager manager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new ProfileView(), "Profile");
        adapter.addFragment(new MainView(), "Main");
        adapter.addFragment(new HistoryView(), "History");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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
