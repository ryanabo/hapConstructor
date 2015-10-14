//******************************************************************************
// EmpiricalRandomizer.java
//******************************************************************************
package edu.utah.med.genepi.util;

import java.util.Iterator;
import java.util.TreeMap;

//==============================================================================
public class EmpiricalRandomizer {

  private final Randy randNums;
  private TreeMap<Object, Counter>	mEvent2Counter;
  //private Map<Object, Counter>	mEvent2Counter;
  private TreeMap<Integer, Object>  	mUBound2Event;
  private int         cumulativeCount;

  //----------------------------------------------------------------------------
  public EmpiricalRandomizer(Randy rand_int_source)
  {
    randNums = rand_int_source;
    openSampling();
  }

  //----------------------------------------------------------------------------
  public void openSampling()
  {
    //Use TreeMap to retrieve allele in order
    //mEvent2Counter = new HashMap();
    mEvent2Counter = new TreeMap<Object, Counter>();
    mUBound2Event = null;
    //System.out.println("open sampling " );
  }

  //----------------------------------------------------------------------------
  public void sampleNext(Object event)
  {
    assert mEvent2Counter != null : "sampling not open";

    //if ( mEvent2Counter != null )
      //System.out.println("sample : " + event.toString() );
    //else 
      //System.out.println("mEvent is null ");
    Counter c = (Counter) mEvent2Counter.get(event);
    if (c == null)
    {
      c = new Counter();
      c.set(0);
      mEvent2Counter.put(event, c);
    }
    c.increment();
  }

  //----------------------------------------------------------------------------
  public void closeSampling()
  {
    assert mEvent2Counter != null : "sampling not open";

    mUBound2Event = new TreeMap<Integer, Object>();
    for (Iterator it = mEvent2Counter.keySet().iterator(); it.hasNext(); )
    {
      Object event = it.next();
      //cumulativeCount += ((Counter) mEvent2Counter.get(event)).current();
      cumulativeCount += ((Counter) mEvent2Counter.get(event)).current().intValue();
      mUBound2Event.put(new Integer(cumulativeCount), event);
    }
    mEvent2Counter = null;
  }

  //----------------------------------------------------------------------------
  public Object getNext()
  {
    assert mUBound2Event != null : "sampling not closed";

    // if cumulativeCount is 0 that means this marker has no data
    if ( cumulativeCount == 0 )
      return new Byte("0");

    int x = randNums.nextInt(cumulativeCount);
    for (Iterator i = mUBound2Event.keySet().iterator(); i.hasNext(); )
    {
      Integer upper_bound = (Integer) i.next();
      if (x < upper_bound.intValue())
        return mUBound2Event.get(upper_bound);
    }

    assert false;

    return null;
  }

  //----------------------------------------------------------------------------
  public int getNumKey()
  {
    assert mUBound2Event != null : "sampling not closed";

    return mUBound2Event.size();
  }

  //----------------------------------------------------------------------------
  public TreeMap<Object, Counter> getCounterMap()
  {
    return mEvent2Counter;
  }
}
