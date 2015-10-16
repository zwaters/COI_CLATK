package load;

import features.FeatureExtractor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zak on 27/09/15.
 */
public class userdatafromJSON {

    FeatureExtractor fe;

    public static void main(String[] args) throws IOException, ParseException {


    }

    public userdatafromJSON(/*String fp*/) throws IOException, ParseException {
        File f;
        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Integer> id_holder = new ArrayList<>();

        //TODO: Support labels

        // if (args[0] != null){
        //   f = new File(args[0]);
        //} else {
        f = new File("/Users/zak/Desktop/forums_clean.json");
        //}

        JSONParser parser = new JSONParser();
        JSONArray slist = (JSONArray) parser.parse(new FileReader(f));
        JSONArray xapis = new JSONArray();

        for (Object e : slist) {
            JSONObject o = (JSONObject) e;

            xapis.add(o.get("xapi"));
            id_holder.add((int) ((long) o.get("id")));

        }

        /*
         * Verb: d[#]['verb']['display']['en-US']
         * Content: d[#]['object']['definition']['name']['en-US']
         * User: d[#]['actor']['account']['name']
         * Tags (JSONArray): d[#]['context']['contextActivities']['other']
         * Tag-Content: d[#]...['other'][#]['definition']['name']['en-US']
         */

        int i = 0;
        ArrayList<Integer> ids = new ArrayList<>();

        for (Object p : xapis) {

            JSONObject post = (JSONObject) p;

            String user = (String) ((JSONObject) ((JSONObject) post.get("actor")).get("account")).get("name");
            String verb = (String) ((JSONObject) ((JSONObject) post.get("verb")).get("display")).get("en-US");
            String content = (String) ((JSONObject) ((JSONObject) ((JSONObject) post.get("object")).get("definition")).get("name")).get("en-US");
            JSONArray tags = (JSONArray) ((JSONObject) ((JSONObject) post.get("context")).get("contextActivities")).get("other");

            ArrayList<String> hashtags = new ArrayList<>();
            ArrayList<String> usertags = new ArrayList<>();

            System.out.println(user + " " + verb + " '" + html2text(content) + "'");

            if (verb != "shared") {
                data.add(content);
                users.add(user);
                ids.add(id_holder.get(i));
            }

            i++;
        }

        //Convert data into the format required: ArrayList<ArrayList<String>> a.
        //Where each arraylist entry (a.get(n)) represents the instance data (a.get(n).get(0))
        //and it's label if available (a.get(n).get(1)).

        //Since there are no labels yet, just process data
        //TODO: Support labels

        ArrayList<ArrayList<String>> data_formatd =  new ArrayList<>();

        for (int j = 0; j < data.size(); j++) {
            ArrayList<String> d_wrap = new ArrayList<>();

            d_wrap.add(data.get(j));
            d_wrap.add(null);
            d_wrap.add(ids.get(j).toString());

            data_formatd.add(d_wrap);
        }

        fe = new FeatureExtractor(data_formatd, false);
    }

    public FeatureExtractor getExtractor() throws Exception {
        if (fe != null) {
            return fe;
        } else {
            throw new Exception("Could not load data.");
        }

    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

}
