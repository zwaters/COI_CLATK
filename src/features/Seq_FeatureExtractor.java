package features;

import cc.mallet.types.InstanceList;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by zak on 14/09/15.
 */
public class Seq_FeatureExtractor {

    private StanfordCoreNLP pipeline;
    private Properties props;
    private ArrayList<String> data;
    //private int m

    public Seq_FeatureExtractor(ArrayList<String> thread){
        this.props = new Properties();
        this.props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");
        this.pipeline = new StanfordCoreNLP(this.props);

        this.data = thread;
    }

    public InstanceList extractAll() {
        //InstanceList dataset = new

        for (int i=0; i < data.size(); i++) {
            String msgString = data.get(i);

            int numReplies = (data.size()-1)-i;

            int isFirst = (i == 0) ? 1 : 0;
            int isLast = (i == data.size()-1) ? 1 : 0;
            //int commentDepth = getDepth(); //TODO: getDepth()

            boolean has_prev = (i-1 > 0);
            boolean has_next = (i+1 <= data.size()-1);

            Annotation annotation = new Annotation(msgString);
            pipeline.annotate(annotation);

            int wordCount = annotation.get(CoreAnnotations.TokensAnnotation.class).size();
            int sentenceCount = annotation.get(CoreAnnotations.SentencesAnnotation.class).size();
            int entities = annotation.get(CoreAnnotations.MentionsAnnotation.class).size();

            double tdf_cosinep = 0;
            double tdf_cosinen = 0;
            ArrayList<String> unigrams =  new ArrayList<String>();

            if (has_next | has_prev) {
                if (has_prev) {
                    String prev_post_string = data.get(i-1);

                    Annotation annotation1 = new Annotation(prev_post_string);
                    pipeline.annotate(annotation1);

                    ArrayList<ArrayList<String>> allTerms = getAllTerms(annotation, annotation1);
                    unigrams = allTerms.get(1);

                    //TFIDF similarity_calc = new TFIDF(allTerms); //TODO: TFIDF.class

                    //tdf_cosinep = similarity_calc.get_CosineSimilarity(); //TODO: TFIDF.get_Cosine

                }

                if (has_next) {
                    String next_post_string = data.get(i+1);

                    Annotation annotation1 = new Annotation(next_post_string);
                    pipeline.annotate(annotation1);

                    ArrayList<ArrayList<String>> allTerms = getAllTerms(annotation, annotation1);

                    unigrams = allTerms.get(1);

                    //TFIDF similarity_calc = new TFIDF(allTerms); //TODO: TFIDF.class

                   // tdf_cosinen = similarity_calc.get_CosineSimilarity(): //TODO: TFIDF.get_Cosine
                }
            }

            String space = " ";
            String feature_string = numReplies + space +
                    isFirst + space +
                    isLast + space +
                    //commentDepth + space +
                    tdf_cosinep + space +
                    tdf_cosinen + space +
                    wordCount + space +
                    sentenceCount + space +
                    entities;

            for (int j = 0; j < unigrams.size(); j++) {
                feature_string += space + unigrams.get(i);
            }

            feature_string += "\n";

        }


        return (InstanceList)null;
    }

    private static ArrayList<ArrayList<String>> getAllTerms(Annotation a, Annotation a1) {

        return (ArrayList<ArrayList<String>>) null;
    }



}
