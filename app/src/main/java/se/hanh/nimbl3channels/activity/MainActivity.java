package se.hanh.nimbl3channels.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import se.hanh.nimbl3channels.R;
import se.hanh.nimbl3channels.adapter.PagerAdapter;
import se.hanh.nimbl3channels.fragment.LiveChannelCardFragment;
import se.hanh.nimbl3channels.fragment.PopularChannelCardFragment;
import se.hanh.nimbl3channels.fragment.UpcommingChannelCardFragment;


public class MainActivity extends AppCompatActivity
        implements LiveChannelCardFragment.OnFragmentInteractionListener,
        UpcommingChannelCardFragment.OnFragmentInteractionListener,
        PopularChannelCardFragment.OnFragmentInteractionListener
{

    public static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Define tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.live_tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.upcoming_tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.popular_tab_title)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Define pager fragment for swipe action feature
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent preferenceIntent = new Intent(this, ChannelPreferencesActivity.class);
            startActivity(preferenceIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.d(TAG, "Fragment " + id);
    }
}
