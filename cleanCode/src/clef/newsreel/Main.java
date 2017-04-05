package clef.newsreel;

import java.util.*;
import clef.newsreel.DataLoader.ItemUpdate;
import clef.newsreel.DataLoader.ClickEvent;
import clef.newsreel.DataLoader.RecommendationReq;
import clef.newsreel.DataLoader.KeyWordsObject;

/**
 * Created by gram on 23.03.17.
 */
public class Main {



    public static void main(String[] args){

        //String filePathLog = "/export/b/home/lemeiz/clefnew/idomaar/datastreammanager/input/newsreel-test/2017-NewsREEL/";
        //String filePathSer = "/export/b/home/lemeiz/clefnew/idomaar/datastreammanager/input/newsreel-test/2017-NewsREEL/";
        String filePathLog = "/home/havikbot/Documents/CLEFdata/";
        String filePathSer = "/home/havikbot/Documents/CLEFdata/";
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
                datastore.registerRecommendationReq((RecommendationReq) event, recommender, false, keyWordsObject);
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

        System.out.println("keywords size:" + datastore.all_keywords.size());
        System.out.println("articles size:");
        for(long dKey : datastore.domains.keySet()){
            System.out.println("  domain:" + dKey+" : " + datastore.domains.get(dKey).articles.size());
        }

        recommender.buildCollaborativeProfiles(5, 1, 10);

        //ArrayList<int[]> ratingSparseMatrix =  datastore.getArticlesRead(20, 2);
        //int nbArticles = datastore.getNoOfArticles();


        /**
         for (int[] userLine : ratingSparseMatrix){
         System.out.println(Arrays.toString(userLine));
         }


        Spark spark = new Spark();
        spark.getPcaSvdRowList(ratingSparseMatrix, nbArticles);
        spark.runSVD(1000, ratingSparseMatrix, nbArticles);
        spark.runPCA(1000, ratingSparseMatrix, nbArticles

         **/
        // The timing is inconsistent for recommendationReq objects, CHECK this
        //datastore.printUserSessions();
        evaluation(datastore);





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


    public void printUserPerArticleCount(Datastore datastore){
        HashMap<Integer, Integer> article_counts = new HashMap<Integer, Integer>();
        for(long dKey : datastore.domains.keySet()){
            for(long itemID : datastore.domains.get(dKey).articles.keySet()){
                Article article = datastore.domains.get(dKey).articles.get(itemID);
                Integer user_count = article.user_visited.size();
                if(!article_counts.containsKey(user_count)){article_counts.put(user_count, 0);}
                article_counts.put(user_count, article_counts.get(user_count) + 1);
            }
        }
        List sortedKeys=new ArrayList(article_counts.keySet());
        Collections.sort(sortedKeys);
        for(Object k : sortedKeys){
            System.out.println( k + "\t" + article_counts.get(k));
        }
    }





}
