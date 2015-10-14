//******************************************************************************
// Gtype.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class Gtype {

  public final AllelePair[] aPairs;
  double haploFreq = 0.0;
  byte missingData = 0;

  //----------------------------------------------------------------------------
  public Gtype(AllelePair[] pairs) { aPairs = pairs; }

  /// Object operation re-imp ///

  //----------------------------------------------------------------------------
  public String toString()
  {
    List<AllelePair> l = new ArrayList<AllelePair>();
    for (int i = 0; i < aPairs.length; ++i)
    {
      AllelePair ap = aPairs[i];
      l.add(ap != null ? ap : AllelePair.MISSING);
    }
    return Ut.join(l, " ");
  }

  //----------------------------------------------------------------------------
  public String toString(AlleleFormat af) throws GEException
  {
    String s = new String();
    for (int i = 0; i < aPairs.length; ++i)
    {
      AllelePair ap = aPairs[i];
      if ( ap != null )
      {
        try 
        {
          s += af.toString(ap.getAlleleCode(true)) + " ";  
          s += af.toString(ap.getAlleleCode(false)) + " ";  
        }
        catch ( Exception e )
        {
          throw new GEException("Failed to convert Allele code from byte to String"); 
        }
      }
      else 
        s += "0 0 ";
    }
    return s;
  }

  //----------------------------------------------------------------------------
  public double getGtPercent()
  {
    int nGtype = 0;
    // Return the percentage of non-missing Gtype 
    for ( int i = 0 ; i < aPairs.length; i++ )
    {
      AllelePair ap = aPairs[i];
      nGtype += ( ap != null ? 1 : 0 );
    }
    return (double) (nGtype * 100) / aPairs.length;
  }
   
  //----------------------------------------------------------------------------
  public double getGtPercent(int[] loci)
  {
    int nGtype = 0;
    // Return the percentage of non-missing Gtype 
    for ( int i = 0 ; i < loci.length; i++ )
    {
      AllelePair ap = aPairs[i];
      nGtype += ( ap != null ? 1 : 0 );
    }
    return (double) nGtype / loci.length;
  }

  //----------------------------------------------------------------------------
  public void setHaploFrequency(double freq)
  {
    haploFreq = freq;
  }

  //----------------------------------------------------------------------------
  public double getHaploFrequency()
  {
    return haploFreq;
  }

  //----------------------------------------------------------------------------
  public AllelePair[] getAllelePairs() { return aPairs; }

  //----------------------------------------------------------------------------
  public AllelePair getAllelePairAt(int ilocus) { return aPairs[ilocus]; }

  //----------------------------------------------------------------------------
  public byte[][] getHaplotype()
  {
    int nPairs = aPairs.length;
    byte[][] haplo = new byte[2][nPairs];
    byte a1;
    byte a2;
    int n = 0;
    for ( int i = 0; i < nPairs; i++ ) {
      if ( aPairs[i] != null ) {
        a1 = aPairs[i].getAlleleCode(true);
        a2 = aPairs[i].getAlleleCode(false);
        if ( a1 == missingData ) {
          n++;
	}
      } else {
 	a1 = missingData;
 	a2 = missingData;
        n++;
      }
      haplo[0][i] = a1; 
      haplo[1][i] = a2; 
    }
    if ( n == nPairs )
      return null;
    else 
      return haplo;
  }

  //----------------------------------------------------------------------------
  public Gamete doMeiosis() // assert: all pairs non-null
  {
    //int[] alleles = new int[aPairs.length];
    byte[] alleles = new byte[aPairs.length];

    if ( aPairs[0] != null )
      alleles[0] = aPairs[0].transmitAllele();
    else
      alleles[0] = 0;
    for (int i = 1; i < alleles.length; ++i)
    {
      boolean a1 = false;
      alleles[i] = 0;
      if ( aPairs[i-1] != null )
        a1 = aPairs[i - 1].transmittedA1();
      if ( aPairs[i] != null )
        alleles[i] = aPairs[i].transmitAllele(a1);
    }
    return new Gamete(alleles);
  }
  //----------------------------------------------------------------------------
  public Gtype getPsuedoGtype ( Indiv inInd,
				Ptype inPtype,
				Indiv.GtSelector inGtSelector,
				int inSimIndex,
				Thread p )
  {
    if ( inInd.getIsFounder() )
      return null;
    Indiv father = inInd.getParent_Indiv('1');
    Indiv mother = inInd.getParent_Indiv('2');
    Gtype fatherGtype = father.getGtype(inGtSelector, inSimIndex);
    Gtype motherGtype = mother.getGtype(inGtSelector, inSimIndex);
    int nPairs = aPairs.length;
    int n = 0;
    AllelePair[] fatherAP = fatherGtype.getAllelePairs(); 
    AllelePair[] motherAP = motherGtype.getAllelePairs(); 
    AllelePair[] psuedoAP = new AllelePair[nPairs];
    for ( int i = 0; i < nPairs; i++ ) {
      boolean myfirst = true;
      byte psuedoAllele1 = missingData;
      byte psuedoAllele2 = missingData;
      byte myAllele;
      boolean fatherMatched = false;
      boolean motherMatched = false;
      for ( int my = 0; my < 2; my++ ) {
        myAllele = aPairs[i].getAlleleCode(myfirst);
        motherMatched = false;
        fatherMatched = false;
        if ( myAllele == missingData ) {
          psuedoAllele1 = missingData;
          psuedoAllele2 = missingData;
          fatherMatched = true;
          motherMatched = true;
          n++;
          break;
        }
        // check father
        boolean fatherfirst = true;
        boolean motherfirst = true;
        for ( int f = 0; f < 2; f++ ) {
          byte fatherAllele = fatherAP[i].getAlleleCode(fatherfirst);
	  if ( myAllele == fatherAllele ) {
	    psuedoAllele1 = fatherAP[i].getAlleleCode(!fatherfirst);
	    fatherMatched = true;
	    break;
	  } else {
	    fatherfirst = !fatherfirst;
	  }
        }
	if ( fatherMatched ) {
          myAllele = aPairs[i].getAlleleCode(!myfirst);
	  for ( int m = 0; m < 2; m++ ) {
            byte motherAllele = motherAP[i].getAlleleCode(motherfirst);
	    if ( myAllele == motherAllele ) {
	      psuedoAllele2 = motherAP[i].getAlleleCode(!motherfirst);
              motherMatched = true;
	      break;
	    } else {
	      motherfirst = !motherfirst;
	    }
	  }
        }
        if (!motherMatched) {
          // re-check father with the other allele 
	  myfirst = !myfirst;
        }
      }
      if ( fatherMatched && motherMatched )
        psuedoAP[i] = new AllelePair(psuedoAllele1, psuedoAllele2, 0.0, false);
      else 
      {
	System.out.println("Error creating Psuedo Gtype, failed to match alleles with parents.");
        System.exit(0);
      }
    }
    if ( n == nPairs )
      return null;
    else 
      return new Gtype(psuedoAP);
  }
  //----------------------------------------------------------------------------
  public Gtype getPsuedoHaplotype ( Indiv inInd,
                                Ptype inPtype,
                                Indiv.GtSelector inGtSelector,
                                int inSimIndex,
                                Thread p )
  {
    int nPairs = aPairs.length;
    AllelePair[] ap = new AllelePair[nPairs];
    if ( inInd.getIsFounder() )
    {
      return null;
    }
    byte[][] myHaplo     = getHaplotype();
    Indiv father = inInd.getParent_Indiv('1');
    Indiv mother = inInd.getParent_Indiv('2');
    Gtype fatherGtype = father.getGtype(inGtSelector, inSimIndex);
    Gtype motherGtype = mother.getGtype(inGtSelector, inSimIndex);
    byte[][] fatherHaplo;
    byte[][] motherHaplo;
    if ( fatherGtype != null ) {
      fatherHaplo = fatherGtype.getHaplotype();
    } else {
      return null;
    }
    if ( motherGtype != null ) {
      motherHaplo = motherGtype.getHaplotype();
    } else {
      return null;
    }
    //System.out.println("father gtype : " + fatherGtype.toString());
    //System.out.println("mother gtype : " + motherGtype.toString());
    
    byte[][] psuedoHaplo = new byte[2][];
    psuedoHaplo[0] = null;
    psuedoHaplo[1] = null;
    int h = 0;
    //AllelePair[] psuedoAP = new AllelePair[nPairs]; 
    boolean fatherMatched;
    boolean motherMatched;

    if ( myHaplo == null ||
	 fatherHaplo == null || motherHaplo == null )
      return null;

    int my = 1;
    for ( int i = 0; i < 2; i++ ) {
      //System.out.println(" my is : " + my);
      // check father
      fatherMatched = false;
      motherMatched = false;
      h = 1;
      //System.out.println("my Haplo : " + myHaplo[i]);
      for ( int f = 0; f < 2; f++ ) {
        int matchCount = 0;
        for ( int np = 0; np < nPairs; np++) {
          if ( myHaplo[i][np] != fatherHaplo[f][np] ) 
            break;
          else
	    matchCount++;
        }
        if ( matchCount == nPairs ) {
          psuedoHaplo[0] = fatherHaplo[h];
          fatherMatched = true;
	  //System.out.print(" psuedo haplo from father : " );
          //for ( byte b : psuedoHaplo[0])
		//System.out.print(b+"/");
          //System.out.println("");
          break;
	}
	h--;
      }
      if ( psuedoHaplo[0] != null ) {
        h = 1;
        //System.out.println("found one from father, and now my is " + my);
        for ( int m = 0; m < 2; m++ ) {
        int matchCount = 0;
          for ( int np = 0; np < nPairs; np++) {
            if ( myHaplo[my][np] != motherHaplo[m][np] )
              break;
            else
              matchCount++;
          }
          if ( matchCount == nPairs ) {
            psuedoHaplo[1] = motherHaplo[h];
            motherMatched = true;
	  //System.out.print(" psuedo haplo from mother : " );
          //for ( byte b : psuedoHaplo[1])
		//System.out.print(b+"/");
          //System.out.println("");
            break;
          }
          h--;
        }
        if ( fatherMatched && motherMatched ) {
          //if ( psuedoHaplo != null ) {
	  //System.out.println("   psuedo control is : " + psuedoHaplo[0] + " " + psuedoHaplo[1]);
          //}
          for ( int np = 0; np < nPairs; np++ ) {
            ap[np] = new AllelePair( psuedoHaplo[0][np], 
			      	     psuedoHaplo[1][np] );
	    //System.out.println(" each allele pair is " + ap[np].toString());
	  }
          break;
        }
      } else {
	//System.out.println(" one of them does not match, go to my next haplotype order ");
        my--;
      }
    }
    //System.out.println(" Psuedo gtype is : " + (new Gtype(ap)).toString());
    return new Gtype(ap);
  }
}

