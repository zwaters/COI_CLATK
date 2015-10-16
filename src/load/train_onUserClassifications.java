package load;

import classify.NaiveBayes;
import db.psql_InputHandler;
import features.FeatureExtractor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

/**
 * Created by zak on 15/10/15.
 */
public class train_onUserClassifications {


    public static void main(String[] args) throws Exception {
        File f = new File(args[0]);
        JSONObject configs = (JSONObject) new JSONParser().parse(new FileReader(f));
        configs.put("course_code", args[1]);
        configs.put("platform", args[2]);

        //Get Classifier model
        String model = (String) configs.get("model_file");

        NaiveBayes nb;
        if (model != null) {
            nb = new NaiveBayes(model);
        } else {
            throw new Exception("train_onUserClassifications.java (line 31): A trained Classification model" +
                    " must be supplied in " + args[0]);
        }

        //Initialise Postgres Input Handler
        psql_InputHandler psql = new psql_InputHandler(configs);

        //Obtain feature extractor
        FeatureExtractor fe = psql.getExtractor(true);

        //Train on Reclassified Data
        nb.trainOnFeedback(fe.extract(nb.getClassifier()));


        /*System.out.println("loading model.");
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
        System.out.println("fin");*/
    }

}
