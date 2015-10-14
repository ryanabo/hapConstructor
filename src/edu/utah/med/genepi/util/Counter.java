//******************************************************************************
// Counter.java
//******************************************************************************
package edu.utah.med.genepi.util;

//==============================================================================
public class Counter {

  private Number theCount;
  private int the_n;

  //----------------------------------------------------------------------------
  public Counter(int value)
  {
	  theCount = new Integer(value);
  }
  
  //----------------------------------------------------------------------------
  public Counter(double value)
  {
	  theCount = new Double(value);
  }

  //----------------------------------------------------------------------------
  //public int current() { return theCount; }
  public Number current() { return theCount; }

  //----------------------------------------------------------------------------
  public void increment() 
  { 
    add(1);
  } 

  //----------------------------------------------------------------------------
  public void add (int value)
  {
    theCount = new Integer(theCount.intValue() + value);
  }

  //----------------------------------------------------------------------------
  public void sum (double value)
  {
    theCount = new Double(theCount.doubleValue() + value);
    the_n++;
  }

  //----------------------------------------------------------------------------
  public void sum (double value, int nvalue)
  {
    theCount = new Double(theCount.doubleValue() + value);
    the_n += nvalue;
  }
  //----------------------------------------------------------------------------
  public int get_n() { return the_n; }

  //----------------------------------------------------------------------------
  //public void reset() { theCount = 0; }
  public void set(int value) 
  { 
    theCount = new Integer(value);  
  }

  public void set(double value) 
  { 
    theCount = new Double(value); 
  }

}
