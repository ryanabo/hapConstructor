package alun.genepi;

public class PaternalInheritance extends GeneticFunction
{
	public PaternalInheritance(Inheritance ih, Genotype pat, Genotype bod)
	{
		h = ih;
		p = pat;
		x = bod;
		s.add(h);
		s.add(p);
		s.add(x);
	}

	public double getValue()
	{
		int a = x.pat();
		if (a == p.pat())
		{
			if (a == p.mat())
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
			if (a == p.mat())
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
		return "PI ["+h+","+p+","+x+"]";
	}

	private Genotype p = null;
	private Genotype x = null;
	private Inheritance h = null;
}
