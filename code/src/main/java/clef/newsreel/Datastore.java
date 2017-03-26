package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import clef.newsreel.DataLoader.ItemUpdate;
import clef.newsreel.DataLoader.ClickEvent;
import clef.newsreel.DataLoader.RecommendationReq;

/**
 * Created by havikbot on 23.03.17.
 */
public class Datastore {


    public HashMap<Long, Domain> domains = new HashMap<Long, Domain> ();
    public HashMap<Long, User> users = new HashMap<Long, User>();


    public Datastore(){}

    public class Domain{
        long domainID;
        public HashMap<Long, Article> articles = new HashMap<Long, Article>();

        public Domain(long domainID){
            this.domainID = domainID;
        }
    }


    public void registerArticle(ItemUpdate itemUpdate){

        if(!domains.containsKey(itemUpdate.domainID)){
            domains.put(itemUpdate.domainID, new Domain(itemUpdate.domainID));
        }
        HashMap<Long, Article> articles = domains.get(itemUpdate.domainID).articles;
        if(itemUpdate.itemID != 0) {
            Article article;
            if (articles.containsKey(itemUpdate.itemID)) {
                article = articles.get(itemUpdate.itemID);
                article.domain = domains.get(itemUpdate.domainID);
                article.created_date = itemUpdate.created_date;

            } else {
                article = new Article(itemUpdate.itemID, domains.get(itemUpdate.domainID), itemUpdate.created_date);
                articles.put(itemUpdate.itemID, article);
            }
            article.title = itemUpdate.title;
            article.text_content = itemUpdate.text;
            article.recommendable = itemUpdate.recommendable;

        }
        else{
            System.out.println("itemUpdate.itemID = 0;  " + itemUpdate.itemID);
        }
    }


    public void registerRecommendationReq(RecommendationReq rec){
        if(domains.containsKey(rec.domainID) && domains.get(rec.domainID).articles.containsKey(rec.itemID)){

            // If user has not been seen before, crete new and add to user-HashMap
            if(!users.containsKey(rec.userID)){ users.put(rec.userID, new User(rec.userID)); }

            User user = users.get(rec.userID);
            Article article = domains.get(rec.domainID).articles.get(rec.itemID);
            user.registerReqEventForUser(domains.get(rec.domainID), article, rec.timeStamp);
            article.user_visited.put(rec.userID, user);
        }


    }

    public void registerClickEvent(ClickEvent clickEvent){
        if(domains.containsKey(clickEvent.domainID) && users.containsKey(clickEvent.userID)) {
            User user = users.get(clickEvent.userID);
            if (clickEvent.clickedArticles.size() > 0) {
                user.registerClickEvent(domains.get(clickEvent.domainID), clickEvent.itemID, clickEvent.clickedArticles.get(0));
            }
        }
    }



    public void printUserSessions(){
        int count = 0;
        for (Long userID : users.keySet()){
            System.out.println("For User:" + userID);
            users.get(userID).printSessions();
            count++;
            if(count > 1500){ break; }
        }
        System.out.println("#User:" + users.size());

    }






















}
