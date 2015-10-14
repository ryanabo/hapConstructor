package alun.mcld;

import alun.viewgraph.AnnealMover;

public class DistanceAnnealMover extends AnnealMover
{
	public DistanceAnnealMover(double d)
	{
		super(d);
		setNParameters(3);
	}

	public DistanceAnnealMover()
	{
		this(10);
	}

	public void setParameter(int i, double d)
	{
		switch(i)
		{
		case 2:
			distpen = d;
			break;
		default:
			super.setParameter(i,d);
		}
	}

	public double getParameterScale(int i)
	{
		switch(i)
		{
		case 2:
			return 1;
		default:
			return super.getParameterScale(i);
		}
	}

	public double getParameterInit(int i)
	{
		switch(i)
		{
		case 2: 
			return 0.0;
		default:
			return super.getParameterInit(i);
		}
	}
		
	public String getParameterName(int i)
	{
		switch(i)
		{	
		case 2:
			return "Distance penalty";
		default:
			return super.getParameterName(i);
		}
	}

	public double getDistancePenalty()
	{
		return distpen;
	}

	private double distpen = 0;
}
