package alun.markov;

import java.util.Set;

public class TableMaker
{
	public static Table makeTable(Function f, boolean sparse)
	{
		double x = 1;
		for (Variable u : f.getVariables())
			x *= u.getNStates();
		
		if (x < Integer.MAX_VALUE && !sparse)
			return new DenseTable(f);
		else if (x < Integer.MAX_VALUE)
			return new SparseTable(f);
		else
			return new VerySparseTable(f);
	}

	public static Table makeTable(Set<? extends Variable> v, boolean sparse)
	{
		double x = 1;
		for (Variable u : v)
			x *= u.getNStates();
		
		if (x < Integer.MAX_VALUE && !sparse)
			return new DenseTable(v);
		else if (x < Integer.MAX_VALUE)
			return new SparseTable(v);
		else
			return new VerySparseTable(v);
	}
}
