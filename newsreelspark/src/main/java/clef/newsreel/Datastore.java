package clef.newsreel;

import java.util.*;

import clef.newsreel.DataLoader.ItemUpdate;
import clef.newsreel.DataLoader.ClickEvent;
import clef.newsreel.DataLoader.RecommendationReq;
import clef.newsreel.DataLoader.KeyWordsObject;

/**
 * Created by havikbot on 23.03.17.
 */
public class Datastore {

    public Set<Long> all_keywords = new HashSet<Long>();

    public HashMap<Long, Domain> domains = new HashMap<Long, Domain> ();
    public HashMap<Long, User> users = new HashMap<Long, User>();


    public Datastore(){

    }

    public class Domain{
        long domainID;
        public HashMap<Long, Article> articles = new HashMap<Long, Article>();
        public HashMap<Long, Article> recommendableArticles = new HashMap<Long, Article>();

        public Domain(long domainID){
            this.domainID = domainID;
        }
    }


    public void registerArticle(ItemUpdate itemUpdate){

        if(!domains.containsKey(itemUpdate.domainID)){ domains.put(itemUpdate.domainID, new Domain(itemUpdate.domainID));}
        Domain domain = domains.get(itemUpdate.domainID);

        if(itemUpdate.itemID != 0) {
            if (!domain.articles.containsKey(itemUpdate.itemID)) {
                domain.articles.put(itemUpdate.itemID, new Article(itemUpdate.itemID));
                domain.articles.get(itemUpdate.itemID).articleIndex = getNoOfArticles()-1;
            }
            Article article = domain.articles.get(itemUpdate.itemID);
            article.updateInfo(domain, itemUpdate.created_date, itemUpdate.title, itemUpdate.text, itemUpdate.recommendable);
            if(article.recommendable){ domain.recommendableArticles.put(itemUpdate.itemID, article); }


        }
        else{
            System.out.println("itemUpdate.itemID = 0;  " + itemUpdate.itemID);
        }
    }

    // Maybe allow recommendationReq for articles not yet added by item_update?

    public void registerRecommendationReq(RecommendationReq rec, Recommender recommender,
                                          boolean includeUnkownItems, KeyWordsObject keyWordsObject){

        if(domains.containsKey(rec.domainID) && domains.get(rec.domainID).articles.containsKey(rec.itemID)){

            // If user has not been seen before, crete new and add to user-HashMap
            if(!users.containsKey(rec.userID)){ users.put(rec.userID, new User(rec.userID, domains)); }

            User user = users.get(rec.userID);
            Article article = domains.get(rec.domainID).articles.get(rec.itemID);
            article.setKeyWords(keyWordsObject.getKeyWords(rec.domainID, rec.itemID, rec.timeStamp));
            for (long keyword : article.keyWords.keySet()){
                all_keywords.add(keyword);
            }

            user.registerReqEventForUser(domains.get(rec.domainID), article, rec.timeStamp);
            article.user_visited.put(rec.userID, user);

            // Call recommendation algorithm
            ArrayList<Long> recArticleIDs = recommender.recommendArticle(domains.get(rec.domainID), article, user, rec.limit);
            user.registerRecommendation(domains.get(rec.domainID), recArticleIDs);
        }
        // This part adds articles not added by item_update
        else if(includeUnkownItems){
            if(!domains.containsKey(rec.domainID)){ domains.put(rec.domainID, new Domain(rec.domainID));}

            Domain domain = domains.get(rec.domainID);
            if(!domain.articles.containsKey(rec.itemID)) {
                domain.articles.put(rec.itemID, new Article(rec.itemID));
                Article article = domain.articles.get(rec.itemID);
                article.articleIndex = getNoOfArticles()-1;
                article.updateInfo(domain, rec.timeStamp, "", "", false);
            }
            registerRecommendationReq(rec, recommender, false, keyWordsObject);
        }

    }


    public void registerClickEvent(ClickEvent clickEvent){
        if(domains.containsKey(clickEvent.domainID) && users.containsKey(clickEvent.userID)){

            User user = users.get(clickEvent.userID);
            if (clickEvent.clickedArticles.size() > 0
                    && domains.get(clickEvent.domainID).recommendableArticles.containsKey(clickEvent.clickedArticles.get(0))){

                user.registerClickEvent(domains.get(clickEvent.domainID), clickEvent.itemID, clickEvent.clickedArticles.get(0));
            }
        }




    }


    public ArrayList<int[]> getArticlesRead(int nbArticlesThreshold, int nbSessionsThreshold){

        ArrayList<int[]> ratingSparseMatrix = new ArrayList<int[]>();

        for(long userID : users.keySet()){
            ArrayList<Integer> articlesRead = new ArrayList<Integer>();
            for(long dKey : users.get(userID).articlesVisited.keySet()){
                for(long vKey : users.get(userID).articlesVisited.get(dKey).keySet()){
                    articlesRead.add(users.get(userID).articlesVisited.get(dKey).get(vKey).article.articleIndex);
                }
            }
            Set<Integer> set = new HashSet<Integer>(articlesRead );
            if(set.size() < articlesRead .size()){
                System.out.println("there are duplicates");
            }
            int[] articleVector = new int[articlesRead.size()];
            for(int i = 0; i < articleVector.length; i++){ articleVector[i] = articlesRead.get(i);}

            if(articlesRead.size() >= nbArticlesThreshold && users.get(userID).getNoOfSessions() >=nbSessionsThreshold){
                ratingSparseMatrix.add(articleVector);
            }
        }

        return ratingSparseMatrix;

    }


    public int getNoOfArticles(){
        int count = 0;
        for(long dKey : domains.keySet()){
            count += domains.get(dKey).articles.size();
        }
        return count;
    }







    public void printUserSessions(){
        int count = 0;
        for (Long userID : users.keySet()){
            System.out.println("For User:" + userID);
            users.get(userID).printSessions();
            count++;
            if(count > 1500){ break; }
        }
        System.out.println("#User:" + users.size());

    }






















}
