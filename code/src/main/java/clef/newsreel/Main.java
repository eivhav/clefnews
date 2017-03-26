package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


/**
 * Created by gram on 23.03.17.
 */
public class Main {



    public static void main(String[] args){

        String filePath = "/home/havikbot/Documents/";
        int[] fileNumbers = {1,1};

        DataLoader dataloader = new DataLoader();
        ArrayList<Object> datastream = dataloader.loadDataStream(filePath, fileNumbers);


        /**
         * Some code for testing
         *
         */

        for(Object o : datastream){
            if(o instanceof DataLoader.ClickEvent){
                DataLoader.ClickEvent ce = (DataLoader.ClickEvent) o;
                if ((""+ce.userID).equals("36908532294343288")){
                    System.out.println("click:     \t" + ce.listOfDisplayedRecs + "\t" + ce.itemID  +"\t" + new Date(ce.timeStamp) );
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




    }





}
