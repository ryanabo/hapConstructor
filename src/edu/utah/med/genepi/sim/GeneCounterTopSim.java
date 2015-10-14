//******************************************************************************
// GeneCounterTopSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.io.IOException;

import alun.genepi.GeneCounter;
import alun.genio.LinkageDataSet;
import alun.genio.LinkageLocus;
import alun.genio.LinkageParameterData;
import edu.utah.med.genepi.gchapext.LinkageDataSetExt;
import edu.utah.med.genepi.gm.FreqDataSet;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.GEException;
//==============================================================================
public class GeneCounterTopSim extends HapFreqTopSim 
{
  LinkageParameterData lpd = null;
  int numGDef = 0;
  
  //----------------------------------------------------------------------------
  public GeneCounterTopSim()
  {
    super();
  }

  //----------------------------------------------------------------------------
  public void preProcessor ( Study study,
                             GDef  gDef,
                             Indiv.GtSelector selector )
         throws GEException
  {
    numGDef = gDef.getLocusCount();
    LinkageDataSet lds = null;
    PedData pd = study.getPedData();
    Indiv[] indiv = pd.getIndividuals(PedQuery.IS_ANY);
    lpd = study.getLinkageParameterData();
    if ( lpd == null )
      throw new GEException("Missing Linkage Parameter File, please specify");
    try
    {
      lds = new LinkageDataSetExt( lpd, indiv, Indiv.GtSelector.OBS );
    }
    catch ( IOException e )
    {
      throw new GEException("can't create LinkageDataSet " + e);
    }
    if ( lds != null )
    {
      lds.countAlleleFreqs();
      GeneCounter.geneCount(lds);
      LinkageLocus[] loc = lds.getParameterData().getLoci();
      FreqDataSet[][] freq = new FreqDataSet[numGDef][];

      // make sure genec unter and GDef are pointed at the same locus
      for ( int j = 0; j < numGDef; j++)
      {
        GDef.Locus specLocus = gDef.getLocus(j);
        for ( int i = 0; i < loc.length; i++ )
        {
          if ( specLocus.getID() == i + 1 )
          {
            double[] freqvalues = loc[i].alleleFrequencies();
            freq[j] = new FreqDataSet[freqvalues.length];
            for ( int k = 0 ; k < freqvalues.length; k++ )
              freq[j][k] = new FreqDataSet( freqvalues[k], 
                                            (new Integer(k+1)).byteValue(),
                                            "GeneCounter");
          }
        }
      }
      study.setFreqDataSet(freq);
    }
    else
    {
      System.out.println("Cannot process GeneCounter." );
      System.exit(0);
    }
  }

}
