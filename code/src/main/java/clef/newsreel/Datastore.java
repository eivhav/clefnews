package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by havikbot on 23.03.17.
 */
public class Datastore {

    private JSONparsers jsonparsers = new JSONparsers();

    public HashMap<Long, Article> articles = new HashMap<Long, Article>();
    public HashMap<Integer, User> users = new HashMap<Integer, User>();



    public Datastore(){}

    public void register_article(String jsonMessageBody){
        jsonparsers.parseAndUpdateArticles(jsonMessageBody, articles);




    }
















}
