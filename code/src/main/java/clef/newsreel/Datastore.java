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

    public HashMap<Integer, Article> articles = new HashMap<Integer, Article>();
    public HashMap<Integer, User> users = new HashMap<Integer, User>();


    public Datastore(){}

    public void register_article(String jsonMessageBody){
        final JSONObject jObj = (JSONObject) JSONValue.parse(jsonMessageBody);

        //Article article = new Article(Integer.parseInt(String(jObj.get("item_id"))), jObj.get("domain_id"), jObj.get("created_date"))



    }










    class Article{

        public int item_id;
        public int domain_id;
        public Date created_date;

        public ArrayList<Integer> keywords = new ArrayList<Integer>();
        public HashMap<Date, Article> users_cliked_on = new HashMap<Date, Article>();

        public Article(int item_id, int domain_id, String date){
            this.item_id = item_id;
            this.domain_id = domain_id;
            this.created_date = null; //TODO: add date
        }
    }

    class User{

        public int user_id;
        public HashMap<Date, Article> articles_clicked_on = new HashMap<Date, Article>();

        public User(int user_id){
            this.user_id = user_id;
        }



    }



}
