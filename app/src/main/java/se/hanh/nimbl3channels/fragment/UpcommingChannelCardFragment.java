package se.hanh.nimbl3channels.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.hanh.nimbl3channels.R;

import se.hanh.nimbl3channels.adapter.SwipeChannelCardAdapter;
import se.hanh.nimbl3channels.app.NimbleApplication;
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
public class UpcommingChannelCardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static UpcommingChannelCardFragment newInstance(String param1, String param2) {
        UpcommingChannelCardFragment fragment = new UpcommingChannelCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UpcommingChannelCardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Change Adapter to display your content
        channelList = new ArrayList<>();
        adapter = new SwipeChannelCardAdapter(this.getActivity(), channelList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channelcard_upcomming, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.upcoming_channel_swipe_refresh_layout);
        // Set the adapter
        mListView.setAdapter(adapter);

        // attach the listener to the AdapterView onCreate
        mListView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                Log.d(UpcommingChannelCardFragment.TAG, "Page + Total Items: " + page + "+" + totalItemsCount);
                loadMoreChannelFromAPI(offSet, totalItemsCount);
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

                                        fetchUpComingChannels();
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
        // appending offset to url
        String url = URL_UPCOMING_CHANNEL + offSet;

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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

                                    channelList.add(m);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            ++offSet; // increase page index
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(NimbleApplication.getInstance().getApplicationContext(),
                                    getResources().getString(R.string.empty_response_message), Toast.LENGTH_LONG).show();

                            setEmptyText("Channel is empty.");
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

//                CommonHelper.showAlertDialog(getActivity(),
//                        getString(R.string.failed_fetched_data_message));

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

                // Just show a toast message
                Toast.makeText(getActivity(), getString(R.string.failed_fetched_data_message), Toast.LENGTH_LONG).show();
            }
        }){
            /**
             * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
             * {@link AuthFailureError} as authentication may be required to provide these values.
             * <p/>
             * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
             *
             * @throws AuthFailureError in the event of auth failure
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("access_token", ACCESS_TOKEN);
                return params;
            }
        };

        // Adding request to request queue
        NimbleApplication.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onRefresh() {
        fetchUpComingChannels();
    }

    /**
     * Fetching upcoming channel json by making http call
     */
    private void fetchUpComingChannels() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = URL_UPCOMING_CHANNEL + offSet;

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(UpcommingChannelCardFragment.TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject channelObj = response.getJSONObject(i);

                                    String coverImage = channelObj.getString("cover_image");
                                    int rating = channelObj.getInt("rating");
                                    String title = channelObj.getString("name");
                                    JSONObject userInfo = channelObj.getJSONObject("user");
                                    String username = userInfo.getString("username");
                                    int followers = channelObj.getInt("followers_count");
                                    String profilePic = userInfo.getString("profile_picture");
                                    ChannelCard m = new ChannelCard(coverImage, title, username, rating, followers, profilePic);

                                    channelList.add(0, m);


                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                            ++offSet;
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(NimbleApplication.getInstance().getApplicationContext(),
                                    getResources().getString(R.string.empty_response_message), Toast.LENGTH_LONG).show();

                            setEmptyText("Channel is empty.");
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

//                CommonHelper.showAlertDialog(getActivity(),
//                        getString(R.string.failed_fetched_data_message));
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

                // Just show a toast message
                Toast.makeText(getActivity(), getString(R.string.failed_fetched_data_message), Toast.LENGTH_LONG).show();
            }
        }){
            /**
             * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
             * {@link AuthFailureError} as authentication may be required to provide these values.
             * <p/>
             * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
             *
             * @throws AuthFailureError in the event of auth failure
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("access_token", ACCESS_TOKEN);
                return params;
            }
        };

        // Adding request to request queue
        NimbleApplication.getInstance().addToRequestQueue(req);
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
