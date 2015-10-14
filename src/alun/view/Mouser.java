package alun.view;

/**
 A template class to handle mouse events, a combination of 
 a mouse adapter and a mouse motion adapter.
*/
public class Mouser extends MouseKey 
{
	public Mouser()
	{
	}

	public Mouser(ViewingApplet t)
	{
		target = t;
	}

	protected ViewingApplet getTarget()
	{
		return target;
	}

// Private data
	
	private ViewingApplet target = null;
}
