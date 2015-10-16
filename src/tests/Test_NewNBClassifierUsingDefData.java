package tests;

import db.psql_InputHandler;
import db.psql_OutputHandler;
import features.FeatureExtractor;
import load.userdatafromJSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zak on 5/10/15.
 */
public class Test_NewNBClassifierUsingDefData {

    public static void main(String[] args) throws Exception, IOException, ParseException {
        classify.NaiveBayes nb = new classify.NaiveBayes();

        System.out.println("Create new model and predict on it...");
        nb.predict_single("How are we all today? Do you think we could run through yesterday's topic again?");
        System.out.println("Saving model..");
        nb.save_model(new File("./NaiveBayes_t1.model"));

        System.out.println("Loading model and predicting with it...");
        classify.NaiveBayes nb2 = new classify.NaiveBayes("./NaiveBayes_t1.model");
        nb2.predict_single("I like this course, I think more people should take it...");

        System.out.println("Loading model and predicting forum data with it...");
        classify.NaiveBayes nb3 = new classify.NaiveBayes("./NaiveBayes_t1.model");
        FeatureExtractor fe = new userdatafromJSON().getExtractor();
        ArrayList<String[]> results = nb3.predict(fe.extract());

        for (String[] r : results) {
            System.out.println("Entry ID: " + r[0] + ". Predicted as: " + r[1]);
        }

        new psql_OutputHandler().addall(results);


    }

}
