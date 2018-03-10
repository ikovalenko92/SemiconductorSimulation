/*
 * Created on 26.06.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.JMatLink;
import junit.framework.TestCase;

/**
 * @author stefan
 */
public class testEngOutputBuffer extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
    public void testEngOutputBuffer01() {
        
        jmatlink.engOpen();
        jmatlink.engOutputBuffer();
        
        jmatlink.engEvalString("aaabbb=1234.5");
        
        String output =    jmatlink.engGetOutputBuffer();

        assertTrue(output.indexOf("aaabbb")>0);
        
        jmatlink.engClose();
    }

    public void testEngOutputBuffer02() {
        
        long e1 = jmatlink.engOpenSingleUse();
        long e2 = jmatlink.engOpenSingleUse();
        jmatlink.engOutputBuffer(e1);
        jmatlink.engOutputBuffer(e2);

        jmatlink.engEvalString(e2, "ccc=789.0");
        String output2 =    jmatlink.engGetOutputBuffer(e2);

        jmatlink.engEvalString(e1, "bbb=1234.5");
        
        String output1 =    jmatlink.engGetOutputBuffer(e1);

        assertTrue(output1.indexOf("bbb")>0);
        assertTrue(output2.indexOf("ccc")>0);
        
        jmatlink.engClose(e1);
        jmatlink.engClose(e2);
    }

    public void testEngOutputBuffer03() {
        
        long e1 = jmatlink.engOpenSingleUse();
        long e2 = jmatlink.engOpenSingleUse();
        
        jmatlink.engOutputBuffer(e1);
        jmatlink.engOutputBuffer(e2);
        

        jmatlink.engEvalString(e2, "ccc=789.0");
        jmatlink.engEvalString(e1, "bbb=1234.5");
        
        
        String output1 =    jmatlink.engGetOutputBuffer(e1);
        String output2 =    jmatlink.engGetOutputBuffer(e2);

        System.out.println("1111"+output1);
        System.out.println("2222"+output2);

        assertTrue(output1.indexOf("bbb")>0);
        assertTrue(output2.indexOf("ccc")>0);
                
        jmatlink.engClose(e1);
        jmatlink.engClose(e2);
    }


}
