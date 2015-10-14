//******************************************************************************
// GeneticDataSourceImp.java
//******************************************************************************
package edu.utah.med.genepi.gchapext;

import java.util.Hashtable;

import alun.genio.GeneticDataSource;
import alun.util.IntArray;
import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Study;

//==============================================================================
public class GeneticDataSourceImp implements GeneticDataSource
{
	protected final Study study;
	protected final Indiv[] indiv;
	protected final Indiv.GtSelector selector;
	protected final GDef gdef;
	protected final int index;
	private Hashtable<IntArray,double[][]> naps = null;

	//----------------------------------------------------------------------------
	public GeneticDataSourceImp( Study std, 
			Indiv[] ind, 
			Indiv.GtSelector gts,
			int num,
			GDef gDef )
	{
		study = std;
		indiv = ind; 
		selector = gts;
		gdef = gDef;
		index = num;
		naps = new Hashtable<IntArray,double[][]>();
	}

	//--------------------------------------------------------------------------
	/**
   The identifier of this data source.
	 */
	public String name()
	{ return "PedGenie"; }

	//--------------------------------------------------------------------------
	/**
   The number of genetic loci for which data is available.
   The loci will be accessed by index from 0 to nLoci()-1
   and are assumed to be in corresponding physical order.
	 */

	public int nLoci()
	{
		return gdef.getLocusCount();
	}

	//----------------------------------------------------------------------------
	// Returns a String identifier for the jth locus.
	public String locName(int j)
	{
		return gdef.getLocus(j).getMarker();
	}

	//----------------------------------------------------------------------------
	// The number of alleles at the jth genetic locus.
	public int nAlleles(int j)
	{
		return study.getLocusNumAllele(j);
	}

	//----------------------------------------------------------------------------
	/**
   Returns a string representation of the observation for the ith individuals 
   at the jth locus.
	 */
	public String call(int i,int j)
	{
		String pair = null;
		if ( (selector == Indiv.GtSelector.SIM) && (index >= 0))
			pair = indiv[i].getSimulatedGtype(index).getAllelePairAt(j).toString();
		else 
			pair = indiv[i].getGtype(selector).getAllelePairAt(j).toString();
		return pair;
	}

	//----------------------------------------------------------------------------
	/**
   The number of phenotypes at the jth genetic locus. 
   Must be greater than zero and may be infinite.
   Phenotype is used to mean any observation for the locus, including
   genotype information. So for a co-dominant genetic marker nPhenotypes(j)
   should return the same as nAlleles(j)*(nAlleles(j)-1)/2.
	 */
	public int nPhenotypes(int j)
	{
		return nAlleles(j) * (nAlleles(j) - 1) /2;
	}

	//----------------------------------------------------------------------------
	/**
   The frequencies of the alleles at the jth genetic locus.
	 */
	public double[] alleleFreqs(int j)
	{
		int alleles = study.getLocusNumAllele(j);
		double fill = (double) 1.0 / alleles;
		double[] x = new double[alleles];
		for ( int i = 0; i < alleles; i++)
		{
			x[i] = fill;
		}
		return x;
		//throw new RuntimeException("Method alleleFreqs() is not implemented" );
	}

	//----------------------------------------------------------------------------
	/**
   Returns the physical distance in Mb between loci i and j.
	 */
	public double getPhysicalDistance(int i, int j)
	{
		//return par.getPhysicalDistance(i,j);
		throw new RuntimeException("Method getPhysicalDistance() is not implemented" );
	}

	//----------------------------------------------------------------------------
	/**
   Returns the genetic distance in cM between loci i and j.
	 */
	public double getGeneticDistance(int i, int j)
	{
		//return par.getGeneticDistance(i,j);
		throw new RuntimeException("Method getGeneticDistance() is not implemented");
	}

	//----------------------------------------------------------------------------
	/**
   Returns the probability of a recombination between loci i and j in
   a single meiosis in females.
	 */
	public double getFemaleRecomFrac(int i, int j)
	{
		return getMaleRecomFrac(i, j);
	}

	//----------------------------------------------------------------------------
	/**
   Returns the probability of a recombination between loci i and j in
   a single meiosis in males.
	 */
	public double getMaleRecomFrac(int i, int j)
	{
		return 0.1;  
		//throw new RuntimeException("Method getMaleRecomFrac() is not implemented");
	}

	//----------------------------------------------------------------------------
	/**
   Returns the number of individuals who are being considered.
   Individuals will be accessed by integers between 0 and 
   nIndiviuals()-1 so repeated calls to the methods below must
   give consistent responses.
	 */
	public int nIndividuals()
	{ 
		return indiv.length;
	}

	//----------------------------------------------------------------------------
	/**
   Returns the position in the list of individuals of the father
   of the individual in the ith position in the list. If father
   is not in the list -1 should be returned.
	 */
	public int pa(int i)
	{    
		int papa = -1;
		String ped_id = indiv[i].myPed.getID();
		if ( (indiv[i].getParent_Indiv('1')) != null )
		{

			String id = (indiv[i].getParent_Indiv('1')).getID();
			for ( int j = 0; j < indiv.length; j++ )
			{
				if ( indiv[j].getID().equals(id) && indiv[j].myPed.getID().equals(ped_id) )
				{
					papa = j;
					break;
				}
			}
		}
		else 
		{
			papa = -1;
		}
		return papa;
	}

	//----------------------------------------------------------------------------
	/**
   Returns the position in the list of individuals of the mother
   of the individual in the ith position in the list. If mother
   is not in the list -1 should be returned.
	 */
	public int ma(int i)
	{
		int mama = -1;
		String ped_id = indiv[i].myPed.getID();
		if ( (indiv[i].getParent_Indiv('2')) != null )
		{
			String id = (indiv[i].getParent_Indiv('2')).getID();
			for ( int j = 0; j < indiv.length; j++ )
			{
				if ( indiv[j].getID().equals(id) && indiv[j].myPed.getID().equals(ped_id) )
				{
					mama = j;
					break;
				}
			}
		}
		else 
		{
			mama = -1;
		}
		return mama;	  
	}

	//----------------------------------------------------------------------------
	/**
   Returns the positions in the list of individuals of the children
   of the individual in the ith position in the list. If there
   are no offspring an array of length zero is returned.
	 */
	public int[] kids(int i)
	{
		/**
    List<Indiv> lkid = new ArrayList<Indiv>();
    Indiv[] allkids; 
    int[] kidlist;
    for ( Iterator m = indiv[i].getMarriages().iterator(); m.hasNext(); )
    {
      Marriage marriage = (Marriage) m.next();
      for ( Iterator k = marriage.getKids().iterator(); k.hasNext(); )
      {
        Indiv kid = (Indiv) k.next();
        lkid.add(kid);
      }
      allkids = (Indiv[]) lkid.toArray(new Indiv[0]);
    }
    kidlist = new int[allkids.length];
    for ( int k = 0; k < allkids.length; k++ )
      kidlist[k] = allkids[k].getIndividual_id(); 
    return kidlist;
		 */
		throw new RuntimeException("Method is not implemented" );
	}

	//----------------------------------------------------------------------------
	/**
   Returns a matrix of integers indexing the positions of a
   nuclear family. One row per family. The first element in
   the row is the father, the second the mother and the remainder
   are the indexes of the children.
	 **/
	public int[][] nuclearFamilies()
	{ throw new RuntimeException("Method is not implemented"); }

	//----------------------------------------------------------------------------
	/**
   Returns a String identifyer for the ith person in the list.
	 */
	public String id(int i)
	{
		//return String.valueOf(indiv[i].getPedigree().getPed_id()) + 
		//       " " + String.valueOf(indiv[i].getIndividual_id());
		return indiv[i].getPedigree().getPed_id() + 
		" " + indiv[i].getID();
	}

	//----------------------------------------------------------------------------
	/**
   If k = 0, returns the first allele for the ith individual
   at the jth locus. If k = 1, returns the second allele
   for the ith individual at the jth locus.
   Allele numbers should start at 0. If such a clear allele call
   is not possible return -1.
	 */
	public int indAllele(int i, int j, int k) 
	{
		int aa = -1;
		Gtype gtype = indiv[i].getGtype(selector);
		if ( gtype != null )
		{
			AllelePair allelepair = gtype.getAllelePairAt(j);
			if ( allelepair != null )
				aa = allelepair.getAlleleCode( k == 1 ) - 1;
			//aa = allelepair.getAlleleCode( k == 1 ) - 1;
			//aa = Integer.parseInt(allelepair.getAlleleCode( k == 1 )) - 1;
		}   
		return aa ;
	}

	//Ryan 06-20-07 overloaded to accomodate for an index for saved simulated data.
	public int indAllele(int i, int j, int k, int index) 
	{
		int aa = -1;
		Gtype gtype = indiv[i].getGtype(selector,index);
		if ( gtype != null )
		{
			AllelePair allelepair = gtype.getAllelePairAt(j);
			if ( allelepair != null )
				aa = allelepair.getAlleleCode( k == 1) - 1;
			//aa = allelepair.getAlleleCode( k == 1 ) - 1;
			//aa = Integer.parseInt(allelepair.getAlleleCode( k == 1 )) - 1;
		}   
		return aa ;
	}

	//----------------------------------------------------------------------------
	/**
   Returns the penetrance function for the ith person in the list
   at the jth genetic locus.
   This is a matrix of dimension nAlleles(j) x nAlleles(j) where the
   entry at row a, column b is the probability of the individual's 
   observed phenotype given that they have allele number a on the
   paternal chromosome and allele number b on the maternal chromosome.
   Again, phenotype is used in a very general way so this function is
   also used to express genotypic information. If an individual i
   is observed with genotype (x,y) at locus j then 
    	penetrance(i,j)[x,y] = penetrance(i,j)[y,x] = 1
   with zeros elsewhere.
   If the locus is not perfectly co-dominant, or there is error
   in the observation, this should be expressed appropriately in this
   matrix.
   If no information is present for the ith individual at the jth locus
   this method can return null.
	 */
	public double[][] penetrance(int i, int j) 
	{ 
		int a1 = -1;
		int a2 = -1;
		double[][] nt = null;
		Gtype gtype = indiv[i].getGtype(selector);
		if ( gtype != null )
		{
			AllelePair allelepair = gtype.getAllelePairAt(j);
			if ( allelepair != null )
			{
				a1 = allelepair.getAlleleCode(true) - 1;
				a2 = allelepair.getAlleleCode(false) - 1;
				nt = napTable(nAlleles(j), a1, a2);
			}
		}
		else
		{
			nt = null;
		}
		return nt;
	}

	//------------------------------------------------------------------------------
	private double[][] napTable(int na, int p, int q)
	{
		if (p < 0  && q < 0)
			return null;

		IntArray a = new IntArray(na,p,q);
		double[][] x = (double[][]) naps.get(a);

		if (x == null)
		{
			x = new double[na][];
			for (int i=0; i<x.length; i++)
				x[i] = new double[na];

			if (p < 0)
			{
				if (q < 0)
					for (int i=0; i<x.length; i++)
						for (int j=0; j<x[i].length; j++)
							x[i][j] = 1;
				else
					for (int i=0; i<x.length; i++)
						x[i][q] = x[q][i] = 1;
			}
			else
			{
				if (q < 0)
					for (int i=0; i<x.length; i++)
						x[i][p] = x[p][i] = 1;
				else
					x[p][q] = x[q][p] = 1;
			}

			naps.put(a,x);
		}

		return x;
	}

	public int nProbands() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int proband(int i) {
		// TODO Auto-generated method stub
		return 0;
	}  

}
