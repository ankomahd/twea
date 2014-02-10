/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseware;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
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
    private Form submit_form, grade_form, data;
    TextField index_no, phone_number, checkGrd;
    ChoiceGroup ecommerce, mobileweb, networks;
    DateField date;
    private Vector storage;
    List lstMenu;
    Alert msg;
    private static final Command BACK = new Command("BACK", Command.OK, 1);
    private static final Command NEXT = new Command("NEXT", Command.SCREEN, 2);
    private static final Command EXIT = new Command("EXIT", Command.EXIT, 1);
    private static final Command SAVE = new Command("SAVE", Command.OK, 1);
    private static final Command SUBMIT = new Command("SUBMIT", Command.OK, 1);

    public Midlet(){
        homeScreen();
        firstForm();
        checkGrades();
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
    private void checkGrades(){
        grade_form = new Form("Check Grades");
        checkGrd = new TextField("Index Number: ", "", 30, TextField.NUMERIC);
        grade_form.append(checkGrd);
        grade_form.addCommand(SUBMIT);
        grade_form.addCommand(BACK);
        grade_form.setCommandListener(this);   
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
        } else if(command == BACK && d == grade_form){
            switchCurrentScreen(lstMenu);
        }else if(command == SUBMIT && d==grade_form){
            try {
                getViaHttpConnection("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(command==List.SELECT_COMMAND && d== lstMenu){
            //list item selected. Do something
            int selected=lstMenu.getSelectedIndex();
            switch(selected){
                case 0:
                    switchCurrentScreen(submit_form);
                    break;
                case 1:
                    switchCurrentScreen(grade_form); //show my alert
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

    private void getViaHttpConnection(String url)  throws IOException{
        HttpConnection c = null;
        InputStream is = null;
        int rc;
        
        try{
            c = (HttpConnection)Connector.open(url);
            
            //get Response
            rc = c.getResponseCode();
            if(rc != HttpConnection.HTTP_OK){
                throw new IOException("HTTP response code: " + rc);
            }
            
            is = c.openInputStream();
            
            //Get the contentType
            String type = c.getType();
            
            //Get the Length and process the data
            int len = (int)c.getLength();
            if(len > 0){
                int actual = 0;
                int bytesread = 0;
                byte[] data = new byte[len];
                while((bytesread != len) && (actual != -1)){
                    actual = is.read(data, bytesread, len - bytesread);
                    bytesread += actual;
                }
            }else{
                int ch;
                while((ch = is.read()) != -1){
                    
                }
            }
            
        }catch(ClassCastException e){
            throw new IllegalArgumentException("Not an HTTP URL");
        }finally {
            if(is != null)
                is.close();
            if(c!= null)
                c.close();
        }
        
    }
     
     
}
