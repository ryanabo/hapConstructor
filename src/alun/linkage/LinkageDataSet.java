package alun.linkage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 This class holds the information from a standard Linkage parameter data file
 and a standard Linkage pedigree file.
*/
public class LinkageDataSet
{
	public LinkageDataSet()
	{
	}

	public LinkageDataSet(String datain, String pedin) throws IOException
	{
		this(datain, pedin, false);
	}

/**
 Creates a new LinkageDataSet with only the loci indicated in the given array
 of indexes.
*/
	public LinkageDataSet(LinkageDataSet l, int[] x)
	{
		set(new LinkageParameterData(l.getParameterData(),x), new LinkagePedigreeData(l.getPedigreeData(),x));
	}

/**
 Creates a new LinkageDatSet from the parameter data and the pedigree data
 in the files whose names are given in the constructor.
*/
	public LinkageDataSet(String datain, String pedin, boolean premake) throws IOException
	{
		parfile = datain;
		LinkageFormatter f = new LinkageFormatter(new BufferedReader(new FileReader(parfile)),"Par file");
		LinkageParameterData lpd = new LinkageParameterData(f);
		
		pedfile = pedin;
		f = new LinkageFormatter(new BufferedReader(new FileReader(pedfile)),"Ped file");
		LinkagePedigreeData ldd = new LinkagePedigreeData(f,lpd,premake);
		set(lpd,ldd);
	}

	public LinkageDataSet(LinkageParameterData parin, LinkagePedigreeData pedin)
	{
		set(parin,pedin);
	}

	public void set(LinkageParameterData parin, LinkagePedigreeData pedin)
	{
		par = parin;
		ped = pedin;
		name = ( parfile == null || pedfile == null ? "" : parfile+":"+pedfile );
	}

/**
 Returns the object containting the data from the linkage .par file.
*/
	public LinkageParameterData getParameterData()
	{
		return par;
	}

/**
 Returns the object containing the data from the linkage .ped file.
*/
	public LinkagePedigreeData getPedigreeData()
	{
		return ped;
	}

/**
 Returns a string representation of the data contained in the data set.
*/
	public String toString()
	{
		return parfile + ":\n" + par + "\n" + pedfile + ":\n" + ped;
	}

	public String name()
	{
		return name;
	}

	public LinkageDataSet[] splitByPedigree()
	{
		LinkagePedigreeData[] lpd = getPedigreeData().splitByPedigree();
		LinkageDataSet[] ld = new LinkageDataSet[lpd.length];
		for (int i=0; i<ld.length; i++)
		{
			ld[i] = new LinkageDataSet(new LinkageParameterData(getParameterData()),lpd[i]);
			int x = lpd[i].getIndividuals()[0].pedid;
			ld[i].name = ""+lpd[i].getIndividuals()[0].pedid;
/*
			if (name.equals(""))
				ld[i].name = name+lpd[i].getIndividuals()[0].pedid;
			else
				ld[i].name = name+":"+lpd[i].getIndividuals()[0].pedid;
*/
		}

		return ld;
	}

	public void countAlleleFreqs(int j)
	{
		if (!(par.getLocus(j) instanceof NumberedAlleleLocus))
			return;

		LinkageIndividual[] ind = ped.getIndividuals();
		double[] c = new double[par.getLocus(j).nAlleles()];
		int n = 0;
		for (int i=0; i<ind.length; i++)
		{
			if (((NumberedAllelePhenotype)ind[i].pheno[j]).a1 > 0)
			{
				c[((NumberedAllelePhenotype)ind[i].pheno[j]).a1-1] += 1;
				n++;
			}
			if (((NumberedAllelePhenotype)ind[i].pheno[j]).a2 > 0)
			{
				c[((NumberedAllelePhenotype)ind[i].pheno[j]).a2-1] += 1;
				n++;
			}
		}

		for (int k=0; k<c.length; k++)
			c[k] /= n;

		par.getLocus(j).setAlleleFrequencies(c);
	}

	public void countAlleleFreqs()
	{
		//LinkageIndividual[] ind = ped.getIndividuals();
		for (int i=0; i<par.nLoci(); i++)
		{
			countAlleleFreqs(i);
/*
			if (!(par.getLocus(i) instanceof NumberedAlleleLocus))
				continue;

			double[] c = new double[par.getLocus(i).nAlleles()];
			int n = 0;
			for (int j=0; j<ind.length; j++)
			{
				if (((NumberedAllelePhenotype)ind[j].pheno[i]).a1 > 0)
				{
					c[((NumberedAllelePhenotype)ind[j].pheno[i]).a1-1] += 1;
					n++;
				}
				if (((NumberedAllelePhenotype)ind[j].pheno[i]).a2 > 0)
				{
					c[((NumberedAllelePhenotype)ind[j].pheno[i]).a2-1] += 1;
					n++;
				}
			}

			for (int j=0; j<c.length; j++)
				c[j] /= n;

			par.getLocus(i).setAlleleFrequencies(c);
*/
		}
	}

	public void downCode(boolean hard)
	{
		downCode(hard ? 2 : 0);
	}
	
	public void downCode(int hard)
	{
// hard == 2 ==> Hard downcode: only observed alleles kept.
// hard == 0 ==> Soft downcode: observed alleles plus one catchall kept.
// hard == 1 ==> Like Hard downcode, but loci with one observed allele treated as in Soft.

		for (int i=0; i<par.nLoci(); i++)
		{
			int[] c = downCode(i,hard);
		}

	}

	public int[] downCode(int i, int hard)
	{
		int[] c = new int[par.getLocus(i).nAlleles()+1];

		if (!(par.getLocus(i) instanceof NumberedAlleleLocus))
		{
			for (int j=0; j<c.length; j++)
				c[j] = j;
			return c;
		}

		LinkageIndividual[] ind = ped.getIndividuals();


		for (int j=0; j<ind.length; j++)
		{
			c[((NumberedAllelePhenotype)ind[j].pheno[i]).a1]++;
			c[((NumberedAllelePhenotype)ind[j].pheno[i]).a2]++;
		}

		c[0] = 0;
		int k = 0;
		for (int j=1; j<c.length; j++)
			if (c[j] > 0)
				c[j] = ++k;

		if (k == 0)
		{
			for (int j=0; j<c.length; j++)
				c[j] = 1;
			return c;
		}

		if (k == c.length-1)
			return c;

		if (hard == 0 || (hard == 1 && k == 1) )
		{
			++k;
			for (int j=1; j<c.length; j++)
				if (c[j] == 0)
					c[j] = k;
		}

		((NumberedAlleleLocus)par.getLocus(i)).reCode(c);
		for (int j=0; j<ind.length; j++)
			((NumberedAllelePhenotype)ind[j].pheno[i]).reCode(c);

		return c;
	}

// Private data.

	private String parfile = null;
	private String pedfile = null;
	private String name = null;
	private LinkageParameterData par = null;
	private LinkagePedigreeData ped = null;

/**
 Main reads a Linkage data file and a Linkage pedigree file and writes
 the output to standard output.
*/
	public static void main(String[] args)
	{
		try
		{

			switch(args.length)
			{
			case 2:
				LinkageDataSet l = new LinkageDataSet(args[0],args[1]);
				System.out.println(l);
				break;
			default:
				System.err.println("Specify Linkage parameter file and Linkage pedigree file.");
				System.exit(0);
			}
		}
		catch (Exception e)
		{
			System.err.println("Caught in LinkageDataSet:main()");
			e.printStackTrace();
		}
	}
}
