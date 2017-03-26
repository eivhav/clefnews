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

        Datastore datastore = new Datastore();
        String filepath = "/home/havikbot/Documents/";
        int[] files = {1,1};
        String prefix = "nr2016-02-";

        ArrayList<Object> datastream = new ArrayList<Object>();
        for(int i = files[0]; i <= files[1]; i++){
            String fileName = "";
            if(i < 10){ fileName = prefix + "0" + i; }
            else { fileName = prefix  + i; }

            ArrayList<Object> singleFileStream;
            try{
                FileInputStream fileIn = new FileInputStream(filepath+fileName+".ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                singleFileStream = (ArrayList<Object>) in.readObject();
                datastream.addAll(singleFileStream);
                in.close();
                fileIn.close();
            }
            catch(IOException ioE){
                System.out.println("Could not find .ser file. Reading from .log file at"+filepath+fileName+".log");
                singleFileStream = datastore.parseFile(filepath, fileName+".log");
                datastream.addAll(singleFileStream);
                try {
                    FileOutputStream fileOut = new FileOutputStream(filepath+fileName+".ser");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(singleFileStream);
                    out.close();
                    fileOut.close();
                    System.out.println("Serialized data is saved in "+filepath+fileName+".ser");
                }
                catch(IOException ioE2) {
                    ioE2.printStackTrace();
                    System.err.println("Could not save .ser file");
                }
            }
            catch(ClassNotFoundException c){
                System.err.println("ArrayList<Object> class not found when loading .ser file");
            }
        }

        datastore.dataStream = datastream;
        System.out.println("DataStream loaded. Size:" + datastream.size());

    }





}
