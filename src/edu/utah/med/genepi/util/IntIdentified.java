//******************************************************************************
// IntIdentified.java
//******************************************************************************
package edu.utah.med.genepi.util;

//==============================================================================
public abstract class IntIdentified implements Comparable {

  //private final int myID;
  private final String myID;

  //----------------------------------------------------------------------------
  //public IntIdentified(int id)
  public IntIdentified(String id)
  { myID = id; }
 
  //----------------------------------------------------------------------------
  //public final int getID() { return myID; }
  public final String getID() { return myID; }

  //----------------------------------------------------------------------------
  //public final boolean isLike(int x) { return getID() == x; }
  public final boolean isLike(String x) { return myID.equals(x); }

  /// Object operation reimplementations ///

  //----------------------------------------------------------------------------
  //public final int hashCode() { return getID(); }
  public final int hashCode() { return myID.hashCode(); }

  //----------------------------------------------------------------------------
  public boolean equals(Object o)  // caution: lax equality
  {
    // return getID() == o.hashCode();
    return  this.hashCode() == o.hashCode();
  }

  //----------------------------------------------------------------------------
  //public String toString() { return String.valueOf(getID()); }

  /// Comparable implementation ///

  //----------------------------------------------------------------------------
  public int compareTo(Object o)  // caution: lax comparison
  {
    return this.hashCode() - o.hashCode();
    //return getID() - o.hashCode();
  }
}
