//******************************************************************************
// XLoader.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.File;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class XLoader {

  private final DocumentBuilder docBuilder;

  //----------------------------------------------------------------------------
  public XLoader() throws GEException
  {
    this(null);
  }

  //----------------------------------------------------------------------------
  public XLoader(ErrorHandler eh) throws GEException
  {
    try {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(true);
      dbf.setNamespaceAware(true);

      docBuilder = dbf.newDocumentBuilder();

      if (eh == null)
        eh = new BasicSAXErrorHandler(new PrintWriter(System.err));

      docBuilder.setErrorHandler(eh);

    } catch (Exception e) {
      throw new GEException("Can't get XML parser: ", e);
    }
  }

  //----------------------------------------------------------------------------
  public Element loadDocumentElement(File xml, EntityResolver er)
  throws GEException
  {
    try {

      if (er != null)
        docBuilder.setEntityResolver(er);

      return docBuilder.parse(xml).getDocumentElement();

    } catch (Exception e) {
      throw new GEException("Can't parse '" + xml + "': ", e);
    }
  }

  //============================================================================
  private static class BasicSAXErrorHandler implements ErrorHandler {

    private PrintWriter pwWarn;

    //--------------------------------------------------------------------------
    public BasicSAXErrorHandler(PrintWriter warning_writer)
    {
      pwWarn = warning_writer;
    }

    /// interface methods (next 3) ///

    //--------------------------------------------------------------------------
    public void warning(SAXParseException spe) throws SAXException
    {
      pwWarn.println(formatInfo("Warning", spe));
    }

    //--------------------------------------------------------------------------
    public void error(SAXParseException spe) throws SAXException
    {
      throw new SAXException(formatInfo("Error", spe));
    }

    //--------------------------------------------------------------------------
    public void fatalError(SAXParseException spe) throws SAXException
    {
      throw new SAXException(formatInfo("Fatal Error", spe));
    }

    //--------------------------------------------------------------------------
    private String formatInfo(String type, SAXParseException spe)
    {
      String sysid = spe.getSystemId();
      if (sysid == null)
        sysid = "[none]";

      return Ut.N + "*** XML " + type + " ***" +
             Ut.N + "URI=" + sysid +
             Ut.N + "Line=" + spe.getLineNumber() +
             Ut.N + ">> " + spe.getMessage();
    }
  }
}

