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

public class MotivationFragment extends Fragment implements View.OnClickListener{
    private String TAG = "MoodFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static int mCount;

    public MotivationFragment() {
    }

    public static MotivationFragment newInstance(int sectionNumber, int count) {
        mCount = count;

        MotivationFragment fragment = new MotivationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.motivation_fragment, container, false);

        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        view.findViewById(R.id.motiv1).setOnClickListener(this);
        view.findViewById(R.id.motiv2).setOnClickListener(this);
        view.findViewById(R.id.motiv3).setOnClickListener(this);
        view.findViewById(R.id.motiv4).setOnClickListener(this);
    }

    private void setMotive(int eId) {
        // notify main activity
        Intent i = new Intent(GlobalVariable.MOTIV_UPDATE);

        Bundle mBundle = new Bundle();
        mBundle.putInt(GlobalVariable.MOTIV_MSG, eId);
        i.putExtra(GlobalVariable.MOTIV_MSG, mBundle);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.sendBroadcast(i);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.motiv1:
                setMotive(0);
                break;
            case R.id.motiv2:
                setMotive(1);
                break;
            case R.id.motiv3:
                setMotive(2);
                break;
            case R.id.motiv4:
                setMotive(3);
                break;
        }
    }
}
