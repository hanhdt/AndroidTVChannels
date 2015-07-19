package se.hanh.nimbl3channels.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ym.volley.RequestCallback;
import com.ym.volley.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.hanh.nimbl3channels.R;
import se.hanh.nimbl3channels.adapter.SwipeChannelCardAdapter;
import se.hanh.nimbl3channels.app.NimbleApplication;
import se.hanh.nimbl3channels.request.UpcomingChannelJsonRequest;
import se.hanh.nimbl3channels.util.ChannelCard;
import se.hanh.nimbl3channels.util.CommonHelper;
import se.hanh.nimbl3channels.util.InfiniteScrollListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class UpcommingChannelCardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{

    public static final String TAG = UpcommingChannelCardFragment.class.getSimpleName();

    private String ACCESS_TOKEN = "9008ab338d1beba78b8914124d64d461a9a9253894b29ea5cd70a0cf9c955177";

    private String URL_UPCOMING_CHANNEL = "http://api-staging.zeemi.tv/1/channels/upcoming.json?access_token=" + ACCESS_TOKEN + "&page=";

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SwipeChannelCardAdapter adapter;
    private List<ChannelCard> channelList;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 1;

    private OnFragmentInteractionListener mListener;
    private RequestCallback mRequestCallback = new RequestCallback<JSONArray, Integer>() {
        @Override
        public Integer doInBackground(JSONArray response) {
            Log.d(UpcommingChannelCardFragment.TAG, response.toString());

            if (response.length() > 0) {

                // looping through json and adding to movies list
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject channelObj = response.getJSONObject(i);

                        String coverImage = channelObj.getString("cover_image");
                        int rating = channelObj.getInt("rating");
                        String title = channelObj.getString("name");
                        int followers = channelObj.getInt("followers_count");
                        JSONObject userInfo = channelObj.getJSONObject("user");
                        String username = userInfo.getString("username");
                        String profilePic = userInfo.getString("profile_picture");
                        ChannelCard m = new ChannelCard(coverImage, title, username, rating, followers, profilePic);
                        // Set type of channel for upcoming channels which define the channel is available or not
                        m.setTypeOfChannel((new Random().nextInt(5) + 1));
                        channelList.add(m);

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    }
                }
            }

            return response.length();
        }

        @Override
        public void onPostExecute(Integer result) {

            if (result <= 0) {
                Toast.makeText(NimbleApplication.getInstance().getApplicationContext(),
                        getResources().getString(R.string.empty_response_message), Toast.LENGTH_LONG).show();
            }

            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onError(VolleyError error) {
            Log.e(TAG, "Server Error: " + error.getMessage());
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UpcommingChannelCardFragment() {
    }

    public static UpcommingChannelCardFragment newInstance(String param1, String param2) {
        UpcommingChannelCardFragment fragment = new UpcommingChannelCardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change Adapter to display your content
        channelList = new ArrayList<>();
        adapter = new SwipeChannelCardAdapter(this.getActivity(), channelList);

        // Init request manager
        RequestManager.initializeWith(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channelcard_upcomming, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.upcoming_channel_swipe_refresh_layout);
        // Set the adapter
        mListView.setAdapter(adapter);
        // Set onItemClick listener
        mListView.setOnItemClickListener(this);
        // attach the listener to the AdapterView onCreate
        mListView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                Log.d(UpcommingChannelCardFragment.TAG, "Page + Total Items: " + page + "+" + totalItemsCount);
                // Check connection first
                if(CommonHelper.hasConnection()){
                    // Check preferences
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                    boolean updatePopularChannelPref = sharedPreferences.getBoolean("updateUpcomingChannels", false);
                    if(updatePopularChannelPref){
                        loadMoreChannelFromAPI(offSet, totalItemsCount);
                    }
                    else{
                        Toast.makeText(getActivity(), getString(R.string.upcoming_tab_title) + ": " + getString(R.string.sync_data_message), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    CommonHelper.showAlertDialog(getActivity(),
                            getString(R.string.no_connection_message));
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        // Check connection first
                                        if(CommonHelper.hasConnection()){
                                            // Check preferences
                                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                                            boolean updatePopularChannelPref = sharedPreferences.getBoolean("updatePopularChannels", false);
                                            if(updatePopularChannelPref){
                                                fetchUpComingChannels();
                                            }
                                            else{
                                                Toast.makeText(getActivity(), getString(R.string.upcoming_tab_title) + ": " + getString(R.string.sync_data_message), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else{
                                            CommonHelper.showAlertDialog(getActivity(),
                                                    getString(R.string.no_connection_message));
                                        }
                                    }
                                }
        );

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     *
     * @param pageIndex
     * @param totalItems
     */
    private void loadMoreChannelFromAPI(int pageIndex, int totalItems){
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        //Queue use default volley Response and Error listener
        com.ym.volley.RequestManager
                .queue()
                .useBackgroundQueue()
                .addRequest(new UpcomingChannelJsonRequest(offSet++), mRequestCallback)
                .start();
    }

    @Override
    public void onRefresh() {
        // Check connection first
        if(CommonHelper.hasConnection()){
            // Check preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            boolean updatePopularChannelPref = sharedPreferences.getBoolean("updatePopularChannels", false);
            if(updatePopularChannelPref){
                fetchUpComingChannels();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.upcoming_tab_title) + ": " + getString(R.string.sync_data_message), Toast.LENGTH_LONG).show();
            }
        }
        else{
            CommonHelper.showAlertDialog(getActivity(),
                    getString(R.string.no_connection_message));
        }
    }

    /**
     * Fetching upcoming channel json by making http call
     */
    private void fetchUpComingChannels() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        //Queue use default volley Response and Error listener
        RequestManager
                .queue()
                .useBackgroundQueue()
                .addRequest(new UpcomingChannelJsonRequest(offSet++), mRequestCallback)
                .start();
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView channelTitle = (TextView) view.findViewById(R.id.channel_name);
        Toast.makeText(getActivity(), getString(R.string.click_on_channel_message)
                + " " + channelTitle.getText() + "! :-)", Toast.LENGTH_LONG).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
