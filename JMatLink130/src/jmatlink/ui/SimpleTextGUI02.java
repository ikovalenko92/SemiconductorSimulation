/*
 * Created on 11.06.2005
 *
 */
package jmatlink.ui;

import jmatlink.*;
import java.io.*;

/**
 * @author stefan
 *
 */
public class SimpleTextGUI02 {

    public static void main(String[] args) {
        
        JMatLink engine = new JMatLink();
        
        engine.engOpen();
        engine.engOutputBuffer();
        
        
        BufferedReader in = new BufferedReader( new InputStreamReader(System.in));
        
        String input = "";

        while (true)
        {
            try {
                System.out.print("input:");
                input = in.readLine();
            }
            catch (Exception e){;}

            if (input.equals("end") || input.equals("exit"))
                break;
            
            engine.engEvalString(input);


            System.out.println(engine.engGetOutputBuffer());
            
        }
        
        engine.engClose();
        
    }
}
