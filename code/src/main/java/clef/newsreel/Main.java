package clef.newsreel;

import java.io.*;

/**
 * Created by gram on 23.03.17.
 */
public class Main {



    public static void main(String[] args) {

        /*String filePathLog = "/Users/zhanglemei/Documents/project/dataset/newsreel2017/";
        String filePathSer = "/Users/zhanglemei/Documents/project/dataset/tempfile/";
        int[] fileNumbers = {1,1};  // {1,1} for 2016-02-01.log,
                                    // {1,3} for (2016-02-01.log + 2016-02-02.log + 2016-02-03.log) etc.

        DataLoader dataloader = new DataLoader();
        Datastore datastore = new Datastore();
        Recommender recommender = new Recommender(datastore);


        ArrayList<Object> dataStream = dataloader.loadDataStream(filePathLog, filePathSer, fileNumbers);

        double count = 0;
        double size = dataStream.size();
        double lastProg = 0;

        KeyWordsObject keyWordsObject = null;

        for(Object event : dataStream) {
            if (event instanceof KeyWordsObject) {
                keyWordsObject = (KeyWordsObject) event;
            } else if (event instanceof ItemUpdate) {
                datastore.registerArticle((ItemUpdate) event);
            } else if (event instanceof RecommendationReq) {
                datastore.registerRecommendationReq((RecommendationReq) event, recommender, true, keyWordsObject);
            } else if (event instanceof ClickEvent) {
                datastore.registerClickEvent((ClickEvent) event);
            }
            double progress = (int) (20 * count / size);
            if(progress != lastProg) {
                System.out.print(5*progress + "% ");
                lastProg = progress;
            }
            count++;
        }
        System.out.println("Complete");

        // The timing is inconsistent for recommendationReq objects, CHECK this
        //datastore.printUserSessions();
        evaluation(datastore);*/


        //python test code
        System.out.println("Python test begin!");
        String fileName = "/Users/zhanglemei/Documents/project/dataset/testfile/test.txt";
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("Read file by line: ");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int lineNum = 1;
            while ((tempString = reader.readLine()) != null) {
                System.out.println(">>>Read line " + lineNum + ": " + tempString);
                Process pr = Runtime.getRuntime().exec("python /Users/zhanglemei/Documents/project/clefnews/code/src/main/python/client.py --data " + tempString);
                BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                in.close();
                pr.waitFor();
                lineNum++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        System.out.println("Python test end!");
    }




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




    }*/

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
