package alun.genepi;

public class Theta extends GeneticVariable
{
	public Theta(double[] values)
	{
		super(values.length);
		tab = new double[values.length];
		for (int i=0; i<tab.length; i++)
			tab[i] = values[i];
	}

//	public String toString()
//	{
//		return "Theta";
//	}

	public double getValue()
	{
		return tab[getState()];
	}

	protected double[] tab = null;
}
