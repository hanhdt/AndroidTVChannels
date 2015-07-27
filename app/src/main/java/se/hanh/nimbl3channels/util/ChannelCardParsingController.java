package se.hanh.nimbl3channels.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chicky-PC on 7/27/2015.
 */
public class ChannelCardParsingController {

    private String channelCardsJSON = "";

    private ObjectMapper objectMapper = null;
    private JsonFactory jsonFactory = null;
    private JsonParser jp = null;
    private ArrayList<ChannelCard> channelCards = null;
    private ChannelCards channelCardsMap = null;

    public ChannelCardParsingController(String response){
        this.channelCardsJSON = response;
        objectMapper = new ObjectMapper();
        jsonFactory = new JsonFactory();
    }

    public void init(){
        try
        {
            jp = jsonFactory.createJsonParser(channelCardsJSON);
            channelCardsMap = objectMapper.readValue(jp, ChannelCards.class);
            channelCards = channelCardsMap.get(0);
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<ChannelCard> findAll()
    {
        return channelCards;
    }

    public ChannelCard findById(int id)
    {
        return channelCards.get(id);
    }

    class ChannelCards extends HashMap<String, ArrayList<ChannelCard>>{

    }
}
