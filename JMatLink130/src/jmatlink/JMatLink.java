/*
 * This file is part of JMatLink 
 * visit JMatLink at http://jmatlink.sourceforge.net
 */

package jmatlink;

import java.io.*;
import java.awt.*;
import java.util.*;
//import java.awt.image.*;

//#if defined(V5_COMPAT)
//#define engPutArray(ep, ap)   engPutVariable(ep, mxGetName(ap), ap)
//#define engGetArray(ep, name) engGetVariable(ep, name)
//#else
//#define engPutArray() engPutArray_is_obsolete
//#define engGetArray() engGetArray_is_obsolete


public class JMatLink { //implements ImageObserver  { 

    // the core class of JMatLink
    private CoreJMatLink    coreJMatLink;

    // flag to indicate if debugging is set / unset
    private boolean         debugB       =  false;

    // current version of JMatLink
    private final String    VERSION      =  "JMatLink_V1.3.0";

    // random number generator for image generation
    private Random randomGenerator      = new Random();

    // storage for filenames of temporary created images
    private Vector imageVector = new Vector();
    
    
    /** This is the constructor for the JMatLink library. 
     *  
     * <p>E.g.:<br>
     * <pre>
     *   <b>JMatLink</b> engine = new <b>JMatLink()</b>;
     *   engine.engOpen();   
     *   engine.engEvalString("surf(peaks)");  
     *   engine.engClose();
     * </pre>
     */
    public JMatLink() {
       if (debugB) System.out.println("JMatLink constructor");

       // create an instance of the core class
       coreJMatLink = new CoreJMatLink();
    }

    /** Returns the current version of JMatLink
     * 
     * <p>E.g.:<br>
     * <pre>
     *   JMatLink engine = new JMatLink();
     *   System.out.println("version 0 "+ <b>engine.getVersion()</b>);
     * </pre>
     * @return VERSION of JMatLink
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * obsolete method
     *
     */
    public void kill() {
        //coreJMatLink.kill();
        System.out.println("JMatLink.kill() is obsolete. Automatic thread start-kill implemented.");
    }


    /**  Open engine. This command is used to open a <b>single</b> connection
     *  to matlab.
     *  
     * <p>E.g.:<br>
     * <pre>
     *   JMatLink engine = new JMatLink();
     *   engine.<b>engOpen()</b>;   
     *   engine.engEvalString("surf(peaks)");  
     *   engine.engClose();
     * </pre>
     */
    public synchronized  void  engOpen()
    {
        engOpen( "" );  
    }


    /** Open engine. This command is used to open a <b>single</b> connection
     *   to matlab.<p> This command is only useful on unix systems. On windows
     *   the optional parameter <b>must</b> be NULL.
     *  
     * <p>E.g.:<br>
     * <pre>
     *   JMatLink engine = new JMatLink();
     *   engine.<b>engOpen("commands to start matlab")</b>;   
     *   engine.engEvalString("surf(peaks)");  
     *   engine.engClose();
     * </pre>
     */
    public void engOpen(String startCmdS)
    {
        int i=0;
        
        // try to start the engine 10 times
        while (i<10)
        {    
            // check if engine is running or dead, and then try to open a new engine connection
            if ((coreJMatLink.getThreadStatus() == CoreJMatLink.THREAD_RUNNING) ||
                (coreJMatLink.getThreadStatus() == CoreJMatLink.THREAD_DEAD))
            {
                // thread is running or dead -> call engine open function
                coreJMatLink.engOpen(startCmdS);
                return;
            }
            
            // if this code part is reached the engine thread is starting or dying
            //   -> wait some time and then check all over again
            if (debugB) System.out.println("J starting or dying");
            i++;
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e){}
        }
        
        // if this part is reached the engine thread has been in a transition phase
        //    dying or starting for a very long period of time without completion
        throw(new JMatLinkException("engine still starting or dying"));

    }


    /** **************************   engOpenSingleUse   *****************************
    *  Open engine for single use. This command is used to open
    *  <b>multiple</b> connections to matlab.
    *  
    * <p>E.g.:<br>
    * <pre>
    *   long a,b;
    *   JMatLink engine = new JMatLink();
    *   a = engine.<b>engOpenSingleUse()</b>;   // start first matlab session
    *   b = engine.<b>engOpenSingleUse()</b>;   // start second matlab session
    *   engine.engEvalString(a, "surf(peaks)");  
    *   engine.engEvalString(b, "foo=ones(10,0)");
    *   engine.engClose(a);
    *   engine.engClose(b);
    * </pre>
    ***************************************************************************/
    public long engOpenSingleUse()
    {
        return engOpenSingleUse("");
    }

    
    /**Open engine for single use. This command is used to open
    *  <b>multiple</b> connections to matlab.
    *  
    * <p>E.g.:<br>
    * <pre>
    *   long a,b;
    *   JMatLink engine = new JMatLink();
    *   a = engine.<b>engOpenSingleUse("start matlab")</b>;   // start first matlab session
    *   b = engine.<b>engOpenSingleUse("start matlab")</b>;   // start second matlab session
    *   engine.engEvalString(a, "surf(peaks)");  
    *   engine.engEvalString(b, "foo=ones(10,0)");
    *   engine.engClose(a);
    *   engine.engClose(b);
    * </pre>
    ***************************************************************************/
    public long engOpenSingleUse(String startCmdS)
    {

        int i=0;

        // try to start the engine 10 times
        while (i<10)
        {    
            // check if engine is running or dead, and then try to open a new engine connection
            if ((coreJMatLink.getThreadStatus() == CoreJMatLink.THREAD_RUNNING) ||
                (coreJMatLink.getThreadStatus() == CoreJMatLink.THREAD_DEAD))
            {
                // thread is running or dead -> call engine open function
                return coreJMatLink.engOpenSingleUse(startCmdS);
            }
            
            // if this code part is reached the engine thread is starting or dying
            //   -> wait some time and then check all over again
            if (debugB) System.out.println("J starting or dying");
            i++;
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e){}
        }
        
        // if this part is reached the engine thread has been in a transition phase
        //    dying or starting for a very long period of time without completion
        throw(new JMatLinkException("engine still starting or dying"));

    }

    
    /** Close the connection to matlab.
    *
    * <p>E.g.:<br>
    * <pre>
    *  JMatLink engine = new JMatLink();
    *  engine.engOpen();
    *  engine.engEvalString("surf(peaks)");   
    *  engine.<b>engClose()</b>;  
    * </pre>
    ***************************************************************************/
    public  void engClose()
    {
        coreJMatLink.engClose( );
        deleteTempImages();
    }


    /** Close a specified connection to an instance of matlab.
    *
    * <p>E.g.:<br>
    * <pre>
    *  long a,b;
    *  JMatLink engine = new JMatLink();
    *  a = engine.engOpenSingleUse();       // start first  matlab session
    *  b = engine.engOpenSingleUse();       // start second matlab session
    *  engine.engEvalString(b, "surf(peaks)");   
    *  engine.engEvalString(a, "array = randn(23)");   
    *  engine.<b>engClose</b>(a);      // Close the first  connection to matlab
    *  engine.<b>engClose</b>(b);      // Close the second connection to matlab
    * </pre>
    ***************************************************************************/
    public  void engClose( long epI)
    {
        coreJMatLink.engClose(epI);
        deleteTempImages();
    }

    
    /** Close all connections to matlab
    * <p>E.g.:<br>
    * <pre>
    *  long a,b;
    *  JMatLink engine = new JMatLink();
    *  engine.engOpen();                    // start the general session
    *  engine.engEvalString("x=sin(5)");
    *  a = engine.engOpenSingleUse();       // start first  matlab session
    *  b = engine.engOpenSingleUse();       // start second matlab session
    *  engine.engEvalString(b, "surf(peaks)");   
    *  engine.engEvalString(a, "array = randn(23)");   
    *  engine.<b>engCloseAll</b>();      // Close all connections to matlab
    */
    public  void engCloseAll()
    {
        coreJMatLink.engCloseAll();
        deleteTempImages();
    }

    
    /** Set the visibility of the Matlab window
     * 
     * @param epI  engine handle
     * @param visB desired visiblity true/false
     */
    public  void engSetVisible(long epI, boolean visB)
    {
        coreJMatLink.engSetVisible(epI, visB);
    }

    
    /** return the visibility status of the Matlab window
     * 
     * @param epI engine handle
     * @return  visiblity true/false
     */
    public  boolean engGetVisible(long epI)
    {
        return coreJMatLink.engGetVisible(epI);
    }


    /** Evaluate an expression in matlab's workspace.
    *
    * E.g.:<br>
    * <pre>
    *   JMatLink engine = new JMatLink();
    *   engine.engOpen();   
    *   engine.<b>engEvalString</b>("surf(peaks)");
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  void engEvalString(String evalS)
    {
        coreJMatLink.engEvalString( evalS );
    }


    /** Evaluate an expression in a specified workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *  long a,b;
    *  JMatLink engine = new JMatLink();
    *  a = engine.engOpenSingleUse();  
    *  engine.<b>engEvalString</b>(a, "surf(peaks)");
    *  engine.engClose();
    * </pre>
    ***************************************************************************/
    public  void engEvalString(long epI, String evalS)
    {
        coreJMatLink.engEvalString(epI, evalS);
    }


    /** Get a scalar value from matlab's workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *  double a;
    *  JMatLink engine = new JMatLink();
    *  engine.engOpen();
    *  engine.engEvalString("foo = sin( 3 )");
    *  a = engine.<b>engGetScalar</b>("foo");  
    *  engine.engClose();
    * </pre>
    ***************************************************************************/
    public  double engGetScalar(String arrayS)
    {
        return coreJMatLink.engGetScalar(arrayS);
    }


    /** Get a scalar value from a specified workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   double a;
    *   long b;
    *   JMatLink engine = new JMatLink();
    *   b = engine.engOpenSigleUse();  
    *   engine.engEvalString(b, "foo = sin( 3 )");
    *   a = engine.<b>engGetScalar</b>(b, "foo");
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  double engGetScalar(long epI, String arrayS)
    {
        // Get scalar value or element (1,1) of an array from
        //   MATLAB's workspace  
        // Only real values are supported right now

        return coreJMatLink.engGetScalar(epI, arrayS);
    }

    //public  double[] engGetRowVector(String arrayS)
    //{
    //    return coreJMatLink.engGetScalar(arrayS);
    //}

    //public  double[] engGetColumnVector(String arrayS)
    //{
    //    return coreJMatLink.engGetScalar(arrayS);
    //}


    /** Get an array from matlab's workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   double[][] array;
    *   JMatLink engine = new JMatLink();
    *   engine.engOpen();
    *   engine.engEvalString("array = rand(10);");
    *   array = engine.<b>engGetArray</b>("array");
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  double[][] engGetArray(String arrayS)
    {
        return coreJMatLink.engGetVariable( arrayS );
    }


    /** Get an array from a specified instance/workspace of matlab.
    *
    * <p>E.g.:<br>
    * <pre>
    *   long b;
    *   double[][] array;
    *   JMatLink engine = new JMatLink();
    *   b = engine.engOpenSingleUse();
    *   engine.engEvalString(b, "array = randn(10);");
    *   array = engine.<b>engGetArray</b>(b, "array");
    *   engine.engClose(b);
    * </pre>
    ***************************************************************************/
    public   double[][]  engGetArray( long epI, String arrayS )
    {  
        return coreJMatLink.engGetVariable( epI, arrayS );
        
    }

    
    /** Get an 'char' array (string) from matlab's workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   String array;
    *   JMatLink engine = new JMatLink();
    *   engine.engOpen(); 
    *   engine.engEvalString("array = 'hello world';");
    *   array = engine.<b>engGetCharArray</b>("array");
    *   System.out.println("output = "+ array);
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  String[] engGetCharArray(String arrayS)
    {  
        // convert to double array
        engEvalString( "engGetCharArrayD=double(" + arrayS +")" );

        // get double array
        double[][] arrayD = engGetArray("engGetCharArrayD");

        // delete temporary double array
        engEvalString("clear engGetCharArrayD");

        // If array "engGetCharArrayD" does not exist in matlab's workspace
        //   immediately return null
        if (arrayD == null) return null;

        // convert double back to char
        return double2String( arrayD );
    }


    /** Get an 'char' array (string) from matlab's workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   JMatLink engine = new JMatLink();
    *   long b = engine.engOpenSingleUse(); 
    *   engine.engEvalString(b, "array = 'hello world';");
    *   String array = engine.<b>engGetCharArray</b>(b,"array");
    *   System.out.println("output = "+ array);
    *   engine.engClose(b);
    * </pre>
    ***************************************************************************/
    public  String[] engGetCharArray(long epI, String arrayS)
    {  
        // convert to double array
        engEvalString(epI, "engGetCharArrayD=double(" + arrayS +")" );

        // get double array
        double[][] arrayD = engGetArray(epI, "engGetCharArrayD");

        // delete temporary double array
        engEvalString(epI, "clear engGetCharArrayD");

        // If array "engGetCharArrayD" does not exist in matlab's workspace
        //   immediately return null
        if (arrayD == null) return null;

        // convert double back to char
        return double2String( arrayD );
    }

    
    /** Put an array into a specified workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   int array = 1;
    *   JMatLink engine = new JMatLink();
    *   engine.engOpen();  
    *   engine.<b>engPutArray</b>("array", array);
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  void engPutArray( String arrayS, int valueI )
    {
        engPutArray( arrayS, new Integer(valueI).doubleValue());
    }


    // public  void   engPutArray( String arrayS, int[] valuesI )
    // {
    //     engPutArray( this.epI, arrayS, (double[])valuesI );
    // }

    // public  void   engPutArray( String arrayS, long valuesI )
    // {
    //     engPutArray( this.epI, arrayS, (double[])valuesI );
    // }

    // public  void   engPutArray( String arrayS, long[] valuesI )
    // {
    //     engPutArray( this.epI, arrayS, (double[])valuesI );
    // }

    // public  void   engPutArray( String arrayS, byte valuesI )
    // {
    //     engPutArray( this.epI, arrayS, (double[])valuesI );
    // }

    // public  void   engPutArray( String arrayS, byte[] valuesI )
    // {
    //     engPutArray( this.epI, arrayS, (double[])valuesI );
    // }


    /** Put an array into matlab's workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   double array = 1;
    *   JMatLink engine = new JMatLink();
    *   engine.engOpen(); 
    *   engine.<b>engPutArray</b>("array", array);
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  void engPutArray( String arrayS, double valueD )
    {
        double vDD[][]   = {{0.0}};
        vDD[0][0] = valueD;
        engPutArray( arrayS, vDD );  // nxn dimensional
    }


    /** Put an array into a specified instance/workspace of matlab.
    *
    * <p>E.g.:<br>
    * <pre>
    *   long b;
    *   double array = 1;
    *   JMatLink engine = new JMatLink();
    *   b = engine.engOpenSingleUse(); 
    *   engine.<b>engPutArray</b>(b, "array", array);
    *   engine.engClose(b);
    * </pre>
    ***************************************************************************/
    public  void engPutArray( long epI, String arrayS, double valueD )
    {
        
        double vDD[][]   = {{0.0}};
               vDD[0][0] = valueD;
        engPutArray( epI, arrayS, vDD );  // nxn dimensional
    }


    /** Put an array (1 dimensional) into a specified instance/workspace of 
    *   matlab.
    *
    * <p>E.g.:<br>
    * <pre>
    *   double[] array = {1.0 , 2.0 , 3.0};
    *   JMatLink engine = new JMatLink();
    *   engine.engOpen();  
    *   engine.<b>engPutArray</b>("array", array);
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  void engPutArray(String arrayS, double[] valuesD)
    {
        double[][] vDD = new double[1][valuesD.length]; // 1xn array

        if (debugB) System.out.println("length  = "+valuesD.length);

        vDD[0] = valuesD; // copy row 

        engPutArray( arrayS, vDD );
    }


    /** Put an array (1 dimensional) into a specified instance/workspace of 
    *   matlab.
    *
    * <p>E.g.:<br>
    * <pre>
    *   long b;
    *   double[] array = {1.0 , 2.0 , 3.0};
    *   JMatLink engine = new JMatLink();
    *   b = engine.engOpenSingleUse();  
    *   engine.<b>engPutArray</b>(b, "array", array);
    *   engine.engClose(b);
    * </pre>
    ***************************************************************************/
    public  void engPutArray(long epI, String arrayS, double[] valuesD)
    {
        double[][] vDD = new double[1][valuesD.length]; // 1xn array

        if (debugB) System.out.println("length  = "+valuesD.length);

        vDD[0] = valuesD; // copy row 

        engPutArray( epI, arrayS, vDD );
    }


    /** Put an array (2 dimensional) into matlab's workspace.
    *
    * <p>E.g.:<br>
    * <pre>
    *   double[][] array={{1.0 , 2.0 , 3.0},
    *                     {4.0 , 5.0 , 6.0}};
    *   JMatLink engine = new JMatLink();
    *   engine.engOpenSingleUse();  
    *   engine.<b>engPutArray</b>("array", array);
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public  void engPutArray( String arrayS, double[][] valuesDD )
    {
        coreJMatLink.engPutVariable( arrayS, valuesDD );
    }


    /** Put an array (2 dimensional) into a specified instance/workspace of 
    *   matlab.
    *                  
    * <p>E.g.:<br>                                          
    * <pre>                                          
    *   long b;                                        
    *   double[][] array={{1.0 , 2.0 , 3.0},           
    *                     {4.0 , 5.0 , 6.0}};       
    *   JMatLink engine = new JMatLink();         
    *   b = engine.engOpenSingleUse();        
    *   engine.engPutArray(b, "array", array);         
    *   engine.engClose(b);             
    * </pre>     
    ***************************************************************************/
    public  void engPutArray(long epI, String arrayS, double[][] valuesDD)
    {
        // send an array to MATLAB
        // only real values are supported so far
        
        coreJMatLink.engPutVariable( epI, arrayS, valuesDD );
    }


    /** Return the outputs of previous commands from matlab's workspace.          
    *                                                                          
    * <p>E.g.:<br>                                                                
    * <pre>                                                                    
    *   String buffer;                                                         
    *   JMatLink engine = new JMatLink();                                    
    *   engine.engOpen();                                                   
    *   engine.<b>engOutputBuffer</b>();                              
    *   engine.engEvalString("surf(peaks)");                                   
    *   buffer = engine.<b>engGetOutputBuffer</b>();                              
    *   System.out.println("workspace " + buffer);                             
    *   engine.engClose();                                                     
    * </pre>                                                                   
    ***************************************************************************/
    public int engOutputBuffer( )
    {
        return coreJMatLink.engOutputBuffer( );
    }

    /** Return the outputs of previous commands from a specified instance/
    *   workspace form matlab.          
    *                                                                          
    * <p>E.g.:<br>                                                                
    * <pre>                                                                    
    *   String buffer;        
    *   long b;
    *   JMatLink engine = new JMatLink();                                      
    *   b= engine.engOpenSingleUse();                                                   
    *   engine.<b>engOutputBuffer</b>(b);                               
    *   engine.engEvalString("surf(peaks), a=555");                                   
    *   buffer = engine.<b>engGetOutputBuffer</b>(b);                              
    *   System.out.println("workspace " + buffer);                             
    *   engine.engClose(b);                                                     
    * </pre>                                                                   
    ***************************************************************************/
    public int engOutputBuffer(long epI)
    {
        return engOutputBuffer( epI, 10000 );
    }


    /** Return the ouputs of previous commands in matlab's workspace.          
    *
    * Right now the parameter <i>buflen</i> is not supported.
    *                                                                          
    * <p>E.g.:<br>                                                                
    * <pre>                                                                    
    *   String buffer;
    *   long b;                                                         
    *   JMatLink engine = new JMatLink();                                      
    *   b = engine.engOpen();                                                  
    *   engine.engEvalString(b, "surf(peaks)");                                   
    *   buffer = engine.<b>engOutputBuffer</b>(b, 10000);                              
    *   System.out.println("workspace " + buffer);                             
    *   engine.engClose(b);                                                     
    * </pre>                                                                   
    ***************************************************************************/
    public int engOutputBuffer( long epI, int buflenI )
    {
        return coreJMatLink.engOutputBuffer( epI, buflenI );
    }

    /** Return the outputs of previous commands from a specified instance/
     *   workspace form matlab.          
     *                                                                          
     * <p>E.g.:<br>                                                                
     * <pre>                                                                    
     *   String buffer;        
     *   JMatLink engine = new JMatLink();                                      
     *   engine.engOpen();                                                   
     *   engine.engEvalString("surf(peaks)");                                   
     *   buffer = engine.<b>engGetOutputBuffer</b>();                              
     *   System.out.println("workspace " + buffer);                             
     *   engine.engClose();                                                     
     * </pre>                                                                   
     ***************************************************************************/
     public  String  engGetOutputBuffer( )
     {
         return coreJMatLink.engGetOutputBuffer( );
     }


     /** Return the ouputs of previous commands in matlab's workspace.          
     *
     * Right now the parameter <i>buflen</i> is not supported.
     *                                                                          
     * <p>E.g.:<br>                                                                
     * <pre>                                                                    
     *   String buffer;
     *   long b;                                                         
     *   JMatLink engine = new JMatLink();                                      
     *   b = engine.engOpen();                                                  
     *   engine.engEvalString(b, "surf(peaks)");                                   
     *   buffer = engine.<b>engOutputBuffer</b>(b);                              
     *   System.out.println("workspace " + buffer);                             
     *   engine.engClose(b);                                                     
     * </pre>                                                                   
     ***************************************************************************/
     public  String  engGetOutputBuffer( long epI )
     {
         return coreJMatLink.engGetOutputBuffer( epI );
     }

     /** Return image of figure from Matlab
      * 
      * @param epI     handle to matlab engine
      * @param figure  handle to matlab figure n
      * @param dx      columns of image in pixels
      * @param dy      rows of image in pixels
      * @return        image of the requested figure window from matlab
      */
     public Image engGetFigure(long epI, int figure, int dx, int dy)
     {
         // try to delete old images, maybe they haven't been destroyed
         //  during the last generation process
         deleteTempImages();
         
         // random filename
         String tmpDirS    = System.getProperty("java.io.tmpdir");
         int    randomInt  = Math.abs(randomGenerator.nextInt());
         String imageFileS = tmpDirS + "jmatlink" + figure + "_" + randomInt + ".jpg";
         if (debugB) System.out.println("file "+imageFileS);
         
         // image creation
         engEvalString(epI, "figure("+ figure +")");
         engEvalString(epI, "set(gcf,'PaperUnits','inches')");
         engEvalString(epI, "set(gcf,'PaperPosition',[0,0,"+ dx/100 +","+ dy/100 +"])");
         engEvalString(epI, "print("+ figure +",'-djpeg100','-r100','" +imageFileS+ "')");
         
         // start loading the image via the default toolkit
         // use a media tracker to wait until the image is completely loaded
         Image        image   = Toolkit.getDefaultToolkit().createImage(imageFileS);
         Frame        iframe  = new Frame();
         MediaTracker tracker = new MediaTracker(iframe);
         tracker.addImage(image,0);
         try{
             tracker.waitForID(0);
             }
         catch (InterruptedException e) {}
         //System.out.println("tracker: image loaded: "+tracker.checkID(0));
         iframe = null;

         // add filename to image vector to keep track of created images/files.
         imageVector.add(imageFileS);
         
         // return loaded image
         return image;
     }
     
     /** Return image of figure from Matlab
      * 
      * @param figure  handle to matlab figure n
      * @param dx      columns of image in pixels
      * @param dy      rows of image in pixels
      * @return        image of the requested figure window from matlab
      */
     public Image engGetFigure(int figure, int dx, int dy)
     {
         // try to delete old images, maybe they haven't been destroyed
         //  during the last generation process
         deleteTempImages();
         
         // random filename
         String tmpDirS    = System.getProperty("java.io.tmpdir");
         int    randomInt  = Math.abs(randomGenerator.nextInt());
         String imageFileS = tmpDirS + "jmatlink" + figure + "_" + randomInt + ".jpg";
         if (debugB) System.out.println("file "+imageFileS);
         
         // image creation
         engEvalString("figure("+ figure +")");
         engEvalString("set(gcf,'PaperUnits','inches')");
         engEvalString("set(gcf,'PaperPosition',[0,0,"+ dx/100 +","+ dy/100 +"])");
         engEvalString("print("+ figure +",'-djpeg100','-r100','" +imageFileS+ "')");
         
         // start loading the image via the default toolkit
         // use a media tracker to wait until the image is completely loaded
         Image        image   = Toolkit.getDefaultToolkit().createImage(imageFileS);
         Frame        iframe  = new Frame();
         MediaTracker tracker = new MediaTracker(iframe);
         tracker.addImage(image,0);
         try{
             tracker.waitForID(0);
             }
         catch (InterruptedException e) {}
         //System.out.println("tracker: image loaded: "+tracker.checkID(0));
         iframe = null;

         // add filename to image vector to keep track of created images/files.
         imageVector.add(imageFileS);
         
         // return loaded image
         return image;
     }
     
     // delete all temporary images which have been created for engGetFigure
     private void deleteTempImages()
     {
         // start counting backwards, in order to remove images from the top
         //  (otherwise vector positions are deleted and size is shrinking during
         //   deletion of elements)
         for (int i=imageVector.size()-1; i>=0; i--)
         {
             String name   = (String)(imageVector.elementAt(i));
             //System.out.println("image "+name);
             File   imageF = new File(name);
             
             // check if 
             //if (imageF.exists())
             //{
                 // try to delete image
                 boolean status = imageF.delete();
                 //System.out.println("imageVector("+imageVector.size()+"): del="+i+" status:"+status);

                 // if image has been deleted successfully remove from vector
                 if (status)
                     imageVector.remove(name);
             //}
         }
         
     }
     
     //public boolean imageUpdate(Image img, int x, int y, int z, int a, int b){
     //    System.out.println("image Update "+x+" "+y+z+a+b);
     //    return true;
     //}

    /** Switch on or disable debug information printed to standard output.
    *
    * <p>Default setting is debug info disabled.
    * <p>E.g.:<br>
    * <pre>
    *   JMatLink engine = new JMatLink();
    *   engine.engOpenSingleUse();  
    *   engine.<b>setDebug(true)</b>;
    *   engine.engEvalString("a=ones(10,5);");
    *   engine.engClose();
    * </pre>
    ***************************************************************************/
    public void setDebug( boolean debugB )
    {
        coreJMatLink.setDebug( debugB ); 
    }


////////////////////////////////////////////////////////////////////////////////
////                        Utility methods                                 ////


  // Convert an n*n double array to n*1 String vector
  private String[] double2String(double[][] d) 
  {

      // Check if array is not empty
      if (d == null)
      {
          /* double[][] array is NULL */
          return null;
      }

      String encodeS[]=new String[d.length];  // String vector

      // for all rows
      for (int n=0; n<d.length; n++){
          byte b[] = new byte[d[n].length]; 
          // convert row from double to byte
          for (int i=0; i<d[n].length ;i++) b[i]=(byte)d[n][i];
     
          // convert byte to String
          try { encodeS[n] = new String(b, "UTF8");}
          catch (UnsupportedEncodingException e) {}
      }
      return encodeS;
  } // end double2String

} // end class JMatLink