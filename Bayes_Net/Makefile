###
# This Makefile can generate the main class (Bayes.class) and the
# learning and predicting algorithms (NaiveBayes.class and TAN.class)
#
# make clean: remove all generated files
###

JC = javac

Bayes.class: Bayes.java NaiveBayes.class TAN.class
	$(JC) -g Bayes.java
	
NaiveBayes.class: NaiveBayes.java
	$(JC) NaiveBayes.java
	
TAN.class: TAN.java Probability.class
	$(JC) TAN.java
	
Probability.class: Probability.java
	$(JC) Probability.java
	
Edge.class: Edge.java
	$(JC) Edge.java
	
## test
test:
	java Bayes train.arff test.arff n
	java Bayes train.arff test.arff t
	
###
# clean
###
clean:
	rm -f *~ *.class
