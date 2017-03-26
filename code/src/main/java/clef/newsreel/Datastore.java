package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import clef.newsreel.JSONparsers.ItemUpdate;
import clef.newsreel.JSONparsers.ClickEvent;
import clef.newsreel.JSONparsers.RecommendationReq;

/**
 * Created by havikbot on 23.03.17.
 */
public class Datastore {

    private JSONparsers jsonparsers = new JSONparsers();

    public ArrayList<Object> dataStream = new ArrayList<Object>();

    public HashMap<Long, Article> articles = new HashMap<Long, Article>();
    public HashMap<Long, User> users = new HashMap<Long, User>();

    public ArrayList<ClickEvent> clickEvents = new ArrayList<ClickEvent>();
    int nb_discarded_clicks = 0;
    public ArrayList<RecommendationReq> recommendationReqs = new ArrayList<RecommendationReq>();
    int nb_discarded_reqs = 0;



    public Datastore(){}

    public ArrayList<Object> parseFile(String path, String fileName){

        ArrayList<Object> events = new ArrayList<Object>();
        try{
            int[] counts = {0, 0, 0, 0, 0};     //[total_counts, item_updates, clicks, recommendations]
            int[] discarded = {0, 0, 0};

            BufferedReader br = new BufferedReader(new FileReader(path+fileName));
            for(String line; (line = br.readLine()) != null && counts[0] < 100000; ) {
                counts[0]++;

                if(line.substring(0,11).equals("item_update")){
                    ItemUpdate itemUpdate = jsonparsers.parseItemUpdates(line.substring(12, line.length()-24));
                    if(itemUpdate != null){
                        events.add(itemUpdate);
                        counts[1]++;
                    }
                    else{
                        discarded[0]++;
                    }
                }
                else if (line.substring(0,12).equals("event_notifi")){
                    ClickEvent clickEvent = jsonparsers.parseEventNotification(line.substring(19, line.length()-24));
                    if(clickEvent != null){
                        events.add(clickEvent);
                        counts[2]++;
                    }
                    else{
                        discarded[1]++;
                    }
                }
                else if (line.substring(0,14).equals("recommendation")){
                    RecommendationReq recommendationReq = jsonparsers.parseRecommendationRequest(line.substring(23, line.length()-24));
                    if(recommendationReq != null){
                        events.add(recommendationReq);
                        counts[3]++;
                    }
                    else{
                        discarded[2]++;
                    }
                }

                if(counts[0] % 25000 == 0){
                    System.out.println("Processing: " + counts[0]);

                }
            }
            System.out.println(fileName + " completed!");
            System.out.println("#item_updates:"+counts[1] +"  Discarded:"+discarded[0]);
            System.out.println("#clickEvents:"+counts[2] +"  Discarded:"+discarded[1]);
            System.out.println("#reqEvents:"+counts[3] +"  Discarded:"+discarded[2]);
            System.out.println();
        }
        catch (Exception e){
            System.err.println("Could not read file or some other exception");
        }

        return events;

    }




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
        clickEvents.add(clickEvent);



    }

    public void register_recomemndation_request(RecommendationReq recommendationReq){
        recommendationReqs.add(recommendationReq);

    }


















}
