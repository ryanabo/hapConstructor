//******************************************************************************
// AllelePair.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import edu.utah.med.genepi.util.Randy;

//==============================================================================
//public class AllelePair extends IntIdentified {
public class AllelePair 
{
  //static final AllelePair MISSING = new AllelePair("0", "0", 0.5, false);
  static byte zero = 0;
  static final AllelePair MISSING = new AllelePair(zero, zero, 0.5, false);

  private final Randy  theRandomizer = Randy.getInstance();
  //private final int    a1Code, a2Code;
  private final byte    a1Code, a2Code;
  private final double thetaVal;
  //private int          lastTransmission;
  private byte          lastTransmission;
  private boolean	order;

  //----------------------------------------------------------------------------
  public AllelePair(byte a1, byte a2, double locus_theta, boolean order_matters)
  {
    //super(encodeAllelePair(a1, a2, order_matters));
    a1Code = a1;
    a2Code = a2;
    thetaVal = locus_theta;
    order = order_matters;
  }

  public AllelePair(String a1, String a2, double locus_theta, 
		    boolean order_matters)
  {
    //super(encodeAllelePair(a1, a2, order_matters));
    this( Byte.valueOf(a1).byteValue(), Byte.valueOf(a2).byteValue(),
		locus_theta, order_matters);
  }

  public AllelePair( byte a1, byte a2 )  
  {
    this(a1, a2, 0.0, false);
  }

  //----------------------------------------------------------------------------
  public String toString() 
  //{ return new Byte(a1Code).toString() + "/" + new Byte(a2Code).toString(); }
  {  
    return (a1Code + "/" + a2Code); 
  }

  //----------------------------------------------------------------------------
  //public int getAlleleCode(boolean first) { return first ? a1Code : a2Code; }
  public byte getAlleleCode(boolean first) { return first ? a1Code : a2Code; }

  //----------------------------------------------------------------------------
  public boolean transmittedA1() { return lastTransmission==a1Code; }

  //----------------------------------------------------------------------------
  //public int transmitAllele() { return transmitAllele(0.5); }
  public byte transmitAllele() { return transmitAllele(0.5); }

  //----------------------------------------------------------------------------
  //public int transmitAllele(boolean adjacent_sent_a1)
  public byte transmitAllele(boolean adjacent_sent_a1)
  {
    return transmitAllele(adjacent_sent_a1 ? (1.0 - thetaVal) : thetaVal);
  }

  //----------------------------------------------------------------------------
  //private int transmitAllele(double p_a1)
  private byte transmitAllele(double p_a1)
  {
    return lastTransmission = getAlleleCode(theRandomizer.nextBoolean(p_a1));
  }

}

