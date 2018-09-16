package jordan.spproject.helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jordan.spproject.R;
import jordan.spproject.reference.GlobalVariable;

/**
 * Created by hyungiko on 9/14/18.
 */

public class SleepFragment extends Fragment implements View.OnClickListener {
    private String TAG = "Survey";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static int mCount;

    public SleepFragment() {
    }

    public static SleepFragment newInstance(int sectionNumber, int count) {
        mCount = count;

        SleepFragment fragment = new SleepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.sleep_fragment, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        view.findViewById(R.id.sleep1).setOnClickListener(this);
        view.findViewById(R.id.sleep2).setOnClickListener(this);
        view.findViewById(R.id.sleep3).setOnClickListener(this);
        view.findViewById(R.id.sleep4).setOnClickListener(this);
    }

    private void setSleep(int eId) {

        // notify main activity
        Intent i = new Intent(GlobalVariable.SLEEP_UPDATE);

        Bundle mBundle = new Bundle();
        mBundle.putInt(GlobalVariable.SLEEP_MSG, eId);
        i.putExtra(GlobalVariable.SLEEP_MSG, mBundle);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.sendBroadcast(i);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sleep1:
                setSleep(0);
                break;
            case R.id.sleep2:
                setSleep(1);
                break;
            case R.id.sleep3:
                setSleep(2);
                break;
            case R.id.sleep4:
                setSleep(3);
                break;
        }
    }
}
