/*
 * Created on 04.07.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.JMatLink;
import junit.framework.TestCase;

/**
 * @author stefan
 */
public class testEngGetArray extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
    
    
    public void testEngGetArray01() {
        
        jmatlink.engOpen();
        double[] testA = {1.0, 2.0, 3.0, 4.0, 5.0};

        jmatlink.engPutArray("testA",testA);
        
        double[][] myRetrievedArray = jmatlink.engGetArray("testA");

        for (int i=0; i<3; i++) {
          assertTrue(Math.abs(testA[i] - myRetrievedArray[0][i]) <0.001);
        }

        jmatlink.engClose();
    }

    public void testEngPutArrayStringdoubleArray() {
        final int ARRAY_SIZE = 100;

        double[] myOriginalArray1 = new double[ARRAY_SIZE];
        double[] myOriginalArray2 = new double[ARRAY_SIZE];
        
        long eng1 = jmatlink.engOpenSingleUse();
        long eng2 = jmatlink.engOpenSingleUse();

        for (int i=0; i<10; i++) {
          myOriginalArray1[i] = Math.random();
          myOriginalArray2[i] = Math.random();
        }
        
        jmatlink.engPutArray(eng1, "myArray", myOriginalArray1);
        jmatlink.engPutArray(eng2, "myArray", myOriginalArray2);
        
        double[][] myRetrievedArray1 = jmatlink.engGetArray(eng1, "myArray");
        double[][] myRetrievedArray2 = jmatlink.engGetArray(eng2, "myArray");

        for (int i=0; i<ARRAY_SIZE; i++) {
          assertTrue(Math.abs(myOriginalArray1[i] - myRetrievedArray1[0][i]) <0.001);
          assertTrue(Math.abs(myOriginalArray2[i] - myRetrievedArray2[0][i]) <0.001);
        }

        jmatlink.engClose(eng1);
        jmatlink.engClose(eng2);
}


}
