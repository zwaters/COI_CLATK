package load;


import classify.NaiveBayes;
import db.psql_OutputHandler;
import db.psql_InputHandler;
import features.FeatureExtractor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zak on 10/10/15.
 */
public class from_clatk {



    public static void main(String[] args) throws Exception, IOException, ParseException, ClassNotFoundException {
        File f = new File(args[0]);
        JSONObject configs = (JSONObject) new JSONParser().parse(new FileReader(f));
        configs.put("course_code", args[1]);
        configs.put("platform", args[2]);


        //Create classifier
        String model = (String) configs.get("model_file");

        NaiveBayes nb;
        if (model != null) {
            nb = new NaiveBayes(model);
        } else {
            nb = new NaiveBayes();
        }

        //Check if we're pulling labelled data
        boolean labelled = false;

        try {
            if (args[3] == "-tl") {
                labelled = true;
            }
        } catch (Exception e) {
            System.out.println("Not processing lables.");
        }

/*        if (args[3] != null && args[3] == "-tl") {
            labelled = true;
        }*/

        psql_InputHandler psql = new psql_InputHandler(configs); //Postgres input handler

        FeatureExtractor fe = psql.getExtractor(labelled); //Extractor

        ArrayList<String[]> results = nb.predict(fe.extract()); //Get classifications

        for (String[] r : results) {
            System.out.println("Entry ID: " + r[0] + ". Predicted as: " + r[1]);
        }

        new psql_OutputHandler().addall(results);

    }



    public static void printError(Exception e) {
        System.err.println(e.getClass().getName()+": "+ e.toString());
        System.exit(0);
    }

}
