package clef.newsreel;

import clef.newsreel.Datastore.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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


    public HashMap<Long, CollaborativeProfile> buildCollaborativeProfiles(int nbArticlesThreshold,
                                                                          int nbSessionsThreshold, int nbThreads){

        HashMap<Long, CollaborativeProfile> profiles = new HashMap<Long, CollaborativeProfile>();
        ArrayList<User> users = datastore.getUsersAboveThreshold(nbArticlesThreshold, nbSessionsThreshold);

        System.out.println("Running buildCollaborativeProfiles. Size:" + users.size());

        ArrayList<ArrayList<User>> usersForThreds = new ArrayList<ArrayList<User>>();
        for(int i = 0; i < nbThreads; i++){
            usersForThreds.add(new ArrayList<User>());
        }
        for(int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            u.articlesVisitedAllDomians = u.getAllArticles();
            usersForThreds.get(i % nbThreads).add(u);
        }
        ArrayList<CFsimThread> threads = new ArrayList<CFsimThread>();
        for(int i = 0; i < nbThreads; i++){
            threads.add(new CFsimThread(profiles, usersForThreds.get(i), users, i, nbThreads));
            threads.get(i).start();
        }

        return profiles;
    }




    class CFsimThread extends Thread{

        HashMap<Long, CollaborativeProfile> profiles;
        ArrayList<User> allUsers;
        ArrayList<User> threadUsers;
        int threadNo;
        int nbThreads;

        public CFsimThread(HashMap<Long, CollaborativeProfile> profiles, ArrayList<User> threadUsers,
                           ArrayList<User> allUsers, int no, int nbThreads){
            this.profiles = profiles;
            this.allUsers = allUsers;
            this.threadUsers = threadUsers;
            this.threadNo = no;
            this.nbThreads = nbThreads;
        }

        public void run(){
            int count = 0;
            for(User u : threadUsers) {
                count++;
                if (count % 100 == 0) {
                    System.out.println("Thread " + threadNo + " : " + count*nbThreads );
                }
                CollaborativeProfile userProfile = new CollaborativeProfile(u.userID);
                for (User u2 : allUsers) {
                    if (u != u2) {
                        userProfile.addUserSim(u2.userID, calculateCosineDistance(u, u2));
                    }
                }
                profiles.put(u.userID, userProfile);

            }
        }

        public double calculateCosineDistance(User u1, User u2){
            HashMap<Long, User.Visit> u1_articles = new  HashMap<Long, User.Visit>(u1.articlesVisitedAllDomians);
            HashMap<Long, User.Visit> u2_articles = new  HashMap<Long, User.Visit>(u2.articlesVisitedAllDomians);
            HashSet<Long> crossProduct = new HashSet<Long>(u1_articles.keySet());
            crossProduct.retainAll(u2_articles.keySet());

            if(crossProduct.size() == 0){ return 0;}

            double cosineValue = crossProduct.size();
            return cosineValue / (Math.sqrt((double)u1.articlesVisitedAllDomians.size()) +
                    Math.sqrt((double)u2.articlesVisitedAllDomians.size()));


        }


    }







}
