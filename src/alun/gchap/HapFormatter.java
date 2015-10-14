package alun.gchap;

import alun.genio.GeneticDataSource;

/**
 A class for formatting output.
*/
public class HapFormatter
{
/**
 Returns a string representation of the haplotypes and 
 frequencies for the given observation set.
*/
	public static String formatHaplotypes(Observation y)
	{
		double[] freq = y.getTrait().getLocus().alleleFrequencies();
		String[] names = y.getTrait().getLocus().alleleNames();

		StringBuffer s = new StringBuffer();
		for (int i=0; i<freq.length; i++)
			//s.append(StringFormatter.format(100*freq[i],5)+"\t"+names[i]+"\n");
			s.append(freq[i]+"\t"+names[i]+"\n");
		s.setLength(s.length()-1);

		return s.toString();
	}

/**
 Returns a string representation of the most likely pair of 
 haplotypes for each observed phenotype.
*/
	public static String formatGuesses(Observation y, GeneticDataSource gds)
	{
		StringBuffer s = new StringBuffer();
		
		Phenotype[] o = y.getData();
		Locus l = y.getTrait().getLocus();
		for (int i=0; i<o.length; i++)
		{
			s.append("\n");
			s.append( gds.id(i)+"\n");
			//s.append(x[i].pedid+" "+x[i].id+"\n");
			s.append("Phenotype: "+o[i]+"\n");

			Genotype g = null;
			Genotype best = null;
			double tot = 0;	
			double bb = 0;
			for (o[i].initGenotypes(); (g=o[i].nextGenotype()) != null; )
			{
				double f = l.alleleFrequencies()[g.a];
				f *= l.alleleFrequencies()[g.b];
				if (g.a != g.b)
					f *= 2;
				
				tot += f;
				if (f > bb)
				{
					best = g;
					bb = f;
				}
			}

			s.append("Best explanation\n");
			s.append("\t"+l.alleleNames()[best.a]);
			s.append("\n");
			s.append("\t"+l.alleleNames()[best.b]);
			s.append("\n");
			s.append("\t"+((int)(100*100*bb/tot)/100.0)+"%\n");
		}

		return s.toString();
	}
}
