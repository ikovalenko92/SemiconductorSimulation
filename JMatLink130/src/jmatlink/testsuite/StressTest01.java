/*
 * Created on 25.05.2005
 *
 */
package jmatlink.testsuite;

import jmatlink.JMatLink;

/**
 * @author stefan
 *
 */
public class StressTest01 {

    public static void main(String[] args) {
        
        System.out.println("Stresstest for JMatLink");
        
        System.out.println(" ");
        JMatLink engine = new JMatLink();
        engine.engOpen("startCmd=55;");
        String evalS = "";
        
        System.out.println("plotting");
        for (int i=0; i<200; i++)
        {
            engine.engEvalString("plot(rand(50,5))");
        }
        
        System.out.println("matrix creation");
        for (int i=0; i<200; i++)
        {
            evalS = "aaa=rand(5," +i+ ");";
            System.out.println(evalS);
            engine.engEvalString(evalS);
        }
        
        System.out.println("engPutArray");
        double array2d100[][]= new double[100][100];
        for (int y=0; y<100; y++)
        {
            for (int x=0; x<100; x++)
            {    
                array2d100[y][x]= Math.random();
            }
        }
        
        for (int i=0; i<1000; i++)
        {
            System.out.println("copy 100*100: time "+ i);
            engine.engPutArray("barfoo", array2d100);
        }
        
        engine.engClose();
        
    }
}
