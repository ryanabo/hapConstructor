package alun.genepi;

public class Inheritance extends GeneticVariable
{
	public Inheritance()
	{
		super(2);
		mem = 0;
	}

	public void remember()
	{
		mem = getState();
	}

	public void recall()
	{
		setState(mem);
	}

	public void flip()
	{
		setState(1-getState());
	}

//	public String toString()
//	{
//		return super.toString()+" "+getNStates();
//	}

// Private data.

	private int mem = 0;
}
