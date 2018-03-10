/*
 * simple text GUI that just sends a command to MATLAB and prints the
 * ouput of MATLAB's workspace
 * */
package jmatlink.ui;

import jmatlink.*;

/**
 *
 */
public class SimpleTextGUI01 {

    public static void main(String[] args) {
        
        // create instance of engine
        JMatLink engine = new JMatLink();
        
        // open engine and start MATLAB
        engine.engOpen();

        // this should be called once to set up the buffer
        engine.engOutputBuffer();

        // test string: create array of random numbers
        String testCommand = "a=rand(4)";
        
        // send command to MATLAB
        System.out.println("sending command to matlab: " + testCommand );
        engine.engEvalString(testCommand);

        // reading output buffer of MATLAB
        System.out.println("read output buffer of matlab");
        String buffer = engine.engGetOutputBuffer();
        System.out.println(buffer);
        
        // terminate MATLAB and close engine
        engine.engClose();
        
    }
}
