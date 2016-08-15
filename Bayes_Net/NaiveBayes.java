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
import java.text.*;

/**
 * class: NaiveBayes
 * Set up a naive bayes learning algorithm and make it trained through the 
 * training set. Then, use the test set to check the accuracy of prediction.
 */    
class NaiveBayes {
    public NaiveBayes() {
        y = new HashMap<String, Double>();
        x = new HashMap<String, List<HashMap<String, Double>>>();
        relation = new String();
    }

    /*
     * train
     * Use the input file for training, calculate P(x|y) and P(y)
     * @param filename: the input file for training
     */
    public void train(String filename) {
        int yTotal = 0;
        // Read the training file
        try {
            String line = null;
            FileReader inFile = new FileReader(filename);
            BufferedReader fileBuffer = new BufferedReader(inFile);
            // a list to store all the features
            List<HashMap<String, Double>> xList1 = new ArrayList<HashMap<String, Double>>();
            List<HashMap<String, Double>> xList2 = new ArrayList<HashMap<String, Double>>();
            // Read the header
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ ,{}\t\']+");
                if (tokens.length == 0) {
                    continue;
                }
                // Relation
                if (tokens[0].equals("@relation")) {
                    relation = tokens[1];
                    continue;
                }
                // Data, end of header
                if (tokens[0].equals("@data")) {
                    System.out.println();
                    break;
                }
                // Attribute, build hashmap
                if (tokens[0].equals("@attribute")) {
                    // Class label, update y map and y dict
                    if (tokens[1].equals("class")) {
                        y.put(tokens[2], 0.0);
                        y.put(tokens[3], 0.0);
                        x.put(tokens[2], new ArrayList<HashMap<String, Double>>(xList1));
                        x.put(tokens[3], new ArrayList<HashMap<String, Double>>(xList2));
                    }
                    else {
                        System.out.println(tokens[1] + " class");
                        HashMap<String, Double> temp = new HashMap<String, Double>();
                        for (int i = 2; i < tokens.length; ++i) {
                            temp.put(tokens[i], 0.0);
                        }
                        xList1.add(new HashMap<String, Double>(temp));
                        xList2.add(new HashMap<String, Double>(temp));
                    }
                }
                else {
                    System.out.println("Error: incorrect header format");
                    System.exit(1);
                }
            }
            // Read the data
            while ((line = fileBuffer.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ ,\t]+");
                if (tokens.length == 0) {
                    continue;
                }
                String label = tokens[tokens.length - 1];
                if (!y.containsKey(label)) {
                    System.out.println("Error: undefined label in data");
                    System.exit(1);
                }
                // Update y count
                y.put(label, y.get(label) + 1);
                ++yTotal;
                // Update x count
                List<HashMap<String, Double>> xList = x.get(label);
                for (int i = 0; i < xList.size(); ++i) {
                    HashMap<String, Double> xMap = xList.get(i);
                    String status = tokens[i];
                    xMap.put(status, xMap.get(status) + 1);
                }
            }
            fileBuffer.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Error: " + filename + " not exist");
            System.exit(1);
        }
        catch (IOException ex) {
            System.out.println("Error: unexpected exception encountered");
            System.exit(1);
        }
        // Calculate possibility from count, pseudocount = 1
        for (String yStr : y.keySet()) {
            double yNum = y.get(yStr);
            // Update x from count to possibility P(x|y)
            for (HashMap<String, Double> xMap : x.get(yStr)) {
                for (String xStr : xMap.keySet()) {
                    xMap.put(xStr, (xMap.get(xStr) + 1) / (yNum + xMap.size()));
                }
            }
            // Update y from count to possibility P(y)
            y.put(yStr, (yNum + 1) / (yTotal + y.size()));
        }
    }

    public void test(String filename) {
        try {
            FileReader inFile = new FileReader(filename);
            BufferedReader fileBuffer = new BufferedReader(inFile);
            String line = null;
            int xListIndex = 0;
            List<HashMap<String, Double>> xList = null;
            // Get one list of hashmaps from x for parameter check
            for (String temp : x.keySet()) {
                xList = x.get(temp);
                break;
            }
            // Read header, check parameters
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ ,{}\'\t]+");
                if (tokens.length == 0) {
                    continue;
                }
                // Relation
                if (tokens[0].equals("@relation")) {
                    if (!relation.equals(tokens[1])) {
                        System.out.println("Error: Unknown relation in testing set");
                        System.exit(1);
                    }
                    continue;
                }
                // Data
                if (tokens[0].equals("@data")) {
                    break;
                }
                // Attribute
                if (tokens[0].equals("@attribute")) {
                    // Label
                    if (tokens[1].equals("class")) {
                        if (!y.containsKey(tokens[2]) || !y.containsKey(tokens[3])) {
                            System.out.println("Error: unknown label in testing set");
                            System.exit(1);
                        }
                    }
                    // Feature
                    else {
                        HashMap<String, Double> xMap = xList.get(xListIndex++);
                        for (int i = 2; i < tokens.length; ++i) {
                            if (!xMap.containsKey(tokens[i])) {
                                System.out.println("Error: unknown feature in "
                                    + "testing set: " + tokens[i]);
                                System.exit(1);
                            }
                        }
                    }
                }
            }
            // Analyze data
            int count = 0;
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.length() == 0 || line.charAt(0) == '%') {
                    continue;
                }
                // Data
                String[] tokens = line.split("[ ,\t]+");
                if (tokens.length == 0) {
                    continue;
                }
                String trueValue = tokens[tokens.length - 1];
                String predictValue = null;
                double totalProb = 0;
                HashMap<String, Double> probMap = new HashMap<String, Double>();
                // Calculate probability for each label: P(y)P(x|y)
                for (String label : y.keySet()) {
                    xList = x.get(label);
                    double currentProb = y.get(label);
                    for (int i = 0; i < tokens.length - 1; ++i) {
                        HashMap<String, Double> xMap = xList.get(i);
                        currentProb *= xMap.get(tokens[i]);
                    }
                    totalProb += currentProb;
                    probMap.put(label, currentProb);
                }
                // Normalize prob map and find the prediction
                double maxProb = 0;
                for (String label : probMap.keySet()) {
                    double normal = probMap.get(label) / totalProb;
                    if (normal > maxProb) {
                        maxProb = normal;
                        predictValue = label;
                    }
                }
                // Output the result
                DecimalFormat df = new DecimalFormat("#.############");
                System.out.print(predictValue + " " + trueValue + " "); 
                System.out.println(df.format(maxProb));
                if (trueValue.equals(predictValue)) {
                    ++count;
                }
            }
            System.out.println("\n" + Integer.toString(count));
            fileBuffer.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Error: " + filename + " not exist");
            System.exit(1);
        }
        catch (IOException ex) {
            System.out.println("Error: unexpected exception encountered");
            System.exit(1);
        }
    }
    
    /**
     * print
     * Print all information in x and y, used for debugging
     */
    public void print() {
        System.out.println("*************************debug******************************");
        System.out.println("yMap:");
        for (String yStr : y.keySet()) {
            System.out.println(yStr + " : " + Double.toString(y.get(yStr)));
        }
        System.out.println("xMap:");
        for (String yStr : y.keySet()) {
            System.out.println(yStr);
            for (HashMap<String, Double> xMap : x.get(yStr)) {
                for (String xStr : xMap.keySet()) {
                    System.out.println(xStr + " : " + Double.toString(xMap.get(xStr)));
                }
                System.out.println();
            }
        }
    }

    private String relation;
    private HashMap<String, Double> y; // Store labels and their possibility
    private HashMap<String, List<HashMap<String, Double>>> x; // Store features and possibility based on label
}
