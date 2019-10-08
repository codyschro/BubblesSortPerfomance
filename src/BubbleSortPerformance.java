import java.util.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class BubbleSortPerformance {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 10;
    static int MAXINPUTSIZE  = (int) Math.pow(2,17);
    static int MININPUTSIZE  =  1;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    static String ResultsFolderPath = "/home/codyschroeder/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {
        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("BubbleSort-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("BubbleSort-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("BubbleSort-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");
            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();
            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            //BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random list of integers for each trial
                System.out.print("    Generating test data...");
                long[] newList = createRandomListOfIntegers(inputSize);
                System.out.println("...done.");
                System.out.print("    Running trial batch...");
                /* force garbage collection before each trial run so it is not included in the time */
                System.gc();

                TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                bubbleSort(newList);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static void bubbleSort(long[] list){
        // make N passes through the list (N is the length of the list)
        for(int i = 0; i < list.length-1; i++) {
            // for index from 0 to N-1, compare item[index] to next it, swap if needed
            for(int j = 0; j < list.length-1-i; j++){
                if(list[j] > list[j+1]){
                    long tmp = list[j];
                    list[j] = list[j+1];
                    list[j+1] = tmp;
                }
            }
        }
    }

    //create random integer list containing negative and positive numbers
    public static long[] createRandomListOfIntegers(int size){
        long[] newList = new long[size];
        for(int j = 0; j < size; j++) {
            newList[j] = (long) (MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }
}
