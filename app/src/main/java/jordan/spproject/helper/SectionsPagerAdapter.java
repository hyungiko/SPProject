package jordan.spproject.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by hyungiko on 9/14/18.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public int count = 0;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a SleepFragment (defined as a static inner class below).
        Log.e("TAG", "pos: "+position);
        if(position == 0)
            return MoodFragment.newInstance(position + 1, count);
        else if(position == 1)
            return SleepFragment.newInstance(position + 1, count);
        else
            return MotivationFragment.newInstance(position + 1, count);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}