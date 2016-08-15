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
 * This class is where all the training happens
 **/ 
class NeuralNetwork {
    /**
     * Construcor
     * @brief Initialize the neural network
     * @param trainfile The name of the training file
     *
     * @return A neural network instance
     */
    public NeuralNetwork(String trainfile) {
        myClasses = new String[2];
        myClass0 = new ArrayList<Datum>();
        myClass1 = new ArrayList<Datum>();
        myData = new ArrayList<Datum>();
        myFolds = new ArrayList<List<Datum>>();
        myBias = 1;

        try {
            String line = null;
            FileReader inFile = new FileReader(trainfile);
            BufferedReader fileBuffer = new BufferedReader(inFile);
            int attrCount = 0;

            // Read the header
            while ((line = fileBuffer.readLine()) != null) {
                // Comment
                if (line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ ,{}\t\']+");
                // Empty line or @relation
                if (tokens.length == 0 || tokens[0].equals("@relation")) {
                    continue;
                }
                // Data, end of header
                if (tokens[0].equals("@data")) {
                    break;
                }
                // Attribute, add to count
                if (tokens[0].equals("@attribute")) {
                    if (tokens[1].toLowerCase().equals("class")) {
                        myClasses[0] = tokens[2];
                        myClasses[1] = tokens[3];
                    }
                    else {
                        attrCount++;
                    }
                }
                else {
                    System.out.println("Error: incorrect header format");
                    System.exit(1);
                }
            }

            myWeights = new double[attrCount + 1];
            int count = 0;
            
            // Read the data
            while ((line = fileBuffer.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '%') {
                    continue;
                }
                String[] tokens = line.split("[ ,\t]+");
                if (tokens.length == 0) {
                    continue;
                }
                
                myData.add(new Datum(tokens, myClasses));
                count++;
                
                // Seperate Datum according to their class
                if (tokens[tokens.length - 1].equals(myClasses[0]))
                    myClass0.add(myData.get(count - 1));
                else if (tokens[tokens.length - 1].equals(myClasses[1])) {
                    myClass1.add(myData.get(count - 1));
                }
                else {
                    System.out.println("Error: Unknown classes");
                    System.exit(1);
                }
                
            }
            fileBuffer.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Error: " + trainfile + " not exist");
            System.exit(1);
        }
        catch (IOException ex) {
            System.out.println("Error: unexpected exception encountered");
            System.exit(1);
        }
    }

    // Train the network except the given fold
    public void train(int fold, double learningRate, int numEpochs) {
        // Initialize the weights
        for (int i = 0; i < myWeights.length; ++i) {
            myWeights[i] = 0.1;
        }

        // Merge all other folds as training set
        List<Datum> trainSet = new ArrayList<Datum>();
        List<Datum> testSet = myFolds.get(fold);
        for (int i = 0; i < myFolds.size(); ++i) {
            if (i != fold) {
                trainSet.addAll(myFolds.get(i));
            }
        }
        
        // Train the network using the merged set
        for (int i = 0; i < numEpochs; ++i) {
            for (int j = 0; j < trainSet.size(); ++j) {
                Datum datum = trainSet.get(j);
                int classNum = datum.myClass;
                double output = myBias * myWeights[0];
                double error = 0;

                // Calculate the output and error
                for (int k = 0; k < datum.myDatum.length; ++k) {
                    output += myWeights[k + 1] * datum.myDatum[k];
                }
                output = sigmoid(output);
                error = output * (1 - output) * (classNum - output);

                // Update weights
                myWeights[0] += learningRate * myBias * error;
                for (int k = 0; k < datum.myDatum.length; ++k) {
                    myWeights[k + 1] += learningRate * datum.myDatum[k] * error;
                }
            }
        }

        // Test on the chosen fold
        for (int i = 0; i < testSet.size(); ++i) {
            Datum datum = testSet.get(i);
            double output = myBias * myWeights[0];
            for (int j = 0; j < datum.myDatum.length; ++j) {
                output += myWeights[j + 1] * datum.myDatum[j];
            }
            output = sigmoid(output);

            datum.myFold = fold + 1;
            datum.myConfidence = output;
            datum.myPredictedClass = (output < 0.5) ? myClasses[0] : myClasses[1];
        }
    }

    // Partition the data in a stratified way
    public void partition(int numFolds) {
        // Initialize
        int[] index0 = new int[myClass0.size()];
        int[] index1 = new int[myClass1.size()];

        for (int i = 0; i < numFolds; ++i) {
            myFolds.add(new ArrayList<Datum>());
        }

        // Ramdomize the indices
        Collections.shuffle(myClass0);
        Collections.shuffle(myClass1);

        // Add Datum into list
        for (int i = 0; i < myClass0.size(); ++i) {
            myFolds.get(i % numFolds).add(myClass0.get(i));
        }

        for (int i = 0; i < myClass1.size(); ++i) {
            myFolds.get(i % numFolds).add(myClass1.get(i));
        }

        // Shuffle each folds
        for (int i = 0; i < numFolds; ++i) {
            Collections.shuffle(myFolds.get(i));
        }
    }

    public void print() {
        for (Datum datum : myData) {
            System.out.print(datum.myFold);
            System.out.print(' ' + datum.myPredictedClass);
            System.out.print(' ' + datum.myActualClass + ' ');
            System.out.println(datum.myConfidence); 
        }
    }

    // Calculate the sigmoid result of x
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public double[] myWeights; // 0 means bias

    public String[] myClasses;
    public List<Datum> myClass0;
    public List<Datum> myClass1;
    public List<Datum> myData;
    public List<List<Datum>> myFolds;
    public double myBias;
}
