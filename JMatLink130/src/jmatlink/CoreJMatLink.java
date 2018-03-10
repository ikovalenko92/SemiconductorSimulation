/*
 * This file is part of JMatLink. 
 * This class is the core class of JMatLink. It makes the connection to the "C"-part
 * of the project. Only really necessary methods are in this class. All other 
 * functionality is inside JMatLink.
 *
 */

//****************************************************************************
// The MATLAB-engine must only be called from the SAME thread                *
//   all the time!! On Windows systems the ActiveX implementation is         *
//   supposed to be the reason for this. I don't know if that happens        *
//   on other platforms, too.                                                *
// To achieve this. All native methods of this class, all accesses to        *
//   the engine, are called from the SAME thread. Since I don't know         *
//   how to call them directly I set up a mechanism to send messages         *
//   to that thread and ask it to process all requests. Some locking         *
//   mechanism locks up the engine for each single call in order             *
//   to stay out of concurrent situations / accesses.                        *
//The problem is ActiveX: one only can make calls to the engine library
//(especially engEvalString) from ONE single thread. 
//****************************************************************************

package jmatlink;

//import java.io.*;
import java.util.Vector;


// the following functions are available in engine.h of MATLAB
//extern Engine *engOpenSingleUse(const char *startcmd, void *reserved, int *retstatus );
//extern mxArray *engGetVariable( Engine  *ep, const char *name   );
//extern int engPutVariable( Engine *ep,  const char *var_name, const mxArray *ap           );
//extern int engOutputBuffer( Engine *ep, char *buffer, int buflen    );

//#define engOpenV4()      cannot_call_engOpenV4
//#define engGetFull()     engGetFull_is_obsolete
//#define engPutFull()     engPutFull_is_obsolete
//#define engGetMatrix()   engGetMatrix_is_obsolete
//#define engPutMatrix()   engPutMatrix_is_obsolete

// obsolete
//#define engPutArray(ep, ap)   engPutVariable(ep, mxGetName(ap), ap)
//#define engGetArray(ep, name) engGetVariable(ep, name)


public class CoreJMatLink extends Thread { 

   // static declarations
   // the variable "status" is used to tell the main
   //   thread what to do.
   private final static int idleI                =   0;
   private final static int engOpenI             =   1;  
   private final static int engCloseI            =   2;  
   private final static int engEvalStringI       =   3;
   private final static int engGetScalarI        =   4;
   private final static int engGetVariableI      =   5;
   private final static int engPutVariableI      =   6;
   private final static int engOutputBufferI     =   7;
   private final static int engGetOutputBufferI  =   8;
   //private final static int engGetCharArrayI     =   9;  
   private final static int destroyJMatLinkI     =  10;
   private final static int engOpenSingleUseI    =  11;
   private final static int engSetVisibleI       =  12;
   private final static int engGetVisibleI       =  13;

   // All variables are global to allow all methods
   //   and the main thread to share all data
   private int              status           =   idleI;
   private String           arrayS;
   private String           engEvalStringS;
   private int              engOutputBufferInt;
   private String           engOutputBufferS;
   private double           engGetScalarD;
   private double[][]       engGetVariableD;
   private double[][]       engPutVariable2dD;
   //private String[]         engGetCharArrayS;
   private long             epL;                 /* Engine pointer */
   private int              retValI;             /* return Value of eng-methods */
   private String           startCmdS;           /* start command for engOpen... */
   private int              buflenI;             /* output buffer length         */
   private boolean          debugB            =  false;
   private boolean          engVisB           =  false;
   
   // Locks
   private boolean          lockEngineB       =  false;
   private boolean          lockThreadB       =  false;
   private boolean          lockWaitForValueB =  false;
                                                        
   public  static final int THREAD_DEAD     = 0;
   public  static final int THREAD_STARTING = 1;
   public  static final int THREAD_RUNNING  = 2;
   public  static final int THREAD_DYING    = 3;
   private              int threadStatus    = THREAD_DEAD; 
   
   // vector for all open pointers to MATLAB engines
   private Vector          enginePointerVector   = new Vector();

   
   private long             engOpenPointerL   = 0; // pointer for engOpen() without pointer as argument
   
   private Thread runner;

   // ***********************  native declarations  ****************************
   // NEVER call native methods directly, like
   //   JMatLink.engEvalStringNATIVE("a=1"). MATLAB's engine has quite some 
   //   thread problems. 
   private native void       engTestNATIVE();    

   private native long       engOpenNATIVE          (String startCmdS );
   private native long       engOpenSingleUseNATIVE (String startCmdS );

   private native int        engCloseNATIVE         (long epL);

   private native int        engSetVisibleNATIVE    (long epL, boolean visB);
   private native int        engGetVisibleNATIVE    (long epL);

   private native int        engEvalStringNATIVE    (long epL, String evalS );

   private native double     engGetScalarNATIVE     (long epL, String nameS );
   private native double[][] engGetVariableNATIVE   (long epL, String nameS );

   private native void       engPutVariableNATIVE   (long epL, String matrixS, double[][] valuesDD);

   private native int        engOutputBufferNATIVE   (long epL, int buflenI );
   private native String     engGetOutputBufferNATIVE(long epL );

   private native void       setDebugNATIVE         (boolean debugB       );

   // *******************************************************************************
   // **************     load JMatLink library into memory     **********************
   static {
      try { //System.out.println("loading");
            System.loadLibrary("JMatLink");
            //System.loadLibrary("jmatlink");
            //System.out.println("loaded"); 
          }
      catch (UnsatisfiedLinkError e) {
          
          // print some useful information for fault tracking
          System.out.println("ERROR:  Could not load the JMatLink library");
          System.out.println(" Win:   This error occures, if the path to");
          System.out.println("        MATLAB's <matlab>\\bin directory is");
          System.out.println("        not set properly.");
          System.out.println("        Or if JMatLink.dll is not found.\n");
          System.out.println(" Linux: Check if <matlab>/extern/lib/glnx86 (libeng.so, libmat.so, etc.)");
          System.out.println("        and <matlab>/sys/os/glnx86 (libstdc++-libc6.1-2.so.3) are in your path.\n");
          System.out.println("        (you can also copy missing libraries to your local path).\n");

          System.out.println("**** Find important information below ****");
          String os_name = System.getProperty("os.name");
          System.out.println("OS Name        = "+ os_name);

          String libpathnames = System.getProperty("java.library.path");
          System.out.println("Libpathnames   = "+libpathnames);
          
          String classpathnames = System.getProperty("java.classpath");
          System.out.println("Classpathnames = "+classpathnames);
          
          String os_dependant_lib_file_name = System.mapLibraryName("JMatLink");
          System.out.println("os dependant lib file name = "+ os_dependant_lib_file_name);

          System.out.println("**** Copy all above text and send it to ****");
          System.out.println("****    stefan@held-mueller.de          ****");
          System.out.println("**** for inspection and fault tracking  ****");
          
      }
   }

    /** This is the constructor for the CoreJMatLink library. 
     */
   public CoreJMatLink() {
       if (debugB) System.out.println("JMatLink constructor");
   }
   
   /** this restarts the engine thread
    * 
    */
   private void restart()
   {
       
       // check if thread is already running, if yes: do not restart thread
       if ((threadStatus == THREAD_RUNNING ) ||
           (threadStatus == THREAD_STARTING)    )
           return;
       
       lockEngineLock();
       
       
       // check if thread is still dyning and not finally dead
       //if (destroyJMatLinkB == false)
       if (threadStatus != THREAD_DEAD )
           throw(new JMatLinkException("engine still open "+threadStatus));

       
       // thread is not running -> restart
       threadStatus = THREAD_STARTING;
       runner = new Thread(this);
       runner.start();

       releaseEngineLock();
       
       if (debugB) System.out.println("JMatLink restarted");
   }

   /** closing of the engine thread takes some time. During this time NO call 
    *    to engOpen() or engOpenSingleUse() MUST be made.
    * 
    * @return status of the CoreJMatLink thread (DEAD, STARTING, DYING, RUNNING)
    */
   public int getThreadStatus()
   {
       return threadStatus;
   }

    /** this kills the engine thread
     *
     */
    public void kill() {
 
        lockEngineLock();
        
        // set current thread status from RUNNING to DYING
        threadStatus = THREAD_DYING;

        callThread(destroyJMatLinkI);
    
        runner = null;
        
        releaseEngineLock();
            
        if (debugB) System.out.println("JMatLink kill");
    }

    /** Returns the number of currently opened engines
     * 
     * @return number of open engines
     */
    public int getNoOfEngines()
    {
        return enginePointerVector.size();
    }
    
    /** Open engine. This command is used to open a <b>single</b> connection
     *  to MATLAB.<p> This command is only useful on unix systems. On windows
     *  the optional parameter <b>must</b> be NULL.
     *  This method is used in conjunction with engClose()
     * @param startCmdS start command for engine. Does not work on Windows.
     */
    public synchronized void engOpen(String startCmdS)
    {
        // check if engOpen() has been called before
        if (engOpenPointerL != 0)
            throw(new JMatLinkException("engine already open"));

        // restart thread, maybe it has been killed before
        restart();
        
        lockEngineLock();             
        lockWaitForValue();

        this.startCmdS = startCmdS;

        callThread( engOpenI );    

        WaitForValue();
        releaseEngineLock();  

        // if native method return 0, then engine didn't open
        if (engOpenPointerL==0)
            throw(new JMatLinkException("couldn't open engine"));
        
        // Check if engine pointer is already in use
        //  This must never be true, since we keep track of the pointer
        //  in CoreJMatLink. But we are checking anyway, because the 
        //  pointer is created in the "C"-part
        if (enginePointerVector.contains(new Long(engOpenPointerL)))
            throw(new JMatLinkException("pointer already in use "+engOpenPointerL));

        // store handle to engine in vector
        enginePointerVector.add(new Long(engOpenPointerL));

    } // end engOpen

    /** Open engine for single use. This command is used to open
     *  <b>multiple</b> connections to MATLAB.
     *  This method is used in conjunction with engClose( enginePointer )
     * @param startCmdS start command for engine. Does not work on Windows.
     */
    public synchronized long engOpenSingleUse(String startCmdS)
    {
        // restart thread, maybe it has been killed before
        restart();
        
        lockEngineLock();             
        lockWaitForValue();

        this.startCmdS = startCmdS;

        callThread( engOpenSingleUseI );    

        WaitForValue();
        releaseEngineLock();

        // if native method returns 0, then engine didn't open
        if (this.epL==0)
                throw(new JMatLinkException("couldn't open engine"));

        // Check if engine pointer is already in use.
        //  This must never be true, since we keep track of the pointer
        //  in CoreJMatLink. But we are checking anyway, because the 
        //  pointer is created in the "C"-part
        if (enginePointerVector.contains(new Long(epL)))
            System.out.println("engine pointer already in use "+epL);

        // store handle to engine in has map
        enginePointerVector.add(new Long(epL));

        return this.epL; 
        
    } // end engOpenSingleUse


    /** Close the connection to MATLAB. 
     *  This method is used in conjunction with engOpen()
     */
    public synchronized void engClose()
    {
        // close conncection to general engine
        engClose( engOpenPointerL );

        // set marker of engOpen() to 0
        engOpenPointerL = 0;

    } // end engClose

    /** Close a specified connection to an instance of MATLAB.
     * @param epL close one connection to the engine, with pointer epL
     */
    public synchronized void engClose(long epL)
    {
        // check if handle exists and is still open
        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("handle does not exist"));

        // remove buffer for engOutputBuffer
        engOutputBuffer(epL, 0);
        
        lockEngineLock();
        lockWaitForValue();

        this.epL = epL;

        callThread( engCloseI );

        WaitForValue();
        releaseEngineLock();

        // if retValI returns <>0, then command didn't work 
        if (retValI!=0)
            throw(new JMatLinkException("engClose didn't work"));
        
        // remove handle to engine from hash table
        enginePointerVector.remove(new Long(epL));
        
        // check if all engines are closed, if yes terminate JMatLink-thread
        if (enginePointerVector.isEmpty())
            kill();
        
        // return retValI; Return value indicates success
    }

    /**
     * Close all open handles to MATLAB. This methods closes all connections, which
     * have been opened using engOpen() and engOpenSingleUse().
     *
     */
    public synchronized void engCloseAll( )
    {
        
       // loop until all connections to the engine are closed/removed
       while(!enginePointerVector.isEmpty())
       {
           // get pointer from vector of pointers
           long pointer = ((Long)enginePointerVector.elementAt(0)).longValue();
           
           // call closing function
           engClose(pointer);

           // remove pointer from vector of pointers
           enginePointerVector.remove(new Long(pointer));

           // if pointer to general engine has been removed, clear general pointer
           if (engOpenPointerL == pointer)
               engOpenPointerL = 0;
      
       } // end while

       // check if all engines are closed, if yes terminate JMatLink-thread
       //if (enginePointerVector.isEmpty())
       //    kill();

       
    } // end engCloseAll

    /**
     * 
     * @param epL
     * @param engVisB
     */
    public synchronized void engSetVisible(long epL, boolean engVisB)
    {
        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));
        

        lockEngineLock();
        lockWaitForValue();

        this.epL       = epL;
        this.engVisB   = engVisB;

        callThread( engSetVisibleI );

        WaitForValue();
        releaseEngineLock();

        // if retValI returns <>0, then command didn't work (e.g. engine closed)
        if (retValI!=0)
            throw(new JMatLinkException("engSetVisibility didn't work"));

    } // end engSetVisible

    /**
     * 
     * @param epL
     * @return
     */
    public synchronized boolean engGetVisible(long epL)
    {
        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));
       
        lockEngineLock();
        lockWaitForValue();

        this.epL       = epL;

        callThread( engGetVisibleI );

        WaitForValue();
        releaseEngineLock();

        System.out.println("retValI " + retValI);
        
        if (retValI==0)
            return false;
        else if (retValI==1)
            return true;
        else
            throw(new JMatLinkException("engGetVisibility didn't work"));

    } // end engGetVisible

    /** Evaluate an expression in MATLAB's workspace. This function is used for the 
     *  general engine connection. Usage is in conjuction with engOpen(), engClose()
     *
     * @param evalS String which contains MATLAB commands (e.g. "sin(a);b=rand(3,4)")
     */
    public synchronized void engEvalString(String evalS)
    {
        if (debugB) System.out.println("engopenPointerL eval "+engOpenPointerL);
        engEvalString( engOpenPointerL, evalS);
    }


    /** Evaluate an expression in MATLAB's workspace. This function is used for all 
     *  connections to MATLAB which have been opened by epL=engOpenSingleUse(), engClose(epL)
     *
     * @param epL pointer to individual connection to the engine
     * @param evalS String which contains MATLAB commands (e.g. "sin(a);b=rand(3,4)")
     */
    public synchronized void engEvalString(long epL, String evalS)
    {
        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));
        
        // evaluate expression "evalS" in specified engine Ep
        if (debugB) System.out.println("eval(ep,String) in  " + epL + " "+evalS);
        lockEngineLock();
        lockWaitForValue();

        this.epL       = epL;
        engEvalStringS = evalS;

        callThread( engEvalStringI );

        WaitForValue();
        releaseEngineLock();

        // if retValI returns <>0, then command didn't work (e.g. engine closed)
        if (retValI!=0)
            throw(new JMatLinkException("engEvalString didn't work"));

        if (debugB) System.out.println("eval(ep,String) out "+epL+" "+evalS);

    } // end engEvalString


    /** Get a scalar value from MATLAB's workspace.
     * @param scalar variable in MATLAB's workspace
     */
    public synchronized double engGetScalar(String arrayS)
    {
        return engGetScalar( engOpenPointerL, arrayS);
    }


    /** Get a scalar value from a specified workspace.
     *
     * @param epL pointer to individual connection to the engine
     * @param evalS String which contains MATLAB commands (e.g. "sin(a);b=rand(3,4)")
     * @return scalar value from the workspace
     */
    public synchronized double engGetScalar(long epL, String arrayS)
    {
        // Get scalar value or element (1,1) of an array from
        //   MATLAB's workspace  
        // Only real values are supported right now

        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));

        lockEngineLock();
        lockWaitForValue();

        /* copy parameters to global variables */
        this.epL    = epL;
        this.arrayS = arrayS;

        callThread( engGetScalarI );

        WaitForValue();
        releaseEngineLock();

        return engGetScalarD;
    } // end engGetScalar

    /** Get an array from MATLAB's workspace.
     *
     * @param
     */
    public synchronized  double[][] engGetVariable(String arrayS )
    {
        return engGetVariable(engOpenPointerL, arrayS );
    }

    /** Get an array from a specified instance/workspace of MATLAB.
     * @param
     * @param
     */
    public synchronized double[][] engGetVariable(long epL, String arrayS )
    {  
        // only real values are supported so far

        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));

        lockEngineLock();
        lockWaitForValue();

        this.epL    = epL;
        this.arrayS = arrayS;

        callThread( engGetVariableI );
        WaitForValue();
        releaseEngineLock();

        return engGetVariableD;
    }
 

    /** Put an array (2 dimensional) into MATLAB's workspace.
     * @param
     * @param
     */
    public synchronized void engPutVariable( String arrayS, double[][] valuesDD )
    {
        engPutVariable( engOpenPointerL, arrayS, valuesDD );
    }


    /** Put an array (2 dimensional) into a specified instance/workspace of 
     *   MATLAB.
     *                  
     * @param
     * @param
     * @param
     */
    public synchronized void engPutVariable(long epL, String arrayS, double[][] valuesDD)
    {
        // send an array to MATLAB
        // only real values are supported so far
 
        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));

        lockEngineLock();
        lockWaitForValue();

        this.epL            = epL;
        this.arrayS         = arrayS;
        this.engPutVariable2dD = valuesDD;

        callThread( engPutVariableI );

        WaitForValue();
        releaseEngineLock();
    }

    /** Return the outputs of previous commands from MATLAB's workspace.          
     *                                                                          
     * @return
     */
    public synchronized int engOutputBuffer( )
    {
        if (!enginePointerVector.contains(new Long(engOpenPointerL)))
            throw(new JMatLinkException("engine unknown"));

        return engOutputBuffer( engOpenPointerL, 10000 );
    }

    /** Return the ouputs of previous commands in MATLAB's workspace.          
     *
     * 
     * @param
     * @param
     * @return
     */
    public synchronized int engOutputBuffer( long epL, int buflenI )
    {
        // get the output buffer from MATLAB

        if (!enginePointerVector.contains(new Long(epL)))
            throw(new JMatLinkException("engine unknown"));

        lockEngineLock();
        lockWaitForValue();

        this.epL     = epL;
        this.buflenI = buflenI;

        callThread( engOutputBufferI );

        WaitForValue();
        releaseEngineLock();

        return engOutputBufferInt;
    }

    /** Return the outputs of previous commands from MATLAB's workspace.          
     *                                                                          
     */
     public synchronized String engGetOutputBuffer( )
     {
         return engGetOutputBuffer( engOpenPointerL );
     }

     /** Return the ouputs of previous commands in MATLAB's workspace.          
     *
     * Right now the parameter <i>buflen</i> is not supported.
     * @param
     * @return
     */
     public synchronized String engGetOutputBuffer( long epL )
     {
         // get the output buffer from MATLAB

         if (!enginePointerVector.contains(new Long(epL)))
             throw(new JMatLinkException("engine unknown"));

         lockEngineLock();
         lockWaitForValue();

         this.epL     = epL;

         callThread( engGetOutputBufferI );

         WaitForValue();
         releaseEngineLock();

         return engOutputBufferS;
     }


    /** Switch on or disable debug information printed to standard output.
     */
    public void setDebug( boolean debugB )
    {
        this.debugB = debugB;
        setDebugNATIVE( debugB ); 
    }


    ////////////////////////////////////////////////////////////////////////////////
    // This method notifys the main thread to call MATLAB's engine
    //    Since threads don't have methods, we set a variable which
    //    contains the necessary information about what to do.
    private synchronized void callThread(int status)
    {
        this.status = status;
        lockThreadB = false;
        notifyAll();
    }


////////////////////////////////////////////////////////////////////////////////
// The run methods does ALL calls to the native methods
// The keyword "synchronized" is neccessary to block the run()
//    method as long as one command needs to get executed.
public synchronized void run()
{ 
    //int tempRetVal;

    threadStatus = THREAD_RUNNING;

    if (debugB) System.out.println("JMatLink: thread is running");  
    while (true) {
        // System.out.println("Number of Java-Threads: "+Thread.activeCount()+"");
        // Thread thread = Thread.currentThread();
        // System.out.println("Name of active Java-Threads: "+thread.getName()+"");
        // System.out.println("active Java-Thread is Daemon: "+thread.isDaemon();+"");  
 
     if (debugB) System.out.println("JMatLink run status: "+status);

     switch (status) {
     case engOpenI:          engOpenPointerL = engOpenNATIVE( startCmdS );
                             releaseWaitForValue();
                             break;

     case engOpenSingleUseI: epL = engOpenSingleUseNATIVE( startCmdS );
                             releaseWaitForValue();
                             break;

     case engCloseI:         retValI = engCloseNATIVE( epL );
                             releaseWaitForValue();
                             break;

     case engSetVisibleI:    retValI = engSetVisibleNATIVE( epL, engVisB );
                             releaseWaitForValue();
                             break;

     case engGetVisibleI:    retValI = engGetVisibleNATIVE( epL );
                             releaseWaitForValue();
                             break;

     case engEvalStringI:    retValI = engEvalStringNATIVE(epL, engEvalStringS);
                             releaseWaitForValue();
                             break;

     case engGetScalarI:     engGetScalarD  = engGetScalarNATIVE(epL, arrayS );
                             releaseWaitForValue();
                             break;

     case engGetVariableI:   engGetVariableD  = engGetVariableNATIVE(epL, arrayS );
                             releaseWaitForValue();
                             break;

     case engPutVariableI:   engPutVariableNATIVE( epL, arrayS, engPutVariable2dD );
                             releaseWaitForValue();
                             break;

     case engOutputBufferI:  engOutputBufferInt = engOutputBufferNATIVE( epL, buflenI );
                             releaseWaitForValue();
                             break;

     case engGetOutputBufferI:  engOutputBufferS = engGetOutputBufferNATIVE( epL );
                             releaseWaitForValue();
                             break;

     default:                //System.out.println("thread default switch statem.");
     }  
     status=0;

     lockThreadB = true;
     while (lockThreadB == true) {
       synchronized(this) {
          try {
                  if (debugB) System.out.println("JMatLink:wait"); 
                  wait();
              } // wait until next command is available
          catch (InterruptedException e) {}
       }
     }
     
     //System.out.println("JMatLink: thread awoke and passed lock"); 
     if (threadStatus == THREAD_DYING) break;
   } // end while
    
   // thread has finally terminated 
   threadStatus = THREAD_DEAD;
   if (debugB) System.out.println("JMatLink: thread terminated");
} // end run


////////////////////////////////////////////////////////////////////////////////
// The MATLAB engine is served by a thread. Threads don't have methods
//   which can be called. So we need to send messages to that thread
//   by using notifyAll. In the meantime NO OTHER methods is allowed to
//   access our thread (engine) so we lock everything up. 
   private void lockEngineLock(){
      synchronized(this){
         while (lockEngineB==true){
            try { //System.out.println("lockEngineLock locked");
                  wait();} // wait until last command is finished
            catch (InterruptedException e) { }
         }
         //now lockEngineB is false
         lockEngineB = true;   
      }
   } // end lockEngine

   private synchronized void releaseEngineLock(){
      lockEngineB = false;
      notifyAll();
   }

////////////////////////////////////////////////////////////////////////////////
// The MATLAB engine is served by a thread. Threads don't have methods
//    which can be called directly. If we send a command that returns data
//    back to the calling function e.g. engGetArray("array"), we'll notify
//    the main thread to get the data from MATLAB. Since the data is collected
//    in another thread, we don't know exactly when the data is available, since
//    this is a concurrent situation. 
//    The solution is simple: I always use a locking-mechanism to wait for the
//    data. The main thread will release the lock and the calling method can
//    return the data.
//
//    Steps:
//    1. a method that returns data calls the locking method
//    2. notify the thread to call MATLAB
//    3. wait for the returned data
//    4. after the thread itself got the data it releases the locks method
//    5. return data

   private synchronized void lockWaitForValue(){
      lockWaitForValueB = true;
   }

   private void WaitForValue(){
      synchronized(this){
         while (lockWaitForValueB==true){
            try { //System.out.println("lockWaitForValue locked");
                  wait();} // wait for return value
            catch (InterruptedException e) { }
         }
      }
      //System.out.println("WaitForValue released");
   } 

   private synchronized void releaseWaitForValue(){
      lockWaitForValueB = false;
      notifyAll();
   }

} // end class JMatLink
