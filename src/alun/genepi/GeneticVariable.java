package alun.genepi;

import alun.markov.BasicVariable;

public class GeneticVariable extends BasicVariable
{
	public GeneticVariable(int n)
	{
		super(n);
	}

	public void save()
	{
		memory = getState();
	}

	public void restore()
	{
		setState(memory);
	}

//	public String toString()
//	{
//		return "GV "+super.toString();
//	}

	private int memory = 0;
}
