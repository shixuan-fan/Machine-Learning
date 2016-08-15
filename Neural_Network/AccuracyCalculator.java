///////////////////////////////////////////////////////////////////////////////
//  Title:                         NaiveBayes.java                           //
//  Course:                        CS 760                                    //
//  Assignment:                    #2                                        //
//  Instructor:                    David Page                                //
//                                                                           //
//  Author:                        Shixuan Fan                               //
//  CS login:                      shixuan                                   //
//  Email:                         shixuan.fan@wisc.edu                      //
///////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.*;

class AccuracyCalculator {

    public static void accuracy(NeuralNetwork network) {
        double correct = 0, total = network.myData.size();
        for (Datum datum : network.myData) {
            if (datum.myPredictedClass.equals(datum.myActualClass)) {
                ++correct;
            }
        }

        System.out.println(correct / total);
    }

}
