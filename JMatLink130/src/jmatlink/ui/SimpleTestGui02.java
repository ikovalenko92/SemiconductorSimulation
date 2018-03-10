/*****************************************************************************
*                      TestGui for   JMatLink                                *
******************************************************************************/

package jmatlink.ui;

import java.awt.*;
import java.awt.event.*;

import jmatlink.*;

public class SimpleTestGui02 extends Frame implements ActionListener{

    Canvas   f1;
    Canvas   f2;
    
    TextField evalT;

    JMatLink engine;
   
    Button    engOpenB;
    Button    engCloseB;
    Button    getFigure1B;
    Button    getFigure2B;
    Button    exampleFigure1B;
    Button    exampleFigure2B;
    
    Button exitB;
   
    
public SimpleTestGui02(String title) {
     
    super(title);
    
    engine = new JMatLink();

    engine.setDebug( false );

    this.setLayout(new FlowLayout());

    engOpenB            = new Button("engOpen()");
    engCloseB           = new Button("engClose()");

    exampleFigure1B   = new Button("figure(1); surf(peaks)");
    exampleFigure2B   = new Button("figure(2); plot(rand(50,4))");
    getFigure1B       = new Button("getFigure(1)");
    getFigure2B       = new Button("getFigure(2)");

    exitB               = new Button("Exit"); 

    f1 = new Canvas();
    f1.setSize(200,300);
    f1.show();

    f2 = new Canvas();
    f2.setSize(300,300);
    f2.show();

    engOpenB.addActionListener(this); 
    engCloseB.addActionListener(this); 
    getFigure1B.addActionListener(this);
    getFigure2B.addActionListener(this);
    exampleFigure1B.addActionListener(this);
    exampleFigure2B.addActionListener(this);
    
    exitB.addActionListener(this);
 
   this.add(engOpenB);
   this.add(engCloseB);
   this.add(exampleFigure1B);
   this.add(exampleFigure2B);
   this.add(getFigure1B);
   this.add(getFigure2B);
   this.add(exitB);

   this.add(f1);
   this.add(f2);
   

addWindowListener(new WindowAdapter() { public void
         windowClosing(WindowEvent e) { System.exit(0); } } );

}

    

public void actionPerformed(ActionEvent evt) {  
    Object src = evt.getSource();
    String evalS;        
    String outputBufferS;
    int    outputBufferI;
    double scalar; 
    int    scalarI;
    long   EpI;
    int    retValI;

    if (src==engOpenB) 
    { 
        System.out.println("engOpen()"); 
        engine.engOpen();
    }
    else if (src==engCloseB) 
    {
        engine.engClose();
    }
    else if (src==getFigure1B){
        System.out.println("getFigure1");

        Image image = engine.engGetFigure(1, 200, 300);
        
        f1.getGraphics().drawImage(image,0,0,null);
    }
    else if (src==getFigure2B){
        System.out.println("getFigure2");

        Image image = engine.engGetFigure(2, 300, 300);
        
        f2.getGraphics().drawImage(image,0,0,null);
    }
    else if (src==exampleFigure1B) {
        engine.engEvalString("figure(1);surf(peaks)");
    }
    else if (src==exampleFigure2B) {
        engine.engEvalString("figure(2);plot(rand(50,4))");
    }
    else if (src==exitB) {
        engine.engCloseAll();
        System.exit(0);
    }
    repaint();
} // actionPerformed

    
/**
 * main class for this GUI
 * @param args not used right now
 */
    public static void main(String[] args) {
        Frame f = new SimpleTestGui02("JMatLink");
        f.setSize(700, 400);
        f.show();
    }
    
} // end SimpleTestGui.java


