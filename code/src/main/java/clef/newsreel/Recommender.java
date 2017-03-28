package clef.newsreel;

import clef.newsreel.Datastore.Domain;

import java.util.ArrayList;

/**
 * Created by havikbot on 26.03.17.
 */
public class Recommender {

    Datastore datastore;


    public Recommender(Datastore datastore){
        this.datastore = datastore;
    }

    //TODO

    public ArrayList<Long> recommendArticle(Domain domain, Article article, User user, long limit){



        return recommendRandomArticle(domain, article, user, limit);
    }


    public ArrayList<Long> recommendRandomArticle(Domain domain, Article article, User user, long limit){
        ArrayList<Long> recs = new ArrayList<Long>();
        int count = 0;
        for(long itemID : domain.recommendableArticles.keySet()){
            count++;
            if(recs.size() == limit){
                break;
            }
            if(!user.articlesVisited.get(domain.domainID).containsKey(itemID) && article.itemID != itemID) {
                recs.add(itemID);

            }
        }
        //System.out.println("domain: "+ domain.domainID + ", "+domain.articles.size() + ": " +count + "   limit: " + limit);
        return recs;
    }



}
