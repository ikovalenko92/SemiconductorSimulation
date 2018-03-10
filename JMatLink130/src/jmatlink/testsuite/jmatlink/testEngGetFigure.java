package jmatlink.testsuite.jmatlink;

import java.awt.Image;

import jmatlink.JMatLink;
//import jmatlink.JMatLinkException;
import junit.framework.TestCase;

/**
 * @author stefan
 *
 */
public class testEngGetFigure extends TestCase {
    JMatLink jmatlink = new JMatLink ();


    public void testEngGetFigure01() {
        jmatlink.engOpen();
        
        jmatlink.engEvalString("figure(1);surf(peaks);");
        Image image = jmatlink.engGetFigure(1, 200, 300);
        jmatlink.engClose();
       
    }

    public void testEngGetFigure02() {
        jmatlink.engOpen();
        
        for (int i=0; i<10; i++)
        {
            jmatlink.engEvalString("figure(1);plot(rand(4,50));");
            Image image = jmatlink.engGetFigure(1, 200, 300);
        }
        jmatlink.engClose();
    }

    public void testEngGetFigure03() {
        long a = jmatlink.engOpenSingleUse();
        long b = jmatlink.engOpenSingleUse();
        
        for (int i=0; i<10; i++)
        {
            jmatlink.engEvalString(a,"figure(1);plot(rand(4,50));");
            jmatlink.engEvalString(b,"figure(1);plot(rand(5,50));");
            Image image1 = jmatlink.engGetFigure(a,1, 200, 300);
            Image image2 = jmatlink.engGetFigure(b,1, 200, 300);
        }
        jmatlink.engCloseAll();

    }

    
}
