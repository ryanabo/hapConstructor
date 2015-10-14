//*****************************************************************************
// LinkageIndExt.java 27 January 2004
//*****************************************************************************
package edu.utah.med.genepi.gchapext;

import alun.linkage.LinkageFormatter;
import alun.linkage.LinkageIndividual;
import alun.linkage.LinkageLocus;
import alun.linkage.LinkageParameterData;
import alun.linkage.LinkagePhenotype;
import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;

//==============================================================================
public class LinkageIndExt extends LinkageIndividual
{
	int phenotype;
	int liabclass;

	//----------------------------------------------------------------------------
	public LinkageIndExt ( LinkageParameterData par, Indiv ind, boolean premake, Indiv.GtSelector selector ) 
	{
		pedid = Integer.parseInt(ind.getPedigree().getPed_id());
		id    = Integer.parseInt(ind.getID());

		if ( ind.getParent_Indiv('1') != null )
			paid  = Integer.parseInt(ind.getParent_Indiv('1').getID());

		if ( ind.getParent_Indiv('2') != null )
			maid  = Integer.parseInt(ind.getParent_Indiv('2').getID());

		//sex   = Integer.parseInt(ind.getSex_id());
		char ind_sex = ind.getSex_id(); 
		if ( ind_sex == '1' || ind_sex == 'm' )
			sex = new Integer(1);
		else
			sex = new Integer(2);

		phenotype = Integer.parseInt(ind.getPtype().getPtype_ID());
		liabclass = 1;
		LinkageLocus[] ll = (LinkageLocus[]) par.getLoci();
		LinkageLocusExt[] l = new LinkageLocusExt[ll.length];
		for ( int i = 0 ; i < ll.length; i++ )
			try 
		{
				l[i] = (LinkageLocusExt) ll[i];
		}
		catch ( Exception e )
		{
			System.out.println(e.getMessage());
		}
		pheno = new LinkagePhenotype[l.length];

		// retrieve genotype from individual's Gtype
		Gtype gt = ind.getGtype(selector);
		for ( int i = 0; i < pheno.length; i++ )
		{
			AllelePair ap = gt.getAllelePairAt(i);
			int a1 = (new Byte(ap.getAlleleCode(true))).intValue();
			int a2 = (new Byte(ap.getAlleleCode(false))).intValue();
			pheno[i] = l[i].readPhenotype(a1, a2);
		}
		comment = null;
	}

	//----------------------------------------------------------------------------
	public LinkageIndExt ( LinkageFormatter b, 
			LinkageParameterData par )
	{
		pedid = b.nextInt();
		id = b.nextInt();
		paid = b.nextInt();
		maid = b.nextInt();
		sex = b.nextInt();
		//added phenotype and liab class 
		phenotype = b.nextInt();
		liabclass = b.nextInt();

		LinkageLocus [] l = par.getLoci();
		pheno = new LinkagePhenotype[l.length];

		for ( int i = 0; i< pheno.length; i++)
			pheno[i] = l[i].readPhenotype(b);

		comment = b.restOfLine();
	}

	//---------------------------------------------------------------------
	public LinkageIndExt ( LinkageFormatter b, 
			LinkageParameterData par,
			boolean indicator ) 
	{ this(b, par); }
}
