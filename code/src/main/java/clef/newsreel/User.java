package clef.newsreel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import clef.newsreel.Datastore.Domain;


/**
 * Created by havikbot on 23.03.17.
 */
public class User{

    public Long userID;

    public HashMap<Long, ArrayList<Session>> sessions = new HashMap<Long, ArrayList<Session>>();    //<DomainID, List<Session>>
    public HashMap<Long, ArrayList<Visit>> articlesVisited = new HashMap<Long, ArrayList<Visit>>();  //<DomainID, List<Visit>>
    public HashMap<Long, HashMap<Long, Article>> articlesNotVisited = new HashMap<Long, HashMap<Long, Article>>();
    public HashMap<Long, UserStatistics> statistics = new HashMap<Long, UserStatistics>();




    public User(Long userID, HashMap<Long, Domain> domains){
        this.userID = userID;
        //Should we perhaps have som more info about our users?

        /* // Performance problem
        // New user means that we need to copy all articles into nonVisitedArticles
        for(Long domainID : domains.keySet()){
            HashMap<Long, Article> articles = domains.get(domainID).articles;
            if(!articlesNotVisited.containsKey(domainID)){ articlesNotVisited.put(domainID, new HashMap<Long, Article>()); }
            for(Long itemID : articles.keySet()) {
                articlesNotVisited.get(domainID).put(itemID, articles.get(itemID));
            }
        }
         */
    }


    public void addNewArticle(Domain domain, Article article) {
        if (!articlesNotVisited.containsKey(domain.domainID)) {
            articlesNotVisited.put(domain.domainID, new HashMap<Long, Article>());
        }
        if (!articlesNotVisited.get(domain.domainID).containsKey(article.itemID)) {
            articlesNotVisited.get(domain.domainID).put(article.itemID, article);
        }
    }


    public void registerReqEventForUser(Domain domain, Article article, long time){

        long sessionTimeLimit = 30*60000L;      // 30 minutes
        Visit visit = new Visit(article, time);

        // Pull the latest session if inside the time window, else create new session
        if(!sessions.containsKey(domain.domainID)){ sessions.put(domain.domainID, new ArrayList<Session>()); }
        ArrayList<Session> sessionList = sessions.get(domain.domainID);
        if(sessionList.size() == 0 || time > (sessionTimeLimit + sessionList.get(sessionList.size()-1).timeLastVisit)){
            sessionList.add(new Session(domain));
        }
        sessionList.get(sessionList.size()-1).addVisit(visit);

        // Add visit to the articleVisited list
        if(!articlesVisited.containsKey(domain.domainID)){ articlesVisited.put(domain.domainID, new ArrayList<Visit>()); }
        articlesVisited.get(domain.domainID).add(visit);

        // Remove from nonVisited list. No effect due to the performance issue
        if(articlesNotVisited.containsKey(domain.domainID) && articlesNotVisited.get(domain.domainID).containsKey(article.itemID)){
            articlesNotVisited.get(domain.domainID).remove(article.itemID);
        }

        // Add to statistic for the current user at the current domain
        if(!statistics.containsKey(domain.domainID)){ statistics.put(domain.domainID, new UserStatistics()); }
        statistics.get(domain.domainID).nb_recommendationRequests++;

    }


    public void registerClickEvent(Domain domain, long previousArticleID, long clickedArticleID){

        if(sessions.containsKey(domain.domainID) && sessions.get(domain.domainID).size() > 0) {

            // Check if ClickEvent.itemID = session.lastArticle and ClickEvent.clickedArticles = lastRecommendation
            Session currentSession = sessions.get(domain.domainID).get(sessions.get(domain.domainID).size() - 1);
            if(currentSession.visits.size() > 0){

                Visit lastVisit = currentSession.visits.get(currentSession.visits.size()-1);
                if(lastVisit.article.itemID == previousArticleID && currentSession.lastRecommendation == clickedArticleID){
                    statistics.get(domain.domainID).nb_sucssessfullRecs++;
                }
            }
            currentSession.lastRecommendation = 0;
            statistics.get(domain.domainID).nb_clicks++;
        }
    }


    public void registerRecommendation(Domain domain, Long articleID){
        if(sessions.containsKey(domain.domainID) && sessions.get(domain.domainID).size()>0){
            Session currentSession = sessions.get(domain.domainID).get(sessions.get(domain.domainID).size() - 1);
            currentSession.lastRecommendation = articleID;
        }
    }


    public void printSessions(){
        for(Long domainID : sessions.keySet()){
            for (Session s : sessions.get(domainID)){
                s.printSession();
            }
        }
    }


    /**
     * A session is the recorded user behaviour(articles visited withinn a certain time frame).
     * A session ends when the user is inactive for a certain time window.
     */

    class Session{

        Domain domain;
        ArrayList<Visit> visits = new ArrayList<Visit>();
        long lastRecommendation = 0;        // Set by the recommender
        long timeLastVisit = 0;

        public Session(Domain domain){
            this.domain = domain;
        }

        public void addVisit(Visit visit){
            visits.add(visit);
            timeLastVisit = visit.time;
        }

        public void printSession(){
            System.out.println("Session at " + domain.domainID);
            for(Visit v : visits){
                System.out.println(" Article visited: " + v.article.itemID + "\t" + new Date(v.time) + "\t" + v.article.title);
            }
            System.out.println();

        }

    }

    class Visit{
        final Article article;
        final long time;
        //TODO: Add importance(calculated by visiting time)

        public Visit(Article article, Long time){
            this.article = article;
            this.time = time;
        }

    }

    class UserStatistics{
        int nb_recommendationRequests = 0;
        int nb_clicks = 0;
        int nb_sucssessfullRecs = 0;

        public UserStatistics(){}
    }



}