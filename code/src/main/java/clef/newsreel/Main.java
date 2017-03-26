package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import clef.newsreel.DataLoader.ItemUpdate;
import clef.newsreel.DataLoader.ClickEvent;
import clef.newsreel.DataLoader.RecommendationReq;

/**
 * Created by gram on 23.03.17.
 */
public class Main {



    public static void main(String[] args){

        String filePath = "/home/havikbot/Documents/";
        int[] fileNumbers = {1,1};  // {1,1} for 2016-02-01.log,
                                    // {1,3} for (2016-02-01.log + 2016-02-02.log + 2016-02-03.log) etc.

        DataLoader dataloader = new DataLoader();
        Datastore datastore = new Datastore();
        Recommender recommender = new Recommender(datastore);
        ArrayList<Object> datastream = dataloader.loadDataStream(filePath, fileNumbers);


        for(Object event : datastream) {
            if (event instanceof ItemUpdate) {
                datastore.registerArticle((ItemUpdate) event);
            } else if (event instanceof RecommendationReq) {
                datastore.registerRecommendationReq((RecommendationReq) event);
                //TODO Run recommender and set user.latestSession[domainID].recomenderItem = recomended item

            } else if (event instanceof ClickEvent) {
                datastore.registerClickEvent((ClickEvent) event);

            }
        }

        // The timing is inconsistent for recommendationReq objects, CHECK this
        datastore.printUserSessions();





        /**
         * Some code for testing
         *


        for(Object o : datastream){
            if(o instanceof DataLoader.ClickEvent){
                DataLoader.ClickEvent ce = (DataLoader.ClickEvent) o;
                if ((""+ce.userID).equals("36908532294343288")){
                    System.out.println("click:     \t" + ce.clickedArticles + "\t" + ce.itemID  +"\t" + new Date(ce.timeStamp) );
                }
                //System.out.println(ce.userID);
            }
            else if(o instanceof DataLoader.RecommendationReq) {
                DataLoader.RecommendationReq re = (DataLoader.RecommendationReq) o;
                if (("" + re.userID).equals("36908532294343288")) {
                    System.out.println("RecRequest: \t" + re.itemID + "\t" + re.limit + "\t"+ new Date(re.timeStamp) );
                }
                //System.out.println(ce.userID);
            }
        }

         */


    }







}
