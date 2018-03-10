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
public class EnduranceTest01 {

    public static void main(String[] args) {
        
        System.out.println("endurance test for JMatLink");
        System.out.println("**********************************");
        System.out.println("** THIS TEST DOES NOT TERMINATE **");
        System.out.println("**  use CTRL+C to terminate !!! **");
        System.out.println("**  check your resource and system monitor **\n\n");
        
        System.out.println("creating instance");
        JMatLink engine = new JMatLink();

        System.out.println("opening engine");
        engine.engOpen();
        
        System.out.println("creating array");
        int N = 100;
        double array2d100[][]= new double[N][N];
        for (int y=0; y<N; y++)
        {
            for (int x=0; x<N; x++)
            {    
                array2d100[y][x]= Math.random();
            }
        }
        

        System.out.println("engPutArray");
        //engine.engPutArray("barfoo", array2d100);

        double array2dret[][] = new double[N][N];
        double i=0;
        String output="";
        double x = 0.0;
        while(true)
        {
            i++;
            System.out.println("copy "+N+"*"+N+": time "+ i);

            //engine.engOpen();
            
            engine.engPutArray("barfoo", array2d100);

            engine.engEvalString("hello=sin(3)");

            x = engine.engGetScalar("hello");
            
            //output = engine.engOutputBuffer();
            
            //array2dret = engine.engGetArray("barfoo");

            //engine.engClose();
        }
        
        //engine.engClose();
        
    }
}
