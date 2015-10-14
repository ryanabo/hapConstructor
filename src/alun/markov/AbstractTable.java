package alun.markov;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

abstract public class AbstractTable implements Table
{
	public AbstractTable(Collection<? extends Variable> vars)
	{
		varset = new LinkedHashSet<Variable>(vars);
		v = (Variable[]) varset.toArray(new Variable[0]);

		double s = 1.0;
		maxs = 1;
		int[] nstates = new int[v.length];
		for (int i=0; i<v.length; i++)
		{
			nstates[i] = v[i].getNStates();
			maxs *= nstates[i];
			s *= nstates[i];
		}

		if (s > Integer.MAX_VALUE)
			throw new RuntimeException("\n\tNumber of states "+s+" exeeds Integer.MAX_VALUE "+Integer.MAX_VALUE+
				"\n\tState index will overrun number of possible integers"+
				"\n\tUse VerySparseTable instead of DenseTable or SparseTable");
		
		a = new int[v.length];
		if (a.length > 0)
		{
			a[a.length-1] = 1;
			for (int i=a.length-2; i>=0; i--)
				a[i] = a[i+1]*nstates[i+1];
		}

		c = new int[v.length][];
		b = new int[v.length][];
		for (int i=0; i<c.length; i++)
		{
			int[] st = v[i].getStates();
			int j = 0;
			for (int k=0; k<st.length; k++)
				if (j < st[k])
					j = st[k];

			b[i] = new int[nstates[i]];
			c[i] = new int[j+1];

			j=0;
			for (int k=0; k<st.length; k++)
			{
				c[i][st[k]] = k;
				b[i][k] = st[k];
			}
		}
			
	}

	public Set<Variable> getVariables()
	{
		return varset;
	}

// Private data.

	private Set<Variable> varset = null;
	private int[] a = null;
	protected Variable[] v = null;
	private int maxs = 0;
	private int[][] c = null;
	private int[][] b = null;

	protected int getMaxStates()
	{
		return maxs;
	}

	protected int getStateIndex()
	{
		int k = 0;
		for (int i=0; i<v.length; i++)
			k += a[i]*c[i][v[i].getState()];
		return k;
	}

	protected boolean setStateIndex(int k)
	{
		for (int i=0; i<v.length; i++)
		{
			if (!v[i].setState(b[i][k/a[i]]))
				return false;
			k = k % a[i];
		}
		return true;
	}
}
