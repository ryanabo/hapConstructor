package alun.genepi;

import java.util.Iterator;
import java.util.Set;

import alun.markov.BasicClique;
import alun.markov.Clique;
import alun.markov.Function;
import alun.markov.Table;
import alun.markov.Variable;

public class TripletClique extends BasicClique
{
	public TripletClique(Set<Variable> inv, Clique nx)
	{
		super(inv,nx);
	}

	private Genotype[] u = null;
	private Function[][] f = null;

	public void compute(Table[] t)
	{
		if (u ==  null)
		{
			u = new Genotype[3];
			Iterator<Variable> i = invol().iterator();
			u[2] = (Genotype) i.next();
			u[0] = (Genotype) i.next();
			u[1] = (Genotype) i.next();
			f = orderInputs(u);
		}
/*
		Genotype[] u = new Genotype[3];
		Iterator<Variable> i = invol().iterator();
		u[2] = (Genotype) i.next();
		u[0] = (Genotype) i.next();
		u[1] = (Genotype) i.next();
		Function[][] f = orderInputs(u);
*/

		for (int j=0; j<t.length; j++)
			t[j].initToZero();

		double[] x = new double[u.length+1];
		x[0] = 1;
		for (u[0].init(); u[0].next(); )
		{
			x[1] = x[0];
			for (int j=0; j<f[0].length; j++)
				x[1] *= f[0][j].getValue();	
			if (!(x[1] > 0))
				continue; 

			for (u[1].init(); u[1].next(); )
			{
				x[2] = x[1];
				for (int j=0; j<f[1].length; j++)
					x[2] *= f[1][j].getValue();
				if (!(x[2] > 0))
					continue;

				for (int k=0; k<4; k++)
				{
					switch(k)
					{
					case 0: if ( !u[2].setState(u[0].pat(),u[1].pat()) ) 
							continue; 
						break;
					case 1: if ( u[1].mat() == u[1].pat() || !u[2].setState(u[0].pat(),u[1].mat())) 
							continue; 
						break;
					case 2: if ( u[0].mat() == u[0].pat() || !u[2].setState(u[0].mat(),u[1].pat()) ) 
							continue; 
						break;
					case 3: if ( u[0].mat() == u[0].pat() || u[1].mat() == u[1].pat() || !u[2].setState(u[0].mat(),u[1].mat()) ) 
							continue; 
						break;
					}
					
					x[3] = x[2];
					for (int j=0; j<f[2].length; j++)
						x[3] *= f[2][j].getValue();
					if (!(x[3]>0))
						continue;

					for (int j=0; j<t.length; j++)
						t[j].increase(x[3]);
				}
			}
		}
	}
}
