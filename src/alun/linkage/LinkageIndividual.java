package alun.linkage;

import java.io.PrintStream;
import java.io.PrintWriter;

import alun.util.StringFormatter;

/**
 A class that contains the information for an individual from the
 linkage .ped file.
*/
public class LinkageIndividual implements LinkConstants
{
	public LinkageIndividual()
	{
	}

	public LinkageIndividual(LinkageIndividual l, int[] x)
	{
		pedid = l.pedid;
		id = l.id;
		paid = l.paid;
		maid = l.maid;
		firstbornid = l.firstbornid;
		pasnextid = l.pasnextid;
		masnextid = l.masnextid;
		sex = l.sex;
		proband = l.proband;
		comment = l.comment;
		pheno = new LinkagePhenotype[x.length];
		for (int i=0; i<pheno.length; i++)
			pheno[i] = l.pheno[x[i]];
	}

	public LinkageIndividual(LinkageIndividual l, int newid)
	{
		id = newid;
		pedid = l.pedid;
		if (l.pheno != null)
		{
			pheno = new LinkagePhenotype[l.pheno.length];
			for (int i=0; i<pheno.length; i++)
				pheno[i] = l.pheno[i].nullCopy();
		}
	}

/**
 Creates a new linkage individual by reading the data from the given 
 input formatter. Checks the data for consiency with with the data
 from the parameter file as specified in the second argument.
*/ 
/*
	public LinkageIndividual(LinkageFormatter b, LinkageParameterData par, boolean premake)
	{
		this(b,par,(premake ? PREMAKE : STANDARD));
	}
*/

/*

	public LinkageIndividual(LinkageFormatter b, LinkageParameterData par, int format)
	{
		if (format == TRIPLET)
		{
			pedid = 0;

			id = b.readInt("individual id",0,true,true);
			if (id <= 0)
				b.crash("individual id "+id+" is not positive");
	
			paid = b.readInt("father's id",0,true,false);
			if (paid < 0)
			{
				b.warn("father's id "+id+" is negative"+"\n\tSetting to 0");
				paid = 0;
			}
	
			maid = b.readInt("mother's id",0,true,false);
			if (maid < 0)
			{
				b.warn("mother's id "+id+" is negative"+"\n\tSetting to 0");
				maid = 0;
			}
	
			if ( (paid == 0 && maid > 0) || (paid > 0 && maid == 0))
				b.warn("individual "+id+" has only one parent specified "+paid+" "+maid+
						"\n\tThis may crash some programs");

			comment = b.restOfLine();
			return;
		}

		pedid = b.readInt("kindred number",0,true,false);

		id = b.readInt("individual id",0,true,true);
		if (id <= 0)
			b.crash("individual id "+id+" is not positive");

		paid = b.readInt("father's id",0,true,false);
		if (paid < 0)
		{
			b.warn("father's id "+id+" is negative"+"\n\tSetting to 0");
			paid = 0;
		}

		maid = b.readInt("mother's id",0,true,false);
		if (maid < 0)
		{
			b.warn("mother's id "+id+" is negative"+"\n\tSetting to 0");
			maid = 0;
		}

		if ( (paid == 0 && maid > 0) || (paid > 0 && maid == 0))
			b.warn("individual "+id+" has only one parent specified "+paid+" "+maid+
					"\n\tThis may crash some programs");

		if (format == STANDARD)
		{
			firstbornid = b.readInt("first child pointer",0,false,false);
			pasnextid = b.readInt("father's next child pointer",0,false,false);
			masnextid = b.readInt("mother's next child pointer",0,false,false);
		}

		sex = b.readInt("sex",0,true,false);
		if (sex < 0 || sex > 2)
			b.warn("sex indicator "+sex+" is out of range "+0+" "+2+"\n\tSetting to 0");

		if (format == STANDARD)
			proband = b.readInt("proband status",0,false,false);

		if (par != null)
		{
			LinkageLocus[] l = par.getLoci();
			pheno = new LinkagePhenotype[l.length];
			for (int i=0; i<pheno.length; i++)
				pheno[i] = l[i].readPhenotype(b);
		}

		comment = b.restOfLine();
	}
*/

	public LinkageIndividual(LinkageFormatter b, LinkageParameterData par, int format)
	{
		if (format == TRIPLET)
		{
			pedid = 0;

			id = b.readInt("individual id",0,true,true);
			if (id <= 0)
				b.crash("individual id "+id+" is not positive");
	
			paid = b.readInt("father's id",0,true,false);
			if (paid < 0)
			{
				b.warn("father's id "+id+" is negative"+"\n\tSetting to 0");
				paid = 0;
			}
	
			maid = b.readInt("mother's id",0,true,false);
			if (maid < 0)
			{
				b.warn("mother's id "+id+" is negative"+"\n\tSetting to 0");
				maid = 0;
			}
	
			if ( (paid == 0 && maid > 0) || (paid > 0 && maid == 0))
				b.warn("individual "+id+" has only one parent specified "+paid+" "+maid+
						"\n\tThis may crash some programs");

			comment = b.restOfLine();
			return;
		}

		pedid = b.readInt("kindred number",0,true,false);

		id = b.readInt("individual id",0,true,true);
		if (id <= 0)
			b.crash("individual id "+id+" is not positive");

		paid = b.readInt("father's id",0,true,false);
		if (paid < 0)
		{
			b.warn("father's id "+id+" is negative"+"\n\tSetting to 0");
			paid = 0;
		}

		maid = b.readInt("mother's id",0,true,false);
		if (maid < 0)
		{
			b.warn("mother's id "+id+" is negative"+"\n\tSetting to 0");
			maid = 0;
		}

		if ( (paid == 0 && maid > 0) || (paid > 0 && maid == 0))
			b.warn("individual "+id+" has only one parent specified "+paid+" "+maid+
					"\n\tThis may crash some programs");

		if (format == STANDARD)
		{
			firstbornid = b.readInt("first child pointer",0,false,false);
			pasnextid = b.readInt("father's next child pointer",0,false,false);
			masnextid = b.readInt("mother's next child pointer",0,false,false);
		}

		sex = b.readInt("sex",0,true,false);
		if (sex < 0 || sex > 2)
			b.warn("sex indicator "+sex+" is out of range "+0+" "+2+"\n\tSetting to 0");

		if (format == STANDARD)
			proband = b.readInt("proband status",0,false,false);

		if (par != null)
		{
			LinkageLocus[] l = par.getLoci();
			pheno = new LinkagePhenotype[l.length];
			for (int i=0; i<pheno.length; i++)
				pheno[i] = l[i].readPhenotype(b);
		}

		comment = b.restOfLine();
	}

/**
 Returns the array of linkage phonetypes for this individual.
*/
	public LinkagePhenotype[] getPhenotypes()
	{
		return pheno;
	}

	public LinkagePhenotype getPhenotype(int i)
	{
		return pheno[i];
	}

	public int index = 0;

/**
 The individual's pedigree id number.
*/
	public int pedid = 0;
/**
 The individual's id number.
*/
	public int id = 0;
/**
 The individual's father's id number.
*/
	public int paid = 0;
/**
 The individual's  mother's id number.
*/
	public int maid = 0;
/**
 The individual's first born child's id number.
*/
	public int firstbornid = 0;
/**
 The individual's father's next born child's id number.
*/
	public int pasnextid = 0;
/**
 The individual's mother's next born child's id number.
*/
	public int masnextid = 0;
/**
 The individual's sex coded as 1 for female and 2 for male.
*/
	public int sex = 0;
/**
 The individual's proband status coded as 1 for proband 0 for non-proband.
*/
	public int proband = 0;
/**
 A string representing anything that was left on this individual's input line
 after all thee pedigree, sex, proband and locus informatio was read in.
*/
	public String comment = null;
/**
 A list of phenotypes as read in from the input line.
 For co-dominant markers these phenotypes will be the genotypes.
*/
	public LinkagePhenotype[] pheno = null;

/**
 Returns a string representation of this individual's data
 in the same format as the original linkage input .ped file.
*/
	public String longString()
	{
		StringFormatter f = new StringFormatter();
		StringBuffer s = new StringBuffer();

		s.append(f.format(pedid,2)+" ");
		s.append(" ");
		s.append(f.format(id,3)+" ");
		s.append(f.format(paid,3)+" ");
		s.append(f.format(maid,3)+" ");
		s.append(f.format(firstbornid,3)+" ");
		s.append(f.format(pasnextid,3)+" ");
		s.append(f.format(masnextid,3)+" ");
		s.append("   ");
		s.append(f.format(sex,1)+" ");
		s.append(f.format(proband,1)+" ");
		s.append("  ");
		if (pheno != null)
			for (int i=0; i<pheno.length; i++)
				s.append(pheno[i]+"  ");
		
		if (comment != null)
			s.append(comment);

		return s.toString();
	} 

	public void writeTo(PrintStream s)
	{
		writeTo(new PrintWriter(s));
	}

	public void writeTo(PrintWriter p)
	{
		StringFormatter f = new StringFormatter();

		p.print(f.format(pedid,2)+" ");
		p.print(" ");
		p.print(f.format(id,3)+" ");
		p.print(f.format(paid,3)+" ");
		p.print(f.format(maid,3)+" ");
		p.print(f.format(firstbornid,3)+" ");
		p.print(f.format(pasnextid,3)+" ");
		p.print(f.format(masnextid,3)+" ");
		p.print("   ");
		p.print(f.format(sex,1)+" ");
		p.print(f.format(proband,1)+" ");
		p.print("  ");
		if (pheno != null)
			for (int i=0; i<pheno.length; i++)
				p.print(pheno[i]+"  ");
		
		if (comment != null)
			p.print(comment);
		p.flush();
	}

/**
 Returns a string representation of this individual's data
 in pre makeped format.
*/
	public String shortString()
	{
		StringFormatter f = new StringFormatter();
		StringBuffer s = new StringBuffer();

		s.append(f.format(pedid,2)+" ");
		s.append(" ");
		s.append(f.format(id,3)+" ");
		s.append(f.format(paid,3)+" ");
		s.append(f.format(maid,3)+" ");
		s.append("   ");
		s.append(f.format(sex,1)+" ");
		s.append("  ");
		if (pheno != null)
			for (int i=0; i<pheno.length; i++)
				s.append(pheno[i]+"  ");
		
		if (comment != null)
			s.append(comment);

		return s.toString();
	} 

	public String toString()
	{
		return ""+id;
	}

	public String uniqueName()
	{
		return pedid+" "+id;
	}
}
