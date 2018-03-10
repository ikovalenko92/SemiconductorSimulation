/*
 * Created on 23.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jmatlink.testsuite;

//import jmatlink.testsuite.jmatlink.testJMatLink;
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
        TestSuite suite = new TestSuite("Test for jmatlink.testsuite");
        //$JUnit-BEGIN$
        suite.addTest(jmatlink.testsuite.jmatlink.AllTests.suite());
        //$JUnit-END$
        return suite;
    }
}
