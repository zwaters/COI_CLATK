package Seq_Classify;

import cc.mallet.fst.*;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.*;
//import cc.mallet.util.CommandOption.String;
//import cc.mallet.util.CommandOption.*;

import java.io.*;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by zak on 14/09/15.
 */
public class CRF_main {
    //Model arg - specify Seq_Classify model to use (default model if none supplied)
    private static final CommandOption.String model = new CommandOption.String(CRF.class,
            "CRF_MODEL", "FILENAME", false, "crfcoi.model", "specifify specific model, otherwise default is used", (String)null);

    private static final CommandOption.Boolean train = new CommandOption.Boolean(CRF.class,
            "train", "true|false", true, false, "train model: true or false", (String)null);

    private static final CommandOption.Boolean predict = new CommandOption.Boolean(CRF.class,
            "predict", "true|false", true, false, "predict labels: true or false", (String)null);

    private static final CommandOption.List commandOpts = new CommandOption.List("Training/Predicting/Updating Seq_Classify" +
            "via CLA Toolkit", new CommandOption[]{model, train, predict});




    //TODO: Not reading in an InstanceList with features already extracted..
    //We will be taking in (presumably) one post/message at a time, therefore:

    //TODO: 1. Obtain message 2. Extract features and build/load crf 3. Either predict or train, based on args.
    public static void main(java.lang.String[] args) throws IOException, ClassNotFoundException {

        /*
        **
        **
        **  We will be taking a single message (maybe a batch of messages, assume single atm
        **
        **  In order to train/predict we need to transform message by:
        *** 1. Extract features
        *** 2. Contain in a data structure suitable for crf
        *** 3. Send off for train/classification
        *
        *
        *
        *
        *   Problems with current approach
        **
        **
        **
        */

        boolean t = train.value;
        boolean pv = predict.value;
        String msg = "";

        int argnum = commandOpts.processOptions(args);

        if (argnum == args.length) {
            commandOpts.printUsage(true);
            throw new IllegalArgumentException("Missing argument(s): data file");
        } else {
            //data = new FileReader(new File(args[argnum]));
            msg = (String) args[argnum];
        }






















        FileReader data = null;
        InstanceList dataset = null;
        CRF crf = null;



        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(model.value));
        crf = (CRF) ois.readObject();
        ois.close();

        TransducerTrainer tt = new NoopTransducerTrainer(crf);
        //TransducerEvaluator eval = null;

        Object p = crf.getInputPipe();
        Iterator<Instance> inst = new LineGroupIterator(data, Pattern.compile("^\\s*$"), true);

        if (train.value) {
            //update Seq_Classify model
            ((Pipe)p).setTargetProcessing(true);
            dataset = createInstanceListFromData(inst,(Pipe)p);
        }

        else if (predict.value) {
            ((Pipe)p).setTargetProcessing(false);
            dataset = createInstanceListFromData(inst, (Pipe)p);
        }
    }


    private static InstanceList createInstanceListFromData(Iterator<Instance> inst, Pipe p) {
        InstanceList tmp = new InstanceList(p);

        while (inst.hasNext()) {
            tmp.addThruPipe(inst.next());
        }

        return tmp;
    }

    //private static boolean predict()

}
