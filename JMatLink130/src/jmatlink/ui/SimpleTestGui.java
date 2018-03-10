/*****************************************************************************
*                      TestGui for   JMatLink                                *
******************************************************************************/

package jmatlink.ui;

import java.awt.*;
import java.awt.event.*;

import jmatlink.*;

public class SimpleTestGui extends Frame implements ActionListener{

    MenuBar menubar;                // the menubar
    Menu file, help;                // menu panes
    Choice choice;
    TextArea  eqnTA;             
    Frame   eqnF;
    FileDialog file_dialog;
    
    TextField evalT;

    JMatLink engine;
   
    Button    engOpenB;
    Button    engOpenSingleUseB;
    TextField engPointerT;
    Button    engCloseB;
    Button    engCloseEngPB;
    Button    engCloseAllB;
    Button    engSetVisibleB;
    Button    engGetVisibleB;
    Button    engEvalStringB;
    Button    engEvalStringEpB;
    Button    outputBufferB; 
    Button    outputBufferEB; 
    Button    engGetOutputBufferEB; 
    Button    engGetOutputBufferB; 
    Button    engPutArrayB;
    Button    engGetScalarB; 
    Button    engGetVectorB;
    Button    engGetArrayB;
    Button    testB;
    Button    charArrayB;
    Button    engSetDebugB;
    Button    getFigureB;

    Button okay, cancel;            // buttons
    Button exitB;
   
    double testarray[]={1,2,3,4,5.555,6,7,8.1234};
    double testarray2d[][]={{1,2,3},{4,5,6}};
    double[][] array2d; // = new double[4][4];

    boolean debugToggleB = false;
    
public SimpleTestGui(String title) {
     
    super(title);
    
    engine = new JMatLink();

    engine.setDebug( false );

    // Create the menubar.  Tell the frame about it.
    menubar = new MenuBar();
    this.setMenuBar(menubar);
    // Create the file menu.  Add two items to it.  Add to menubar.
    file = new Menu("File");
    file.add(new MenuItem("Open"));
    file.add(new MenuItem("Quit"));
    menubar.add(file);
    // Create Help menu; add an item; add to menubar
    help = new Menu("Help");
    help.add(new MenuItem("About"));
    menubar.add(help);
    // Display the help menu in a special reserved place.
    menubar.setHelpMenu(help);
       
    this.setLayout(new FlowLayout());


    // Create pushbuttons
    okay   = new Button("Okay");
    cancel = new Button("Cancel");

    engOpenB            = new Button("engOpen()");
    engOpenSingleUseB   = new Button("engOpenSingleUse()");
    engPointerT         = new TextField(10);
    engCloseB           = new Button("engClose()");
    engCloseEngPB       = new Button("engClose(Ep)");
    engCloseAllB        = new Button("engCloseAll()");
    engSetVisibleB      = new Button("engSetVisible()");
    engGetVisibleB      = new Button("engGetVisible()");
    exitB               = new Button("Exit"); 

    evalT               = new TextField(40);
    engEvalStringB      = new Button("Eval(String)");
    engEvalStringEpB    = new Button("Eval(Ep, String)"); 

    outputBufferB        = new Button("engOutputBuffer()");
    outputBufferEB       = new Button("engOutputBuffer(Ep, length)");
    engGetOutputBufferB  = new Button("engGetOutputBuffer()");
    engGetOutputBufferEB = new Button("engGetOutputBuffer(Ep)");

    engPutArrayB     = new Button("engPutArray");

    engGetScalarB    = new Button("engGetScalar");
    engGetVectorB    = new Button("engGetVector");
    engGetArrayB     = new Button("engGetArray");

   testB            = new Button("test function");
   charArrayB       = new Button("charArray(a)");
   engSetDebugB     = new Button("toggle Debug");
   getFigureB       = new Button("getFigure()");
   
   engOpenB.addActionListener(this); 
   engOpenSingleUseB.addActionListener(this); 
   engCloseB.addActionListener(this); 
   engCloseEngPB.addActionListener(this);
   engCloseAllB.addActionListener(this);
   engSetVisibleB.addActionListener(this);
   engGetVisibleB.addActionListener(this);
   engEvalStringB.addActionListener(this); 
   engEvalStringEpB.addActionListener(this); 
   outputBufferB.addActionListener(this);
   outputBufferEB.addActionListener(this);
   engGetOutputBufferB.addActionListener(this);
   engGetOutputBufferEB.addActionListener(this);
   engPutArrayB.addActionListener(this); 
   engGetScalarB.addActionListener(this); 
   engGetVectorB.addActionListener(this); 
   engGetArrayB.addActionListener(this);
   engSetDebugB.addActionListener(this);
   testB.addActionListener(this);
   charArrayB.addActionListener(this);
   getFigureB.addActionListener(this);
   exitB.addActionListener(this);
 
   this.add(engOpenB);
   this.add(engOpenSingleUseB);
   this.add(engPointerT);
   this.add(engCloseB);
   this.add(engCloseEngPB);
   this.add(engCloseAllB);
   this.add(engSetVisibleB);
   this.add(engGetVisibleB);
   this.add(evalT);
   this.add(engEvalStringB);
   this.add(engEvalStringEpB);
   this.add(outputBufferB);
   this.add(outputBufferEB);
   this.add(engGetOutputBufferB);
   this.add(engGetOutputBufferEB);
   this.add(engPutArrayB);  
   this.add(engGetScalarB);
   this.add(engGetVectorB);
   this.add(engGetArrayB);
   this.add(testB);
   this.add(charArrayB);
   this.add(engSetDebugB);
   this.add(getFigureB);
   
   this.add(okay);
   this.add(cancel);   
   this.add(exitB);

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
        //engPointerT.setText(new Long(EpI).toString());
    }
    else if (src==engOpenSingleUseB)
    { 
        System.out.println("engOpenSingleUse()");
        EpI = engine.engOpenSingleUse("startCmd=55;");
        engPointerT.setText(new Long(EpI).toString());
    }
    else if (src==engCloseB) 
    {
        engine.engClose();
    }
    else if (src==engCloseEngPB)
    {
       EpI = Integer.parseInt(engPointerT.getText());
       System.out.println("engClose (" + EpI +")" );
       engine.engClose( EpI );
    }
    else if (src==engCloseAllB)
    {
        System.out.println("engCloseAll()");
        engine.engCloseAll();
    }
    else if(src==engSetVisibleB)
    {
        EpI = Integer.parseInt(engPointerT.getText());
        System.out.println("engSetVisible (" + EpI +")" );
        boolean visB = false;
        if (evalT.getText().equals("1"))
            visB = true;
        System.out.println("engSetVisible (" + EpI +","+ visB +")" );
        engine.engSetVisible( EpI, visB );
    }
    else if(src==engGetVisibleB)
    {
        EpI = Integer.parseInt(engPointerT.getText());
        System.out.println("engGetVisible (" + EpI +")" );
        evalT.setText( new Boolean(engine.engGetVisible(EpI)).toString() );
    }
    else if (src==engEvalStringB) 
    {
        evalS = evalT.getText();
        System.out.println("engEvalString() "+evalS);
        engine.engEvalString(evalS);
    }
    else if ( src == engEvalStringEpB ) 
    {
        evalS = evalT.getText();
        EpI = Integer.parseInt(engPointerT.getText());
        System.out.println("engEvalString "+ EpI +" "+ evalS);
        engine.engEvalString(EpI, evalS);
    }
    else if (src==outputBufferB) {
        System.out.println("outputBufferB ");
        outputBufferI = engine.engOutputBuffer();
        System.out.println(outputBufferI);
     }
    else if (src==outputBufferEB) {
       EpI = Integer.parseInt(engPointerT.getText());
       int len =  Integer.parseInt(evalT.getText());
       System.out.println("outputBufferEB "+EpI+" "+len);
       outputBufferI = engine.engOutputBuffer(EpI, len);
       System.out.println(outputBufferI);
    }
    else if (src==engGetOutputBufferB)
    {
        System.out.println("getOutputBufferB ");
        outputBufferS = engine.engGetOutputBuffer();
        System.out.println(outputBufferS);
    }
    else if (src==engGetOutputBufferEB)
    {
        EpI = Integer.parseInt(engPointerT.getText());
        System.out.println("getOutputBufferB "+EpI);
        outputBufferS = engine.engGetOutputBuffer(EpI);
        System.out.println(outputBufferS);
    }
    else if (src==engPutArrayB) {
       System.out.println("engPutArray");
       System.out.println("1x1");
       engine.engPutArray("a",1);
       System.out.println("1xn");
       engine.engPutArray("b",testarray);
       System.out.println("nxn");
       engine.engPutArray("c",testarray2d);
    }
    else if (src==engGetScalarB) {
       System.out.println("engGetScalar");
       scalar = engine.engGetScalar( "a" );
       System.out.println("scalar "+scalar);
       evalT.setText(new Double(scalar).toString());
    }
    else if (src==engGetArrayB) {
       System.out.println("engGetArray");
       array2d = engine.engGetArray( "a" );
       evalT.setText(new Double(array2d[0][0]).toString());
       System.out.println("length "+ array2d[1].length);
       System.out.println("length "+ array2d.length);
       System.out.println(array2d[0][0]+" "+array2d[0][1]+" "+array2d[0][2]);
       System.out.println(array2d[1][0]+" "+array2d[1][1]+" "+array2d[1][2]);
       System.out.println(array2d[2][0]+" "+array2d[2][1]+" "+array2d[2][2]);
    }
    else if (src==testB) {
       array2d = engine.engGetArray( "a" );
       System.out.println("size "+array2d.length+" "+array2d[0].length);
       engine.engPutArray("b",array2d);
    }
    else if (src==charArrayB) {
        String[] aS = engine.engGetCharArray("a");
        if (aS == null) return;
        for (int i=0; i<aS.length; i++) 
            System.out.println("engGetCharArray "+i+" "+aS[i]);
    }
    else if (src==engSetDebugB){
        if (debugToggleB == false)
            debugToggleB=true;
        else
            debugToggleB=false;
        
        System.out.println("engSetDebug("+debugToggleB+")");
        engine.setDebug(debugToggleB);
    }
    else if (src==getFigureB){
        System.out.println("getFigure");

        engine.engEvalString("surf(peaks)");
        Image image = engine.engGetFigure(1, 500, 400);
        
        int x = image.getWidth(null);
        int y = image.getHeight(null);
        System.out.println("size ["+y+","+x+"]");
        
        ImageFrame iFrame = new ImageFrame();
        iFrame.setSize(x,y);
        iFrame.setImage(image);
        iFrame.show();
    }
    else if (src==exitB) {
        engine.engCloseAll();
        //engine.kill();
        System.exit(0);
        //this.dispose();
    }
    repaint();
} // actionPerformed

    
/**
 * main class for this GUI
 * @param args not used right now
 */
    public static void main(String[] args) {
        Frame f = new SimpleTestGui("JMatLink");
        f.setSize(500, 250);
        f.show();
    }

    
} // end SimpleTestGui.java

class ImageFrame extends Frame implements WindowListener
{
    private Image image = null;
    
    public ImageFrame(){
        addWindowListener(this);
    }
    
    public void setImage(Image image)
    {
        this.image = image;
        repaint();
    }
    
    public void paint(Graphics g)
    {
        g.drawImage(image, 0,0, null);
    }
    public void windowClosing(WindowEvent e)
    {
        this.dispose();
    }

    public void windowActivated(WindowEvent e){}
    
    public void windowDeactivated(WindowEvent e){}
    
    public void windowClosed(WindowEvent e){}
   
    public void windowIconified(WindowEvent e){}

    public void windowDeiconified(WindowEvent e){}

    public void windowOpened(WindowEvent e){}
     
}

