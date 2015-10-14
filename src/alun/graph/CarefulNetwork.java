package alun.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class CarefulNetwork<V,E> extends Network<V,E>
{
	public CarefulNetwork(int initcap)
	{
		super(initcap);
		everin = new LinkedHashSet<V>(initcap);
	}

	public CarefulNetwork()
	{
		everin = new LinkedHashSet<V>();
	}

	public boolean add(V x)
	{
		super.add(x);
		everin.add(x);
		return true;
	}

	public Set<V> getVertices()
	{
		Set<V> s = new LinkedHashSet<V>(everin);
		s.retainAll(super.getVertices());
		return s;
	}

	private Set<V> everin = null;
}
