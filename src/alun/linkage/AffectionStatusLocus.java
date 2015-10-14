package alun.linkage;

import java.io.IOException;

import alun.util.StringFormatter;

/**
 This class represents a locus as specified in the
 linkage affectation status format.
*/
public class AffectionStatusLocus extends LinkageLocus
{
/**
 Creates a new locus with data read from the given input formatter.
*/
	public AffectionStatusLocus(LinkageFormatter b) throws IOException
	{
		type = AFFECTION_STATUS;

		int na = b.readInt("number of alleles",0,true,true);
		freq = new double[na];
		line1comment = b.restOfLine();

		b.readLine();
		for (int i=0; i<freq.length; i++)
		{
			freq[i] = b.readDouble("frequency "+(i+1),0,true,true);
			if (freq[i] < 0)
				b.crash("Negative allele frequency "+freq[i]);
		}
		line2comment = b.restOfLine();

		b.readLine();
		int nliab = b.readInt("number of liability classes",0,true,true);
		liab = new double[nliab][];
		comment = new String[liab.length];
		line3comment = b.restOfLine();
		
		int ng = freq.length;
		ng = (ng*(ng+1))/2;
		for (int i=0; i<liab.length; i++)
		{
			b.readLine();
			liab[i] = new double[ng];
			for (int j=0; j<liab[i].length; j++)
				liab[i][j] = b.readDouble("penetrance "+(i+1)+" "+(j+1),0,true,true);
			comment[i] = b.restOfLine();
		}
	}

	public AffectionStatusLocus(AffectionStatusLocus a)
	{
		super(a);

		type = AFFECTION_STATUS;

		liab = new double[a.liab.length][];
		for (int i=0; i<liab.length; i++)
		{
			liab[i] = new double[a.liab[i].length];
			for (int j=0; j<liab[i].length; j++)
				liab[i][j] = a.liab[i][j];
		}
		
		comment = new String[a.comment.length];
		for (int i=0; i<comment.length; i++)
			comment[i] = a.comment[i];
		
		line2comment = a.line2comment;
		line3comment = a.line3comment;
	}

/**
 Reads the data for a phenotype at this locus from the given input
 formatter.
*/
	public LinkagePhenotype readPhenotype(LinkageFormatter f)
	{
		int s = f.readInt("disease status",0,true,false);
		if (s < 0 || s > 2)
		{
			f.warn("disease status "+s+" is out of range "+0+" "+2+"\n\tSetting to 0");
			s = 0;
		}
		int l = 1;
		if (liab.length > 1)
		{
			l = f.readInt("liability class",0,true,false);
			if (s != 0)
			{
				if (l < 1 || l > liab.length)
				{
					f.warn("liability class "+l+" is out of range "+1+" "+liab.length+
							"\n\tSetting disease status to 0 and libility class to 0");
					s = 0;
					l = 0;
				}
			}
			else
			{
				l = 0;
			}
		}
		return new AffectionStatusPhenotype(this,s,l);
	}

/**
 Returns the two dimentional array containing the liability data.
*/
	public double[][] getLiabilities()
	{
		return liab;
	}
	
/**
 The two dimentional array containing the liability data.
 These will typically be penetrance probabilities.
*/
	public double[][] liab = null;
/**
 An array holding the comment strings for each linkage liability class.
*/
	public String comment[] = null;
/**
 The string of comment from the second line of input.
*/
	public String line2comment = null;
/**
 The string of comment from the third line of input.
*/
	public String line3comment = null;

/**
 Returns a string representing the parameters of this locus in the
 same format at in the linkage .par file.
*/
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append(type+" "+freq.length+" "+line1comment+"\n");

		for (int i=0; i<freq.length; i++)
			s.append(StringFormatter.format(freq[i],2,6)+" ");
		s.append(line2comment+"\n");

		s.append(liab.length+" "+line3comment+"\n");

		for (int i=0; i<liab.length; i++)
		{
			for (int j=0; j<liab[i].length; j++)
				s.append(StringFormatter.format(liab[i][j],2,6)+" ");
			s.append(comment[i]+"\n");
		}

		return s.toString();
	}
}
