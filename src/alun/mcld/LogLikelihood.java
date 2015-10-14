package alun.mcld;

public class LogLikelihood
{
	public LogLikelihood()
	{
		this(0,0,0);
	}

	public LogLikelihood(double x, double i)
	{
		this (x,i,0);
	}

	public LogLikelihood(double x, double i, double s)
	{
		ll = x;
		df = i;
		ss = s;
	}

	public LogLikelihood(LogLikelihood x)
	{
		ll = x.ll;
		df = x.df;
		ss = x.ss;
	}

	public final void add(LogLikelihood x)
	{
		ll += x.ll;
		df += x.df;
		ss += x.ss;
	}

	public final void subtract(LogLikelihood x)
	{
		ll -= x.ll;	
		df -= x.df;
		ss -= x.ss;
	}

	public final double ll()
	{
		return ll;
	}

	public final double df()
	{
		return df;
	}
	
	public final double ss()
	{
		return ss;
	}

	public String toString()
	{
		//return ll + " " + df + " " + ss ;
		return ll + " " + df ;
	}

// Private data.

	private double ll = 0;
	private double df = 0;
	private double ss = 0;
}
