//******************************************************************************
// Study.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.io.File;

import alun.genio.LinkageParameterData;
import edu.utah.med.genepi.gm.FreqDataSet;
import edu.utah.med.genepi.util.EmpiricalRandomizer;

public class Study 
{
  File genotypefile, quantitativefile, haplotypefile, linkageparfile = null;
  String studyname = null;
  PedData pedData;
  FreqDataSet[][] freqdataset = null;
  EmpiricalRandomizer[] allelemap = null;
  LinkageParameterData linkageparameterdata = null;
  
  //---------------------------------------------------------------------------
  public Study ( String stdname,
                 String genofile,
                 String quantfile,
                 String hapfile,
                 String parfile )
  {
    studyname = stdname;
    genotypefile = new File(genofile);
    if ( quantfile != null )
      quantitativefile = new File(quantfile);
    if ( hapfile != null )
      haplotypefile = new File(hapfile);
    if ( parfile != null )
      linkageparfile = new File(parfile);
  }

  //---------------------------------------------------------------------------
  public String getStudyName()
  { return studyname; }

  //---------------------------------------------------------------------------
  public File getGenotypeFile()
  { return genotypefile; }

  //---------------------------------------------------------------------------
  public File getQuantitativeFile()
  { return quantitativefile; }

  //---------------------------------------------------------------------------
  public File getHaplotypeFile()
  { return haplotypefile; }
  
  //---------------------------------------------------------------------------
  public File getLinkageParFile()
  { return linkageparfile; }

  //---------------------------------------------------------------------------
  public void setLinkageParameterData( LinkageParameterData lpd )
  {
    linkageparameterdata = lpd;
  }
 
  //---------------------------------------------------------------------------
  public LinkageParameterData getLinkageParameterData()
  {
    return linkageparameterdata;
  }

  //---------------------------------------------------------------------------
  public void setPedData(PedData ped)
  { pedData = ped; }

  //---------------------------------------------------------------------------
  public void setFreqDataSet(FreqDataSet[] freq)
  {  
    freqdataset = new FreqDataSet[1][freq.length];
    freqdataset[0] = freq; 
  }

  public void setFreqDataSet(FreqDataSet[][] freq)
  { freqdataset = freq; }

  //---------------------------------------------------------------------------
  public void addFreqDataSet(FreqDataSet[] freq)
  {
    int freqlength  = freqdataset.length; 
    int newfreqlgth = freq.length;
    FreqDataSet[] newfreq;
  }
  //---------------------------------------------------------------------------
  public PedData getPedData()
  { return pedData; }

  //---------------------------------------------------------------------------
  public FreqDataSet[][] getFreqDataSet()
  { return freqdataset; }

  //---------------------------------------------------------------------------
  public void setAlleleMap( EmpiricalRandomizer[] alp )
  {
    allelemap = alp;
  }

  //---------------------------------------------------------------------------
  public int getLocusNumAllele ( int locusID )
  {
    return allelemap[locusID].getNumKey();
  }
}
