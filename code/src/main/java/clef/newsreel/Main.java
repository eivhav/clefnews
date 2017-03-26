package clef.newsreel;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;

import java.util.ArrayList;
import java.util.Set;


/**
 * Created by gram on 23.03.17.
 */
public class Main {



    public static void main(String[] args){

        String filePath = "/home/havikbot/Documents/";
        int[] fileNumbers = {1,1};

        DataLoader dataloader = new DataLoader();
        ArrayList<Object> DataLoader = dataloader.loadDataStream(filePath, fileNumbers);



    }





}
