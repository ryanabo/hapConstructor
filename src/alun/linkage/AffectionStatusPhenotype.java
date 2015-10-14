package alun.linkage;

/**
 This represents the data for an individual at a locus
 specified in the linkage affected status format.
*/
public class AffectionStatusPhenotype extends LinkagePhenotype
{
/**
 Creates a new phenotype from the given status and liability class numbers.
 The data is checked for consistency with the data for the specified locus.
*/
	public AffectionStatusPhenotype(AffectionStatusLocus l, int stat, int liab)
	{
		setLocus(l);

		switch(status)
		{
		case UNKNOWN:
		case UNAFFECTED:
		case AFFECTED:
			status = stat;
			break;
		default:
			throw new LinkageException("Inappropriate affectation status "+status);
		}

		nliab = l.getLiabilities().length;

		//if (liab < 1 || liab > l.getLiabilities().length)
		if (liab < 0 || liab > nliab)
			throw new LinkageException("Liability class out of range 0 to "+nliab+" : "+liab);
		else
			liability = liab;
		
	}

	public LinkagePhenotype nullCopy()
	{
		return new AffectionStatusPhenotype((AffectionStatusLocus)getLocus(),0,1);
	}

/**
 Returns a string representation of the data.
*/
	public String toString()
	{
		if (nliab > 1)
			return f.format(status,2)+" "+f.format(liability,2);
		else
			return f.format(status,2);
	}

// Private data.

	public static final int UNKNOWN = 0;
	public static final int AFFECTED = 2;
	public static final int UNAFFECTED = 1;

	public int status = 0;
	public int liability = 0;
	public int nliab = 0;
}
