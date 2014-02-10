/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseware;

import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;

/**
 * @author Daniel
 */
public class Midlet extends MIDlet implements CommandListener {

    private Display display;
    private Form submit_form, form2, data;
    TextField index_no, phone_number;
    ChoiceGroup ecommerce, mobileweb, networks;
    DateField date;
    private Vector storage;
    List lstMenu;
    Alert msg;
    private static final Command BACK = new Command("BACK", Command.OK, 1);
    private static final Command NEXT = new Command("NEXT", Command.SCREEN, 2);
    private static final Command EXIT = new Command("EXIT", Command.EXIT, 1);
    private static final Command SAVE = new Command("SAVE", Command.OK, 1);

    public Midlet(){
        homeScreen();
        firstForm();
    }
    public void startApp() {
        display = Display.getDisplay(this);
        display.setCurrent(lstMenu);
    }

    private void homeScreen() {
        //create a list (implicit)
        String theList[] = {"Submit grades", "Check grades"};
        lstMenu = new List("Choose one", Choice.IMPLICIT, theList, null);
        lstMenu.addCommand(EXIT);
        lstMenu.setCommandListener(this);
    }

    private void firstForm(){
        //create a form  
         submit_form = new Form("Enter Grades");
         index_no = new TextField("Index Number:", "", 30, TextField.NUMERIC);
         mobileweb =  new ChoiceGroup("Mobile Web",Choice.POPUP, new String[]{"A","B","C","D", "E"}, null);
         networks =  new ChoiceGroup("Networks",Choice.POPUP, new String[]{"A","B","C","D", "E"}, null);
         ecommerce =  new ChoiceGroup("Ecommerce",Choice.POPUP, new String[]{"A","B","C","D", "E"}, null);
        phone_number = new TextField("Phone Number:", "", 30, TextField.NUMERIC);
         date = new DateField("Date:", DateField.DATE);
       
         submit_form.append(index_no);
         submit_form.append(mobileweb);
         submit_form.append(networks);
         submit_form.append(ecommerce);
         submit_form.append(phone_number);
         submit_form.append(date);
         
         submit_form.addCommand(BACK);
         submit_form.addCommand(SAVE);
         submit_form.setCommandListener(this);
    }
    
    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command command, Displayable d) {
        if (command == EXIT) {
            showConfirmation("Confirmation", "Do you really want to exit?");
        } 
        if(command==List.SELECT_COMMAND && d== lstMenu){
            //list item selected. Do something
            int selected=lstMenu.getSelectedIndex();
            switch(selected){
                case 0:
                    switchCurrentScreen(submit_form);
                    break;
                default:
                   // display.setCurrent(msg, lstMenu); //show my alert
                    break;
            }  
        }
    }
    
    protected void showConfirmation(String title, String text) {
        msg = new Alert(title, text, null, AlertType.CONFIRMATION);
        msg.addCommand(new Command("Yes", Command.OK, 1));
        msg.addCommand(new Command("No", Command.CANCEL, 1));
        msg.setCommandListener(new CommandListener() {
            public void commandAction(Command c, Displayable d) {
                if (c.getLabel().equals("Yes")) {
                    notifyDestroyed();
                }
                if (c.getLabel().equals("No")) {
                    closeAlert();
                }
            }
        });
        Display.getDisplay(this).setCurrent(msg, lstMenu);
    }
    
     private void closeAlert() {
        switchCurrentScreen(lstMenu);
    }
     
     private void switchCurrentScreen(Displayable displayable) {
        display.setCurrent(displayable);
    }
     
     
}
