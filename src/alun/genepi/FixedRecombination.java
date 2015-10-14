package alun.genepi;


public class FixedRecombination extends GeneticFunction
{
	public FixedRecombination(Inheritance x, Inheritance y, double theta)
	{
		a = x;
		b = y;
		t = theta;
		s.add(a);
		s.add(b);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(a);
		s.add(b);
		return s;
	}
*/

	public void fix(double th)
	{
		t = th;
	}

	public double getValue()
	{
		return a.getState() == b.getState() ? 1-t : t;
	}

	public String toString()
	{
		return "RECOM ["+a+","+b+"]";
	}

	private Inheritance a = null;
	private Inheritance b = null;
	private double t = 0;
}
