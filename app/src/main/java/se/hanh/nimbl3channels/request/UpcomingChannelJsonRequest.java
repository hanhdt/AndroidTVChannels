package se.hanh.nimbl3channels.request;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ym.volley.RequestInterface;

import org.json.JSONArray;

import se.hanh.nimbl3channels.util.GlobalConstants;

/**
 * Created by Hanh on 20/07/2015.
 */
public class UpcomingChannelJsonRequest extends RequestInterface<JSONArray, Void> {

    private final String URL_UPCOMING_CHANNEL = "1/channels/upcoming.json";

    private int pageIndex = 1;

    public UpcomingChannelJsonRequest(int page) {
        this.pageIndex = page;
    }

    @Override
    public Request create() {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme(GlobalConstants.API_URL_SCHEMA);
        uri.authority(GlobalConstants.API_AUTHORITY_URL);
        uri.path(URL_UPCOMING_CHANNEL);
        uri.appendQueryParameter(GlobalConstants.API_KEY_ACCESS_TOKEN, GlobalConstants.API_ACCESS_TOKEN);
        uri.appendQueryParameter(GlobalConstants.API_KEY_PAGE_INDEX, String.valueOf(pageIndex));
        String url = uri.build().toString();

        Request request = new JsonArrayRequest(url, useInterfaceListener(), useInterfaceErrorListener());

        return request;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
