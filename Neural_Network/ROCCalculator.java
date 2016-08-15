///////////////////////////////////////////////////////////////////////////////
//  Title:                         ROCCalculator.java                        //
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

// This class will do the calculation of ROC points
class ROCCalculator {
    static void getPoints(NeuralNetwork network) {
        // Sort Data by confidence from high to low
        List<Datum> data = network.myData;
        Collections.sort(data, new Comparator<Datum>() {
            public int compare(Datum datum1, Datum datum2) {
                return datum2.comparesTo(datum1);
            }
        });

        // Calculate ROC points
        int count0 = 0, count1 = 0;
        int total0 = network.myClass0.size();
        int total1 = network.myClass1.size();
        for (int i = 0; i < data.size() - 1; ++i) {
            if (data.get(i).myClass == 0) {
                count0++;
            }
            else {
                count1++;
            }

            // A threshold is found
            if (data.get(i).myClass != data.get(i + 1).myClass) {
                System.out.print((double) count1 / (double) total1);
                System.out.print(' ');
                System.out.println((double) count0 / (double) total0);
            }
        }

        System.out.println("1.0 1.0");
    }
}
