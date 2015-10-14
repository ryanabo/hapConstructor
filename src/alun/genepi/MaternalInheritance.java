package alun.genepi;

public class MaternalInheritance extends GeneticFunction
{
	public MaternalInheritance(Inheritance ih, Genotype mat, Genotype bod)
	{
		h = ih;
		m = mat;
		x = bod;
		s.add(h);
		s.add(m);
		s.add(x);
	}

	public double getValue()
	{
		int a = x.mat();
		if (a == m.pat())
		{
			if (a == m.mat())
			{
				return 0.5;
			}
			else
			{
				return h.getState() == 0 ? 1 : 0;
			}
		}
		else
		{
			if (a == m.mat())
			{
				return h.getState() == 1 ? 1 : 0;
			}
			else
			{
				return 0;
			}
		}
	}

	public String toString()
	{
		return "MI ["+h+","+m+","+x+"]";
	}

	private Genotype m = null;
	private Genotype x = null;
	private Inheritance h = null;
}
