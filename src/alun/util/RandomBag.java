package alun.util;

import java.util.ArrayList;
import java.util.Collection;

public class RandomBag<V> extends ArrayList<V>
{
	public RandomBag()
	{
		super();
	}
 
	public RandomBag(Collection<? extends V> c)
	{
		super(c);
	}

/** 
 Selects a random element from the bag but does not change the contents 
 of the bag.
 Returns null if the bag is empty.
*/
	public V next()
	{
		if (isEmpty())
			return null;
		else
			return get( (int) (Math.random() * size()) );
	}

/**
 Selects and removes a random element from the bag.
 Returns null if the bag is empty.
*/
	public V draw()
	{
		if (isEmpty())
			return null;
		else
			return remove( (int) (Math.random() * size()) );
	}
}
