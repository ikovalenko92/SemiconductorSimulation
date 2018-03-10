/*
 * Created on 19.06.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.JMatLink;
//import jmatlink.JMatLinkException;
import junit.framework.TestCase;

/**
 * @author stefan
 */
public class testEngineThread extends TestCase {

    JMatLink jmatlink = new JMatLink();
    
   
    public void testEngineThread01() {
        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engClose();
        
        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engEvalString("a=rand(4)");

        long engine1 = jmatlink.engOpenSingleUse();
        long engine2 = jmatlink.engOpenSingleUse();

        jmatlink.engClose(engine2);

        jmatlink.engCloseAll();

        jmatlink.engOpen();
        jmatlink.engEvalString("a=rand(4)");
        jmatlink.engClose();
       
    }

    // call engOpen() engClose() engOpen() engClose() in a row to check if the
    //   killing mechanism of the engine thread works properly. The thread must
    //   be completely dead before it is safe to restart it again.
    public void testEngineThread02() {
        jmatlink.engOpen();
        jmatlink.engClose();
        jmatlink.engOpen();
        jmatlink.engClose();
        jmatlink.engOpen();
        jmatlink.engClose();
       
    }

    
}
