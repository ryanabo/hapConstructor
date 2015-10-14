package alun.mcld;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import alun.genio.ParameterData;
import alun.markov.DenseTable;
import alun.markov.Function;
import alun.markov.Product;
import alun.markov.Table;
import alun.markov.Variable;
import alun.util.InputFormatter;

public class LDModel extends Product
{
	public LDModel(Collection<? extends Variable> l)
	{
		loci = new Vector<Variable>(l);
	}


/**
 Creates a linkage disequilibrium model corresponding to loci in
 linkage equibrium with alleles frequencies as specified by the 
 locus parameters given in the ParameterData
*/
	public LDModel(ParameterData d)
	{
		loci = new Vector<Variable>();
		for (int i=0; i<d.nLoci(); i++)
		{
			double[] f = d.alleleFreqs(i);
			double x = 0;
			for (int j=0; j<f.length; j++)
				x += f[j];
			for (int j=0; j<f.length; j++)
				f[j] /= x;

			Locus l = new Locus(i,f.length);
			Set<Variable> s = new LinkedHashSet<Variable>();
			s.add(l);

			Table t = new DenseTable(s,f);
			loci.add(l);

			add(t);
		}
	}

	public LDModel(InputFormatter f) throws IOException
	{
		loci = new Vector<Variable>();
		f.newLine();
		for (int i=0; f.newToken(); i++)
			loci.add(new Locus(i,f.getInt()));

		while (f.newLine())
		{
			LinkedHashSet<Variable> v = new LinkedHashSet<Variable>();
			while (f.newToken())
				v.add(loci.get(f.getInt()));
			f.newLine();

			Table t = new DenseTable(v);
			Variable[] u = (Variable[]) t.getVariables().toArray(new Variable[0]);
			if (u.length == 0)
				t.setValue(f.nextDouble());
			else
			{
				for (int i=0; i<u.length; i++)
					u[i].init();
				for (int i=0; i>=0; )
				{
					if (!u[i].next())
						i--;
					else
					{
						if (++i == u.length)
						{
							t.setValue(f.nextDouble());
							i--;
						}
					}
				}
			} 

			add(t);
		}
	}

	public Product replicate(Collection<? extends Variable> sl)
	{
		Vector<Variable> nl = new Vector<Variable>(sl);
		LinkedHashMap<Variable,Variable> hash = new LinkedHashMap<Variable,Variable>();
		for (int i=0; i<loci.size(); i++)
		{
			int x = loci.get(i).getNStates();
			int y = nl.get(i).getNStates();
			if (loci.get(i).getNStates() == nl.get(i).getNStates())
				hash.put(loci.get(i),nl.get(i));
			else
				throw new RuntimeException("State space size missmatch in LDModel:replicate()"
					+" "+loci.get(i).getNStates() + " " + nl.get(i).getNStates());
		}

		return replicate(hash);
	}

	public Product replicate(Map<Variable,Variable> hash)
	{
		Product ld = new Product();

		for (Function f : getFunctions())
		{
			DenseTable t = (DenseTable) f;
			LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
			for (Variable v : t.getVariables())
				s.add(hash.get(v));
			DenseTable newt = new DenseTable(s,t.getArray());
			ld.add(newt);
		}

		return ld;
	}

/* 
	Returns a set of the loci involved in the model. Unlike the getVariables() method
	in the superclass Product, the order will be the same at input from file.
	It is important to use getLoci() not getVariables() in order to make a proper
	replicate of the model. May also be needed to ensure information is output
	int the correct order.
*/
	
	public LinkedHashSet<Variable> getLoci()
	{
		return new LinkedHashSet<Variable>(loci);
	}

	public Variable getLocus(int j)
	{
		return loci.get(j);
	}

/*
	public void add(Function t)
	{
		if (loci.containsAll(t.getVariables()))
			super.add(t);
	}
*/

/*
	This function allows simulation from an LDModel without having to go
	through the whole graphical model machinery. It assumes that the
	model has been made as by the LikelihoodCalculator class which
	adds the locus conditionals to the product in the correct order.
	That is, if there are n variables there are n functions which
	are 
		P(v_i | { v_j : j < i} )
	and where, usually, the right hand side of the conditioning
	depends only of a few of the variables.
	This should make simulation much faster and not require any
	extra space.
*/
	public void simulate()
	{
		for (Function f : getFunctions())
		{
			Variable v = f.getVariables().iterator().next();
			v.setState(0);
			double p = f.getValue();
			double u = Math.random();
			
			while (u > p)
			{
				v.next();
				p += f.getValue();
			}
		}
	}

	public void writeTo(PrintStream s)
	{
		writeTo(new PrintWriter(s));
	}

	public void writeTo(PrintWriter p)
	{
		for (Variable l : loci)
			p.print(" "+l.getNStates());

		for (Function t : getFunctions())
		{
			p.print("\n");

			Variable[] u = (Variable[]) t.getVariables().toArray(new Variable[0]);
			for (int i=0; i<u.length; i++)
				p.print(" "+((Locus)u[i]).getIndex());
			p.print("\n");
			
			if (u.length == 0)
			{
				p.print(" "+t.getValue());
				continue;
			}
			
			for (int i=0; i<u.length; i++)
				u[i].init();

			for (int i=0; i>=0; )
			{
				if (!u[i].next())
					i--;
				else
				{
					if (++i == u.length)
					{
						p.print(" "+t.getValue());
						i--;
					}
				}
			}
		}
		p.flush();
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (Variable l : loci)
			s.append(" "+l.getNStates());

		for (Function t : getFunctions())
		{
			s.append("\n");
			Variable[] u = (Variable[]) t.getVariables().toArray(new Variable[0]);

			for (int i=0; i<u.length; i++)
				s.append(" "+((Locus)u[i]).getIndex());
			s.append("\n");
			
			if (u.length == 0)
				s.append(" "+t.getValue());
			else
			{
				for (int i=0; i<u.length; i++)
					u[i].init();
				for (int i=0; i>=0; )
				{
					if (!u[i].next())
						i--;
					else
					{
						if (++i == u.length)
						{
							s.append(" "+t.getValue());
							i--;
						}
					}
				}
			}
		}

		return s.toString();
	}

// Private data.

	private Vector<Variable> loci = null;

/**
  Test main.
*/
	public static void main(String[] args)
	{
		try
		{
/*
			//System.out.println(new LDModel(new InputFormatter())); 
			Vector<Locus> v = new Vector<Locus>(); 
			for (int i=0; i<10; i++) v.add(new Locus(i,3,i));

			LDModel m = new LDModel(v);
			System.out.println(m);
			System.out.println(m.getVariables());
			System.out.println(m.getFunctions());
*/

			long starttime = System.currentTimeMillis();

			LDModel ldm = new LDModel(new InputFormatter()); 

			for (Variable v : ldm.getLoci())
				System.err.println(v);

			for (Variable v : ldm.getVariables())
				v.setState(-2);
	
			System.out.print(ldm.getVariables().size()+"\t");
			System.out.print((System.currentTimeMillis()-starttime)/1000.0+"\t");
			starttime = System.currentTimeMillis();

			for (int i=0; i<1000; i++)
				ldm.simulate();

			System.out.println((System.currentTimeMillis()-starttime)/1000.0);
/*
			for (Variable v : ldm.getVariables())
				System.out.print(v.getState()+" ");
			System.out.println();
*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
