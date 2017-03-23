package clef.newsreel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by havikbot on 23.03.17.
 */

public class Article{

    public Long item_id;
    public Long domain_id;
    public Long created_date;
    public String title = "";
    public String text_content = "";

    public ArrayList<Integer> keywords = new ArrayList<Integer>();
    public HashMap<Long, User> users_cliked_on = new HashMap<Long, User>();

    public boolean recommendable = false;

    public Article(Long item_id, Long domain_id, Long time){
        this.item_id = item_id;
        this.domain_id = domain_id;
        this.created_date = time;
    }
}