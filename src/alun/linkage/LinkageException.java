package alun.linkage;

/**
 A run time exception thrown by objects in the linkage package.
*/
public class LinkageException extends RuntimeException
{
/**
 Creates a new exception with the default message.
*/
	public LinkageException()
	{
		this("Error in Linkage classes");
	}

/**
 Creates a new exception with the given message.
*/
	public LinkageException(String s)
	{
		super(s);
	}
}
