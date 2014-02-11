/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseware;

import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.*;

/**
 * @author Daniel
 */
public class DataDisplay extends MIDlet implements CommandListener {

    usefulCanvas myCanvas;
    private int[] values;
    private Display display;
    private Form select_form;
    private ChoiceGroup choice;
    Alert msg, myAlert;
    private static final Command BACK = new Command("BACK", Command.OK, 1);
    private static final Command EXIT = new Command("EXIT", Command.EXIT, 1);
    private static final Command SAVE = new Command("SAVE", Command.OK, 1);
    private static final Command SUBMIT = new Command("SUBMIT", Command.OK, 1);
    private static final Command RETRIEVE = new Command("RETRIEVE", Command.OK, 1);

    DataDisplay() {
        homeScreen();
        myCanvas = new usefulCanvas(values);
    }

    public void startApp() {
        display = Display.getDisplay(this);
        display.setCurrent(select_form);
        myCanvas.repaint();
    }

    public void homeScreen() {
        select_form = new Form("Choose Data Display Method");
        choice = new ChoiceGroup("Display Method", Choice.POPUP, new String[]{"Pie Chart", "Line Graph", "Histogram", "Table"}, null);
        select_form.append(new StringItem("Please choose a way to displaying: ", ""));
        select_form.append(choice);
        select_form.addCommand(RETRIEVE);
        select_form.addCommand(EXIT);
        select_form.setCommandListener(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
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
        Display.getDisplay(this).setCurrent(msg, select_form);
    }

    public void commandAction(Command command, Displayable d) {
        if (command == EXIT) {
            showConfirmation("Confirmation", "Do you really want to exit?");
        }else if(command == RETRIEVE){
            
        }
    }

    private void closeAlert() {
        switchCurrentScreen(select_form);
    }

    private void switchCurrentScreen(Displayable displayable) {
        display.setCurrent(displayable);
    }
}

class usefulCanvas extends Canvas {

    private int[] data;
    private String curDiagram;

    public usefulCanvas(int[] numbers) {
        data = numbers;
        curDiagram = "";
    }

    public void setNumbers(int[] numbers) {
        data = numbers;
    }

    public void lineGraph() {
        curDiagram = "linegraph";
    }

    public void histogram() {
        curDiagram = "histogram";
    }

    public void pieGraph() {
        curDiagram = "pieGraph";
    }

    public void table() {
        curDiagram = "table";
    }

    public void paint(Graphics g) {
        if (curDiagram == "table") { //implement the table drawing here 

        } else if (curDiagram == "pieGraph") {

        } else if (curDiagram == "histogram") {

        } else if (curDiagram == "lineGraph") {

        }
//        
//        int width = getWidth();
//        int height = getHeight();
//        Image img = null;
//        try {
//            img = Image.createImage("/res/Pizza_1.jpg");
//        //the file should be in a resource folder
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        g.setColor(0x339966);//set colour green
//        g.fillRect(0, 0, width, height); //fill full screen with red
//        g.setColor(0x0000FF); //blue
//        g.drawRect(UP, UP, 40, 40);
//        g.fillRect(UP, UP, 40, 40);
//        
//        g.setColor(0xFFFF00);
//        g.drawArc(getWidth() - 60, getHeight() - 60, 40, 40, 0, 360);
//        g.fillArc(getWidth() - 60, getHeight() - 60, 40, 40, 0, 360);
//        
//        g.setColor(0xFF0000);
//        g.drawArc(20, getHeight() - 60, 40, 40, 0, 180);
//        g.fillArc(20, getHeight() - 60, 40, 40, 0, 180);
//        g.drawImage(img, width / 2, height / 2, Graphics.VCENTER | Graphics.HCENTER);
    }
}
