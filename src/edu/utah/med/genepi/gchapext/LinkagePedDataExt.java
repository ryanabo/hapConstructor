//******************************************************************************
// LinkagePedDataExt.java					27 January 2004
//******************************************************************************
package edu.utah.med.genepi.gchapext;

import java.io.IOException;
import java.util.Vector;

import alun.genio.LinkageFormatter;
import alun.genio.LinkageIndividual;
import alun.genio.LinkageParameterData;
import alun.genio.LinkagePedigreeData;
import edu.utah.med.genepi.ped.Indiv;

//==============================================================================
/**
 A structure representing the data from a linkage .ped file.
*/
public class LinkagePedDataExt  extends LinkagePedigreeData
{
	// this will ignore the header line 

  //----------------------------------------------------------------------------
  public LinkagePedDataExt(LinkageFormatter b, LinkageParameterData par)
               throws IOException
  {
    Vector<LinkageIndividual> v = new Vector<LinkageIndividual>();

    b.newLine();
    while (b.newLine())
    {
      LinkageIndividual i = new LinkageIndExt(b,par);
      v.addElement(i);
    }

    //ind = (LinkageIndividual[])v.toArray(new LinkageIndividual[v.size()]);
    set(v);
  }
	
  //----------------------------------------------------------------------------
  public LinkagePedDataExt ( LinkageParameterData par,
                             Indiv[] ind,
                             Indiv.GtSelector selector )
	throws IOException
  {
    Vector<LinkageIndividual> v = new Vector<LinkageIndividual>();
    for ( int j = 0; j < ind.length; j++ )
    {
      LinkageIndividual i = new LinkageIndExt( par, ind[j], false, selector );
      v.addElement(i);
    }
    set(v);
  }
}
