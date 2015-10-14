//******************************************************************************
// PsuedoGtype.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;

//==============================================================================
class PsuedoGtype 
{

  //----------------------------------------------------------------------------
  public PsuedoGtype ()
  {}

  //----------------------------------------------------------------------------
  public Gtype getPsuedoGtype ( Indiv caseInd, Gtype inGtype, Ptype inPtype,
                               Indiv.GtSelector gtselector, int simIndex )
  {
    return getPsuedoGtype(caseInd, inGtype, inPtype, gtselector, simIndex, null );
  }

  //----------------------------------------------------------------------------
  public Gtype getPsuedoGtype ( Indiv caseInd,
                                Gtype myGtype,
                                Ptype myPtype,
                                Indiv.GtSelector gtselector,
                                int simIndex,
                                Thread p )
  {
    // myGtype has genotype data
    if ( !caseInd.getIsFounder() )
    {
      Gtype motherGtype = 
            (caseInd.getParent_Indiv('2')).getGtype(gtselector, simIndex);
      Gtype fatherGtype = 
            (caseInd.getParent_Indiv('1')).getGtype(gtselector, simIndex);

      // count psuedo control
      AllelePair[] myAP = myGtype.getAllelePairs();
      AllelePair[] motherAP = motherGtype.getAllelePairs();
      AllelePair[] fatherAP = fatherGtype.getAllelePairs();
      int nAP = myAP.length;
      AllelePair[] psuedoAP = new AllelePair[nAP];
  
      for ( int i = 0; i < nAP; i++ )
      {
        byte[] psuedoAC = new byte[2];
        boolean first = true;
        // match mother
        for ( int j = 0; j < 2; j++ )
        {
          byte myAC = myAP[i].getAlleleCode(first);
          first = !first;
          boolean completedMother = false;
          for ( int k = 0; k < 2; k++ )
          {
            byte motherAC = motherAP[i].getAlleleCode( (k==0) ? true : false);
            if ( motherAC == myAC )
            {
              psuedoAC[j] = motherAP[i].getAlleleCode( (k==0) ? false : true);
              completedMother = true;
              break;
            }
          }
          if ( completedMother )
            break;
        }
        // match father
        byte myAC = myAP[i].getAlleleCode(first);
        for ( int k = 0; k < 2; k++ )
        {
          byte fatherAC = fatherAP[i].getAlleleCode( (k==0) ? true : false);
          if ( fatherAC == myAC )
          {
            psuedoAC[((first == true) ? 0 : 1)] = 
                    fatherAP[i].getAlleleCode( (k==0) ? false : true);
            break;
          }
        }
        //System.out.println("ind : " + caseInd.getID() + " psuedo control : " + psuedoAC[0] + " " + psuedoAC[1]);
        psuedoAP[i] = new AllelePair(psuedoAC[0], psuedoAC[1], 0.5, false);
      } // end nAP loop
      return new Gtype(psuedoAP);
    }
    return null;
  }

  //----------------------------------------------------------------------------
  public boolean checkControl ( Indiv controlInd, Gtype inGtype, Ptype inPtype )
  {
    return checkControl(controlInd, null );
  }

  //----------------------------------------------------------------------------
  public boolean checkControl ( Indiv controlInd, 
                                Thread p )
  {
    // control should be founder only
    if ( controlInd.getIsFounder() )
    {
      boolean noAffectedOffspring = true;
      List<Marriage> lmarriage = controlInd.getMarriages();
      if ( lmarriage != null )
      {
        for ( Iterator it = lmarriage.iterator(); it.hasNext(); )
        {
          Marriage m = (Marriage) it.next();
          for ( Iterator kit = (m.getKids()).iterator(); kit.hasNext(); )
          {
            Indiv kid = (Indiv) kit.next();
            if ( kid.getPtype() == Ptype.CASE )
            {
              noAffectedOffspring = false;
              break;
            }
          }
          if ( !noAffectedOffspring )
            break;
        }
      } // end lmarriage is not null 
      return noAffectedOffspring;
    }
    else 
    { 
      return false; 
    }
  } 
}
