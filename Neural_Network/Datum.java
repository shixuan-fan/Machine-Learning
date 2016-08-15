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

/**
 * This class contains the data array and the class for each input instance
 */
class Datum {
    public Datum(String[] datum, String[] classes) {
        myDatum = new double[datum.length - 1];
        myConfidence = 0;
        myFold = 0;
        myActualClass = datum[datum.length - 1];
        for (int i = 0; i < datum.length - 1; ++i) {
            myDatum[i] = Double.parseDouble(datum[i]);
        }

        if (myActualClass.equals(classes[0])) {
            myClass = 0;
        }
        else if (myActualClass.equals(classes[1])) {
            myClass = 1;
        }
        else {
            System.out.println("Error: unknown classes");
            System.exit(1);
        }
    }

    public int comparesTo(Datum datum) {
        if (myConfidence < datum.myConfidence) {
            return -1;
        }
        else if (myConfidence == datum.myConfidence) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public double[] myDatum;
    public int myClass;
    public String myActualClass;
    public String myPredictedClass;
    public int myFold;
    public double myConfidence;

}
