package alun.gchap;

import alun.genio.GeneticDataSource;
import alun.linkage.LinkageDataSet;
import alun.linkage.LinkageInterface;

/**
 This class just has a main method in it to run the 
 gene counting program.
*/
public class GCHap
{
	public static void main(String[] args)
	{
		try
		{
			GeneticDataSource gds = null;

			switch(args.length)
			{
			case 2: gds = new LinkageInterface(new LinkageDataSet(args[0],args[1]));
				break;
			default:
				System.err.println("Usage: java GCHap parfile pedfile");
				System.exit(1);
			}

			MarkerObservation[] x = new MarkerObservation[gds.nLoci()];
			for (int i=0; i<x.length; i++)
			{
				x[i] = new MarkerObservation(gds,i);
				System.err.println("Marker "+i+"  number of alleles = "+x[i].getTrait().getLocus().nAlleles());
			}

			Observation y = x[0];
			for (int i=1; i<x.length; i++)
			{
				System.err.print("Adding marker "+i);
				y = new MultiLocusObservation(y,x[i]);
				System.err.println(" number of haplotypes = "+y.getTrait().getLocus().nAlleles());
			}

			int its = y.geneCountToConvergence();
			y.getTrait().downCode();
			System.err.println("Log likelihood = "+y.logLikelihood()+" after "+its+" iterations");

			its = y.geneCountToConvergence();
			y.getTrait().downCode();
			System.err.println("Log likelihood = "+y.logLikelihood()+" after "+its+" iterations");

			System.out.println(HapFormatter.formatHaplotypes(y));
			System.out.println(HapFormatter.formatGuesses(y,gds));
		}
		catch (Exception e)
		{
			System.err.println("Caught in GCHap:main()");
			e.printStackTrace();
		}
	}
}
