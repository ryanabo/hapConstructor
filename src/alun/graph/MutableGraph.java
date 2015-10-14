package alun.graph;


public interface MutableGraph<V,E> extends Graph<V,E>
{
	public void clear();

	public boolean add(V v);
	
	public boolean remove(Object v);

	public boolean connect(V u, V v);
	
	public boolean disconnect(Object u, Object v);

	public boolean connect(V u, V v, E e);
}
