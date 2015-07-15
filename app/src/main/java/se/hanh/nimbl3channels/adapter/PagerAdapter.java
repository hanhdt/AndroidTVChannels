package se.hanh.nimbl3channels.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import se.hanh.nimbl3channels.fragment.LiveChannelCardFragment;
import se.hanh.nimbl3channels.fragment.PopularChannelCardFragment;
import se.hanh.nimbl3channels.fragment.UpcommingChannelCardFragment;

/**
 * Created by Hanh on 14/07/2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LiveChannelCardFragment tabLiveChannel = new LiveChannelCardFragment();
                return tabLiveChannel;
            case 1:
                UpcommingChannelCardFragment tabUpcomingChannel = new UpcommingChannelCardFragment();
                return tabUpcomingChannel;
            case 2:
                PopularChannelCardFragment tabPopularChannel = new PopularChannelCardFragment();
                return tabPopularChannel;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
