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
        select_form.append(new StringItem("Choose a data display method ", ""));
        select_form.append(choice);
        select_form.addCommand(RETRIEVE);
        select_form.addCommand(EXIT);
        select_form.setCommandListener(this);
    }

    public void startCanvas() {
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
        } else if (command == RETRIEVE) {
            //get the comboBox selection.
            String selected = choice.getString(choice.getSelectedIndex());
            try {
                getViaHttpConnection("http://localhost/jmobile/data.php?num_id=1");
                myAlert.setString("Your data will be displayed shortly!");
                myCanvas.setNumbers(values);
                
                if(selected == "Pie Chart"){
                    myCanvas.pieGraph();
                }else if(selected == "Line Graph"){
                    myCanvas.lineGraph();
                }else if(selected == "Histogram"){
                    myCanvas.histogram();
                }else{
                    myCanvas.table();
                }
                switchCurrentScreen(myCanvas);
                myCanvas.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (command == BACK && d == myCanvas) {
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
                for (int i = 0; i < datas.length; i++) {
                    stringBuffer.append((char) datas[i]);
                }
                info = Split(stringBuffer.toString(), "#");
                values = new int[info.length];
                //Convert string to numbers
                for (int i = 0; i < info.length; i++) {
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
        curDiagram = "lineGraph";
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
        int width = getWidth();
        int height = getHeight();
        g.setColor(0x99B2FF);//set colour green
        g.fillRect(0, 0, width, height); //fill full screen with red
        if (curDiagram == "table") { //implement the table drawing here 

        } else if (curDiagram == "pieGraph") {

            int colors[] = {0xFF0000, 0xA9E969, 0x00FFFF, 0xC675EC, 0x008800, 0x00C400};

            double startAngle = 0, arcAngle = 0, sum = 0;

            for (int i = 0; i < data.length; i++) {
                sum += data[i];
            }

            for (int i = 0; i < data.length; i++) {
                arcAngle = (data[i] / sum) * 360.0;
                g.setColor(colors[i]);
                g.drawArc(90, 120, 60, 60, (int) startAngle, (int) arcAngle);
                g.fillArc(90, 120, 60, 60, (int) startAngle, (int) arcAngle);
                startAngle += arcAngle;
            }
        } else if (curDiagram == "histogram") {
            int colors[] = {0xFF0000, 0xA9E969, 0x00FFFF, 0xC675EC, 0x008800, 0x00C400};

            int max = findMax();   //Find the maximum value's index within the data array
            int barW = width / 8;   //bar Width
            int barH = height / 20; //bar Height

            g.setColor(0x99B2FF);//set colour green
            g.fillRect(0, 0, width, height); //fill full screen with red

            //draw the rectangles.
            for (int i = 0; i < data.length; i++) {
                g.setColor(colors[i]);
                g.drawRect(barW * (i + 1), (barH * (data[max] - data[i])) + 120, barW, (barH * data[i]));
                g.fillRect(barW * (i + 1), (barH * (data[max] - data[i])) + 120, barW, (barH * data[i]));
            }
            
            //Draw the Axis
            g.setColor(0x003300);
            g.drawLine(barW, barH * data[max] + 120, barW, barH); //draw green line top left to center
            g.drawLine(barW, barH * data[max] + 120, barW * (data.length + 2), barH * data[max] + 120);

        } else if (curDiagram == "lineGraph") {
            int colors[] = {0xFF0000, 0xA9E969, 0x00FFFF, 0xC675EC, 0x008800, 0x00C400};
            
            int max = findMax();   //Find the maximum value's index within the data array
            int barW = width / 8;   //line Width
            int barH = height / 20; //line Height

            g.setColor(0x339966);//set colour green
            g.fillRect(0, 0, width, height); //fill full screen with red
            
            g.setColor(0x66FFFF);
          
            for (int i = 0; i < data.length; i++) {
                if(i==4)
                    break;
                g.drawLine(barW * (i + 1), (barH * (data[max] - data[i])) + 120, barW * (i + 2), (barH * (data[max] - data[i+1])) + 120);
            }
            
            //Draw Axis
            g.setColor(0x003300);
            g.drawLine(barW, barH * data[max] + 120, barW, barH); 
            g.drawLine(barW, barH * data[max] + 120, barW * (data.length + 2), barH * data[max] + 120);
        }
    }

    public int findMax() {
        int max = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > data[max]) {
                max = i;
            }
        }

        return max;
    }
}
