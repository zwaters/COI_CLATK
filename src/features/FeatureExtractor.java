package features;

import cc.mallet.classify.Classifier;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Labeling;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by zak on 27/09/15.
 */

/*
 *FEATURES:
 *
 * - Bag o' Words
 * - Punctuation
 * - Entities
 * - Question count
 * - "I" is used alot - a sign of not much cogpres?
 */
public class FeatureExtractor {

    private StanfordCoreNLP pipeline;
    private Properties props;
    private ArrayList<String> data;
    private String[] labels;
    private String[] user;
    private boolean process_labels;
    //private int m

    public FeatureExtractor(ArrayList<ArrayList<String>> data, boolean process_labels){

        //TODO: Implement Augmentable feature vectors to allow for stanford features
        //this.props = new Properties();
        //this.props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");
        //this.pipeline = new StanfordCoreNLP(this.props);
        this.process_labels = process_labels;

        this.data = new ArrayList<>();

        if (process_labels) {
            this.labels = new String[data.size()];

            for (int i = 0; i < data.size(); i++) {
                this.data.add(data.get(i).get(0));

                if (data.get(i).get(1) != null) {
                    this.labels[i] = data.get(i).get(1);
                } else {
                    this.labels[i] = null;
                }
            }
        } else {
            this.user = new String[data.size()];

            assert(data.size() == this.user.length);

            for (int i = 0; i < data.size(); i++) {

                ArrayList<String> entry = data.get(i);

                this.data.add(entry.get(0));
                this.user[i] = entry.get(2);
            }
        }
    }

    /*
     * Configures/Builds pipe, extracts features and adds them to their feature vector
     * alphabets, finally converting data into instances to be read by a classifier
     *
     * Returns: InstanceList instances - instancelist of instances to be predicted/trained with
     * classifier.
     */
    public InstanceList extract() {
        InstanceList dataset = new InstanceList(buildPipe(this.process_labels));

        if (!this.process_labels) {
            for (int i = 0; i < this.data.size(); i++) {
                //Instance i =
                dataset.addThruPipe(new Instance(this.data.get(i), null, this.user[i], null));
            }
        }
        else {
            for (int i = 0; i < this.data.size(); i++) {
                dataset.addThruPipe(new Instance(this.data.get(i), labels[i], null,null));
            }
        }

        return dataset;
    }

    public InstanceList extract(Classifier c) {
        InstanceList dataset = new InstanceList(c.getInstancePipe());

        if (!this.process_labels) {
            for (int i = 0; i < this.data.size(); i++) {
                //Instance i =
                dataset.addThruPipe(new Instance(this.data.get(i), null, this.user[i], null));
            }
        }
        else {
            for (int i = 0; i < this.data.size(); i++) {
                dataset.addThruPipe(new Instance(this.data.get(i), labels[i], null,null));
            }
        }

        return dataset;
    }

    /*
     * Returns the data read by this object
     */
    public ArrayList<String> getData() {
        return this.data;
    }

    /*
     * Builds the pipe to convert data instances into a readable format required by mallet
     *
     * Returns: the pipe used to parse instances
     *
     * Parameter: boolean - whether training or not
     *
     */
    private static Pipe buildPipe(boolean train) {
        Pattern tokenpattern = Pattern.compile("[\\p{L}\\p{N}_]+");
        Pipe[] pipes;


        if (train) {
            pipes = new Pipe[]{
                    new Input2CharSequence("UTF-8"),
                    new CharSequence2TokenSequence(tokenpattern),
                    //new TokenSequenceRemoveStopwords(),
                    new TokenSequence2FeatureSequence(),
                    new Target2Label(),
                    new FeatureSequence2FeatureVector()
            };
        } else {
            pipes = new Pipe[]{
                    new Input2CharSequence("UTF-8"),
                    new CharSequence2TokenSequence(tokenpattern),
                    //new TokenSequenceRemoveStopwords(),
                    new TokenSequence2FeatureSequence(),
                    //new Target2Label(),
                    new FeatureSequence2FeatureVector()
            };
        }

        SerialPipes p = new SerialPipes(pipes);

        return p;
    }
}


//********************************* CODE GRAVEYARD ********************************//
    /*public InstanceList extract(Classifier c) {
        //We need to process the data using the same
        //pipes (alphabets) as the classifier was trained on..
        Pipe p = buildPipe(this.process_labels); //c.getInstancePipe();
        p.setTargetProcessing(false);
        //p.setTargetProcessing(false);


        InstanceList dataset = new InstanceList(p);
        //dataset.;
        //Labeling l = dataset.

        for (String s : this.data) {

            Instance i = p.instanceFrom(new Instance(s, "",null,null));

            System.out.println(i.getTargetAlphabet() == dataset.getTargetAlphabet());

            dataset.add(i);
        }

        return dataset;
    }*/

/*
    private static ArrayList<ArrayList<String>> getAllTerms(Annotation a, Annotation a1) {

        return (ArrayList<ArrayList<String>>) null;
    }
*/