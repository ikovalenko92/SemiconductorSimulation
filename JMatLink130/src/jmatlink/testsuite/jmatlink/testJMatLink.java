/*
 * Created on 23.05.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.*;
import junit.framework.TestCase;

/**
 * @author stefan
 *
 */
public class testJMatLink extends TestCase {

    JMatLink jmatlink  = new JMatLink();
    
    //public void testJMatLink() {
    //    jmatlink = new JMatLink ();
    //    //jmatlink.engOpenSingleUse();
    //}

    /**
     * Used to set up the test. This method is called by JUnit before each of
     * the tests are executed.
     *** This method is not used any more, because there are many testcases which 
     *    envolve explicitely open and closing of the engine 
     *     */
    //protected void setUp () {
       // jmatlink = new JMatLink ();
       // jmatlink.engOpen();
    //}

    /**
     * Used to clean up after the test. This method is called by JUnit after
     * each of the tests has been completed.
     *** This method is not used any more, because there are many testcases which 
     *    envolve explicitely open and closing of the engine 
     */
    //protected void tearDown () {
    //  jmatlink.engClose();
    //  jmatlink.kill();
    //}


    public void testJMatLinkConstructor() {
        jmatlink = new JMatLink ();
        jmatlink = new JMatLink ();
        jmatlink = new JMatLink ();
        jmatlink = new JMatLink ();
        jmatlink.engOpen();
        
        
    }
    
    /*
     * Class under test for void engEvalString(String)
     */
    public void testEngEvalStringString() {
        jmatlink = new JMatLink ();
        jmatlink.engOpen();
        jmatlink.engEvalString("a=33;b=[1,4,6];"); 
        assertTrue(Math.abs(33.0 - jmatlink.engGetScalar("a"))<0.01);
        jmatlink.engClose();
    }

    /*
     * Class under test for double engGetScalar(String)
     */
    public void testEngGetScalarString() {
        jmatlink = new JMatLink ();
        jmatlink.engOpen();

        jmatlink.engEvalString("b=rand(4);barfoo=123.765;c=44.5;"); 
        
        assertTrue(Math.abs(123.765 - jmatlink.engGetScalar("barfoo"))<0.0001);
        
        jmatlink.engClose();
    }

   
    /*
     * Class under test for double[][] engGetArray(int, String)
     */
    public void testEngGetArrayintString() {
        jmatlink = new JMatLink ();
        jmatlink.engOpen();
        jmatlink.engClose();
    }

    public void testEngGetCharArray01() {
        jmatlink = new JMatLink();
        jmatlink.engOpen();
        String arrayName = "myArray";
        String testString = "Test this.";
        jmatlink.engEvalString(arrayName + " = '" + testString + "'");
        assertTrue(testString.equals(jmatlink.engGetCharArray(arrayName)[0]));
        jmatlink.engClose();
    }

    /*
     * Class under test for void engPutArray(String, int)
     */
    public void testEngPutArrayStringint01() {
        final String VAR_NAME = "myVar";
        jmatlink = new JMatLink();
        jmatlink.engOpen();
        
        jmatlink.engPutArray(VAR_NAME, Math.PI);
        
        double shouldBePi = jmatlink.engGetScalar(VAR_NAME);
        
        assertTrue(Math.abs(Math.PI - shouldBePi)<0.01);
        
        jmatlink.engClose();
    }


    /*
     * Class under test for void engPutArray(String, double[])
     */
    public void testEngPutArrayStringdoubleArray() {
            final int ARRAY_SIZE = 100;
            final String ARRAY_NAME = "myArray";
            double[] myOriginalArray = new double[ARRAY_SIZE];
            jmatlink = new JMatLink();
            jmatlink.engOpen();

            for (int i=0; i<10; i++) {
              myOriginalArray[i] = Math.random();
            }
            jmatlink.engPutArray(ARRAY_NAME, myOriginalArray);
            double[][] myRetrievedArray = jmatlink.engGetArray(ARRAY_NAME);
            for (int i=0; i<ARRAY_SIZE; i++) {
              assertTrue(Math.abs(myOriginalArray[i] - myRetrievedArray[0][i]) <0.001);
            }

            jmatlink.engClose();
    }


    /*
     * Class under test for void engPutArray(String, double[][])
     */
    public void testEngPutArrayStringdoubleArrayArray() {
          final int FIRST_ARRAY_SIZE = 30;
          final int SECOND_ARRAY_SIZE = 100;
          final String ARRAY_NAME = "myArray";
          double[][] myOriginalArray =
              new double[FIRST_ARRAY_SIZE][SECOND_ARRAY_SIZE];

        jmatlink = new JMatLink();
        jmatlink.engOpen();

        for (int i=0; i<FIRST_ARRAY_SIZE; i++) {
          for (int j=0; j<SECOND_ARRAY_SIZE; j++) {
            myOriginalArray[i][j] = 100.0 * Math.random();
          }
        }
        jmatlink.engPutArray(ARRAY_NAME, myOriginalArray);
        double[][] myRetrievedArray = jmatlink.engGetArray(ARRAY_NAME);
        for (int i=0; i<FIRST_ARRAY_SIZE; i++) {
          for (int j=0; j<SECOND_ARRAY_SIZE; j++) {
            assertTrue(Math.abs(myOriginalArray[i][j]-myRetrievedArray[i][j])<0.0001 );
          }
        }
        jmatlink.engClose();

    }


}
