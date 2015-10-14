package alun.linkage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 This structure hold the information associated with the data
 from a linkage parameter file.
*/
public class LinkageParameterData
{
	public LinkageParameterData()
	{
	}

/**
 Creates a new parameter file structure using data read from 
 the given input formatter.
*/
	public LinkageParameterData(LinkageFormatter b) throws IOException
	{
		this(b,false);
	}

	public LinkageParameterData(LinkageFormatter b, boolean forceConversion) throws IOException
	{
		b.readLine();
		nloci = b.readInt("number of loci",0,true,true);
		risklocus = b.readInt("risk locus number",0,false,false);
		sexlinked = b.readInt("sex linkage code",0,false,false);
		programcode = b.readInt("program code",0,false,false);
		line1comment = b.restOfLine();

		b.readLine();
		mutlocus = b.readInt("mutation locus code",0,false,false);
		mutmale = b.readDouble("male mutation rate",0,false,false);
		mutfemale = b.readDouble("female mutation rate",0,false,false);
		disequilibrium = b.readInt("disequilibrium code",0,false,false);
		line2comment = b.restOfLine();

		b.readLine();
		order = new int[nloci];
		boolean ok = true;
		for (int i=0; ok && i<order.length; i++)
		{
			if (b.newToken() && b.nextIsInt())	
				order[i] = b.getInt();
			else 
				ok = false;
		}
		if (!ok)
		{
			b.warn("Can't read physical order of loci.\n\tAssumed to be in same order as in file.");
			for (int i=0; i<order.length; i++)
				order[i] = i+1;
		}
		line3comment = b.restOfLine();

		locus = new LinkageLocus[nloci];
		for (int i=0; i<locus.length; i++)
		{
			b.readLine();
			int type = b.readInt("locus code",0,true,true);

			switch(type)
			{
			case LinkageLocus.AFFECTION_STATUS:
				locus[i] = new AffectionStatusLocus(b);
				break;
			case LinkageLocus.NUMBERED_ALLELES:
				locus[i] = new NumberedAlleleLocus(b);
				break;
			case LinkageLocus.QUANTITATIVE_VARIABLE:
				locus[i] = new QuantitativeLocus(b);
				break;

			case LinkageLocus.BINARY_FACTORS:
				//locus[i] = new LinkageBinaryLocus(b);
			default:
				b.crash("LINKAGE locus code "+type+" is not yet implemented in these programs.");
				break;
			}
		}

		b.readLine();
		sexdifference = b.readInt("sex recombination difference code",0,false,false);
		interference = b.readInt("interference code",0,false,false);
		line4comment = b.restOfLine();
		
		b.readLine();
		malethetas = new double[nloci-1];
		for (int j = 0; j<malethetas.length; j++)
		{
			if (b.newToken() && b.nextIsDouble())	
			{
				malethetas[j] = b.getDouble();
				if (malethetas[j] < 0)
					b.crash("Negative recombination fraction specified "+malethetas[j]+".");
			}
			else 
				b.crash("Can't read "+malethetas.length+" recombiantion fractions as doubles.");
			
		}
		line5comment = b.restOfLine();

		if (sexdifference == 0)
			femalethetas = malethetas;
		else
		{
			b.readLine();
			femalethetas = new double[nloci-1];
			for (int j = 0; j<femalethetas.length; j++)
			{
				if (b.newToken() && b.nextIsDouble())	
				{
					femalethetas[j] = b.getDouble();
					if (malethetas[j] < 0)
						b.crash("Negative recombination fraction specified "+femalethetas[j]+".");
				}
				else 
					b.crash("Can't read "+femalethetas.length+" recombiantion fractions as doubles.");
				
			}
			line5acomment = b.restOfLine();
		}

		ok = true;
		for (int j=0; ok && j<malethetas.length; j++)
		{
			if (malethetas[j] > 0.5 || femalethetas[j] > 0.5)
				ok = false;
		}

		if (forceConversion || !ok)
		{
			if (!forceConversion)
			{
				b.warn("At least one recombination fraction is greater than 0.5."+
					"\n\tWill assume distances are centi Morgans and convert using Kosambi mapping function");
			}
			else
			{
				b.warn("Converting inter locus distances from centi Morgans to recombination fractions using Kosambi mapping function");
			}

			for (int j=0; j<malethetas.length; j++)
				malethetas[j] = cMToTheta(malethetas[j]);

			if (sexdifference != 0)
			{
				for (int j=0; j<femalethetas.length; j++)
					femalethetas[j] = cMToTheta(femalethetas[j]);
			}
		}

		b.readLine();
		variablelocus = b.readInt("variable locus code",0,false,false);
		increment = b.readDouble("increment amount",0,false,false);
		stoppingvalue = b.readDouble("stoppint value",0,false,false);
		line6comment = b.restOfLine();
	}

/** 
 Creates a copy of the given linkage parameter data.
*/
	public LinkageParameterData(LinkageParameterData p)
	{
		nloci = p.nloci;
		risklocus = p.risklocus;
		sexlinked = p.sexlinked;
		programcode = p.programcode;
		line1comment = p.line1comment;
		mutlocus = p.mutlocus;
		mutmale = p.mutmale;
		mutfemale = p.mutfemale;
		disequilibrium = p.disequilibrium;
		line2comment = p.line2comment;
		line3comment = p.line3comment;
		sexdifference = p.sexdifference;
		interference = p.interference;
		line4comment = p.line4comment;
		line5comment = p.line5comment;
		variablelocus = p.variablelocus;
		increment = p.increment;
		stoppingvalue = p.stoppingvalue;
		line6comment = p.line6comment;
		
		
		order = new int[p.order.length];
		for (int i=0; i<order.length; i++)
			order[i] = p.order[i];

		malethetas = new double[p.malethetas.length];
		for (int i=0; i<malethetas.length; i++)
			malethetas[i] = p.malethetas[i];

		if (sexdifference == 0)
			femalethetas = malethetas;
		else
		{
			femalethetas = new double[p.femalethetas.length];
			for (int i=0; i<femalethetas.length; i++)
				femalethetas[i] = p.femalethetas[i];
		}

		locus = new LinkageLocus[p.locus.length];
		for (int i=0; i<locus.length; i++)
			switch(p.locus[i].type)
			{
			case LinkageLocus.AFFECTION_STATUS:
				locus[i] = new AffectionStatusLocus((AffectionStatusLocus)p.locus[i]);
				break;
			case LinkageLocus.NUMBERED_ALLELES:
				locus[i] = new NumberedAlleleLocus((NumberedAlleleLocus)p.locus[i]);
				break;
			case LinkageLocus.QUANTITATIVE_VARIABLE:
				locus[i] = new QuantitativeLocus((QuantitativeLocus)p.locus[i]);
				break;

			case LinkageLocus.BINARY_FACTORS:
				//locus[i] = new BinaryLocus((BinaryLocus)p.locus[i]));
			default:
				throw new RuntimeException("LINKAGE locus code "+p.locus[i].type+" is not yet implemented in these programs.");
			}
	}

/**
 Creates a copy of the subset of the given data as indexed by the array.
*/
	public LinkageParameterData(LinkageParameterData p, int[] x)
	{
		nloci = x.length;
		risklocus = p.risklocus;
		sexlinked = p.sexlinked;
		programcode = p.programcode;
		line1comment = p.line1comment;
		
		mutlocus = p.mutlocus;
		mutmale = p.mutmale;
		mutfemale = p.mutfemale;
		disequilibrium = p.disequilibrium;
		line2comment = p.line2comment;
		
		order = new int[nloci];
		for (int i=0; i<order.length; i++)
			order[i] = i+1;
		line3comment = p.line3comment;

		locus = new LinkageLocus[x.length];
		for (int i=0; i<locus.length; i++)
			locus[i] = p.locus[x[i]];

		sexdifference = p.sexdifference;
		interference = p.interference;
		line4comment = p.line4comment;

		malethetas = new double[nloci-1];
		for (int i=0; i<malethetas.length; i++)
			malethetas[i] = p.maleTheta(x[i],x[i+1]);
		line5comment = p.line5comment;
		
		if (sexdifference == 0)
			femalethetas = malethetas;
		else
		{
			femalethetas = new double[nloci-1];
			for (int i=0; i<femalethetas.length; i++)
				femalethetas[i] = p.femaleTheta(x[i],x[i+1]);
			line5acomment = p.line5acomment;
		}

		variablelocus = p.variablelocus;
		increment = p.increment;
		stoppingvalue = p.stoppingvalue;
		line6comment = p.line6comment;
	}
	
/**
 Returns the array of loci in this set of data.
*/
	public LinkageLocus[] getLoci()
	{
		return locus;
	}

/*
 Returns the indexed locus.
*/
	public LinkageLocus getLocus(int i)
	{
		return locus[i];
	}

/**
 Returns the number of loci in this set of data.
*/
	public int nLoci()
	{
		return locus.length;
	}

/**
 Returns a string representing the data for a collection of 
 loci in the same format as in the .par file from which it
 was read. For large data sets it's better to use the write()
 functions.
*/
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append(nloci+" "+risklocus+" "+sexlinked+" "+programcode+" "+line1comment+"\n");

		s.append(mutlocus+" "+mutmale+" "+mutfemale+" "+disequilibrium+" "+line2comment+"\n");

		for (int i=0; i<order.length; i++)
			s.append(order[i]+" ");
		s.append(line3comment+"\n");

		for (int i=0; i<nloci; i++)
			s.append(locus[i]);

		s.append(sexdifference+" "+interference+" "+line4comment+"\n");
		
		for (int i=1; i<nLoci(); i++)
			s.append(maleTheta(i-1,i)+" ");
		s.append(line5comment+"\n");

		if (sexdifference != 0)
		{
			for (int i=1; i<nLoci(); i++)
				s.append(femaleTheta(i-1,i)+" ");
			s.append(line5acomment+"\n");
		}

		s.append(variablelocus+" "+increment+" "+stoppingvalue+" "+line6comment+"\n");

		s.setLength(s.length()-1);
		return s.toString();
	}

/**
 Writes the data to the given print stream in LINKAGE format.
*/
	public void writeTo(PrintStream s)
	{
		writeTo(new PrintWriter(s));
	}

/**
 Writes the data to the given print writer in LINKAGE format.
*/
	public void writeTo(PrintWriter p)
	{
		p.println(nloci+" "+risklocus+" "+sexlinked+" "+programcode+" "+line1comment);

		p.println(mutlocus+" "+mutmale+" "+mutfemale+" "+disequilibrium+" "+line2comment);

		for (int i=0; i<order.length; i++)
			p.print(order[i]+" ");
		p.println(line3comment);

		for (int i=0; i<nloci; i++)
			p.print(locus[i]);
		p.println(sexdifference+" "+interference+" "+line4comment);
		
		for (int i=1; i<nLoci(); i++)
			p.print(maleTheta(i-1,i)+" ");
		p.println(line5comment);
		
		if (sexdifference != 0)
		{
			for (int i=1; i<nLoci(); i++)
				p.print(femaleTheta(i-1,i)+" ");
			p.println(line5acomment);
		}

		p.print(variablelocus+" "+increment+" "+stoppingvalue+" "+line6comment);
		
		p.flush();
	}

/**
 Returns the male recombination fraction between the specified loci.
*/
	public double maleTheta(int i, int j)
	{
		return cMToTheta(getGeneticDistance(i,j,true));
	}

/**
 Returns the female recombination fraction between the specified loci.
*/
	public double femaleTheta(int i, int j)
	{
		return cMToTheta(getGeneticDistance(i,j,false));
	}

/**
 Converts centiMorgans to recombination fractions using the Kosambi conversion.
*/
	public static double cMToTheta(double d)
	{
		double t = d/100.0;
		t = Math.exp(-4*t);
		t = 0.5 * (1-t) / (1+t);
		return t;
	}

/**
 Converts recombination fractions to centiMorgans using the Kosambi conversion.
*/
	public static double thetaTocM(double t)
	{
		double d = (1 - 2*t) / (1 + 2*t);
		d = -0.25 * Math.log(d);
		d = d*100;
		return d;
	}

// Private data and methods.

	protected int nloci = 0;
	private int risklocus = 0;
	private int sexlinked = 0;
	private int programcode = 0;
	private String line1comment = null;

	private int mutlocus = 0;
	private double mutmale = 0;
	private double mutfemale = 0;
	private int disequilibrium = 0;
	private String line2comment = null;

	protected int[] order = null;
	private String line3comment = null;

	protected LinkageLocus[] locus = null;

	private int sexdifference = 0;
	private int interference = 0;
	private String line4comment = null;

	protected double[] malethetas = null;
	protected double[] femalethetas = null;

	private String  line5comment = null;
	private String  line5acomment = null;

	private int variablelocus = 0;
	private double increment = 0;
	private double stoppingvalue = 0; 
	private String line6comment = null;

	private double getGeneticDistance(int i, int j, boolean ismale)
	{
		double g = 0;
		int ii = i;
		int jj = j;
		if (j < i)
		{
			ii = j;
			jj = i;
		}
	
		for (int k=ii; k<jj; k++)
			g += thetaTocM( ismale ? malethetas[k] : femalethetas[k] );
		return g;
	}

/**
 Test main.
*/
	public static void main(String[] args)
	{
		try
		{
			LinkageFormatter f = new LinkageFormatter(new BufferedReader(new InputStreamReader(System.in)),"Par file");
			LinkageParameterData l = new LinkageParameterData(f);
			System.out.println(l);
		}
		catch (Exception e)
		{
			System.err.println("Caught in LinkageParameterData:main()");
			e.printStackTrace();
		}
	}
}
