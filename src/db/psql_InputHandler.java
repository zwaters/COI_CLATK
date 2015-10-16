package db;

import cc.mallet.classify.Classifier;
import features.FeatureExtractor;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by zak on 10/10/15.
 */
public class psql_InputHandler {

    Connection c;
    Statement stmt;
    String sql;
    String cc;
    String pf;

    public psql_InputHandler(JSONObject configs) {

        String platform = (String) configs.get("platform");
        String course_code = (String) configs.get("course_code");
        String db_name = (String) configs.get("db_name");
        String server = (String) configs.get("server"); //localhost:5432

        this.cc = course_code;
        this.pf = platform;

        try {
            Class.forName("org.postgresql.Driver");

            this.c = DriverManager.getConnection("jdbc:postgresql://" + server + "/" + db_name);
            this.c.setAutoCommit(false);
            //this.stmt = c.createStatement();

            System.out.println("Opened database successfully");

            sql = "SELECT * FROM clatoolkit_learningrecord WHERE clatoolkit_learningrecord.platform='"+platform+"' AND " +
                    "clatoolkit_learningrecord.course_code='"+course_code+"';";


        } catch (Exception e) {
            //System.out.println("In __constructor");
            printError(e, "constructor");
        }
    }

    private ArrayList<ArrayList<String>> g_labelledData() {
        ArrayList<ArrayList<String>> data_formatd = new ArrayList<>();

        String msg_sql = "SELECT * FROM clatoolkit_learningrecord " +
                        "WHERE clatoolkit_learningrecord.platform='"+this.pf+"' " +
                        "AND clatoolkit_learningrecord.course_code='"+this.cc+"' " +
                        "AND clatoolkit_learningrecord.id IN " +
                            "(SELECT xapistatement_id " +
                                "FROM clatoolkit_classification " +
                                "WHERE clatoolkit_classification.id IN " +
                                    "(SELECT classification_id " +
                                        "FROM clatoolkit_userclassification " +
                                        "WHERE clatoolkit_userclassification.trained='False')" +
                            ");";

        String label_sql = "SELECT userreclassification FROM clatoolkit_userclassification ";

        ArrayList<String> ids = new ArrayList<>();

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(msg_sql);

            while (rs.next()) {
                int id = Integer.parseInt(rs.getString(1));
                //System.out.println("Private FUNCTION g_labelledData(): id=" + id);

                ArrayList<String> i_data = new ArrayList<>();
                String text = rs.getString("message");
                //System.out.println("Private FUNCTION g_labelledData(): msg=" + text);

                i_data.add(text);
                i_data.add(Integer.toString(id)); //We put id in 2nd element of arraylist for now
                //When labels are extracted, we will add label to 2nd element, which moves
                //id to the correct 3rd element.

                data_formatd.add(i_data);
            }

            stmt.close();

            //Get labels for user reclassification data
            for (ArrayList<String> i_data : data_formatd) {
                String id = i_data.get(1);
                ids.add(id);

                String labelsql_where = "WHERE classification_id = " +
                        "(SELECT id FROM clatoolkit_classification " +
                        "WHERE xapistatement_id='"+id+"');";

                //System.out.println()

                stmt = c.createStatement();
                ResultSet rs_label = stmt.executeQuery(label_sql+labelsql_where);

                if (rs_label.next()) {
                    String label = rs_label.getString(1);
                    i_data.add(2, label);

                    assert i_data.get(2) == id;
                } else {
                    throw new Exception("Could not find a label for classification with ID: "+id);
                }

            }

        } catch (Exception e) {
            printError(e, "g_labelledData()");
        }

        //Get classification_ids to update UserClassification table ids is xapi_id
        ArrayList<String> c_ids = new ArrayList<>();
        for (String id : ids) {
            String cid_sql = "SELECT id FROM clatoolkit_classification WHERE xapistatement_id='"+id+"';";
            try {
                stmt = c.createStatement();

                ResultSet rs_cid = stmt.executeQuery(cid_sql);

                if (rs_cid.next()) {
                    c_ids.add(rs_cid.getString(1));
                } else {
                    throw new Exception("STATEMENT NOT CLASSIFIED");
                }

            } catch (Exception e) {
                printError(e, "g_labelledData()");
            }

        }

        new psql_OutputHandler().update_UC(c_ids);

        return data_formatd;
    }

    public ArrayList<ArrayList<String>> g_unlabelledData() {
        ArrayList<ArrayList<String>> data_formatd = new ArrayList<>();
        ArrayList<Integer> id_list = get_AllPredictedIds();

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(this.sql);
            System.out.println("l52");

            while (rs.next()) {
                int id = Integer.parseInt(rs.getString(1));
                System.out.println("id: "+id);

                if (!id_list.contains(id)) {
                    ArrayList<String> i_data = new ArrayList<>();
                    String text = rs.getString("message");
                    System.out.println("msg: "+text);

                    i_data.add(text);
                    i_data.add(null);
                    i_data.add(Integer.toString(id));

                    data_formatd.add(i_data);
                }
            }

            stmt.close();
        } catch (Exception e) {
            //System.out.println("In g_AllUnlabelledData");
            printError(e, "g_ALB");
        } finally {
            //stmt.close();
        }

        return data_formatd;
    }

    //private ArrayList<>

    private ArrayList<Integer> get_AllPredictedIds() {
        String classifications_sql = "SELECT xapistatement_id FROM clatoolkit_classification;";
        ArrayList<Integer> id_holder = new ArrayList<>();
        System.out.println("l80");

        try{
            System.out.println("l83");
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(classifications_sql);
            System.out.println("l85");

            while(rs.next()) {
                String id = rs.getString(1);
                System.out.println("p_id: "+id);

                id_holder.add(Integer.parseInt(id));
            }

            stmt.close();
        } catch (Exception e) {
            if (e.getClass().toString() == "java.lang.NullPointerException") {
                return id_holder;
            }
            //System.out.println("In get_AllPredictedIds");
            printError(e, "g_apids");
        }

        return id_holder;
    }

    public FeatureExtractor getExtractor(boolean processLabels) {

        if(processLabels) {
            FeatureExtractor fe = new FeatureExtractor(this.g_labelledData(), processLabels);
            return fe;
        } else {
            FeatureExtractor fe = new FeatureExtractor(this.g_unlabelledData(), processLabels);
            return fe;
        }
        //return fe;
    }


    public static void printError(Exception e, String caller) {
        System.err.println("psql_InputHandlerError in " + caller +": "+ e);
        //System.exit(0);
    }

}
