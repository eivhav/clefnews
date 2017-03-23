package clef.newsreel;

import de.dailab.plistacontest.client.RecommenderItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by havikbot on 23.03.17.
 */
public class JSONparsers {

    public JSONparsers(){}

    public void parseAndUpdateArticles(String _jsonMessageBody, HashMap<Long, Article> articles) {

        try {
            final JSONObject jsonObj = (JSONObject) JSONValue.parse(_jsonMessageBody);

            String itemID = jsonObj.get("id") + "";
            if ("null".equals(itemID)) {
                itemID = "0";
            }
            String domainID = jsonObj.get("domainid") + "";
            String text = jsonObj.get("text") + "";
            String title = jsonObj.get("title") + "";
            String flag = jsonObj.get("flag") + "";

            boolean recommendable = ("0".equals(flag));

            // parse date, now is default
            String createdAt = jsonObj.get("created_at") + "";
            Long created = System.currentTimeMillis();

            // maybe the field is called timeStamp instead of created_at
            if ("null".equals(createdAt)) {
                created = (Long) jsonObj.get("timestamp");
            } else {
                // parse the date string and derive the long date number
                try {
                    String pattern = "yyyy-MM-dd hh:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    created = sdf.parse(createdAt).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if(!itemID.equals("0") && !domainID.equals("")){
                Article article;
                Long longItemID = Long.parseLong(itemID);
                if(articles.containsKey(longItemID)) {
                    article = articles.get(longItemID);
                    article.domain_id = Long.parseLong(domainID);
                    article.created_date = created;
                }
                else{
                    article = new Article(longItemID, Long.parseLong(domainID), created);
                    articles.put(longItemID, article);
                }
                article.title = title;
                article.text_content = text;
                article.recommendable = recommendable;


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public void parseEventNotification(final String _jsonMessageBody) {

        try {
            final JSONObject jsonObj = (JSONObject) JSONValue.parse(_jsonMessageBody);

            // parse JSON structure to obtain "context.simple"
            JSONObject jsonObjectContext = (JSONObject) jsonObj.get("context");
            JSONObject jsonObjectContextSimple = (JSONObject) jsonObjectContext.get("simple");

            Long domainID = -3L;
            try {
                domainID = Long.valueOf(jsonObjectContextSimple.get("27").toString());
            } catch (Exception ignored) {
                try {
                    domainID = Long.valueOf(jsonObj.get("domainID").toString());
                } catch (Exception e) {
                    System.err.println("[Exception] no domainID found in "+ _jsonMessageBody);
                }
            }

            Long itemID = null;
            try {
                itemID = Long.valueOf(jsonObjectContextSimple.get("25").toString());
            } catch (Exception ignored) {
                try {
                    itemID = Long.valueOf(jsonObj.get("itemID").toString());
                } catch (Exception e) {
                    System.err.println("[Exception] no itemID found in " + _jsonMessageBody);
                }
            }

            Long userID = -2L;
            try {
                userID = Long.valueOf(jsonObjectContextSimple.get("57").toString());
            } catch (Exception ignored) {
                try {
                    userID = Long.valueOf(jsonObj.get("userID").toString());
                } catch (Exception e) {
                    System.err.println("[Exception] no userID found in " + _jsonMessageBody);
                }
            }

            // impressionType
            String notificationType = null;
            try {
                notificationType = jsonObj.get("type") + "";
            } catch (Exception e) {
                e.printStackTrace();
            }

            // event_type due to the idomaar data format
            String eventType = null;
            try {
                eventType = jsonObj.get("event_type") + "";
                if (!"null".equals(eventType)) {
                    notificationType = eventType;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // list of displayed recs
            List<Long> listOfDisplayedRecs = new ArrayList<Long>(6);
            try {
                Object jsonObjectRecsTmp = jsonObj.get("recs");
                if (jsonObjectRecsTmp == null || !(jsonObjectRecsTmp instanceof JSONObject)) {
                    System.err.println("[INFO] impression without recs " + jsonObj);
                } else {
                    JSONObject jsonObjectRecs = (JSONObject) jsonObjectRecsTmp;
                    JSONObject jsonObjectRecsInt = (JSONObject) jsonObjectRecs.get("ints");
                    JSONArray array = (JSONArray) jsonObjectRecsInt.get("3");
                    for (Object arrayEntry : array) {
                        listOfDisplayedRecs.add(Long.valueOf(arrayEntry + ""));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("invalid jsonObject: " + jsonObj);
            }

            long timeStamp = 0;
            try {
                timeStamp = (Long) jsonObj.get("created_at") + 0L;
            } catch (Exception ignored) {
                timeStamp = (Long) jsonObj.get("timestamp");
            }

            System.out.println("user: " + userID);
            System.out.println("item: " + itemID);
            System.out.println("domain: " + domainID);
            System.out.println("notificationType: " + notificationType);
            System.out.println("listOfDisplayedRecs: " + listOfDisplayedRecs.toString());




        }
        catch (Exception e){
            System.out.println("failed");

        }
    }

}
