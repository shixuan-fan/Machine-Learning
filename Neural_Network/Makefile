###
# This Makefile can generate the main class (Bayes.class) and the
# learning and predicting algorithms (NaiveBayes.class and TAN.class)
#
# make clean: remove all generated files
###

JC = javac

NeuralNet.class: NeuralNet.java NeuralNetwork.class Datum.class AccuracyCalculator.class
	$(JC) -g NeuralNet.java
	
NeuralNetwork.class: NeuralNetwork.java Datum.class
	$(JC) NeuralNetwork.java
	
Datum.class: Datum.java
	$(JC) Datum.java

AccuracyCalculator.class: AccuracyCalculator.java NeuralNetwork.class Datum.class
	$(JC) AccuracyCalculator.java

ROCCalculator.class: ROCCalculator.java NeuralNetwork.class Datum.class
	$(JC) ROCCalculator.java
	
	
## test
test:
	java NeuralNet train.arff 10 0.1 50

## test
5b1:
	java NeuralNet train.arff 10 0.1 25 a
	java NeuralNet train.arff 10 0.1 25 a
	java NeuralNet train.arff 10 0.1 25 a
	java NeuralNet train.arff 10 0.1 50 a
	java NeuralNet train.arff 10 0.1 50 a
	java NeuralNet train.arff 10 0.1 50 a
	java NeuralNet train.arff 10 0.1 75 a
	java NeuralNet train.arff 10 0.1 75 a
	java NeuralNet train.arff 10 0.1 75 a
	java NeuralNet train.arff 10 0.1 100 a
	java NeuralNet train.arff 10 0.1 100 a
	java NeuralNet train.arff 10 0.1 100 a

5b2:
	java NeuralNet train.arff 5 0.1 50 a
	java NeuralNet train.arff 5 0.1 50 a
	java NeuralNet train.arff 5 0.1 50 a
	java NeuralNet train.arff 10 0.1 50 a
	java NeuralNet train.arff 10 0.1 50 a
	java NeuralNet train.arff 10 0.1 50 a
	java NeuralNet train.arff 15 0.1 50 a
	java NeuralNet train.arff 15 0.1 50 a
	java NeuralNet train.arff 15 0.1 50 a
	java NeuralNet train.arff 20 0.1 50 a
	java NeuralNet train.arff 20 0.1 50 a
	java NeuralNet train.arff 20 0.1 50 a
	java NeuralNet train.arff 25 0.1 50 a
	java NeuralNet train.arff 25 0.1 50 a
	java NeuralNet train.arff 25 0.1 50 a

5b3:
	java NeuralNet train.arff 10 0.1 50 r
	
###
# clean
###
clean:
	rm -f *~ *.class
