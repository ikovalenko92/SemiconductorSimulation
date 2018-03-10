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
public class testEngPutArray extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
    
    
    public void testEngPutArray01() {
        
        jmatlink.engOpen();
        double[] testA = {1.0, 2.0, 3.0};

        jmatlink.engPutArray("testA",testA);
        
        double[][] myRetrievedArray = jmatlink.engGetArray("testA");

        for (int i=0; i<3; i++) {
          assertTrue(Math.abs(testA[i] - myRetrievedArray[0][i]) <0.001);
        }

        jmatlink.engClose();
    }

    public void testEngPutArrayStringdoubleArray() {
        final int ARRAY_SIZE = 100;

        double[] myOriginalArray = new double[ARRAY_SIZE];
        jmatlink.engOpen();

        for (int i=0; i<10; i++) {
          myOriginalArray[i] = Math.random();
        }
        
        jmatlink.engPutArray("myArray", myOriginalArray);
        
        double[][] myRetrievedArray = jmatlink.engGetArray("myArray");

        for (int i=0; i<ARRAY_SIZE; i++) {
          assertTrue(Math.abs(myOriginalArray[i] - myRetrievedArray[0][i]) <0.001);
        }

        jmatlink.engClose();
}


}
