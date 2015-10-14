package alun.genepi;

public class UnorderedGenotype  extends Genotype
{
	public UnorderedGenotype(int n)
	{
		super(n);
		int m = (n*(n+1))/2;
		//int[] x = {n};

		int[] x = new int[m];
		for (int i=0; i<x.length; i++)
			x[i] = i;
		setStates(x);
	//	na = n;
	}

	public int pat()
	{
		int j = mat();
		return getState() - (j*(j+1)/2);
	}

	public int mat()
	{
		return (int)( (Math.sqrt(1+8*getState()) - 1 ) / 2.0) ;
	}

	public boolean setState(int i, int j)
	{
		int a = i;
		int b = j;
		if (j < i)
		{
			a = j;
			b = i;
		}
		return setState((b*(b+1)/2)+a);
	}

	public static void main(String[] args)
	{	
		int na = 4;
		UnorderedGenotype g = new UnorderedGenotype(na);

		for (int i=0; i<na; i++)
			for (int j=0; j<na; j++)
			{
				g.setState(i,j);
				System.out.println(i+" "+j+" = "+g.getState()+" = "+g.pat()+" "+g.mat());
			}
	}
}
