package alun.genepi;

import java.util.Iterator;

public class AuxLinkFunction extends AuxiliaryFunction
{
	public AuxLinkFunction(AuxiliaryVariable uu, AuxiliaryVariable vv, double mt, double ft)
	{
		u = uu;
		v = vv;
		mtheta = mt;
		ftheta = ft;
		s.clear();
		s.add(u);
		s.add(v);
	}

	public double getValue()
	{
		return tab[u.getState()][v.getState()];
	} 

	public void preCompute()
	{
		int n = 0;
		for (u.init(); u.next(); )
			if (n < u.getState())
				n = u.getState();
		tab = new double[n+1][];
		n = 0;
		for (v.init(); v.next(); )
			if (n < v.getState())
				n = v.getState();
		for (int i=0; i<tab.length; i++)
			tab[i] = new double[n+1];

		for (u.init(); u.next(); )
			for (v.init(); v.next(); )
			{
				u.set();
				v.set();
				double x = 1;
				for (Iterator<Inheritance> i = u.getPats().iterator(), j = v.getPats().iterator(); i.hasNext() && j.hasNext(); )
					x *= ( i.next().getState() == j.next().getState() ? 1-mtheta : mtheta );
				for (Iterator<Inheritance> i = u.getMats().iterator(), j = v.getMats().iterator(); i.hasNext() && j.hasNext(); )
					x *= ( i.next().getState() == j.next().getState() ? 1-ftheta : ftheta );
				tab[u.getState()][v.getState()] = x;
			}
	}

	public String toString()
	{
		return "JV "+getVariables();
	}

// Private Data.

	private double[][] tab = null;
	private AuxiliaryVariable u = null;	
	private AuxiliaryVariable v = null;
	private double mtheta = 0; 
	private double ftheta = 0; 
}
