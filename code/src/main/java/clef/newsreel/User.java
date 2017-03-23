package clef.newsreel;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by havikbot on 23.03.17.
 */
public class User{

    public Long user_id;
    public HashMap<Date, Article> articles_clicked_on = new HashMap<Date, Article>();

    public User(Long user_id){
        this.user_id = user_id;
    }



}