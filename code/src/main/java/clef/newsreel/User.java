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
    public HashMap<Long, ArrayList<Visit>> articlesVisited = new HashMap<Long, ArrayList<Visit>>();  //<DomainID, List<Visit>>
    public HashMap<Long, ArrayList<Session>> sessions = new HashMap<Long, ArrayList<Session>>();    //<DomainID, List<Session>>
    public HashMap<Long, UserStatistics> statistics = new HashMap<Long, UserStatistics>();




    public User(Long userID){
        this.userID = userID;
        //Should we perhaps have som more info about our users?

    }

    public void registerReqEventForUser(Domain domain, Article article, long time){

        long sessionTimeLimit = 30*60000L;      // 30 minutes
        Visit visit = new Visit(article, time);

        // Pull the latest session if inside time window, else create new session
        if(!sessions.containsKey(domain.domainID)){ sessions.put(domain.domainID, new ArrayList<Session>()); }
        ArrayList<Session> sessionList = sessions.get(domain.domainID);
        if(sessionList.size() == 0 || time - sessionTimeLimit > sessionList.get(sessionList.size()-1).timeLastVisit){
            sessionList.add(new Session(domain));
        }
        sessionList.get(sessionList.size()-1).addVisit(visit);

        // Add visit to the articleVisited list
        if(!articlesVisited.containsKey(domain.domainID)){ articlesVisited.put(domain.domainID, new ArrayList<Visit>()); }
        articlesVisited.get(domain.domainID).add(visit);

        // Add to statistic for the current user at the current domain
        if(!statistics.containsKey(domain.domainID)){ statistics.put(domain.domainID, new UserStatistics()); }
        statistics.get(domain.domainID).nb_recomendationRequest++;

    }

    public void registerClickEvent(Domain domain){
        //TODO

    }



    /**
     * A session is the recorded user behaviour(articles visited withinn a certain time frame).
     * A session ends when the user is inactive for a certain time window.
     *
     */

    class Session{

        Domain domain;
        ArrayList<Visit> visits = new ArrayList<Visit>();
        long lastRecommendation = 0;        // Set by the recomender
        long timeLastVisit = 0;

        public Session(Domain domain){
            this.domain = domain;
        }

        public void addVisit(Visit visit){
            visits.add(visit);
            timeLastVisit = visit.time;
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
        int nb_recomendationRequest = 0;
        int nb_clicks = 0;
        int nb_sucsesssfullRecs = 0;

        public UserStatistics(){}
    }



}