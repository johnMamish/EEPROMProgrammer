/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eepromprogrammer;

import javax.swing.JFrame;

/**
 *
 * @author John
 */
public class EEPROMProgrammer
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        SerialPortInterface.LoadLibrary();
        JFrame foo = new MainWindow();
        foo.setTitle("EEPROM Programmer");
        foo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        foo.setVisible(true);
    }
}
