package alun.genepi;

public class Allele extends GeneticVariable
{
	public Allele(int n)
	{
		super(n);
	}

//	public String toString()
//	{
//		return "AL"+id;
//	}

	public void setId(int i)
	{
		id = i;
	}

	private int id = 0;
}
