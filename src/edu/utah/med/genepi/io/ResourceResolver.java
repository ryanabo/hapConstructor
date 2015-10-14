//******************************************************************************
// ResourceResolver.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//==============================================================================
public class ResourceResolver implements EntityResolver {

  private final String entityID;
  private final URL    entityURL;

  //----------------------------------------------------------------------------
  public ResourceResolver(String entity_name)
  {
    entityID = entity_name;
    entityURL = getClass().getResource(
      entityID.startsWith("/") ? entityID : ("/" + entityID)
    );
  }

  //----------------------------------------------------------------------------
  public InputSource resolveEntity(String public_id, String system_id)
  throws SAXException, IOException
  {
    if (system_id != null && system_id.endsWith(entityID))
      return new InputSource(entityURL.openStream());
    return null;
  }
}

