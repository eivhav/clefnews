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



    public HashMap<Long, Article> articles = new HashMap<Long, Article>();
    public HashMap<Long, User> users = new HashMap<Long, User>();


    public Datastore(){}



    public void register_article(ItemUpdate itemUpdate){

        if(itemUpdate.itemID != 0) {
            Article article;
            if (articles.containsKey(itemUpdate.itemID)) {
                article = articles.get(itemUpdate.itemID);
                article.domainID = itemUpdate.domainID;
                article.created_date = itemUpdate.created_date;

            } else {
                article = new Article(itemUpdate.itemID, itemUpdate.domainID, itemUpdate.created_date);
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

    public void register_clickEvent(ClickEvent clickEvent){
        //TODO



    }

    public void register_recomemndation_request(RecommendationReq recommendationReq){
        //TODO

    }


















}
