/*
 * Created on 23.05.2005
 *
 */
package jmatlink.testsuite.jmatlink;

import jmatlink.*;
import junit.framework.TestCase;

/**
 * @author stefan
 *
 */
public class testSingleUse extends TestCase {

    JMatLink jmatlink  = new JMatLink();
    

    public void testEngOpenSingleUse01() {
        jmatlink = new JMatLink ();

        System.out.println("opening two engines");
        long engine1 = jmatlink.engOpenSingleUse();
        long engine2 = jmatlink.engOpenSingleUse();

        System.out.println("send values");
        jmatlink.engPutArray(engine1, "aaa", 1234.0);
        jmatlink.engPutArray(engine2, "aaa", 5678.0);
        
        System.out.println("receive values");
        double ret1 = jmatlink.engGetScalar(engine1,"aaa");
        double ret2 = jmatlink.engGetScalar(engine2,"aaa");
        
        assertTrue(Math.abs(ret1 - 1234.0)<0.001);
        assertTrue(Math.abs(ret2 - 5678.0)<0.001);

        jmatlink.engClose(engine1);
        jmatlink.engClose(engine2);
        System.out.println("closed two engines");
        
    }

    public void testEngOpenSingleUse02() {
        jmatlink = new JMatLink ();

        System.out.println("opening two engines");
        long engine1 = jmatlink.engOpenSingleUse();
        long engine2 = jmatlink.engOpenSingleUse();

        // send different numbers to both engines
        // variable names are constant
        for (int i=0; i<500; i++)
        {
            System.out.println("send values: time "+ i);
            
            double value1 = Math.random() * 100;
            double value2 = Math.random() * 1000;
            jmatlink.engPutArray(engine1, "aaa", value1);
            jmatlink.engPutArray(engine2, "aaa", value2);
            
            System.out.println("receive values");
            double ret1 = jmatlink.engGetScalar(engine1,"aaa");
            double ret2 = jmatlink.engGetScalar(engine2,"aaa");
            
            assertTrue(Math.abs(ret1 - value1)<0.001);
            assertTrue(Math.abs(ret2 - value2)<0.001);
        }

        jmatlink.engClose(engine2);
        jmatlink.engClose(engine1);
        System.out.println("closed two engines");
        
    }


}
