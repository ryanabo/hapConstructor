package alun.gchap;

import alun.genio.GeneticDataSource;
import alun.linkage.LinkageDataSet;
import alun.linkage.LinkageInterface;

/**
 This class contains a main method for running the approximate 
 equivalent of GCHap. In this version as each locus is added
 gene counting is run to convergence and haplotypes with
 frequency estimate at or below a certain threshold are removed.
 The default threshold is 0. To change this add a value as the 
 third argument on the command line following the linkage .par and .ped files.
*/
public class ApproxGCHap
{
	public static void main(String[] args)
	{
		try
		{
			double thresh = 0.0;
			int nits = 0;

			GeneticDataSource gds = null;

			switch(args.length)
			{
			case 4: nits = new Integer(args[3]).intValue();

			case 3: thresh = new Double(args[2]).doubleValue();

			case 2: gds = new LinkageInterface(new LinkageDataSet(args[0],args[1]));
				break;

			default:
				System.err.println("Usage: java ApproxGCHap parfile pedfile [threshold]");
				System.exit(1);
			}

			MarkerObservation[] x = new MarkerObservation[gds.nLoci()];
			for (int i=0; i<x.length; i++)
				x[i] = new MarkerObservation(gds,i);

			Observation y = x[0];
			for (int i=1; i<x.length; i++)
			{
				System.err.print("Adding locus "+i+"  "+x[i].getTrait().getLocus().nAlleles());
				y = new MultiLocusObservation(y,x[i]);
				System.err.print("  Number of haplotypes = "+y.getTrait().getLocus().nAlleles());
				if (nits == 0)
					y.geneCountToConvergence();
				else
					y.getTrait().geneCount(nits);

				y.getTrait().downCode(thresh);
				System.err.print(" downcoded to = "+y.getTrait().getLocus().nAlleles());
				y.getTrait().parsDownCode();
				System.err.println(" then to = "+y.getTrait().getLocus().nAlleles());
			}

			y.geneCountToConvergence();
			y.getTrait().downCode(thresh);

			System.out.println(HapFormatter.formatHaplotypes(y));
			System.out.println(HapFormatter.formatGuesses(y,gds));
		}
		catch (Exception e)
		{
			System.err.println("Caught in ApproxGCHap:main()");
			e.printStackTrace();
		}
	}
}
