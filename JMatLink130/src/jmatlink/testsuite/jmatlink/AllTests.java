/*
 * Created on 23.05.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author stefan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for jmatlink.testsuite.jmatlink");
        //$JUnit-BEGIN$
        suite.addTestSuite(testCoreJMatLink.class);
        suite.addTestSuite(testEngClose.class);
        suite.addTestSuite(testEngEvalString.class);
        suite.addTestSuite(testEngGetArray.class);
        suite.addTestSuite(testEngGetCharArray.class);
        suite.addTestSuite(testEngGetFigure.class);
        suite.addTestSuite(testEngineThread.class);
        suite.addTestSuite(testEngPutArray.class);
        suite.addTestSuite(testEngOpen.class);
        suite.addTestSuite(testEngOutputBuffer.class);
        suite.addTestSuite(testEngPutArray.class);
        suite.addTestSuite(testJMatLink.class);
        suite.addTestSuite(testSingleUse.class);
        //$JUnit-END$
        return suite;
    }
}
