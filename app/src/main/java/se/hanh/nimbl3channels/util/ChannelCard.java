package se.hanh.nimbl3channels.util;

/**
 * Created by Hanh on 14/07/2015.
 */
public class ChannelCard {
    private String coverImage;
    private String userAvatar;
    private String channelName;
    private String username;
    private int rating;
    private int numOfFollowers;

    public ChannelCard(String coverImage, String channelName, String username, int rating, int followersCount, String profilePic) {
        this.coverImage = coverImage;
        this.channelName = channelName;
        this.username = username;
        this.rating = rating;
        this.numOfFollowers = followersCount;
        this.userAvatar = profilePic;
    }

    public ChannelCard(String channelName, int rating) {
        this.channelName = channelName;
        this.rating = rating;
    }

    public ChannelCard(String coverImage, String userAvatar, String channelName,
                       String username, int rating, int numOfFollowers) {
        this.coverImage = coverImage;
        this.userAvatar = userAvatar;
        this.channelName = channelName;
        this.username = username;
        this.rating = rating;
        this.numOfFollowers = numOfFollowers;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNumOfFollowers() {
        return numOfFollowers;
    }

    public void setNumOfFollowers(int numOfFollowers) {
        this.numOfFollowers = numOfFollowers;
    }
}
