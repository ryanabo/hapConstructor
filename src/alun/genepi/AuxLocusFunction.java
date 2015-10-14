package alun.genepi;


public class AuxLocusFunction extends AuxiliaryFunction
{
	public AuxLocusFunction(AuxiliaryVariable vv)
	{
		v = vv;
		s.clear();
		s.add(v);
	}

	public void preCompute()
	{
		int n = 0;
		for (v.init(); v.next(); )
			if (n < v.getState())
				n = v.getState();
		tab = new double[n+1];

		for (v.init(); v.next(); )
		{
			v.set();
			tab[v.getState()] = v.getValue();
		}

		double tot = 0;
		for (int i=0; i<tab.length; i++)
			tot += tab[i];
		for (int i=0; i<tab.length; i++)
			tab[i] /= tot;
	}
		
	public double getValue()
	{	
		return tab[v.getState()];
	}

	public String toString()
	{
		return "LF "+getVariables();
	}

// Private data.

	private double[] tab = null;
	private AuxiliaryVariable v = null;
}
