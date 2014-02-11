/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseware;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
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
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
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
    Alert msg, myAlert;
    private static final Command BACK = new Command("BACK", Command.OK, 1);
    private static final Command NEXT = new Command("NEXT", Command.SCREEN, 2);
    private static final Command EXIT = new Command("EXIT", Command.EXIT, 1);
    private static final Command SAVE = new Command("SAVE", Command.OK, 1);
    private static final Command SUBMIT = new Command("SUBMIT", Command.OK, 1);
    StringBuffer result;
    String[] info;

    public Midlet() {
        homeScreen();
        firstForm();
        checkGrades();
        viewGrade();
        Alert register;
        register = new Alert("FYI");
        register.setType(AlertType.INFO);
        register.setTimeout(1000);
        myAlert = register;
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

    private void checkGrades() {
        grade_form = new Form("Check Grades");
        checkGrd = new TextField("Index Number: ", "", 30, TextField.NUMERIC);
        grade_form.append(checkGrd);
        grade_form.addCommand(SUBMIT);
        grade_form.addCommand(BACK);
        grade_form.setCommandListener(this);
    }

    private void firstForm() {
        //create a form  
        submit_form = new Form("Enter Grades");
        index_no = new TextField("Index Number:", "", 30, TextField.NUMERIC);
        mobileweb = new ChoiceGroup("Mobile Web", Choice.POPUP, new String[]{"A", "B", "C", "D", "E"}, null);
        networks = new ChoiceGroup("Networks", Choice.POPUP, new String[]{"A", "B", "C", "D", "E"}, null);
        ecommerce = new ChoiceGroup("Ecommerce", Choice.POPUP, new String[]{"A", "B", "C", "D", "E"}, null);
        phone_number = new TextField("Phone Number:", "", 30, TextField.NUMERIC);
        date = new DateField("Date:", DateField.DATE);

        submit_form.append(index_no);
        submit_form.append(mobileweb);
        submit_form.append(networks);
        submit_form.append(ecommerce);
        submit_form.append(phone_number);
        submit_form.append(date);

        submit_form.addCommand(BACK);
        submit_form.addCommand(SUBMIT);
        submit_form.setCommandListener(this);
    }

    private void viewGrade() {
        data = new Form("View Details");
        data.addCommand(BACK);
        data.setCommandListener(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command command, Displayable d) {
        if (command == EXIT) {
            showConfirmation("Confirmation", "Do you really want to exit?");
        } else if (command == BACK && d == grade_form) {
            switchCurrentScreen(lstMenu);
        } else if (command == SUBMIT && d == grade_form) {
            System.out.println("You have clicked submit");
            try {
                getViaHttpConnection("http://localhost/jmobile/data.php?index_no=" + checkGrd.getString());
                myAlert.setString("Submitted");
                display.setCurrent(myAlert, data);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (command == BACK && d == data) {
            switchCurrentScreen(lstMenu);
        } else if (command == SUBMIT && d == submit_form) {
            System.out.println("You have clicked submit");
            try {
                // getViaHttpConnection("http://localhost/mobile_web/data.php?index_no=" + checkGrd.getString());
                saveDetails();
                myAlert.setString("Submitted");
                display.setCurrent(myAlert, lstMenu);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (command == List.SELECT_COMMAND && d == lstMenu) {
            //list item selected. Do something
            int selected = lstMenu.getSelectedIndex();
            switch (selected) {
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
                data.append(new StringItem("Index Number: ", info[0]));
                data.append(new StringItem("Mobile Web: ", info[1]));
                data.append(new StringItem("Networks: ", info[2]));
                data.append(new StringItem("Ecommerce: ", info[3]));
                data.append(new StringItem("Phone Number: ", info[4]));
                data.append(new StringItem("Date: ", info[5]));
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

    private void saveDetails() throws IOException {
        System.out.print("Hello");
        HttpConnection httpConn = null;
        String url = "http://localhost/jmobile/data.php";
        InputStream is = null;
        DataOutputStream os = null;

        try {
            // Open an HTTP Connection object
            httpConn = (HttpConnection) Connector.open(url);
            // Setup HTTP Request to POST
            httpConn.setRequestMethod(HttpConnection.POST);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            os = new DataOutputStream(httpConn.openDataOutputStream());

            String params;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date.getDate());
            String theDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            System.out.println(theDate);

            params = "index_no=" + index_no.getString() + "&mobileweb="
                    + mobileweb.getString(mobileweb.getSelectedIndex()) + "&networks="
                    + networks.getString(networks.getSelectedIndex()) + "&ecommerce="
                    + ecommerce.getString(ecommerce.getSelectedIndex()) + "&phone_number="
                    + phone_number.getString() + "&date=" + theDate;

            os.write(params.getBytes());
            System.out.print(params);
            os.close();
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (httpConn != null) {
                httpConn.close();
            }
        }
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
