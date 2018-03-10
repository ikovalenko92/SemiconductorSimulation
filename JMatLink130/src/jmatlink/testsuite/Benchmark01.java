
package jmatlink.testsuite;

import jmatlink.*;

/*
 * This is a benchmark suite to test the power and speed of JMatLink 
 *
 */
public class Benchmark01 {
    
 public static void main(String[] args) {


  JMatLink eng = new JMatLink();

  long eP[]= {0,0,0,0,0,0,0,0,0,0};

  int tempI = 0;

  double array[][];
  double d;

  try { Thread.sleep(1000); }
  catch (InterruptedException e) {};

  // Set debug level to display useful messages
  //eng.setDebug(true);

  System.out.println("**********************************************");
  System.out.println("**         Benchmark Tests                  **");
  System.out.println("**                                          **");
  System.out.println("** If program crashed please send output to **");
  System.out.println("**   to the author.                         **");
  System.out.println("** Test: use engOpen() and engClose()       **");
  eng.engOpen();
  eng.engClose();
  
  eng.engOpen();
  eng.engClose();

  eng.engOpen();
  eng.engClose();
  
  eng.engOpen();
  eng.engClose();
  
  System.out.println("** engOpen() and engclosed called 4 times");

  try { Thread.sleep(1000); }
  catch (InterruptedException e) {};


  System.out.println("** engOpenSingleUse Test ");
  System.out.println("* open 10 connections    "); 
  for (int i=0; i<10; i++)
  {
    System.out.println("open no. "+i);
    eP[i]=eng.engOpenSingleUse();
    eng.engEvalString(eP[i],"figure(1)");

    try { Thread.sleep(50); }
    catch (InterruptedException e) {};

    eng.engEvalString(eP[i],"surf(peaks)");

  }


  // wait for 1000ms
  try { Thread.sleep(1000); }
  catch (InterruptedException e) {};



  System.out.println("** Close Test **");
  for (int i=0; i<10; i++)
  {
    System.out.println("close no. "+i);
    eng.engClose(eP[i]);

    try { Thread.sleep(100); }
    catch (InterruptedException e) {};

  }

  System.out.println("** Open Close Test: done **");

    try { Thread.sleep(10000); }
    catch (InterruptedException e) {};



  System.out.println("\n\n** engGetArray and engPutArray **");
  System.out.println("this my take some minutes");

  eng.engOpen();
  for (int i=100; i<1000; i=i+100)
  {
    eng.engEvalString("array=randn(" +i+ ")");
    array=eng.engGetArray("array");
    eng.engEvalString("clear array");
    eng.engPutArray("array",array);
    //eng.engEvalString("plot(array(:,1))");
  }
  eng.engClose();

    try { Thread.sleep(500); }
    catch (InterruptedException e) {};



  System.out.println("\n\n\n** engPutArray and engGetArray speed **");
  eng.engOpen();
  eng.engEvalString("array=randn(500)");
  eng.engEvalString("tic");
  for (int i=1; i<=10; i++)
  {
    array=eng.engGetArray("array");
    eng.engEvalString("clear array");
    eng.engPutArray("array",array);
  }
  eng.engEvalString("duration=toc;");
  System.out.println("** Duration engPut/GetArray = "+
                      eng.engGetScalar("duration")   +" s");
  System.out.println("** Data rate                = "+
                      (500*500*4*2*10)/eng.engGetScalar("duration")/1024 +
                     " kB/s");                     
  eng.engClose();



  System.out.println("\n\n** engGetScalar and engPutArray speed **");
  System.out.println("** Send and receive 10000 scalar values ");
  System.out.println("**  to/from matlab.");
  System.out.println("** (Duration on Duron 700MHz 17sec)");
  eng.engOpen();
  eng.engPutArray("array",0);
  eng.engEvalString("tic");
  for (int i=1; i<=10000; i++)
  {
    eng.engPutArray("array",i);
    d     = eng.engGetScalar("array");
  }
  eng.engEvalString("duration=toc;");
  System.out.println("** Duration engPutArray/engGetScalar = "+
                      eng.engGetScalar("duration")   +" s");
  System.out.println("** Data rate                = "+
                      (4*2*10000)/eng.engGetScalar("duration")/1024 +
                     " kB/s");                     
  System.out.println("\n\n");
  eng.engClose();



  System.out.println("** engOpenSingleUse() speed and reliability test**");
  System.out.println("** (Duration on Athlon 700MHz xx sec)");
  System.out.println("** Opening 10 connections to matlab.");
  for (int e=0; e<10; e++)
  {
    eP[e]=eng.engOpenSingleUse();
  }
  for (int e=0; e<10; e++)
  {
    eng.engEvalString(eP[e], "tic");
  }
  System.out.println("** Sending/receive 1000 scalars over each connection."); 
  for (int i=1; i<=1000; i++)
  {
    for (int e=0; e<10; e++)
    {
      eng.engPutArray(eP[e], "a", i);
      d     = eng.engGetScalar(eP[e], "a");
    }
  }
  for (int e=0; e<10; e++)
  {
    eng.engEvalString(eP[e], "duration=toc;");
    System.out.println("** Duration engPutArray/engGetScalar = "+
                        eng.engGetScalar(eP[e], "duration")   +" s");
  }                   
  System.out.println("** closing 10 connections");
  for (int e=0; e<10; e++)
  {
    eng.engClose(eP[e]);
  }                   



    try { Thread.sleep(3000); }
    catch (InterruptedException e) {};



  System.exit(0);
}
}
