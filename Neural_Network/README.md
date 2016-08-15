#Neural Network

This program could train neural network using n-fold stratified cross validation with sigmoid activation function. It only accepts file in `.arff` format.

To run the program:

1. `make`

2. `java NeuralNet <trainfile> <num_folds> <learning_rate> <num_epochs> [optional c]`

##Output

1. If `c` is present, it will output points of the ROC curve.

2. If `c` is not present, the output will be:

`fold_of_instance predicted_class actual_class confidence_of_prediction`

The output order is in the same order as the source file.




