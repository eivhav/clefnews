package clef.newsreel;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by havikbot on 05.04.17.
 */
public class CollaborativeProfile implements Serializable {

    long userID;
    TreeSet<UserSim> similarUsers;

    public CollaborativeProfile(long userID){
        this.userID = userID;
        this.similarUsers = new TreeSet<UserSim>(new Comparator<UserSim>(){
            public int compare(final UserSim u1,  UserSim u2){
                return u1.sim < u2.sim ? -1 : u1.sim == u2.sim ? 0 : 1;
            }
        });
    }

    public void addUserSim(long userID, double sim){
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


