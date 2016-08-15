#Bayes Net

This program could train a bayes network using Naive Bayes classifer or TAN classifier. It only accepts file in `.arff` format.

To run the program:

1. `make`

2. `java Bayes <trainfile> <testfile> <n|t>`, where `n` indicates Naive Bayes classifer, and `t` indicates TAN classifier.

Note: in TAN, use Prims's algorithm to find a maximal spanning tree. To break tie, always prefer the one with smaller index.

##Output
1. The structure of the Bayes net by listing one line per attribute in which you list: 
a) The name of the attribute.
b) The names of its parents in the Bayes net (for naive Bayes, this will simply be the 'class' variable for each attribute) separated by whitespace.
2. One line for each instance in the test-set (in the same order as this file) indicating:
a) The predicted class. 
b) The actual class. 
c) The posterior probability of the predicted class.
3. The number of the test-set examples that were correctly classified. 
