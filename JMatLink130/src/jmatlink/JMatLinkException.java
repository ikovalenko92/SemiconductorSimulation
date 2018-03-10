package jmatlink;

/**JMatLink specific exception 
 * This is the standard exeption for JMatLink.
 * */
public class JMatLinkException extends RuntimeException
{
    /**Create a new exception object*/
    public JMatLinkException()
    {
    
    }
    
    /**Set the message text
     * This is the standard exception for JMatLink. Each time a problem occurs
     * this exception will be thrown. Other programs should watch out for the
     * JMatLinkExeption and "catch" it in their programs.
     @param text = the text to display*/
    public JMatLinkException(String text)
    {
    	super("ERROR: "+text);
    }

	
}
