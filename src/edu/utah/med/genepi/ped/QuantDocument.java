//******************************************************************************
// QuantDocument.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.util.GEException;
//d

//==============================================================================
public class QuantDocument {

  QuantIndiv[] 	QInd;
  static String[] 	quantnames;
  interface Col
  {
    int PED_ID = 0;
    int IND_ID = 1;
    int QUANT = 2;
  }

  public QuantDocument()
  {
  }

  //----------------------------------------------------------------------------
  public void read(File f, int[] quant_ids) throws IOException, GEException
  {
    QuantIndiv qind;
    QInd = null;
    
    BufferedReader br = new BufferedReader(new FileReader(f));
    String s;
    int numtokens;

    if ( br.readLine().matches("^\\s*\\d+\\s+\\d+.+$"))
    {
      //s = br.readLine();
      //String[] header = s.trim().split("\\s+");
      //StringTokenizer ht = new StringTokenizer(s);
      //numtokens = ht.countTokens();
      //quantnames = new String[numtokens - Col.QUANT];
      //for ( int i = 0 ; i < (numtokens - Col.QUANT); i++ )
      //  quantnames[i] = header[i + Col.QUANT];  

      br.reset();
    }

    Vector v = new Vector();

    while (( s = br.readLine()) != null )
    {
      String[] tokens = s.trim().split("\\s+");
      StringTokenizer st = new StringTokenizer(s);
      numtokens = st.countTokens(); 
      double[] quants = new double[quant_ids.length];
      for ( int i = 0; i < quant_ids.length; i++)
      {
        for ( int j = 2; j < numtokens; j++ )
        { 
          if ( quant_ids[i] == j - 1 )
            quants[i] = Double.parseDouble(tokens[ j ]);
        }
      }
      Qdata qd = new Qdata(quants);
      qind = new QuantIndiv( tokens[Col.PED_ID],
                             tokens[Col.IND_ID],
                             qd );
      v.addElement(qind);
    }
    
    br.close();
  
    QInd = (QuantIndiv[])v.toArray(new QuantIndiv[v.size()]);
  }

  //----------------------------------------------------------------------------
  public QuantIndiv[] getQuantIndiv()
  { return QInd; }

  //----------------------------------------------------------------------------
//  public static String[] getQuantNames()
//  { return quantnames; }

}
