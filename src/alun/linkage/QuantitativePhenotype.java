package alun.linkage;

public class QuantitativePhenotype extends LinkagePhenotype
{
	public QuantitativePhenotype(QuantitativeLocus l, double[] vars)
	{
		setLocus(l);
		v = new double[vars.length];
		for (int i=0; i<v.length; i++)
			v[i] = vars[i];
	}

	public LinkagePhenotype nullCopy()
	{
		return new QuantitativePhenotype((QuantitativeLocus)getLocus(),new double[v.length]);
	}

	public String toString()
	{
		StringBuffer b = new StringBuffer();
		for (int i=0; i<v.length; i++)
			b.append(v[i]+" ");
		if (v.length > 0)
			b.deleteCharAt(b.length()-1);
		return b.toString();
	}

// Private data.

	public double[] v = null;
}
