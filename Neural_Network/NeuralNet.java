///////////////////////////////////////////////////////////////////////////////
//  Title:                         NaiveBayes.java                           //
//  Course:                        CS 760                                    //
//  Assignment:                    #5                                        //
//  Instructor:                    David Page                                //
//                                                                           //
//  Author:                        Shixuan Fan                               //
//  CS login:                      shixuan                                   //
//  Email:                         shixuan.fan@wisc.edu                      //
///////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.*;

/**
 * This class is used for training the neural network based on the input .arff
 * file. We are using 1-layer neural network, using back propagation and 10-fold
 * cross-validation for training. The result will be binary.
 * 
 * @note There will be one bias unit. The initialization of weight is 0.1.
 **/ 
class NeuralNet {
    public static void main(String args[]) {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: neuralnet <trainfile> <num_folds> <learning_rate> <num_epochs>\n");
            System.exit(1);
        }

        String trainfile = args[0];
        int numFolds = Integer.parseInt(args[1]);
        double learningRate = Double.parseDouble(args[2]);
        int numEpochs = Integer.parseInt(args[3]);

        NeuralNetwork network = new NeuralNetwork(trainfile);
        network.partition(numFolds);
        for (int i = 0; i < numFolds; ++i) {
            network.train(i, learningRate, numEpochs);
        }
        if (args.length == 4) {
            network.print();
        }
        else if (args[4].equals("a")) {
            AccuracyCalculator.accuracy(network);
        }
        else if (args[4].equals("r")) {
            ROCCalculator.getPoints(network);
        }
    }
}
