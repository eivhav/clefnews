package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
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

        String filePathLog = "/media/havikbot/F/CLEFdata/";
        String filePathSer = "/home/havikbot/Documents/CLEFdata/";
        int[] fileNumbers = {1,4};  // {1,1} for 2016-02-01.log,
                                    // {1,3} for (2016-02-01.log + 2016-02-02.log + 2016-02-03.log) etc.

        DataLoader dataloader = new DataLoader();
        Datastore datastore = new Datastore();
        Recommender recommender = new Recommender(datastore);


        ArrayList<Object> dataStream = dataloader.loadDataStream(filePathLog, filePathSer, fileNumbers);

        double count = 0;
        double size = dataStream.size();
        double lastProg = 0;

        for(Object event : dataStream) {

            if (event instanceof ItemUpdate) {
                datastore.registerArticle((ItemUpdate) event);
            } else if (event instanceof RecommendationReq) {
                datastore.registerRecommendationReq((RecommendationReq) event, recommender);
            } else if (event instanceof ClickEvent) {
                datastore.registerClickEvent((ClickEvent) event);
            }
            double progress = (int) (100 * count / size);
            if(progress != lastProg) {
                System.out.print(progress + "% ");
                lastProg = progress;
                if(progress % 10 == 0) {
                    System.out.print("\n");
                }
            }
            count++;
        }
        System.out.println("Complete");

        // The timing is inconsistent for recommendationReq objects, CHECK this
        //datastore.printUserSessions();
        evaluation(datastore);






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

    public static void evaluation(Datastore datastore){
        System.out.println("Running Evaluation");
        int[] overallScores = new int[3];   //[nb_recs, nb_clicks, nb_sucsessfullRecs]
        for(Long domainID : datastore.domains.keySet()){
            int[] domainScores = new int[3];
            int nb_users = 0;
            for(Long userID : datastore.users.keySet()){
                if(datastore.users.get(userID).statistics.containsKey(domainID)){
                    domainScores[0] += datastore.users.get(userID).statistics.get(domainID).nb_recommendationRequests;
                    domainScores[1] += datastore.users.get(userID).statistics.get(domainID).nb_clicks;
                    domainScores[2] += datastore.users.get(userID).statistics.get(domainID).nb_sucssessfullRecs;
                    nb_users++;
                }
            }
            calcAndPrintScores(domainScores, "For Domain: " + domainID);
            for(int i = 0; i < 3; i++){
                overallScores[i] += domainScores[i];
            }
        }
        calcAndPrintScores(overallScores, "For Entire dataset:");

    }

    public static void calcAndPrintScores(int[] scores, String title){
        double percentAllreqs = 100*(double)scores[2] / (double) scores[0];
        double percentClicks = 100*(double)scores[2] / (double) scores[1];
        System.out.println(fixedLengthString(title, 20) + "\t" +
                fixedLengthString("[" +scores[0] +", " + scores[1] + ", " + scores[2] +" ] ", 20) + "\t" +
                "[" + String.format( "%.5f", percentAllreqs) +", " + String.format( "%.5f", percentClicks) + "]");

    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }






}
