package alun.genepi;


public class Recombination extends GeneticFunction
{
	public Recombination(Inheritance x, Inheritance y, Theta theta)
	{
		a = x;
		b = y;
		t = theta;
		s.add(a);
		s.add(b);
		s.add(t);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(a);
		s.add(b);
		s.add(t);
		return s;
	}
*/

	public double getValue()
	{
		return a.getState() == b.getState() ? 1-t.getValue() : t.getValue();
	}

	public String toString()
	{
		return "RECOM ["+a+","+b+","+t+"]";
	}

	private Inheritance a = null;
	private Inheritance b = null;
	private Theta t = null;
}
