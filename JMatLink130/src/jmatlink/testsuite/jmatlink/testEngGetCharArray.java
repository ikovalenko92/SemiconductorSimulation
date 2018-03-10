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
public class testEngGetCharArray extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
    
    
    public void testEngGetCharArray01() {
        
        jmatlink.engOpen();

        jmatlink.engEvalString("words=['hello'];");
        
        String[] s = jmatlink.engGetCharArray("words");

        assertTrue( s[0].equals("hello") );

        jmatlink.engClose();
    }

    public void testEngGetCharArray02() {
        
        long a = jmatlink.engOpenSingleUse();
        long b = jmatlink.engOpenSingleUse();

        jmatlink.engEvalString(b, "words=['hellohello'];");
        jmatlink.engEvalString(a, "words=['bar_foo'];");
        jmatlink.engEvalString(a, "joe=['mickey'];");
        
        String[] a1S = jmatlink.engGetCharArray(a, "words");
        String[] a2S = jmatlink.engGetCharArray(a, "joe");
        String[] bS  = jmatlink.engGetCharArray(b, "words");

        assertTrue( a1S[0].equals("bar_foo") );
        assertTrue( a2S[0].equals("mickey") );
        assertTrue( bS[0].equals("hellohello") );

        jmatlink.engClose(a);
        jmatlink.engClose(b);
    }


}
