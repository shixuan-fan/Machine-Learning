///////////////////////////////////////////////////////////////////////////////
//  Title:                         Bayes.java                                //
//  Including:                     NaiveBayes.java                           //
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
 * class: Bayes
 * Main class, read arguments from command line
 */
class Bayes {
    public static void main(String args[]) {
        // Check if argc is correct
        if (args.length != 3) {
            System.out.println("Usage: bayes <train-set-file> <test-set-file> <n|t>");
            System.exit(1);
        }
        // According to the last parameter, use different function for the data
        if (args[2].equals("n")) {
            NaiveBayes naive = new NaiveBayes();
            naive.train(args[0]);
            naive.test(args[1]);
        }
        else if (args[2].equals("t")) {
            TAN tan = new TAN();
            tan.train(args[0]);
            tan.test(args[1]);
        }
        else {
            System.out.println("Error: the algorithm paramter should be n or t");
            System.exit(1);
        }
    }
}
