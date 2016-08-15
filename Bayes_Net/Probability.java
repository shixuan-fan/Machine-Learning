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

/*
 * class Probability
 * Abstract probability, contains OneProbability and TwoProbability
 */ 
abstract class Probability {
    abstract public int hashCode();
    abstract public boolean equals(Object o);
    abstract public void print();
}

class OneProbability extends Probability {
    // Constructor
    public OneProbability(int index, String value, String label) {
        myIndex = index;
        myValue = value;
        myLabel = label;
    }

    public Integer index() {
        return myIndex;
    }

    public String value() {
        return myValue;
    }

    public String label() {
        return myLabel;
    }
    /**
     * hashCode
     * Override the default hashCode function
     */
    public int hashCode() {
        return myIndex.hashCode() ^ myValue.hashCode() ^ myLabel.hashCode();
    }
    /**
     * equals
     * Override the default equals function
     */
    public boolean equals(Object o) {
        Probability prob = (Probability) o;
        if (prob instanceof OneProbability) {
            OneProbability temp = (OneProbability) prob;
            if (myIndex.equals(temp.index()) && myValue.equals(temp.value())
                && myLabel.equals(temp.label())) {
                return true;
            }
        }
        return false;
    }
    /**
     * print
     * Print the attribute object, for debugging
     */
    public void print() {
        System.out.print(myIndex);
        System.out.print(',');
        System.out.print(myValue + ',' + myLabel);
    }

    private Integer myIndex; // Index for x1 feature
    private String myValue; // Value of x1
    private String myLabel;  // Value of y
}

class TwoProbability extends Probability {
    // Constructor
    public TwoProbability(int index1, String value1, int index2, String value2, 
                            String label) {
        myIndex1 = index1;
        myValue1 = value1;
        myIndex2 = index2;
        myValue2 = value2;
        myLabel = label;
    }

    public Integer index1() {
        return myIndex1;
    }

    public String value1() {
        return myValue1;
    }

    public Integer index2() {
        return myIndex2;
    }

    public String value2() {
        return myValue2;
    }

    public String label() {
        return myLabel;
    }
    /**
     * hashCode
     * Override the default hashCode function
     */
    public int hashCode() {
        return myIndex1.hashCode() ^ myValue1.hashCode() ^ myIndex2.hashCode()
                ^ myValue2.hashCode() ^ myLabel.hashCode();
    }
    /**
     * equals
     * Override the default equals function
     */
    public boolean equals(Object o) {
        Probability prob = (TwoProbability) o;
        if (prob instanceof TwoProbability) {
            TwoProbability temp = (TwoProbability) prob;
            // First and second index-value pair could flip
            if (myIndex1.equals(temp.index1()) && myValue1.equals(temp.value1())
                && myIndex2.equals(temp.index2()) && myValue2.equals(temp.value2())
                && myLabel.equals(temp.label())) {

                return true;
            }
            if (myIndex1.equals(temp.index2()) && myValue1.equals(temp.value2())
                && myIndex2.equals(temp.index1()) && myValue2.equals(temp.value1())
                && myLabel.equals(temp.label())) {

                return true;
            }
        }
        return false;
    }
    /**
     * print
     * Print the attribute object, for debugging
     */
    public void print() {
        System.out.print(myIndex1);
        System.out.print(',' + myValue1 + ',');
        System.out.print(myIndex2);
        System.out.println(',' + myValue2 + ',' + myLabel);
        
    }

    private Integer myIndex1; // Index for x1 feature
    private String myValue1; // Value of x1
    private Integer myIndex2; // Index of x2 feature
    private String myValue2; // Value of x2
    private String myLabel;  // Value of y
}
