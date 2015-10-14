package alun.genepi;

import alun.markov.GraphicalModel;
import alun.markov.Product;

public class ExtendedLMSampler extends LocusMeiosisSampler
{
	public ExtendedLMSampler(LinkageVariables l)
	{
		this(l,defups,2,1);
	}

	public ExtendedLMSampler(LinkageVariables l, int[] updates)
	{
		this(l,updates,2,1);
	}

	public ExtendedLMSampler(LinkageVariables l, int[] updates, int level, int fst)
	{
		super(l,level,fst);

		vars = l;
		first = fst;
		randup = updates;

		int n = 0;
		for (int i=0; i<l.nIndividuals(); i++)
			if (l.getInheritance(i,1,0) != null && l.getInheritance(i,1,1) != null)
				n++;

		frame = new int[n];
		n = 0;
		for (int i=0; i<l.nIndividuals(); i++)
			if (l.getInheritance(i,1,0) != null && l.getInheritance(i,1,1) != null)
				frame[n++] = i;
	}

	public void sample()
	{
//		vars.report();
		try
		{
			vars.save(first);
			for (int i=0; i<randup.length; i++)
				sampleMeioses(randup[i]);
		}
		catch (OutOfMemoryError e)
		{
			System.err.println("Warning: failed to make random multi-meiosis update.");
			System.err.println(e);
			System.err.println("Skipping this update.");
			vars.restore(first);
		}

		super.sample();
 	}

	public void sampleMeioses(int nind)
	{
		int nins = nind;
		if (nins > frame.length)
			nins = frame.length;

		int[] x = new int[nins];
		for (int i=0; i<nins; i++)
		{
			int k = i + (int)((frame.length-i)*Math.random());
			int temp = frame[i];
			frame[i] = frame[k];
			frame[k] = temp;
			x[i] = frame[i];
		}

		Product p = vars.peeledSetOfMeiosesProduct(x,first);
		p.triangulate();

		GraphicalModel m = new GraphicalModel(p);
		m.allocateOutputTables();
		m.allocateInvolTables();
		m.collect();
		m.drop();
		m.clearInvolTables();
		m.clearOutputTables();
	}

// Private data.

	private LinkageVariables vars = null;
	private int[] frame = null;
	private int[] randup = null;
	private int first = 1;

	private static int[] defups = {4};
}
