package alun.markov;

import java.util.LinkedHashSet;
import java.util.Set;

public class EqualityFunction implements Function
{
	public EqualityFunction(Variable xx, Variable yy)
	{
		x = xx;
		y = yy;
	}

	public Set<Variable> getVariables()
	{
		Set<Variable> s = new LinkedHashSet<Variable>();
		s.add(x);
		s.add(y);
		return s;
	}

	public double getValue()
	{
		return x.getState() == y.getState() ? 2 : 1;
	}

	private Variable x = null;
	private Variable y = null;
}
