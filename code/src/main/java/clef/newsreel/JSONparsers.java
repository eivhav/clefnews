package clef.newsreel;

import de.dailab.plistacontest.client.RecommenderItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by havikbot on 23.03.17.
 */
public class JSONparsers implements Serializable{

    public JSONparsers(){}

    class ItemUpdate implements Serializable{

        final long itemID;
        final long domainID;
        final String text;
        final String title;
        final boolean recommendable;
        final long created_date;

        public ItemUpdate(long itemID, long domainID, String text, String title, boolean rec, long time){
            this.itemID = itemID;
            this.domainID = domainID;
            this.text = text;
            this.title = title;
            this.recommendable = rec;
            this.created_date = time;
        }

    }


    public ItemUpdate parseItemUpdates(String _jsonMessageBody) {

        try {
            final JSONObject jsonObj = (JSONObject) JSONValue.parse(_jsonMessageBody);

            long itemID = 0;
            long domainID = 0;
            if (!"null".equals(jsonObj.get("id") + "")) { itemID = Long.parseLong(jsonObj.get("id") + ""); }
            if (!"null".equals(jsonObj.get("domainid") + "")) { domainID = Long.parseLong(jsonObj.get("domainid") + ""); }

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

            return new ItemUpdate(itemID, domainID, text, title, recommendable, created);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    class ClickEvent implements Serializable{
        final long domainID;
        final long itemID;
        final long userID;
        List<Long> listOfDisplayedRecs;
        long timeStamp;

        public ClickEvent(long domainID, long itemID, long userID, List<Long> listOfDisplayedRecs, long timeStamp){
            this.domainID = domainID;
            this.itemID = itemID;
            this.userID = userID;
            this.timeStamp = timeStamp;
            this.listOfDisplayedRecs = listOfDisplayedRecs;

        }

    }


    public ClickEvent parseEventNotification(final String _jsonMessageBody) {

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
                    //System.err.println("[Exception] no domainID found in "+ _jsonMessageBody);
                    return null;
                }
            }

            Long itemID = null;
            try {
                itemID = Long.valueOf(jsonObjectContextSimple.get("25").toString());
            } catch (Exception ignored) {
                try {
                    itemID = Long.valueOf(jsonObj.get("itemID").toString());
                } catch (Exception e) {
                    //System.err.println("[Exception] no itemID found in " + _jsonMessageBody);
                    return null;
                }
            }

            Long userID = -2L;
            try {
                userID = Long.valueOf(jsonObjectContextSimple.get("57").toString());
            } catch (Exception ignored) {
                try {
                    userID = Long.valueOf(jsonObj.get("userID").toString());
                } catch (Exception e) {
                    //System.err.println("[Exception] no userID found in " + _jsonMessageBody);
                    return null;
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
                return null;
            }

            long timeStamp = 0;
            try {
                timeStamp = (Long) jsonObj.get("created_at") + 0L;
            } catch (Exception ignored) {
                timeStamp = (Long) jsonObj.get("timestamp");
            }

            return new ClickEvent(domainID, itemID, userID, listOfDisplayedRecs, timeStamp);


        }
        catch (Exception e){
            System.err.println("JSON parse failed at parseEventNotification");
            return null;

        }
    }

    class RecommendationReq implements Serializable {
        final long domainID;
        final long itemID;
        final long userID;
        long timeStamp;
        long limit;

        public RecommendationReq(long domainID, long itemID, long userID, long timeStamp, long limit) {
            this.domainID = domainID;
            this.itemID = itemID;
            this.userID = userID;
            this.timeStamp = timeStamp;
            this.limit = limit;


        }
    }

    public RecommendationReq parseRecommendationRequest(String _jsonMessageBody) {

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
                    domainID = Long.valueOf(jsonObjectContextSimple.get("domainId").toString());
                } catch (Exception e) {
                    System.err.println("[Exception] no domainID found in "+ _jsonMessageBody);
                    return null;
                }
            }


            Long itemID = null;
            try {
                itemID = Long.valueOf(jsonObjectContextSimple.get("25").toString());
            } catch (Exception ignored) {
                try {
                    itemID = Long.valueOf(jsonObjectContextSimple.get("itemId").toString());
                } catch (Exception e) {
                    System.err.println("[Exception] no itemID found in " + _jsonMessageBody);
                    return null;
                }
            }


            Long userID = -2L;
            try {
                userID = Long.valueOf(jsonObjectContextSimple.get("57").toString());
            } catch (Exception ignored) {
                try {
                    userID = Long.valueOf(jsonObjectContextSimple.get("userId").toString());
                } catch (Exception e) {
                    System.err.println("[INFO] no userID found in " + _jsonMessageBody);
                    return null;
                }
            }

            long timeStamp = 0;
            try {
                timeStamp = (Long) jsonObj.get("created_at") + 0L;
            } catch (Exception ignored) {
                timeStamp = (Long) jsonObj.get("timestamp");
            }


            Long limit = 0L;
            try {
                limit = (Long) jsonObj.get("limit");
            } catch (Exception e) {
                System.err.println("[Exception] no limit found in "	+ _jsonMessageBody);
                return null;
            }

            return new RecommendationReq(domainID, itemID, userID, timeStamp, limit);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }







}
