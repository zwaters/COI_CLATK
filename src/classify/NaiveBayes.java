package classify;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import load._default_data;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by zak on 5/10/15.
 */
public class NaiveBayes {

    cc.mallet.classify.NaiveBayes c;
    String fp;

    //No model, train one
    public NaiveBayes() throws IOException, ParseException {
        //NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        /*
         * Load generic dataset
         */
        _default_data d = new _default_data();

        InstanceList data = d.getInstanceList();

        this.c = (cc.mallet.classify.NaiveBayes) train(data);

        //cc.mallet.classify.NaiveBayes cl = trainer.train(data);

    }

    //With trained model
    public NaiveBayes(String model_fp) throws IOException, ClassNotFoundException{
        this.fp = model_fp;

        this.c = (cc.mallet.classify.NaiveBayes) load_model(new File(model_fp));
    }

    public Classifier getClassifier() {
        return c;
    }



    private Classifier train(InstanceList data)  {
        NaiveBayesTrainer trainer = new NaiveBayesTrainer();

        return trainer.train(data);
    }

    //Train on user classifications
    public void trainOnFeedback(InstanceList data) throws IOException, ClassNotFoundException, Exception {
        NaiveBayesTrainer trainer = new NaiveBayesTrainer(this.c);

        trainer.train(data);

        save_model(new File(this.fp));
    }

    //public

    public ArrayList<String[]> predict(InstanceList data) throws Exception {
        if (this.c == null){
            throw new Exception("No model loaded or found.");
        }

        ArrayList<Classification> cls = c.classify(data);

        /*for (Classification cl: cls) {
            System.out.println(cl.getInstance().getData() + " predicted as: " + cl.getLabeling().getBestLabel());
        }*/

        ArrayList<String[]> result_wrapper = new ArrayList<>();

        for (Classification cl: cls) {
            String[] id_class = new String[2];

            id_class[0] = cl.getInstance().getName().toString();
            id_class[1] = cl.getLabeling().getBestLabel().toString();

            result_wrapper.add(id_class);
        }

        return result_wrapper;
    }

    public void predict_single(String data) throws Exception {
        if (this.c == null){
            throw new Exception("No model loaded or found.");
        }

        Classification cl = c.classify(data);

        System.out.println(cl.getInstance().getData() + " predicted as: " + cl.getLabeling().getBestLabel());
    }

    public void predict_strs(ArrayList<String> data) throws Exception {
        if (this.c == null) {
            throw new Exception("No model loaded or found");
        }

        for (String d : data) {
            Classification cl = c.classify(d);

            System.out.println(cl.getInstance().getData() + " predicted as: " + cl.getLabeling().getBestLabel());
        }
    }

    private Classifier load_model(File saved_model) throws IOException, ClassNotFoundException {
        Classifier tmp_c;
        ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(saved_model));
        tmp_c = (Classifier) ois.readObject();
        ois.close();

        return tmp_c;
    }

    public void save_model(File of) throws Exception, IOException {
        if (this.c == null) {
            throw new Exception("No model found.");
        }

        ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(of));
        oos.writeObject(this.c);
        oos.close();
    }
    public void update_model() {
        //TODO
        //here is where we will update the model after feedback
    }

}
