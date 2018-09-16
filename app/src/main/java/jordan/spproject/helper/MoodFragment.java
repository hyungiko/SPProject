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

public class MoodFragment extends Fragment implements View.OnClickListener{
    private String TAG = "MoodFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static int mCount;

    public MoodFragment() {
    }

    public static MoodFragment newInstance(int sectionNumber, int count) {
        mCount = count;

        MoodFragment fragment = new MoodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        rootView = inflater.inflate(R.layout.mood_fragment, container, false);

        initView(rootView);
//        TextView textView = (TextView) rootView.findViewById(R.id.tv1);
//        int pos = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
//        textView.setText(String.format("HEELO %d", pos));
        return rootView;
    }

    private void initView(View view) {
        view.findViewById(R.id.emoji1).setOnClickListener(this);
        view.findViewById(R.id.emoji2).setOnClickListener(this);
        view.findViewById(R.id.emoji3).setOnClickListener(this);
        view.findViewById(R.id.emoji4).setOnClickListener(this);
    }

    private void setEmoji(int eId) {
        // notify main activity
        Intent i = new Intent(GlobalVariable.EMOJI_UPDATE);

        Bundle mBundle = new Bundle();
        mBundle.putInt(GlobalVariable.EMOJI_MSG, eId);
        i.putExtra(GlobalVariable.EMOJI_MSG, mBundle);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.sendBroadcast(i);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emoji1:
                setEmoji(0);
                break;
            case R.id.emoji2:
                setEmoji(1);
                break;
            case R.id.emoji3:
                setEmoji(2);
                break;
            case R.id.emoji4:
                setEmoji(3);
                break;
        }
    }
}
