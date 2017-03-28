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

    public ArrayList<Long> recommendArticle(Domain domain, User user){
        return new ArrayList<Long>();
    }




}
