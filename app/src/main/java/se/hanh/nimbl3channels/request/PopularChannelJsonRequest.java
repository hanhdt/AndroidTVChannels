package se.hanh.nimbl3channels.request;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ym.volley.RequestInterface;

import org.json.JSONArray;

/**
 * Created by Hanh on 20/07/2015.
 */
public class PopularChannelJsonRequest extends RequestInterface<JSONArray, Void> {

    private final String ACCESS_TOKEN = "9008ab338d1beba78b8914124d64d461a9a9253894b29ea5cd70a0cf9c955177";

    private final String URL_LIVE_CHANNEL = "api-staging.zeemi.tv";

    private int pageIndex = 1;

    public PopularChannelJsonRequest(int page) {
        this.pageIndex = page;
    }

    @Override
    public Request create() {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority(URL_LIVE_CHANNEL);
        uri.path("1/channels/popular.json");
        uri.appendQueryParameter("access_token", ACCESS_TOKEN);
        uri.appendQueryParameter("page", String.valueOf(pageIndex));
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
