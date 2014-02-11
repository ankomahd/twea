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
    String[] info;  
    Alert msg, myAlert;
    private static final Command BACK = new Command("BACK", Command.OK, 1);
    private static final Command EXIT = new Command("EXIT", Command.EXIT, 1);
    private static final Command SAVE = new Command("SAVE", Command.OK, 1);
    private static final Command SUBMIT = new Command("SUBMIT", Command.OK, 1);
    private static final Command RETRIEVE = new Command("RETRIEVE", Command.OK, 1);

    DataDisplay() {
        homeScreen();
        startCanvas();
        myAlert = new Alert("FYI");
        myAlert.setType(AlertType.INFO);
        myAlert.setTimeout(1000);
   
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

    public void startCanvas(){
        myCanvas = new usefulCanvas();
        myCanvas.addCommand(BACK);
        myCanvas.setCommandListener(this);
        
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
            System.out.println("You have clicked retrieve");
            try {
                getViaHttpConnection("http://localhost/jmobile/data.php?num_id=1");
                myAlert.setString("Your data will be displayed shortly!");
                display.setCurrent(myAlert, myCanvas);
                myCanvas.setNumbers(values);
                myCanvas.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }            
        }else if(command == BACK && d == myCanvas){
            switchCurrentScreen(select_form);
        }
        
    }
    
    private void getViaHttpConnection(String url) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        HttpConnection c = null;
        InputStream is = null;
        int rc;

        try {
            c = (HttpConnection) Connector.open(url);

             // Getting the response code will open the connection,
            // send the request, and read the HTTP response headers.
            // The headers are stored until requested.
            rc = c.getResponseCode();
            if (rc != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP response code: " + rc);
            }

            is = c.openInputStream();

            // Get the ContentType
            String type = c.getType();

            // Get the length and process the data
            int len = (int) c.getLength();
            if (len > 0) {
                int actual = 0;
                int bytesread = 0;
                byte[] datas = new byte[len];
                while ((bytesread != len) && (actual != -1)) {
                    actual = is.read(datas, bytesread, len - bytesread);
                    bytesread += actual;
                }
                for(int i=0; i<datas.length; i++){
                     stringBuffer.append((char) datas[i]);
                }
                info = Split(stringBuffer.toString(), "#");
                values = new int[info.length];
                //Convert string to numbers
                for(int i=0; i<info.length; i++){
                    values[i] = Integer.parseInt(info[i]);
                }
                System.out.println(values[0]);
            } else {
                int ch;
                while ((ch = is.read()) != -1) {

                }
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Not an HTTP URL");
        } finally {
            if (is != null) {
                is.close();
            }
            if (c != null) {
                c.close();
            }
        }
    }

    private void closeAlert() {
        switchCurrentScreen(select_form);
    }

    private void switchCurrentScreen(Displayable displayable) {
        display.setCurrent(displayable);
    }
    public static String[] Split(String splitStr, String delimiter) {
        StringBuffer token = new StringBuffer();
        Vector tokens = new Vector();
        // split
        char[] chars = splitStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (delimiter.indexOf(chars[i]) != -1) {
                // we bumbed into a delimiter
                if (token.length() > 0) {
                    tokens.addElement(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(chars[i]);
            }
        }
        // don't forget the "tail"...
        if (token.length() > 0) {
            tokens.addElement(token.toString());
        }
        // convert the vector into an array
        String[] splitArray = new String[tokens.size()];
        for (int i = 0; i < splitArray.length; i++) {
            splitArray[i] = (String) tokens.elementAt(i);
        }
        return splitArray;
    }
}

class usefulCanvas extends Canvas {

    private int[] data;
    private String curDiagram;

    public usefulCanvas() {
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
