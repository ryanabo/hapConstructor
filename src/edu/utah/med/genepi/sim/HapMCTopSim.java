//******************************************************************************
// HapMCTopSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import alun.gchap.HapFormatter;
import alun.gchap.MarkerObservation;
import alun.gchap.MultiLocusObservation;
import alun.gchap.Observation;
import alun.genio.GeneticDataSource;
import edu.utah.med.genepi.gchapext.GeneticDataSourceImp;
import edu.utah.med.genepi.gchapext.MarkerObservationExt;
import edu.utah.med.genepi.gm.FreqDataSet;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeBuilder;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;
//Ryan 08-19-07
//==============================================================================
public class HapMCTopSim extends HapFreqTopSim 
{
  Indiv[] sampleIndiv; 
  double thresh = 10E-10;

  //----------------------------------------------------------------------------
  public HapMCTopSim()
  {
  }

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
  }

  //----------------------------------------------------------------------------
  public void setDataSource ( PedQuery.Predicate[] querySample)
  {
    Indiv.GtSelector obs = Indiv.GtSelector.OBS;
    // only preprocess haplotype data
    int nLoci = gdef.getLocusCount();
    if ( nLoci > 1 )
    {
      //System.out.println("hapMCTopSim preProcessor : " );
      for ( int i = 0; i < study.length; i++ )
      {
        PedData peddata       = study[i].getPedData();
        Indiv[] sampleIndiv      = peddata.getIndividuals(pInSample, 50);
        GeneticDataSource gds = new GeneticDataSourceImp( study[i],
                                                          sampleIndiv,
                                                          obs,
                                                          0,
                                                          gdef );
        // calculate haplotype freq and update Study
        study[i].setFreqDataSet( getGCHapFreq(gds) );

        for ( int j = 0 ; j < querySample.length; j++ )
        {
          sampleIndiv    = peddata.getIndividuals(querySample[j], 50);
          GeneticDataSource sampleGds = new GeneticDataSourceImp( study[i], 
                                                                  sampleIndiv,
                                                                  obs,
                                                                  -1,
                                                                  gdef );
 
          setPhasedData ( sampleGds, sampleIndiv, obs, -1, gdef );
        }
      }
    }
  }

  //----------------------------------------------------------------------------
  public Observation getObservation( GeneticDataSource gds )
  {
    MarkerObservation[] x =
      new MarkerObservation[gds.nLoci()];

    for ( int i = 0; i < x.length; i++ )
    {
      try
      {
      x[i] = new MarkerObservation( gds, i );
      }
      catch ( Exception e )
      { e.printStackTrace();}
    }

    Observation y = x[0];

    for ( int i = 1; i < x.length; i++ )
    {
      y = new MultiLocusObservation(y, x[i]);
      //int its = y.geneCountToConvergence();
      y.getTrait().geneCount(1000);
      y.getTrait().downCode(thresh);
      y.getTrait().parsDownCode();
    }

    //y.geneCountToConvergence();
    y.getTrait().geneCount(1000);
    y.getTrait().downCode(thresh);
    return y;
  }

  //----------------------------------------------------------------------------
  //Ryan 06-20-07 overloaded to include index
  public Observation getObservation( GeneticDataSource gds, int index )
  {
    GeneticDataSourceImp gdsImp = (GeneticDataSourceImp) gds;
    MarkerObservation[] x =
      new MarkerObservationExt[gdsImp.nLoci()];

    for ( int i = 0; i < x.length; i++ )
    {
      try
      {
      // use MarkerObservationExt instead
      x[i] = new MarkerObservationExt( gdsImp, i, index );
      }
      catch ( Exception e )
      { e.printStackTrace();}
    }

    Observation y = x[0];

    //use ApproxGCHap - parsDownCode()
    for ( int i = 1; i < x.length; i++ )
    {
      y = new MultiLocusObservation(y, x[i]);

      //int its = y.geneCountToConvergence();
      y.getTrait().geneCount(1000);
      y.getTrait().downCode(thresh);
      y.getTrait().parsDownCode();
    }

    //y.geneCountToConvergence();
    y.getTrait().geneCount(1000);
    y.getTrait().downCode(thresh);
    return y;
  }
  
  //----------------------------------------------------------------------------
  public FreqDataSet[] getGCHapFreq( GeneticDataSource gds )
  {
    Observation y = getObservation(gds);

    double[] freq = y.getTrait().getLocus().alleleFrequencies();
    String[] code = y.getTrait().getLocus().alleleNames();

    FreqDataSet[] freqdataset = new FreqDataSet[freq.length];
    for ( int i = 0; i < freqdataset.length; i++ )
    {
      freqdataset[i] = new FreqDataSet(freq[i], code[i], "HapMC",
                                       gdef.getAlleleFormat() );
      //System.out.println("Freq : " + freq[i] + " Haplotype : " + code[i]);
    }
    return freqdataset;
  }

  //----------------------------------------------------------------------------
  public void setPhasedData ( GeneticDataSource gds,
                              Indiv[] indiv,
                              Indiv.GtSelector selector,
                              int index,
                              GDef gdef )
  {
    //Ryan 06-20-07 change to include index
    Observation y = getObservation(gds,index);

    GtypeBuilder gt_builder = gdef.getGtypeBuilder();

    String newHaplotype = HapFormatter.formatGuesses (y, gds);

    // need to setup a vector for pedid, indid, besta, bestb ? freq
    try
    {
      BufferedReader br = new BufferedReader( new StringReader(newHaplotype) );
      String firstline, line1, line2, line3;

      if ( (firstline = br.readLine()) != null )
      {
        while ( (line1 = br.readLine()) != null )
        {
          String pedid = null, indid = null,
                 besta = null, bestb = null,
                 freq = null;
          if ( line1.startsWith("Phenotype") )
          {
            StringTokenizer st = new StringTokenizer(firstline);
            pedid = st.nextToken();
            indid = st.nextToken();
            if ( ( line2 = br.readLine()) == null )
              System.out.println("failed to read Line 2 ");
            else if ( line2.startsWith("Best explanation") )
            {
              besta = br.readLine().trim();
              bestb = br.readLine().trim();
              freq  = br.readLine().trim().replace("%","");
            }
            if ( pedid != null && indid != null &&
                 besta != null && bestb != null )
            {
              for ( int j = 0; j < indiv.length; j++ )
              {
                String ped = indiv[j].getPedigree().getPed_id();
                String id = indiv[j].getID();
                if ( ped.equals(pedid) && id.equals(indid) )
                {
                  StringTokenizer a = new StringTokenizer(besta);
                  StringTokenizer b = new StringTokenizer(bestb);
                  while ( a.hasMoreTokens() && b.hasMoreTokens() )
                  {
                    String a1 = a.nextToken();
                    String a2 = b.nextToken();
                    gt_builder.addAllelePair( a1, a2 );
                  }
                  try
                  {
                    gt_builder.addHaploFrequency(
                               Double.valueOf(freq).doubleValue());
                    Gtype gt = gt_builder.buildNext();
                    if ( selector == Indiv.GtSelector.SIM )
                      indiv[j].setSimulatedGtype( gt, index );
                    else 
                      indiv[j].setGtype( gt, selector, true );
                  }
                  catch ( GEException g )
                  {
                    System.out.println("Failed to set Gtype for pedigree: " + ped + " indiv: " + id );
                  }
                  break;
                }
              } // end indiv for loop
            }
          } // end if startsWith Phenotype
          else
          {
            firstline = line1;
          }
        } // end of while loop
      }
    }
    catch ( IOException e )
    {
      System.out.println ( "Failed to build new Gtype for individual" );
      System.out.println( e + "\n" + e.getMessage() );
      e.printStackTrace();
    }
  }
  
  //----------------------------------------------------------------------------
  //Ryan 08-19-07 overloaded to pass in compressGtype object to store genotype data.
  public void setPhasedData(GeneticDataSource gds, Indiv[] indiv,
                            Indiv.GtSelector selector, int index, GDef gdef,
                            compressGtype cGtype, PedQuery.Predicate query) 
  {
    // Ryan 06-20-07 change to include index...
    // Observation y = getObservation(gds,index);
    // Ryan 08-19-07 changed back to not include index.
    String affstatus = query.toString();
    if(index == 0){
    //Divide total cases or controls by 16 (2 genotypes per person)
    //for 32 bits. This provides the number of cols for storage.
      double cells = Math.ceil(indiv.length / 16.0);
      int cels = (int)Math.ceil(cells);
      cGtype.initiate_simStorage(cels,affstatus);
    }

    Observation y = getObservation(gds);
    GtypeBuilder gt_builder = gdef.getGtypeBuilder();
    String newHaplotype = HapFormatter.formatGuesses(y, gds);

    // need to setup a vector for pedid, indid, besta, bestb ? freq
    try 
    {
      BufferedReader br = new BufferedReader(new StringReader(
                                             newHaplotype));
      String firstline, line1, line2, line3;

      if ((firstline = br.readLine()) != null) 
      {
        int storage_counter = 0;
        int cell = 0;
        while ((line1 = br.readLine()) != null) 
        {
          String pedid = null, indid = null,
          besta = null, bestb = null, freq = null;
          if (line1.startsWith("Phenotype")) 
          {
            StringTokenizer st = new StringTokenizer(firstline);
            pedid = st.nextToken();
            indid = st.nextToken();
            if ((line2 = br.readLine()) == null)
              System.out.println("failed to read Line 2 ");
            else if (line2.startsWith("Best explanation")) 
            {
              besta = br.readLine().trim();
              bestb = br.readLine().trim();
              freq = br.readLine().trim().replace("%", "");
            }
            if (pedid != null && indid != null && besta != null
                && bestb != null) 
            {
              for (int j = 0; j < indiv.length; j++) 
              {
                String ped = indiv[j].getPedigree().getPed_id();
                String id = indiv[j].getID();
                if (ped.equals(pedid) && id.equals(indid)) 
                {
                  StringTokenizer a = new StringTokenizer(besta);
                  StringTokenizer b = new StringTokenizer(bestb);
                  while (a.hasMoreTokens()
                         && b.hasMoreTokens()) 
                  {
                    String a1 = a.nextToken();
                    String a2 = b.nextToken();
                    gt_builder.addAllelePair(a1, a2);
                  }
                  try 
                  {
                    gt_builder.addHaploFrequency(Double.valueOf(freq).doubleValue());
                    Gtype gt = gt_builder.buildNext();
                    if (selector == Indiv.GtSelector.SIM) 
                    {
                      // Ryan changed index to always be zero
                      //System.out.println("Sim " + affstatus + " in HapMCTop " + j + " " + gt.toString());
                      indiv[j].setSimulatedGtype(gt, 0);
                      // Ryan 08-18-07 added to store sim
                      // genotypes in a compressed format.
                      cGtype.store_simgt(gt, storage_counter, index, affstatus, cell);
                      if(storage_counter == 30)
                      {
                        storage_counter = 0;
                        cell++;
                      }
                      else
                      {
                        storage_counter = storage_counter + 2;
                      }
                    } 
                    else
                      indiv[j].setGtype(gt, selector);
                  } 
                  catch (GEException g)
                  {
                    System.out.println("Failed to set Gtype for pedigree: "
                                        + ped + " indiv: " + id);
                  }
                  break;
                }
              } // end indiv for loop
            }
          } // end if startsWith Phenotype
          else 
          {
            firstline = line1;
          }
        } // end of while loop
      }
    }
    catch (IOException e)
    {
      System.out.println("Failed to build new Gtype for individual");
      System.out.println(e + "\n" + e.getMessage());
                         e.printStackTrace();
    }
  }

  //Ryan 08-19-07 added to restore the saved Genotypes to use for the hapBuilder package
 /*
  public void restoreGtypes( GeneticDataSource gds, Indiv[] indiv, Indiv.GtSelector selector,
                             int index, GDef gdef, compressGtype cGtype)
  {
	GtypeBuilder gt_builder = gdef.getGtypeBuilder();
	for ( int j = 0; j < indiv.length; j++ )
    {
      String ped = indiv[j].getPedigree().getPed_id();
      String id = indiv[j].getID();
      
	  int[] aps = cGtype.get_dAllelePairs(j,index);
	  for(int i=0; i<aps.length; i++){
		int code = aps[i];
		String a1 = "";
		String a2 = "";
		if(code == 3){
		  a1 = "2";
		  a2 = "2";
		}
		else if(code == 2){
		  a1 = "1";
		  a2 = "2";
		}
		else if(code == 1){
		  a1 = "2";
		  a2 = "1";
		}
		else{
		  a1 = "1";
		  a2 = "1";
		}
		gt_builder.addAllelePair( a1, a2 );
	  }
      
      try
      {
    	double freq = cGtype.getDFreq(j,index);
        gt_builder.addHaploFrequency(Double.valueOf(freq).doubleValue());
        Gtype gt = gt_builder.buildNext();
        if ( selector == Indiv.GtSelector.SIM )
        {
          //Ryan changed index to always be zero.
          indiv[j].setSimulatedGtype( gt, 0 );
        }
        else 
          indiv[j].setGtype( gt, selector );
      }
      catch ( GEException g )
      {
        System.out.println("Failed to set Gtype for pedigree: " + ped + " indiv: " + id );
      }
      break;
    } // end indiv for loop
  }
  */
}

