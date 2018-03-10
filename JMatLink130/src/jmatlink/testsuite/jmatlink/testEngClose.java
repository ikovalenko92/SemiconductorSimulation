/*
 * Created on 19.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.JMatLink;
import jmatlink.JMatLinkException;
import junit.framework.TestCase;

/**
 * @author stefan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class testEngClose extends TestCase {
    JMatLink jmatlink = new JMatLink ();


    public void testEngClose01() {
        jmatlink = new JMatLink ();
        long a = jmatlink.engOpenSingleUse();
        long b = jmatlink.engOpenSingleUse();
        jmatlink.engClose(b);
        
        // only close engine once
        try {
            jmatlink.engClose(b);  // error
            fail("should fail");
        }
        catch (JMatLinkException e) { assertTrue(true); }

        // only close engine once
        try {
            jmatlink.engClose(b);  // error
            fail("should fail");
        }
        catch (JMatLinkException e) { assertTrue(true); }

        jmatlink.engClose(a);
       
    }

    /*
     * Class under test for void engClose(int)
     */
    public void testEngCloseint() {
    }

}
