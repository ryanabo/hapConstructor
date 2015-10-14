package alun.mcld;

import java.util.Collection;

import alun.util.BasicInterval;
import alun.util.Interval;

public class TiedIntervalSearch extends IntervalSearch
{
	public TiedIntervalSearch(Collection<Locus> locs, double minoverlap)
	{
		super(locs,minoverlap);
	}

	protected Interval pert(Locus l)
	{
		double newL = l.getPosition() - l.getLeft();
		if (Math.random() < 0.5)
			newL += Math.log(Math.random());
		else
			newL -= Math.log(Math.random());
		if (newL < 0)
			newL = -newL;
		newL = l.getPosition() - newL;

		double newR = l.getRight() - l.getPosition();
		if (Math.random() < 0.5)
			newR += Math.log(Math.random());
		else
			newR -= Math.log(Math.random());
		if (newR < 0)
			newR = -newR;
		newR = l.getPosition() + newR;

		return new BasicInterval(newL,newR);
	}
}

