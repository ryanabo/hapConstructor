package alun.viewgraph;

//import alun.graph.Graph;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Set;

/**
 Defines what is needed of a graph in order for it to be visualised.
*/
public interface MappableGraph
{
	public void paint(Graphics g);
	public Mappable getShowing(double x, double y);

	public void show(Collection<? extends Mappable>  x);
	public void show(Mappable  x);
	public void hide(Collection<? extends Mappable> x);
	public void hide(Mappable x);

	public Set<Mappable> getShownVertices();
	public Collection<Mappable> getShownNeighbours(Mappable x);
	public Collection<Mappable> getShownComponent(Mappable x);
	
	public Collection<Mappable> getShownInNeighbours(Mappable x);
	public Collection<Mappable> getShownOutNeighbours(Mappable x);

	public Set<Mappable> getAllVertices();
	public Collection<Mappable> getAllNeighbours(Mappable x);
	public Collection<Mappable> getAllComponent(Mappable x);
}
