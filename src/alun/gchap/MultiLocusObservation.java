package alun.gchap;

/**
 This class represents an observation at a list of loci.
*/
public class MultiLocusObservation extends Observation
{
/**
 Create a new multi locus observation from the
 observations on the constituent two loci, each of which may
 itself be a multi locus observation.
*/
	public MultiLocusObservation(Observation a, Observation b)
	{
		if (a.d.length != b.d.length)
			throw new RuntimeException("Observation missmatch");

		m = new MultiLocusTrait(a.m,b.m);

		d = new Phenotype[a.d.length];
		for (int j=0; j<d.length; j++)
		{
			d[j] = ((MultiLocusTrait)m).findPhenotype(a.d[j],b.d[j]);
			if (d[j] == null)
				continue;
			d[j].incFrequency(1);
		}

		m.downCode();
	}
}
