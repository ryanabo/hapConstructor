package alun.genepi;

import java.util.Set;

import alun.markov.GraphicalModel;
import alun.viewgraph.MapViewer;

public class LinkageSampler
{
	public void initialize()
	{
		if (initgm != null)
			for (GraphicalModel g : initgm)
				sample(g);
	}

	public void sample()
	{
		if (meiogm != null)
			for (GraphicalModel g : meiogm)
				sample(g);
		if (locusgm != null)
			for (GraphicalModel g : locusgm)
				sample(g);
	}

	public void sample(MapViewer m)
	{
		if (meiogm != null)
			for (GraphicalModel g : meiogm)
			{
				sample(g);
				m.setChanged();
			}
		if (locusgm != null)
			for (GraphicalModel g : locusgm)
			{
				sample(g);
				m.setChanged();
			}
	}

	public void setInitialBlocks(Set<GraphicalModel> sgm)
	{
		initgm = sgm;
	}

	public Set<GraphicalModel> getInitialBlocks()
	{
		return initgm;
	}

	public void setLocusBlocks(Set<GraphicalModel> sgm)
	{
		locusgm = sgm;
	}

	public Set<GraphicalModel> getLocusBlocks()
	{
		return locusgm;
	}

	public void setMeiosisBlocks(Set<GraphicalModel> sgm)
	{
		meiogm = sgm;
	}

	public Set<GraphicalModel> getMeiosisBlocks()
	{
		return meiogm;
	}

	public void setThrifty(boolean b)
	{
		if (!b)
		{
			if (getInitialBlocks() != null)
				for (GraphicalModel g : getInitialBlocks())
				{
					g.allocateInvolTables();
					g.allocateOutputTables();
				}
			if (getLocusBlocks() != null)
				for (GraphicalModel g : getLocusBlocks())
				{
					g.allocateInvolTables();
					g.allocateOutputTables();
				}
			if (getMeiosisBlocks() != null)
				for (GraphicalModel g : getMeiosisBlocks())
				{
					g.allocateInvolTables();
					g.allocateOutputTables();
				}
		}
		else
		{
			if (getInitialBlocks() != null)
				for (GraphicalModel g : getInitialBlocks())
				{
					g.clearInvolTables();
					g.clearOutputTables();
				}
			if (getLocusBlocks() != null)
				for (GraphicalModel g : getLocusBlocks())
				{
					g.clearInvolTables();
					g.clearOutputTables();
				}
			if (getMeiosisBlocks() != null)
				for (GraphicalModel g : getMeiosisBlocks())
				{
					g.clearInvolTables();
					g.clearOutputTables();
				}
		}

		thrifty = b;
	}

	public boolean getThrifty()
	{
		return thrifty;
	}

	protected void sample(GraphicalModel g)
	{
//System.err.print("S");
		if (thrifty)
		{
			g.allocateOutputTables();
			g.allocateInvolTables();
			g.collect();
			g.drop();
			g.clearInvolTables();
			g.clearOutputTables();
		}
		else
		{
			g.collect();
			g.drop();
		}
	}
	
// Private data.

	private Set<GraphicalModel> initgm = null;
	private Set<GraphicalModel> locusgm = null;
	private Set<GraphicalModel> meiogm = null;
	private boolean thrifty = true;
}
