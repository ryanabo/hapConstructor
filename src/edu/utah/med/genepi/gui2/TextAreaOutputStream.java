package edu.utah.med.genepi.gui2;

import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream
{
  protected JTextArea textArea;

  public TextAreaOutputStream (JTextArea ta )
  {
    textArea = ta;
  }

  public void write (int i )
  {
    textArea.append((char) i + " " );
  }
}
