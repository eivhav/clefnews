package clef.newsreel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by havikbot on 26.03.17.
 */
public class DataLoader implements Serializable  {

    public DataLoader(){}

    class ItemUpdate implements Serializable{
        long itemID;
        long domainID;
        String text;
        String title;
        boolean recommendable;
        long created_date;

        public ItemUpdate(long itemID, long domainID, String text, String title, boolean rec, long time){
            this.itemID = itemID;
            this.domainID = domainID;
            this.text = text;
            this.title = title;
            this.recommendable = rec;
            this.created_date = time;
        }
    }


    class ClickEvent implements Serializable{
        long domainID;
        long itemID;
        long userID;
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


    class RecommendationReq implements Serializable {
        long domainID;
        long itemID;        //The article
        long userID;
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


    public ArrayList<Object> loadDataStream(String filepath, int[] fileNumbers){

        String prefix = "nr2016-02-";
        ArrayList<Object> datastream = new ArrayList<Object>();

        for(int i = fileNumbers[0]; i <= fileNumbers[1]; i++){
            String fileName;
            if(i < 10){ fileName = prefix + "0" + i; }
            else { fileName = prefix  + i; }

            ArrayList<Object> singleFileStream;
            try{
                FileInputStream fileIn = new FileInputStream(filepath+fileName+".ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                System.out.println("Reading serialized file: "+fileName+".ser");
                singleFileStream = (ArrayList<Object>) in.readObject();
                datastream.addAll(singleFileStream);
                in.close();
                fileIn.close();
            }
            catch(IOException ioE){
                System.out.println("Could not find .ser file. Reading from .log file at"+filepath+fileName+".log");
                singleFileStream = parseFile(filepath, fileName+".log");
                datastream.addAll(singleFileStream);
                try {
                    FileOutputStream fileOut = new FileOutputStream(filepath+fileName+".ser");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(singleFileStream);
                    out.close();
                    fileOut.close();
                    System.out.println("Serialized data is saved in "+filepath+fileName+".ser");
                }
                catch(IOException ioE2) {
                    ioE2.printStackTrace();
                    System.err.println("Could not save .ser file");
                }
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
                System.err.println("ArrayList<Object> class not found when loading .ser file");
            }
        }
        System.out.println("DataStream loaded. Size:" + datastream.size());
        return datastream;
    }


    private ArrayList<Object> parseFile(String path, String fileName){

        ArrayList<Object> events = new ArrayList<Object>();
        try{
            int[] counts = {0, 0, 0, 0, 0};     //[total_counts, item_updates, clicks, recommendations]
            int[] discarded = {0, 0, 0};
            BufferedReader br = new BufferedReader(new FileReader(path+fileName));
            for(String line; (line = br.readLine()) != null; ) {
                counts[0]++;

                if(line.substring(0,11).equals("item_update")){
                    ItemUpdate itemUpdate = parseItemUpdates(line.substring(12, line.length()-24));
                    if(itemUpdate != null){
                        events.add(itemUpdate);
                        counts[1]++;
                    }
                    else{
                        discarded[0]++;
                    }
                }
                else if (line.substring(0,12).equals("event_notifi")){
                    ClickEvent clickEvent = parseEventNotification(line.substring(19, line.length()-24));
                    if(clickEvent != null){
                        events.add(clickEvent);
                        counts[2]++;
                    }
                    else{
                        discarded[1]++;
                    }
                }
                else if (line.substring(0,14).equals("recommendation")){
                    RecommendationReq recommendationReq = parseRecommendationRequest(line.substring(23, line.length()-24));
                    if(recommendationReq != null){
                        events.add(recommendationReq);
                        counts[3]++;
                    }
                    else{
                        discarded[2]++;
                    }
                }

                if(counts[0] % 100000 == 0){
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



    private ItemUpdate parseItemUpdates(String _jsonMessageBody) {

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





    private ClickEvent parseEventNotification(final String _jsonMessageBody) {

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



    private RecommendationReq parseRecommendationRequest(String _jsonMessageBody) {

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
