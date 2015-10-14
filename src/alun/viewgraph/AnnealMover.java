package alun.viewgraph;

public class AnnealMover extends LocalMover
{
	public AnnealMover()
	{
		this(10);
	}

	public AnnealMover(double h)
	{
		setNParameters(2);
		hotest = h;
	}

	public void set()
	{
	}

	public void setParameter(int i, double d)
	{
		switch(i)
		{
		case 1:
			anneal = d;
			break;
		default:
			super.setParameter(i,d);
		}
	}

	public double getParameterScale(int i)
	{
		switch(i)
		{
		case 1:
			return hotest;
		default:
			return super.getParameterScale(i);
		}
	}

	public double getParameterInit(int i)
	{
		switch(i)
		{
		case 1: 
			return 1.0;
		default:
			return super.getParameterInit(i);
		}
	}
		
	public String getParameterName(int i)
	{
		switch (i)
		{	
		case 1:
			return "Annealing temp";
		default:
			return super.getParameterName(i);
		}
	}

	public final double getAnneal()
	{
		return anneal;
	}

	private double anneal = 0;
	private double hotest = 10;
}
