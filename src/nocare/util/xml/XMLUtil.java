package nocare.util.xml;

public class XMLUtil
{
	/**
	 * Fail the test
	 * 
	 * @param message
	 *            The message to describe the failure
	 */
	public static void fail( String message )
	{
		throw new RuntimeException( message );
	}

	/**
	 * Ensure that the given object is not null, if it is fail the test
	 * 
	 * @param object1
	 *            The object to test
	 */
	public static void assertNotNull( Object object1 )
	{
		if ( object1 == null )
		{
			throw new RuntimeException( "TEST FAILS: " + object1 + " must not be null" );
		}
	}

	/**
	 * Ensure that the two values given are equal, if not fail the test
	 * 
	 * @param a1
	 *            The first value to compare
	 * @param a2
	 *            The second value to compare
	 */
	public static void assertEquals( float a1, float a2 )
	{
		if ( a1 != a2 )
		{
			throw new RuntimeException( "TEST FAILS: " + a1 + " should be " + a2 );
		}
	}

	/**
	 * Ensure that the two values given are equal, if not fail the test
	 * 
	 * @param a1
	 *            The first value to compare
	 * @param a2
	 *            The second value to compare
	 */
	public static void assertEquals( int a1, int a2 )
	{
		if ( a1 != a2 )
		{
			throw new RuntimeException( "TEST FAILS: " + a1 + " should be " + a2 );
		}
	}

	/**
	 * Ensure that the two values given are equal, if not fail the test
	 * 
	 * @param a1
	 *            The first value to compare
	 * @param a2
	 *            The second value to compare
	 */
	public static void assertEquals( Object a1, Object a2 )
	{
		if ( !a1.equals( a2 ) )
		{
			throw new RuntimeException( "TEST FAILS: " + a1 + " should be " + a2 );
		}
	}
	
	public static void assertSize( XMLElement root, String child, int size )
	{
		assertNotNull(root.getChildrenByName( child ));
		if(root.getChildrenByName( child ).size() != size)
		{
			throw new RuntimeException("TEST FAILS: " + child + " size of " + root.getChildrenByName( child ).size() + " should be " + size);
		}
	}
}
