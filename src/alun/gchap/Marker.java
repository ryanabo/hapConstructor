package alun.gchap;


/**
 This class extends GeneticTrait to represent a single marker.
*/
public class Marker extends GeneticTrait
{
/** 
 Creates a new genetic trait with an underlying locus that has the
 given number of alleles.
 Phenotypes are derived assuming codominance.
*/
	public Marker(int n)
	{
		super(new Locus(n));
		
		// Allocate complete phenotypes
		phen = new Phenotype[n][];
		for (int i=0; i<phen.length; i++)
			phen[i] = new Phenotype[n];

		for (int j=0; j<phen.length; j++)
			for (int i=0; i<=j; i++)
			{
				int[][] p = {{i,j}};
				//Phenotype o = new MarkerPhenotype(getLocus(),i+":"+j,p);
				Phenotype o = new MarkerPhenotype(getLocus(),(i+1)+":"+(j+1),p);
				phen[i][j] = o;
				phen[j][i] = o;
				putPhenotype(o);
			}

		// Allocate partial phenotypes
		partial = new Phenotype[n];
		for (int i=0; i<partial.length; i++)
		{
			int[][] p = new int[n][];
			for (int j=0; j<n; j++)
			{ 
				int[] x = {i,j};
				p[j] = x;
			}
			Phenotype o = new MarkerPhenotype(getLocus(),(i+1)+":?",p);
			partial[i] = o;
			putPhenotype(o);
		}

		// Allocate the missing phenotype
		int[][] p  = new int[(n*(n+1))/2][];
		for (int i=0, k=0; i<n; i++)
			for (int j=0; j<=i; j++)
			{
				int[] x = {i,j};
				p[k++] = x;
			}
		missing = new MarkerPhenotype(getLocus(),"?:?",p);
		putPhenotype(missing);
	}

/**
 Returns the phenotype for the given pair of alleles.
*/
	public Phenotype findPhenotype(int i, int j)
	{
		return i > -1 ? (j>-1? phen[i][j] : partial[i]) : (j>-1? partial[j] : missing);
	}

/**
 Adds a count for the given observation to the trait.
*/
	public void addObservations(int i, int j, int k)
	{
		phen[i][j].incFrequency(k);
	}

// Private data.

	private Phenotype[][] phen = null;
	private Phenotype[] partial = null;
	private Phenotype missing = null;

/**
 Test main.
*/
	public static void main(String[] args)
	{
		Marker m = new Marker(20);

		m.addObservations(0,0,1000000);
		m.addObservations(0,9,2000000);
		m.addObservations(9,9,2000000);
		m.addObservations(0,5,1);

		System.out.println(m);
		m.downCode();
		System.out.println(m);
		m.geneCount(1);
		System.out.println(m);
	}
}
