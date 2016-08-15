///////////////////////////////////////////////////////////////////////////////
//  Title:                         TAN.java                                  //
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

class TAN {
    // Constructor
    public TAN() {
        relation = null;
        cpt = new HashMap<Probability, Double>();
        yProbMap = new HashMap<String, Double>();
        parent = null;
        attributes = new ArrayList<List<String>>(); // Store the value of all attributes
    }
    /**
     * train
     * The training process of TAN algorithm. Initialize the data structure -> read data ->
     * -> compute conditional mutual information graph -> build MST -> compute CPT
     */
    public void train(String filename) {
        HashMap<String, Double> yCountMap = new HashMap<String, Double>();
        HashMap<Probability, Double> xCountMap = new HashMap<Probability, Double>();
        int total = 0; // Number of total instances
        try {
            FileReader inFile = new FileReader(filename);
            BufferedReader fileBuffer = new BufferedReader(inFile);
            String line = null;
            // Read header
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.length() == 0 || line.charAt(0) == '%') {
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
                    break;
                }
                // Attribute, store the available values
                if (tokens[0].equals("@attribute")) {
                    List<String> attribute = new ArrayList<String>();   
                    for (int i = 2; i < tokens.length; ++i) {
                        attribute.add(tokens[i]);
                    }
                    attributes.add(attribute);
                }
                else {
                    System.out.println("Error: incorrect header format");
                    System.exit(1);
                }
            }
            // Initialize CountMap
            List<String> labels = attributes.get(attributes.size() - 1);
            // Label
            for (int i = 0; i < labels.size(); ++i) {
                String label = labels.get(i);
                yCountMap.put(label, 0.0);
                // OneAttribute
                for (int j = 0; j < attributes.size() - 1; ++j) {
                    List<String> attribute1 = attributes.get(j);
                    for (int k = 0; k < attribute1.size(); ++k) {
                        String value1 = attribute1.get(k);
                        xCountMap.put(new OneProbability(j, value1, label), 0.0);
                        // TwoAttribute
                        for (int m = j + 1; m < attributes.size() - 1; ++m) {
                            List<String> attribute2 = attributes.get(m);
                            for (int n = 0; n < attribute2.size(); ++n) {
                                String value2 = attribute2.get(n);
                                xCountMap.put(new TwoProbability(j, value1, m, value2, 
                                            label), 0.0); 
                            }
                        }
                    }
                }
            }
            
            // Read Data
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.length() == 0 || line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ ,{}\t\']+");
                // Empty line
                if (tokens.length == 0) {
                    continue;
                }
                // Update label count
                String label = tokens[tokens.length - 1];
                if (yCountMap.containsKey(label)) {
                    yCountMap.put(label, yCountMap.get(label) + 1);
                }
                else {
                    System.out.println("Error: unknown label in training data");
                    System.exit(1);
                }
                // Update OneAttribute count
                for (int i = 0; i < tokens.length - 1; ++i) {
                    String value1 = tokens[i];
                    OneProbability onePro = new OneProbability(i, value1, label);
                    if (xCountMap.containsKey(onePro)) {
                        xCountMap.put(onePro, xCountMap.get(onePro) + 1);
                    }
                    else {
                        System.out.println("Error: unknown feature value in training data1");
                        System.exit(1);
                    }
                    // Update TwoAttribute count
                    for (int j = i + 1; j < tokens.length - 1; ++j) {
                        String value2 = tokens[j];
                        TwoProbability twoPro = new TwoProbability(i, value1, j, value2, label);
                        if (xCountMap.containsKey(twoPro)) {
                            xCountMap.put(twoPro, xCountMap.get(twoPro) + 1);
                        }
                        else {
                            System.out.println("Error: unknown feature value in training data2");
                            System.exit(1);
                        }
                    }
                }
                ++total;
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

        // Change count to probability
        HashMap<Probability, Double> xProbMap = new HashMap<Probability, Double>();
        for (Probability prob : xCountMap.keySet()) {
            // OneAttribute
            if (prob instanceof OneProbability) {
                OneProbability temp = (OneProbability) prob;
                double yCount = yCountMap.get(temp.label());
                int pseudo = attributes.get(temp.index()).size(); // Get pseudo counts for denominator
                xProbMap.put(prob, (xCountMap.get(prob) + 1) / (yCount + pseudo));
            }
            // TwoAttribute
            if (prob instanceof TwoProbability) {
                TwoProbability temp = (TwoProbability) prob;
                double yCount = yCountMap.get(temp.label());
                int pseudo = attributes.get(temp.index1()).size()
                                * attributes.get(temp.index2()).size();
                xProbMap.put(prob, (xCountMap.get(prob) + 1) / (yCount + pseudo));
            }
        }
        // label
        for (String y : yCountMap.keySet()) {
            yProbMap.put(y, (yCountMap.get(y) + 1) / (total + yCountMap.size()));
        }

        // Calculate edge values (conditional mutual information graph)
        double[][] edges = new double[attributes.size() - 1][attributes.size() - 1]; //edge value between two features
        List<String> labels = attributes.get(attributes.size() - 1);
        for (int i = 0; i < edges.length; ++i) {
            List<String> feature1 = attributes.get(i);
            edges[i][i] = -1.0; // As the given standard output (no meaning)
            for (int j = i + 1; j < edges.length; ++j) {
                List<String> feature2 = attributes.get(j);
                double edge = 0;
                for (int k = 0; k < labels.size(); ++k) {
                    String label = labels.get(k);
                    // Calculate P(x1|y)
                    for (int index1 = 0; index1 < feature1.size(); ++index1) {
                        String value1 = feature1.get(index1);
                        double probOne1 = xProbMap.get(new OneProbability(i, value1, label));
                        for (int index2 = 0; index2 < feature2.size(); ++index2) {
                            String value2 = feature2.get(index2);
                            // P(x2|y)
                            double probOne2 = xProbMap.get(new OneProbability(j, value2, label));
                            // P(x1, x2|y)
                            TwoProbability two = new TwoProbability(i, value1, j, value2, label);
                            double probTwo = xProbMap.get(two);
                            // P(x1, x2, y)
                            double countThree = xCountMap.get(two);
                            double probThree = (countThree + 1) / (yProbMap.size() * feature1.size() * feature2.size() + total);
                            // Calculate edge value
                            edge += probThree * Math.log(probTwo / (probOne1 * probOne2)) / Math.log(2);
                        }
                    }
                }
                edges[i][j] = edge;
                edges[j][i] = edge;
            }
        }
        // checkGraph(edges); // for debugging

        // Build MST: Prim's Alogrithm
        HashSet<Integer> built = new HashSet<Integer>();
        HashSet<Integer> unbuilt = new HashSet<Integer>();
        parent = new int[edges.length];
        // Initialize built set
        built.add(0);
        // Initialize unbuilt set
        for (int i = 1; i < attributes.size() - 1; ++i) {
            unbuilt.add(i);
        }
        // Initialize parent array
        for (int i = 0; i < parent.length; ++i) {
            parent[i] = -1;
        }
        while (!unbuilt.isEmpty()) {
            // Record the maximum edge and max edge value
            int start = attributes.size(), end = attributes.size();
            double max = 0.0; 
            for (Integer i : built) {
                for (Integer j : unbuilt) {
                    // If current edge is not better than max, try the next edge
                    if (edges[i][j] < max) {
                        continue;
                    }
                    else if (edges[i][j] == max) {
                        if (i > start || (i == start && j > end)) {
                            continue;
                        }
                    }
                    max = edges[i][j];
                    start = i;
                    end = j;    
                }
            }
            parent[end] = start;
            built.add(end);
            unbuilt.remove(end);
        }
        // checkTree(parent);  // for debugging

        // Calculate conditional probability tables (CPT)
        for (String y : yProbMap.keySet()) {
            // Root
            List<String> root = attributes.get(0);
            for (String value : root) {
                OneProbability prob = new OneProbability(0, value, y);
                cpt.put(prob, xProbMap.get(prob));
            }
            // Other nodes
            for (int i = 1; i < parent.length; ++i) {
                List<String> attribute = attributes.get(i);
                int parentIndex = parent[i];
                List<String> parentAttribute = attributes.get(parentIndex);
                for (String parentValue : parentAttribute) {
                    double parentCount = xCountMap.get(new OneProbability(parentIndex, parentValue, y));
                    for (String childValue : attribute) {
                        TwoProbability two = new TwoProbability(i, childValue, parentIndex, parentValue, y);
                        double childCount = xCountMap.get(two);
                        // P(x1|x2, y)
                        cpt.put(two, (childCount + 1) / (parentCount + attribute.size()));
                    }
                }
            }
        }
        // checkCPT();  // for debugging
        // checkYMap();  // for debugging
    }

    public void test(String filename) {
        try {
            FileReader inFile = new FileReader(filename);
            BufferedReader fileBuffer = new BufferedReader(inFile);
            String line = null;
            int index = 0; // the index of the attribute to be checked
            List<String> attributeName = new ArrayList<String>(); // Name of the attributes
            // Read header
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                String[] tokens = line.split("[ ,{}\'\t]+");
                // Empty line
                if (tokens.length == 0) {
                    continue;
                }
                // Relation
                if (tokens[0].equals("@relation")) {
                    if (!tokens[1].equals(relation)) {
                        System.out.println("Error: unknown relation in training set");
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
                    if (index >= attributes.size()) {
                        System.out.println("Error: too many attributes in testing set");
                        System.exit(1);
                    }
                    List<String> attribute = attributes.get(index++);
                    for (int i = 2; i < tokens.length; ++i) {
                        if (!attribute.get(i - 2).equals(tokens[i])) {
                            System.out.println("Error: unknown attribute value");
                            System.exit(1);
                        }
                    }
                    attributeName.add(tokens[1]);
                }
            }
            // Print Attribute and parent
            // Root
            System.out.println(attributeName.get(0) + ' ' + "class");
            // Other attributes
            for (int i = 1; i < parent.length; ++i) {
                System.out.println(attributeName.get(i) + ' ' + attributeName.get(parent[i]) + ' ' + "class");
            }
            System.out.println();
            
            // Analyze data
            int correct = 0;
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.length() == 0 || line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ \t,]+");
                // Empty string
                if (tokens.length == 0) {
                    continue;
                }

                String trueValue = tokens[tokens.length - 1];
                String predictValue = null;
                double maxProb = 0.0;
                double totalProb = 0.0;
                for (String label : yProbMap.keySet()) {
                    // Initialize with root value and label value
                    double currentProb = cpt.get(new OneProbability(0, tokens[0], label)) * yProbMap.get(label);
                    // Other attributes (with parent)
                    for (int i = 1; i < parent.length; ++i) {
                        currentProb *= cpt.get(new TwoProbability(i, tokens[i], parent[i], tokens[parent[i]], label));
                    }
                    totalProb += currentProb;
                    // Update max value and label if eligible
                    if (currentProb > maxProb) {
                        maxProb = currentProb;
                        predictValue = label;
                    }
                }
                // Correct prediction
                if (trueValue.equals(predictValue)) {
                    ++correct;
                }
                // Print the result
                DecimalFormat df = new DecimalFormat("#.############");
                System.out.print(predictValue + ' ' + trueValue + ' ');
                System.out.println(df.format(maxProb / totalProb));
            }
            // Print # of correct predictions
            System.out.println();
            System.out.println(correct);
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
     * checkGraph
     * A debugging tool to print the conditional mutual information graph
     */
    public void checkGraph(double[][] edges) {
        System.out.println("Conditional mutual information graph:");
        for (int i = 0; i < edges.length; ++i) {
            for (int j = 0; j < edges[0].length - 1; ++j) {
                System.out.print(edges[i][j]);
                System.out.print(',');
            }
            System.out.println(edges[i][edges[i].length - 1]);
        }
    }
    
    /**
     * checkTree
     * A debugging tool to print the edges in the MST
     */
    public void checkTree(int[] parent) {
        System.out.println("Edges in maximum weight spanning tree:");
        for (int i = 1; i < parent.length; ++i) {
            System.out.print(parent[i]);
            System.out.print(',');
            System.out.println(i);
        }
    }

    public void checkCPT() {
        for (Probability prob : cpt.keySet()) {
            prob.print();
            System.out.print(":");
            System.out.println(cpt.get(prob));
        }
    } 

    public void checkYMap() {
        for (String y : yProbMap.keySet()) {
            System.out.print(y + ':');
            System.out.println(yProbMap.get(y));
        }
    }   

    private String relation;   
    private HashMap<Probability, Double> cpt;
    private HashMap<String, Double> yProbMap;
    private int[] parent;
    private List<List<String>> attributes;
}
