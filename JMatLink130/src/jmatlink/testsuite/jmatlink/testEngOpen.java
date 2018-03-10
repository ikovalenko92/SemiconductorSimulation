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
public class testEngOpen extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
    public void testEngOpen01() {
        
        jmatlink.engOpen();

        // only open engine once
        try {
            jmatlink.engOpen();
            fail("should fail");
        }
        catch (JMatLinkException e) { assertTrue(true); }

        // only open engine once
        try {
            jmatlink.engOpen();
            fail("should fail");
        }
        catch (JMatLinkException e) { assertTrue(true); }

        jmatlink.engClose();
        
    }

    public void testEngOpen02() {
        
        // only open engine once
        try {
            jmatlink.engClose();
            fail("should fail");
        }
        catch (JMatLinkException e) { assertTrue(true); }
        
        
    }

    public void testEngOpen03() {
        
        jmatlink.engOpen();
        
        // only open engine once
        try {
            jmatlink.engOpen();
            fail("should fail");
        }
        catch (JMatLinkException e) { assertTrue(true); }

        jmatlink.engClose();
    }
    
    public void testEngOpen04() {
        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engClose();
        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engClose();
        jmatlink.engOpen();
        jmatlink.engClose();
       
    }


    
}
