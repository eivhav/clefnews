package clef.newsreel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by havikbot on 23.03.17.
 */

public class Article{

    public Long itemID;
    public Long domainID;
    public Long created_date;
    public String title = "";
    public String text_content = "";

    public ArrayList<Integer> keywords = new ArrayList<Integer>();
    public HashMap<Long, User> users_cliked_on = new HashMap<Long, User>();

    public boolean recommendable = false;

    public Article(Long itemID, Long domainID, Long time){
        this.itemID = itemID;
        this.domainID = domainID;
        this.created_date = time;
    }


    public void print(boolean includeTitleAndText){
        System.out.println("itemID:" + itemID + " domainID;" + domainID + " time:" + created_date + " rec:" +recommendable);
        if(includeTitleAndText) {
            System.out.println("Title: " + title);
            System.out.println("Text: " + text_content);
        }
        System.out.println();
    }


}