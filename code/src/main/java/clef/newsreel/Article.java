package clef.newsreel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import clef.newsreel.Datastore.Domain;

/**
 * Created by havikbot on 23.03.17.
 */

public class Article{

    public Long itemID;
    public Domain domain;
    public Long created_date;
    public String title = "";
    public String text_content = "";

    public ArrayList<Integer> keywords = new ArrayList<Integer>();
    public HashMap<Long, User> user_visited = new HashMap<Long, User>();

    public boolean recommendable = false;

    public Article(Long itemID){
        this.itemID = itemID;
    }


    public void print(boolean includeTitleAndText){
        System.out.println("itemID:" + itemID + " domainID;" + domain.domainID + " time:" + created_date + " rec:" +recommendable);
        if(includeTitleAndText) {
            System.out.println("Title: " + title);
            System.out.println("Text: " + text_content);
        }
        System.out.println();
    }

    public void updateInfo(Domain domain, long time, String title, String text, boolean rec){
        this.domain = domain;
        this.created_date = time;
        this.title = title;
        this.text_content = text;
        this.recommendable = rec;

    }


}