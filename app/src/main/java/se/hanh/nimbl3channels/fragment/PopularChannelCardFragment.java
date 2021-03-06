package se.hanh.nimbl3channels.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.squareup.leakcanary.RefWatcher;
import com.ym.volley.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.hanh.nimbl3channels.R;
import se.hanh.nimbl3channels.adapter.SwipeChannelCardAdapter;
import se.hanh.nimbl3channels.app.NimbleApplication;
import se.hanh.nimbl3channels.request.PopularChannelJsonRequest;
import se.hanh.nimbl3channels.util.ChannelCard;
import se.hanh.nimbl3channels.util.ChannelCardParsingController;
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
public class PopularChannelCardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{
    private static final String TAG = PopularChannelCardFragment.class.getSimpleName();

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
            Log.d(PopularChannelCardFragment.TAG, response.toString());

            if (response.length() > 0) {
                ChannelCardParsingController channelParser = new ChannelCardParsingController(response.toString());
                channelParser.init();
                channelList.addAll(channelParser.findAll());

                // looping through json and adding to movies list
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject channelObj = response.getJSONObject(i);
//
//                        String coverImage = channelObj.getString("cover_image");
//                        int rating = channelObj.getInt("rating");
//                        String title = channelObj.getString("name");
//                        int followers = channelObj.getInt("followers_count");

//                        JSONObject userInfo = channelObj.getJSONObject("user");
//                        String username = userInfo.getString("username");
//                        String profilePic = userInfo.getString("profile_picture");
//                        ChannelCard m = new ChannelCard(coverImage, title, username, rating, followers, profilePic);

//                        // Set type of channel for upcoming channels which define the channel is available or not
//                        m.setTypeOfChannel((new Random().nextInt(5) + 1));
//                        channelList.add(m);
//
//                    } catch (JSONException e) {
//                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
//                    }
//                }
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

            Looper.prepare();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NimbleApplication.getContext(), getString(R.string.failed_fetched_data_message), Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PopularChannelCardFragment() {
    }

    public static PopularChannelCardFragment newInstance(String param1, String param2) {
        PopularChannelCardFragment fragment = new PopularChannelCardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change Adapter to display your content
        channelList = new ArrayList<>();
        adapter = new SwipeChannelCardAdapter(this.getActivity(), channelList);

        // init Request manager
        com.ym.volley.RequestManager.initializeWith(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = NimbleApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channelcard_popular, container, false);


        mListView = (ListView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.popular_channel_swipe_refresh_layout);
        // Set the adapter
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
        // attach the listener to the Adapter View
        mListView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                Log.d(PopularChannelCardFragment.TAG, "Page + Total Items: " + page + "+" + totalItemsCount);
                // Check connection first
                if(CommonHelper.hasConnection()){
                    // Check preferences
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                    boolean updatePopularChannelPref = sharedPreferences.getBoolean("updatePopularChannels", false);
                    if(updatePopularChannelPref){
                        loadMoreChannelFromAPI(offSet, totalItemsCount);
                    }
                    else{
                        Toast.makeText(getActivity(), getString(R.string.popular_tab_title) + ": " + getString(R.string.sync_data_message), Toast.LENGTH_LONG).show();
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
                                                fetchPopularChannels();
                                            }
                                            else{
                                                Toast.makeText(getActivity(), getString(R.string.popular_tab_title) + ": " + getString(R.string.sync_data_message), Toast.LENGTH_LONG).show();
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
        //Queue use custom listener
        com.ym.volley.RequestManager
                .queue()
                .useBackgroundQueue()
                .addRequest(new PopularChannelJsonRequest(offSet++), mRequestCallback)
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
                fetchPopularChannels();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.popular_tab_title) + ": " + getString(R.string.sync_data_message), Toast.LENGTH_LONG).show();
            }
        }
        else{
            CommonHelper.showAlertDialog(getActivity(),
                    getString(R.string.no_connection_message));
        }

    }

    /**
     * Fetching popular channel json by making http call
     */
    private void fetchPopularChannels() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        //Queue use custom listener
        com.ym.volley.RequestManager
                .queue()
                .useBackgroundQueue()
                .addRequest(new PopularChannelJsonRequest(offSet++), mRequestCallback)
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
        public void onFragmentInteraction(String id);
    }
}
