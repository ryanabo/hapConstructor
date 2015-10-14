package alun.mcld;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.genepi.Allele;
import alun.genepi.Error;
import alun.markov.Function;
import alun.markov.Variable;

public class Observation implements Function
{
	public Observation(Allele obs, Error err, int[] pos)
	{
		a = obs;
		e = err;
		x = pos;
		p = 1.0/x.length;
	}

	public Set<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(a);
		s.add(e);
		return s;
	}

	public Allele getAllele()
	{
		return a;
	}

	public double getValue()
	{
		if (e.getState() == 1)
			return p;
		else
		{
			if (t < 0)
				return 1;
			else
			{
				if (t == x[a.getState()])
					return 1;
				else
					return 0;
			}
		}
	}
		
	public void fix(int x)
	{
		t = x;
	}

// Private data.

	private Allele a = null;
	private Error e = null;
	private int[] x = null;
	private int t = 0;
	private double p = 0;
}
