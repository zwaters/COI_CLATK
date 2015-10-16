package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by zak on 7/10/15.
 */
public class psql_OutputHandler {

    Connection c;
    Statement stmt;

    public static void main(String[] args) {

    }

    public psql_OutputHandler() {

        try {
            Class.forName("org.postgresql.Driver");

            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/new_db");
            c.setAutoCommit(false);

            System.out.println("Opened database successfully");

        } catch (Exception e) { printError(e); }
    }

    private static void printError(Exception e) {
        System.err.println(e.getClass().getName()+": "+ e.getMessage());
        System.exit(0);
    }

    public void addall(ArrayList<String[]> preds) {

        try {
            stmt = c.createStatement();
            Date d = new Date();
            Timestamp ts = new Timestamp(d.getTime());

            for (String[] id_class : preds) {
                String sql = "INSERT INTO clatoolkit_classification (classification, classifier, xapistatement_id, created_at) "
                        + "VALUES ('"+id_class[1]+"', 'NaiveBayes_t1.model', '"+id_class[0]+"', '"+ts+"');";

                System.out.println(sql);

                stmt.executeUpdate(sql);

            }

            stmt.close();
            c.commit();
            c.close();

        } catch (Exception e) { printError(e); }
    }

    public void update_UC(ArrayList<String> ids) {
        try{
            stmt = c.createStatement();

            for (String id : ids) {
                String u_sql = "UPDATE clatoolkit_userclassification " +
                        "SET trained=True " +
                        "WHERE classification_id='" + id + "';";

                //System.out.println(u_sql);

                stmt.executeUpdate(u_sql);
            }

            stmt.close();
            c.commit();
            c.close();
            //System.out.println("Updated UserClassifications");

        } catch (Exception e) {
            printError(e);
        }

    }
}
