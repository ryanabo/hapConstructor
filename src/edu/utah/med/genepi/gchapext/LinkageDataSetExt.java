//******************************************************************************
// LinkageDataSetExt.java
//******************************************************************************
package edu.utah.med.genepi.gchapext;

import java.io.IOException;

import alun.linkage.LinkageDataSet;
import alun.linkage.LinkageParameterData;
import alun.linkage.LinkagePedigreeData;
import edu.utah.med.genepi.ped.Indiv;

//==============================================================================
/**
 This class holds the information from a standard Linkage parameter data file
 and a standard Linkage pedigree file.
*/
public class LinkageDataSetExt extends LinkageDataSet
{
	public LinkageDataSetExt (LinkageParameterData lpd,
                                  Indiv[] indiv,
                                  Indiv.GtSelector selector )
		throws IOException
	{
          super ();
	  LinkagePedigreeData ldd = new LinkagePedDataExt(lpd, indiv, selector);
	  set(lpd, ldd);
	}
}
