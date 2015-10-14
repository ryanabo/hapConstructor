package alun.viewgraph;

import alun.view.Mouser;

public class MapMouser extends Mouser
{
	public MapMouser(MapViewer g)
	{
		super(g);
	}

	protected MapCoordinator co()
	{
		return ((MapViewer)getTarget()).getCoordinator();
	}
}
