package se.hanh.nimbl3channels.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import se.hanh.nimbl3channels.R;
import se.hanh.nimbl3channels.app.NimbleApplication;
import se.hanh.nimbl3channels.util.ChannelCard;

/**
 * Created by Hanh on 14/07/2015.
 */
public class SwipeChannelCardAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ChannelCard> channelList;
    private String[] bgColors;

    public SwipeChannelCardAdapter(Activity activity, List<ChannelCard> channelList) {
        this.activity = activity;
        this.channelList = channelList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return channelList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return channelList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        // Get the NetworkImageView that will display the image.
        ImageView mNetworkImageView = (ImageView) convertView.findViewById(R.id.channel_cover_image);
        ImageView mChannelUserAvatar = (ImageView) convertView.findViewById(R.id.channel_user_avatar);
        TextView title = (TextView) convertView.findViewById(R.id.channel_name);
        TextView username = (TextView) convertView.findViewById(R.id.user_name);
        RatingBar mRatingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
        TextView followerCount = (TextView) convertView.findViewById(R.id.follower_count);
        ImageView mChannelType = (ImageView) convertView.findViewById(R.id.channel_status);

        title.setText(channelList.get(position).getChannelName());
        username.setText(channelList.get(position).getUsername());
        mRatingBar.setRating(channelList.get(position).getRating());
        followerCount.setText(String.valueOf(channelList.get(position).getNumOfFollowers() + " Followers"));
        Picasso.with(NimbleApplication.getInstance().getApplicationContext())
                .load(channelList.get(position).getCoverImage())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(mNetworkImageView);

        if(channelList.get(position).getUserAvatar() == null || channelList.get(position).getUserAvatar().equals("")){
            Picasso.with(NimbleApplication.getInstance().getApplicationContext())
                    .load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mChannelUserAvatar);
        }
        else{
            Picasso.with(NimbleApplication.getInstance().getApplicationContext())
                    .load(channelList.get(position).getUserAvatar())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mChannelUserAvatar);
        }

        // Setting channel type (LIVE or not LIVE)
        if(channelList.get(position).getTypeOfChannel() == 1
                || channelList.get(position).getTypeOfChannel() == 2
                || channelList.get(position).getTypeOfChannel() == 3){
            mChannelType.setImageResource(android.R.color.holo_red_light);
            Log.d(SwipeChannelCardAdapter.class.getSimpleName(), "Channel Status: "
                    + channelList.get(position).getTypeOfChannel());
        }
        else{
            mChannelType.setImageResource(android.R.color.holo_green_dark);
            Log.d(SwipeChannelCardAdapter.class.getSimpleName(), "Channel Status: "
                    + channelList.get(position).getTypeOfChannel());
        }
        return convertView;
    }
}
