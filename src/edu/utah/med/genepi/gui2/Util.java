package edu.utah.med.genepi.gui2;

import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Util 
{

  public Util()
  {}
 
  public static void disable ( JButton button ) 
  {
    disableButton(button);
  }

  public static void disable ( JButton[] buttons )
  {
    disableButtons(buttons);
  }

  public static void disable ( JTextField textfield )
  {
    disableTextField (textfield);
  }

  public static void disable ( JTextField[] textfields )
  {
    disableTextFields(textfields);
  }

  public static void disable ( ButtonGroup buttongroup )
  {
    disableButtonGroup ( buttongroup);
  }

  public static void disable ( JCheckBox[] checkboxes )
  {
    disableCheckBoxes ( checkboxes );
  }

  public static void disableButton ( JButton button )
  {
    button.setEnabled(false);
  }

  public static void disableButtons (JButton[] buttons)
  {
    int b = buttons.length;
    if ( b > 0  && buttons != null )
    {
      for ( int i = 0 ; i  < b; i++ )
        buttons[i].setEnabled(false);
    }
  }
      
  public static void disableTextField ( JTextField textfield )
  {
    textfield.setEditable(false);
  }

  public static void disableTextFields ( JTextField[] textfields )
  {
    int tf = textfields.length; 
    if ( tf > 0 && textfields != null )
    {
      for ( int i = 0; i < tf; i++ )
        textfields[i].setEditable(false);
    }
  }
  
  public static void disableButtonGroup ( ButtonGroup buttongroup ) 
  {
    for ( Enumeration en = buttongroup.getElements(); en.hasMoreElements(); )
    {
      JRadioButton rb = (JRadioButton) en.nextElement();
      rb.setEnabled(false);
    }
  }
 
  public static void disableCheckBoxes ( JCheckBox[] checkboxes )
  {
    int cb = checkboxes.length;
    if ( cb > 0 && checkboxes != null )
    {
      for (int i = 0; i < cb; i++ )
        checkboxes[i].setEnabled(false);
    }
  }
}
