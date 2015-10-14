package alun.genepi;

public class OneTheta extends Theta
{
	private static double[] def = {0};

	public OneTheta()
	{
		super(def);
	}

	public String toString()
	{
		return "OneTheta";
	}

	public void fix(double d)
	{
		tab[0] = d;
	}
}
