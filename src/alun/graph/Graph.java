package alun.graph;

import java.util.Collection;
import java.util.Set;

public interface Graph<V,E>
{
	public boolean contains(Object x);

	public boolean connects(Object x, Object y);
	
	public Collection<V> getNeighbours(Object x);

	public Collection<V> inNeighbours(Object x);
	
	public Collection<V> outNeighbours(Object x);

	public Set<V> getVertices();

	public E connection(Object u, Object v);

	public Collection<E> getEdges();
}
