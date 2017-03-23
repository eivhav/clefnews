package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Set;


/**
 * Created by gram on 23.03.17.
 */
public class Main {



    public static void main(String[] args){

        Datastore datastore = new Datastore();
        String filename = "/home/havikbot/Documents/nr2016-02-01.log";
        try{
            int total_count = 0;
            int registered_articles = 0;
            BufferedReader br = new BufferedReader(new FileReader(filename));
            for(String line; (line = br.readLine()) != null; ) {
               total_count++;

               if(line.substring(0,11).equals("item_update")){
                   datastore.register_article(line.substring(12, line.length()-24));
                   registered_articles++;
               }

               if(total_count % 100000 == 0){
                   System.out.println(total_count + ": "+ registered_articles);
                   System.out.println(datastore.articles.size());
                   System.out.println();

               }


            }
            // line is not visible here.

        }
        catch (Exception e){

        }

        //datastore.register_article







    }




}
