//******************************************************************************
// XUt.java
//******************************************************************************
package edu.utah.med.genepi.io;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class XUt {

  //----------------------------------------------------------------------------
  public static NodeList kidsOf(Element e, String kids_name, String ns_uri)
  {
    //System.out.println("NSURI : " + ns_uri + ", kids_name : " + kids_name);
    return e.getElementsByTagNameNS(ns_uri, kids_name);
  }

  //----------------------------------------------------------------------------
  public static int intAtt(Element e, String att_name) throws GEException
  {
    String s = e.getAttribute(att_name);
    if (s.length() > 0)
      try { return Integer.parseInt(s); }
      catch (Exception ex) {throw new GEException(attErrMsg(att_name, s), ex);}
    return -1;
  }

  //----------------------------------------------------------------------------
  public static int[] intsAtt(Element e, String att_name) throws GEException
  {
    String s = e.getAttribute(att_name);
    if (s.length() > 0)
    {
      try {
        String[] ss;
        int[] nn;
        if ( s.contains("-") )
        {
          ss = s.split("-");
          int lower = Integer.parseInt(ss[0]);
          int upper = Integer.parseInt(ss[1]);
          int range = upper - lower + 1;
          nn = new int[range];
          for ( int i = 0; i < range; i++)
          {
            nn[i] = lower;
            lower++;
          }
        }
        else
        { 
          ss = s.split("\\s");
          nn = new int[ss.length];
          for (int i = 0; i < nn.length; ++i)
            nn[i] = Integer.parseInt(ss[i]);
        }
        return nn;
      } catch (Exception ex) {
        throw new GEException(attErrMsg(att_name, s), ex);
      }
    }
    return null;
  }

  //----------------------------------------------------------------------------
  public static String stringAtt(Element e, String att_name) throws GEException
  {
    String s = e.getAttribute(att_name);
    if (s.length() > 0)
    {
      try { return s; }
      catch ( Exception ex )
      { 
        throw new GEException(attErrMsg(att_name, s), ex);
      }
    }
    return null;
  }
  //----------------------------------------------------------------------------
  public static double doubleAtt(Element e, String att_name) throws GEException
  {
    String s = e.getAttribute(att_name);
    if (s.length() > 0)
      try { return Double.parseDouble(s); }
      catch (Exception ex) {throw new GEException(attErrMsg(att_name, s), ex);}
    return Double.NaN;
  }

  //----------------------------------------------------------------------------
  public static boolean booleanAtt(Element e, String att_name)
  throws GEException
  {
    String s = e.getAttribute(att_name);
    if (s.equals("true"))
      return true;
    if (s.equals("false"))
      return false;
    throw new GEException(attErrMsg(att_name, s));
  }

  //----------------------------------------------------------------------------
  public static String attErrMsg(String name, String val)
  {
    return "'" + name + "' attribute can't be '" + val + "'";
  }
}

