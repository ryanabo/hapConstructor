package alun.genepi;


public class VariableRecombination extends GeneticFunction
{
	public VariableRecombination(Inheritance inh, Theta theta)
	{
		h = inh;
		t = theta;
		i = -1;
		s.add(h);
		s.add(t);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(h);
		s.add(t);
		return s;
	}
*/

	public double getValue()
	{
		return h.getState() == i ? 1-t.getValue() : t.getValue();
	}

	public void fix(int x)
	{
		i = x;
	}

	public String toString()
	{
		return "VR ["+h+","+t+"]";
	}

	public Inheritance getInheritance()
	{
		return h;
	}

	private Inheritance h = null;
	private Theta t = null;
	private int i = 0;
}
