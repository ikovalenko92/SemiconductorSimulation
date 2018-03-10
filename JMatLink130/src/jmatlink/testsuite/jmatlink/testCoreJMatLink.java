/*
 * Created on 06.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.CoreJMatLink;
import junit.framework.TestCase;

/**
 * @author stefan
 *
 */
public class testCoreJMatLink extends TestCase {

    public void testDestroy() {
    }

    public void testRun() {
    }

    //public void testCoreJMatLink() {
    //}

    public void testKill() {
    }

    /*
     * Class under test for int engOpen()
     */
    public void testEngOpen01() {
        CoreJMatLink jmatlink = new CoreJMatLink();
        jmatlink.engOpen("");
        jmatlink.engClose();
        
        // do not call engClose() engOpen() right after another in the core class
        //   there is a safeguard if the engine thread is really running and/or dead
        //jmatlink.engOpen("");
        //jmatlink.engClose();
        //jmatlink.engOpen("");
        //jmatlink.engClose();
       
    }

    /*
     * Class under test for int engOpen(String)
     */
    public void testEngOpenString() {
    }

    /*
     * Class under test for int engOpenSingleUse()
     */
    public void testEngOpenSingleUse() {
    }

    /*
     * Class under test for int engOpenSingleUse(String)
     */
    public void testEngOpenSingleUseString() {
    }

    /*
     * Class under test for void engClose()
     */
    public void testEngClose() {
    }

    /*
     * Class under test for void engClose(int)
     */
    public void testEngCloseint() {
    }

    /*
     * Class under test for void engEvalString(String)
     */
    public void testEngEvalStringString() {
    }

    /*
     * Class under test for void engEvalString(int, String)
     */
    public void testEngEvalStringintString() {
    }

    /*
     * Class under test for double engGetScalar(String)
     */
    public void testEngGetScalarString() {
    }

    /*
     * Class under test for double engGetScalar(int, String)
     */
    public void testEngGetScalarintString() {
    }

    /*
     * Class under test for double[][] engGetArray(String)
     */
    public void testEngGetArrayString() {
    }

    /*
     * Class under test for double[][] engGetArray(int, String)
     */
    public void testEngGetArrayintString() {
    }

    public void testEngGetCharArray() {
    }

    /*
     * Class under test for void engPutArray(String, int)
     */
    public void testEngPutArrayStringint() {
    }

    /*
     * Class under test for void engPutArray(String, double)
     */
    public void testEngPutArrayStringdouble() {
    }

    /*
     * Class under test for void engPutArray(int, String, double)
     */
    public void testEngPutArrayintStringdouble() {
    }

    /*
     * Class under test for void engPutArray(String, double[])
     */
    public void testEngPutArrayStringdoubleArray() {
    }

    /*
     * Class under test for void engPutArray(int, String, double[])
     */
    public void testEngPutArrayintStringdoubleArray() {
    }

    /*
     * Class under test for void engPutArray(String, double[][])
     */
    public void testEngPutArrayStringdoubleArrayArray() {
    }

    /*
     * Class under test for void engPutArray(int, String, double[][])
     */
    public void testEngPutArrayintStringdoubleArrayArray() {
    }

    /*
     * Class under test for String engOutputBuffer()
     */
    public void testEngOutputBuffer() {
    }

    /*
     * Class under test for String engOutputBuffer(int)
     */
    public void testEngOutputBufferint() {
    }

    /*
     * Class under test for String engOutputBuffer(int, int)
     */
    public void testEngOutputBufferintint() {
    }

    public void testSetDebug() {
    }

}
