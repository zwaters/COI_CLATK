package load;

import cc.mallet.types.InstanceList;
import features.FeatureExtractor;
import org.json.simple.JSONArray;
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
public class _default_data {
    private ArrayList<ArrayList<String>> data;

    public _default_data() throws IOException, ParseException {
        File f = new File("/Users/zak/Desktop/coiSequence.txt");
        JSONParser parser = new JSONParser();
        JSONArray post_list = (JSONArray) parser.parse(new FileReader(f));

        this.data = new ArrayList<>();


        for (Object o : post_list) {
            ArrayList<String> data_label = new ArrayList<>();
            JSONObject p = (JSONObject) o;

            data_label.add((String) p.get("postBody"));
            data_label.add(getLabel((long) p.get("phaseId")));

            this.data.add(data_label);
        }
    }

    public InstanceList getInstanceList() {
        FeatureExtractor fe = new FeatureExtractor(this.data, true);

        return fe.extract();
    }

    private String getLabel(long id) {

        switch((int)id) {
            case 1:
                return "Triggering";
                //break;
            case 2:
                return "Exploration";
                //break;
            case 3:
                return "Integration";
                //break;
            case 4:
                return "Resolution";
                //break;
            default:
                return "Other";
                //break;
        }

    }

}