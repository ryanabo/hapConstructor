//******************************************************************************
// LpedParser.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.GtypeBuilder;
import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedDocument;
import edu.utah.med.genepi.ped.QuantIndiv;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class LpedParser implements PedDocument.FileParser {

  interface Col {
    int PED_ID  = 0;
    int IND_ID  = 1;
    int DAD_ID  = 2;
    int MOM_ID  = 3;
    int SEX_ID  = 4;
    int PHEN    = 5;
    int LIAB    = 6;
    int MARKER1 = 7;
  }
  int nmarkers;
  int[] marcols;
  int nSim;

  //----------------------------------------------------------------------------
  public void parse	( File fsource, QuantIndiv[] qInd, int[] quant_ids, 
                      GDef gdef, int numSimData, PedData.Loader loader,
                      boolean hasHeader) throws IOException, GEException
  {
    BufferedReader 	in = new BufferedReader(new FileReader(fsource));
    int   nmarkers = gdef.getLocusCount();
    int[] marcols = new int[nmarkers];
    int   nSim = numSimData;
    Qdata quants_val = null;
    String line, ped_id, ind_id;
    GtypeBuilder gt_builder = gdef.getGtypeBuilder();
    AlleleFormat af = gdef.getAlleleFormat();

    for (int i = 0; i < nmarkers; ++i)
      marcols[i] = Col.MARKER1 + (gdef.getLocus(i).getID() - 1) * 2;

    // skip possible header line in non-standard files (e.g., GE labouts)
    //if (in.readLine().matches("^\\s*\\d+\\s+\\d+.+$"))
    //if (in.readLine().trim().startsWith("\\d"))
    if ( hasHeader )
    {
      line = in.readLine();
      String[] s = line.trim().split("\\s+");
      for (int i = 0; i < nmarkers; i++)
      {
        GDef.Locus locus = gdef.getLocus(i);
        if (locus.getMarker() == null)
        {
          locus.setMarker(s[Col.MARKER1 + locus.getID() - 1]);
        }
      }
      //System.out.println("has header");
    }
    else
      System.out.println("no header");

    int linecount = 1;
    while ((line = in.readLine()) != null)
    {
      String[] tokens = line.trim().split("\\s+");
      //System.out.println("line count : " + linecount++  + " token.length : " + tokens.length);
      for (int i = 0; i < nmarkers; ++i)
      {
        byte allele1, allele2;
        int col1 = marcols[i];
        //System.out.println("token at : " + col1);
        
        allele1 = af.convertAllele(tokens[col1]);
        allele2 = af.convertAllele(tokens[col1+1]);
        gt_builder.addAllelePair( allele1, allele2 );
      }
      ped_id = tokens[Col.PED_ID];
      ind_id = tokens[Col.IND_ID];

      if ( qInd != null )
      {
        double[] qt = new double[quant_ids.length];
        for ( int i = 0; i < quant_ids.length; i++ )
          qt[i] = 0.0;
        quants_val = new Qdata(qt);

        for ( int i = 0; i < qInd.length; i++ )
        {
          if ( qInd[i].ped_id.equals(ped_id) && qInd[i].ind.equals(ind_id) )
            quants_val = qInd[i].quants;
        }
      }
      loader.loadIndividual(
        ped_id,
        ind_id,
        tokens[Col.DAD_ID],
        tokens[Col.MOM_ID],
        tokens[Col.SEX_ID].charAt(0),
        tokens[Col.PHEN],
        tokens[Col.LIAB],
        gt_builder.buildNext(),
        quants_val,
        nSim
      );
    }
  }
}

