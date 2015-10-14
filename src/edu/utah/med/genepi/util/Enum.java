//******************************************************************************
// Enum.java
//******************************************************************************
package edu.utah.med.genepi.util;

import java.util.Collections;
import java.util.List;

//==============================================================================
public abstract class Enum extends IntIdentified {

  private static final InstanceRecall instRecall = new InstanceRecall(); 
  private final String                myLabel;

  //----------------------------------------------------------------------------
  //public Enum(int id, String label)
  public Enum(String id, String label)
  {
    super(id);
    myLabel = label;
    //instRecall.putInstance(getClass(), new Integer(getID()), this);
    instRecall.putInstance(getClass(), getID(), this);
  }

  /// Override superclass method ///

  //----------------------------------------------------------------------------
  public String toString() { return myLabel; }

  /// Class methods making use of instance lookup ///

  //----------------------------------------------------------------------------
  //public static Enum valueOf(int id, Class concrete_subclass)
  public static Enum valueOf(String id, Class concrete_subclass)
  {
    //return (Enum) instRecall.getInstance(concrete_subclass, new Integer(id));
    return (Enum) instRecall.getInstance(concrete_subclass, id);
  }

  //----------------------------------------------------------------------------
  public static List<InstanceRecall> instanceList(Class concrete_subclass)
  {
    return Collections.unmodifiableList(
      instRecall.getInstances(concrete_subclass)
    );
  }

  //----------------------------------------------------------------------------
  public static int instanceCount(Class concrete_subclass)
  {
    return instanceList(concrete_subclass).size();
  }
}
