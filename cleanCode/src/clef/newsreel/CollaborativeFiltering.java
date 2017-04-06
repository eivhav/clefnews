package clef.newsreel;

import java.io.Serializable;
import java.util.*;

/**
 * Created by havikbot on 06.04.17.
 */
public class CollaborativeFiltering {

    Datastore datastore;

    public CollaborativeFiltering(Datastore datastore){
        this.datastore = datastore;
    }




    public HashMap<Long, CollaborativeProfile> buildCollaborativeProfiles(
            int nbArticlesThreshold, int nbSessionsThreshold, int nbThreads) throws InterruptedException{

        HashMap<Long, CollaborativeProfile> profiles = new HashMap<Long, CollaborativeProfile>();
        ArrayList<User> users = datastore.getUsersAboveThreshold(nbArticlesThreshold, nbSessionsThreshold);

        System.out.println("Running buildCollaborativeProfiles. Size:" + users.size());

        ArrayList<ArrayList<User>> usersForThreds = new ArrayList<ArrayList<User>>();
        ArrayList<HashMap<Long, CollaborativeProfile>> profilesForThreads = new ArrayList<HashMap<Long, CollaborativeProfile>>();
        for(int i = 0; i < nbThreads; i++){
            usersForThreds.add(new ArrayList<User>());
            profilesForThreads.add(new HashMap<Long, CollaborativeProfile>());
        }
        for(int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            u.articlesVisitedAllDomians = u.getAllArticles();
            usersForThreds.get(i % nbThreads).add(u);
        }
        ArrayList<CFsimThread> threads = new ArrayList<CFsimThread>();
        for(int i = 0; i < nbThreads; i++){
            threads.add(new CFsimThread(profilesForThreads.get(i), usersForThreds.get(i), users, i, nbThreads));
            threads.get(i).start();
        }

        double lastPrecent = 0;
        while(users.size() > getComplteCount(threads)){
            Thread.sleep(150);

            double percentage = (100.0*(double)getComplteCount(threads) / (double)users.size());
            double timeLeft =  (1- percentage)/ ((percentage - lastPrecent) / 15.0);
            lastPrecent = percentage;
            System.out.println("Processing... "
                    + (int)percentage + "%, " + getComplteCount(threads) +"/"+ users.size()
                    + "  ETA: " + (int)(timeLeft / 60) + " min");

        }

        for(HashMap<Long, CollaborativeProfile> pfs : profilesForThreads){
            for(Long key : pfs.keySet()){
                profiles.put(key, pfs.get(key));
            }
        }

        System.out.println("CFprofiles build complete!");

        return profiles;
    }


    public int getComplteCount(ArrayList<CFsimThread> threads){
        int count = 0;
        for(CFsimThread t : threads){
            count += t.profiles.size();
        }
        return count;
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
                CollaborativeProfile userProfile = new CollaborativeProfile(u.userID);
                for (User u2 : allUsers) {
                    if (u != u2) {
                        userProfile.addUserSim(u2.userID, calculateCosineDistance(u, u2));
                    }
                }
                profiles.put(u.userID, userProfile);
            }
            System.out.println("Size for thread " + profiles.size());
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


    public void comparePrintTopKusers(HashMap<Long, CollaborativeProfile> profiles, int k){

        for(Long userID : profiles.keySet()){

            System.out.println("User " + Arrays.asList(datastore.users.get(userID).getAllArticles().keySet()));
            int count = 0;

            for(CollaborativeProfile.UserSim us : profiles.get(userID).similarUsers.descendingSet()){
                count++;
                System.out.println("  " + count + " : User:"+userID +": " +
                        Arrays.asList(datastore.users.get(us.userID).getAllArticles().keySet()));
                if(count == k){
                    break;
                }
            }
        }

    }





    class CollaborativeProfile implements Serializable {

        long userID;
        TreeSet<UserSim> similarUsers;

        public CollaborativeProfile(long userID) {
            this.userID = userID;
            this.similarUsers = new TreeSet<UserSim>(new Comparator<UserSim>() {
                public int compare(final UserSim u1, UserSim u2) {
                    return u1.sim < u2.sim ? -1 : u1.sim == u2.sim ? 0 : 1;
                }
            });
        }

        public void addUserSim(long userID, double sim) {
            similarUsers.add(new UserSim(userID, sim));
        }


        class UserSim implements Serializable {

            long userID = 0;
            double sim = 0;

            public UserSim(long userID, double sim) {
                this.userID = userID;
                this.sim = sim;
            }
        }

    }

}
