package alun.gchap;

/**
 This class represent a single nucleotide polymorphism.
*/
public class SNP extends Marker
{
/**
 Creates a new SNP at which all 4 bases can occur.
 Restriction to the (probably) two bases that are really there
 is done by downcoding later.
*/
	public SNP()
	{
		super(4);
		double[] f = { 1, 1, 1, 1 };
		String[] s = { "A", "C", "G", "T" };
		getLocus().setAlleles(f,s);
		Phenotype[] p = getPhenotypes();
		for (int i=0; i<p.length; i++)
		{
			if (p[i].getName().equals("1:1")) p[i].setName("aa");
			if (p[i].getName().equals("1:2")) p[i].setName("ac");
			if (p[i].getName().equals("1:3")) p[i].setName("ag");
			if (p[i].getName().equals("1:4")) p[i].setName("at");
			if (p[i].getName().equals("2:2")) p[i].setName("cc");
			if (p[i].getName().equals("2:3")) p[i].setName("cg");
			if (p[i].getName().equals("2:4")) p[i].setName("ct");
			if (p[i].getName().equals("3:3")) p[i].setName("gg");
			if (p[i].getName().equals("3:4")) p[i].setName("gt");
			if (p[i].getName().equals("4:4")) p[i].setName("tt");
			if (p[i].getName().equals("?:?")) p[i].setName("??");
/*
			if (p[i].getName().equals("0:0")) p[i].setName("aa");
			if (p[i].getName().equals("0:1")) p[i].setName("ac");
			if (p[i].getName().equals("0:2")) p[i].setName("ag");
			if (p[i].getName().equals("0:3")) p[i].setName("at");
			if (p[i].getName().equals("1:1")) p[i].setName("cc");
			if (p[i].getName().equals("1:2")) p[i].setName("cg");
			if (p[i].getName().equals("1:3")) p[i].setName("ct");
			if (p[i].getName().equals("2:2")) p[i].setName("gg");
			if (p[i].getName().equals("2:3")) p[i].setName("gt");
			if (p[i].getName().equals("3:3")) p[i].setName("tt");
			if (p[i].getName().equals("?:?")) p[i].setName("??");
*/
		}
	}

/**
 Finds the phenotype corresponding to the given string.
*/
	public Phenotype findPhenotype(String s)
	{
		Phenotype[] p = getPhenotypes();
		for (int i=0; i<p.length; i++)
			if (p[i].getName().equals(s))
				return p[i];
		return null;
	}

/**
 Test main.
*/
	public static void main(String[] args)
	{
		SNP s = new SNP();
		System.out.println(s);
	}
}
