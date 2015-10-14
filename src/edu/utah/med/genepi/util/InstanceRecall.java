//******************************************************************************
// InstanceRecall.java
//******************************************************************************
package edu.utah.med.genepi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//==============================================================================
public class InstanceRecall {

  private Map<Object, Map<Comparable, Object>>  mIDSpaces = 
          new HashMap<Object, Map<Comparable, Object>>();

  //----------------------------------------------------------------------------
  public synchronized void putInstance(Object     idspace_key,
                                       Comparable id,
                                       Object     instance)
  {
    // look up id-to-instance "space" under passed key
    Map<Comparable, Object> m = getIDSpace(idspace_key);
    if (m == null) // if no instances registered in this scope
      m = createIDSpace(idspace_key);
    // register instance by mapping it to its ID
    m.put(id, instance);
  }

  //----------------------------------------------------------------------------
  public synchronized Object getInstance(Object idspace_key, Comparable id)
  {
    Map<Comparable, Object> m = getIDSpace(idspace_key);
    return m != null ? m.get(id) : null;
  }

  //----------------------------------------------------------------------------
  public synchronized List getInstances(Object idspace_key)
  {
    Map<Comparable, Object> m = getIDSpace(idspace_key);
    // copy instance refs, preserving instance order
    return m != null ? new ArrayList<Object>(m.values()) : null;
  }

  //----------------------------------------------------------------------------
  private Map<Comparable, Object> createIDSpace(Object idspace_key)
  {
    //TreeMap so that instances kept in id-order
    Map<Comparable, Object> m = new TreeMap<Comparable, Object>(); 
    mIDSpaces.put(idspace_key, m);
    return m;
  }

  //----------------------------------------------------------------------------
  private Map<Comparable, Object> getIDSpace(Object idspace_key)
  {
    return (Map<Comparable, Object>) mIDSpaces.get(idspace_key);
  }
}
