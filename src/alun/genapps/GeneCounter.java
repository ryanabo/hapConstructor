package alun.genapps;

import java.util.Vector;

import alun.genepi.Error;
import alun.genepi.ErrorPrior;
import alun.genepi.Genotype;
import alun.genepi.GenotypeModel;
import alun.genepi.LocusVariables;
import alun.genio.GeneticDataSource;
import alun.linkage.LinkageDataSet;
import alun.linkage.LinkageInterface;
import alun.linkage.NumberedAlleleLocus;
import alun.markov.Function;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Table;
import alun.util.StringFormatter;

public class GeneCounter
{
	private static int maxerralleles = 5;

	public static void geneCount(LinkageDataSet x)
	{
		geneCount(x,false,0.000001,1000,0.01);
	}

	public static void geneCount(LinkageDataSet x, boolean report, double max_diff, int max_its, double err_rate)
	{
		GeneticDataSource d = new LinkageInterface(x);

		for (int j=0; j<d.nLoci(); j++)
		{
			double myerror = err_rate;
			double nposerr = 0;
			if (d.nAlleles(j) > maxerralleles) 
				myerror = 0;
			boolean witherror = myerror > 0;

			if (!(x.getParameterData().getLoci()[j] instanceof NumberedAlleleLocus))
				continue;

			LocusVariables g = new LocusVariables(d,j,myerror);
			g.makeGenotypesUnordered();

			Genotype[] anc = g.founderGenotypes();

			GraphicalModel m = null;
			//m = new GraphicalModel(g.makeUnrelatedProduct());
			m = new GraphicalModel(g.makeUnrelatedUnorderedProduct());
			m.allocateOutputTables();
			m.allocateInvolTables();
			m.reduceStates();

			//Product prod = g.makeLocusProduct();
			Product prod = g.makeUnorderedProduct();
			prod.triangulate();
			m = new GenotypeModel(prod);
			m.allocateOutputTables();
			m.allocateInvolTables();
			if (myerror <= 0)
				m.reduceStates();

			Vector<ErrorPrior> priors = new Vector<ErrorPrior>();
			for (Function f : prod.getFunctions())
				if (f instanceof ErrorPrior)
				{
					priors.add((ErrorPrior)f);
				}
			Error[] err = g.errors();
			

			double[] freqs = new double[d.nAlleles(j)];
			double[] oldfreqs = x.getParameterData().getLoci()[j].alleleFrequencies();
			double[] olderfreqs = new double[d.nAlleles(j)];
			for (int k=0; k<olderfreqs.length; k++)
				olderfreqs[k] = 1.0/olderfreqs.length;

			if (report)
			{
				System.err.println("Locus "+d.locName(j));
				System.err.print("Inital\t");
				for (int k=0; k<oldfreqs.length; k++)
					System.err.print(" "+StringFormatter.format(oldfreqs[k],1,4));
				System.err.print("\n");
				//if (witherror)
				//	System.err.println("Error prior\t"+StringFormatter.format(myerror,1,6));
			}
			
			Vector<double[]> history = new Vector<double[]>();
			double dist = 1;
			double lambda = 0;
			int its=0;
			boolean consistent = true;
			for (; consistent && its<max_its && dist > max_diff; its++)
			{
				m.findMarginals();

				if (err != null)
				{
					nposerr = 0;
					myerror = 0;
					for (int k=0; k<err.length; k++)
					{
						if (err[k] != null)
						{
							Table marg = (Table) m.getMarginal(err[k]);
							err[k].setState(1);
							myerror += marg.getValue();
							nposerr += 1;
						}
					}
					myerror /= nposerr;
					for (ErrorPrior e : priors)
						e.set(myerror);
				}

				for (int k=0; k<anc.length; k++)
				{
					Table marg = (Table) m.getMarginal(anc[k]);
					for (marg.init(); marg.next(); )
					{
						freqs[anc[k].pat()] += marg.getValue();
						freqs[anc[k].mat()] += marg.getValue();
					}
				}
	
				double t = 0;
				for (int k=0; k<freqs.length; k++)
					t += freqs[k];

/*
				if (t < Double.MIN_VALUE)
				{
					System.err.println("Mendeilan inconsistency found. Skipping this locus.");
					consistent = false;
					continue;
				}
*/

				if (t > Double.MIN_VALUE)
					for (int k=0; k<freqs.length; k++)
						freqs[k] /= t;

				dist = 0;
				for (int k=0; k<freqs.length; k++)
				{
					double dd = oldfreqs[k] - freqs[k];
					if (dd < 0) 
						dd = -dd;

					if (dist < dd)
						dist = dd;
				}

				double ll = 0;
				int cc = 0;
				for (int k=0; k<freqs.length; k++)
				{
					double ddd = olderfreqs[k] - freqs[k];
					if (ddd != 0)
					{
						ddd = (oldfreqs[k] - freqs[k])/ddd;
						if (ddd > 0 && ddd < 1)
						{
							ll += ddd;
							cc += 1;
						}
					}
				}

				if (cc > 0)
					lambda = ll/cc;

				for (int k=0; k<freqs.length; k++)
				{
					olderfreqs[k] = oldfreqs[k];
					oldfreqs[k] = freqs[k];
					freqs[k] = 0;
				}
			}

			if (report)
			{
				if (!consistent)
					System.err.println("Using initial frequencies");
				else
					System.err.println("Iterations:\t"+its);
				System.err.print("Final\t");
				for (int k=0; k<oldfreqs.length; k++)
					System.err.print(" "+StringFormatter.format(oldfreqs[k],1,4));
				System.err.println();

				if (consistent)
				{
					int n = 2*anc.length;
					System.err.print("StdErr\t");
					for (int k=0; k<oldfreqs.length; k++)
					{
						double p = oldfreqs[k];
						p = p * (1-p) / n /(1-lambda);
						p = Math.sqrt(p);
						System.err.print(" "+StringFormatter.format(p,1,4));
					}
					System.err.println();
				}

				if (witherror)
				{
					System.err.print("Error mle\t"+StringFormatter.format(myerror,1,6));
					double p = myerror;
					p = p * (1-p) / nposerr / (1-lambda);
					p = Math.sqrt(p);
					System.err.print(" ("+StringFormatter.format(p,1,6)+")");
					System.err.println();
				}
				System.err.println();
			}

		}
	}
}
