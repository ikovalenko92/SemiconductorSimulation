/*
 * Created on 19.06.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.JMatLink;
import jmatlink.JMatLinkException;
import junit.framework.TestCase;

/**
 * @author stefan
 */
public class testEngEvalString extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
    public void testEngEvalString01() {
    }
    
    
    public void testEngEvalString02() {
        
        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        
        try {
            jmatlink.engEvalString("b=2");
            jmatlink.engEvalString("exit");  // close matlab (from inside itself)
            System.out.println("ok: a failing test, because matlab terminated itsel.");
            fail("should fail because matlab terminated itself");
        }
        catch (JMatLinkException e) { assertTrue(true); }

        jmatlink.engClose();
    }

    public void testEngEvalString03() {
        
        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engClose();
        
        try {
            jmatlink.engEvalString("b=2");
            fail("should fail because matlab terminated itself");
        }
        catch (JMatLinkException e) { assertTrue(true); }

    }


}
