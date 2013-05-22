package nocare.util.xml;

import org.newdawn.slick.SlickException;

/**
 * An exception to describe failures in XML. Made a special case because with
 * XML
 * to object parsing you might want to handle it differently
 * 
 * @author kevin
 */
public class XMLException extends SlickException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -457648218292036688L;

	/**
	 * Create a new exception
	 * 
	 * @param message
	 *            The message describing the failure
	 */
	public XMLException( String message )
	{
		super( message );
	}

	/**
	 * Create a new exception
	 * 
	 * @param message
	 *            The message describing the failure
	 * @param e
	 *            The exception causing this failure
	 */
	public XMLException( String message, Throwable e )
	{
		super( message, e );
	}

}
