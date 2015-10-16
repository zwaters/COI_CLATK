package tests;

import cc.mallet.classify.Classifier;
import db.psql_InputHandler;
import features.FeatureExtractor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

/**
 * Created by zak on 15/10/15.
 */

//TODO: ADD MAIN CLASS TO RETRAIN CLASSIFIER.
public class Test_LoadNBandTrainOnFB {
    public static void main(String[] args) throws Exception{
        //Testing User Classification training
        System.out.println("loading model.");
        classify.NaiveBayes nbt = new classify.NaiveBayes("./NaiveBayes_t1.model");
        String course_code = "IFN614";
        String platform = "Forum";
        File f = new File("./config.json");

        JSONObject configs = (JSONObject) new JSONParser().parse(new FileReader(f));
        configs.put("course_code", course_code);
        configs.put("platform", platform);

        System.out.println("psql_ih loading.");
        psql_InputHandler psql = new psql_InputHandler(configs); //psql handler
        System.out.println("psql_ih done");
        FeatureExtractor fet = psql.getExtractor(true);
        System.out.println("Got extractor");

        System.out.println("Training now.");
        nbt.trainOnFeedback(fet.extract(nbt.getClassifier()));
        System.out.println("fin");
    }
}
