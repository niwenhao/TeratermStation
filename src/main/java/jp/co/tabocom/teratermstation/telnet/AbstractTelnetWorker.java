package jp.co.tabocom.teratermstation.telnet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.commons.net.telnet.WindowSizeOptionHandler;

public abstract class AbstractTelnetWorker extends SwingWorker<Boolean, String> {

    public final static String CR = "\r";
    public final static String LF = "\n";

    String authAddress;

    TelnetClient telnet;
    Reader reader;
    Writer writer;

    protected AbstractTelnetWorker(String authAddress) {
        this.authAddress = authAddress;
    }

    @Override
    abstract protected Boolean doInBackground() throws Exception;

    @Override
    protected void process(List<String> chunks) {
        for (String str : chunks) {
            firePropertyChange("console", null, str);
        }
    }

    @Override
    protected void done() {
        super.done();
    }

    protected String readMessage(Reader reader, String waitWord) throws Exception {

        Pattern ptn = Pattern.compile(waitWord, Pattern.DOTALL);

        StringBuffer buffer = new StringBuffer();
        Matcher matcher = null;

        int c;
        while ((c = reader.read()) > -1) {
            buffer.append((char) c);
            if ((char) c == '\n') {
                buffer.setLength(0);
            }
            if (reader.ready() == false) {
                matcher = ptn.matcher(buffer.toString());
                if (matcher.matches()) {
                    publish(String.valueOf(buffer.substring(0, buffer.length())));
                    break;
                }
            }
        }

        if (matcher.find(0) && matcher.groupCount() >= 1) {
            return (matcher.group(1));
        }
        return buffer.toString();
    }

    /**
     * 
     * @param reader
     * @param waitWord
     * @param errorWord
     * @return
     * @throws Exception
     */
    protected String readMessage(Reader reader, String waitWord, String errorWord) throws Exception {

        Pattern ptn = Pattern.compile(waitWord, Pattern.DOTALL);
        Pattern errorPtn = Pattern.compile(".*" + errorWord + ".*", Pattern.DOTALL);

        StringBuffer buffer = new StringBuffer();
        Matcher matcher = null;

        int c;
        while ((c = reader.read()) > -1) {
            buffer.append((char) c);
            if ((char) c == '\n') {
                // publish(String.valueOf(buffer.substring(0, buffer.length() -
                // 1)));
                // publish(String.valueOf(buffer.substring(0,
                // buffer.length())));
                matcher = errorPtn.matcher(buffer.toString());
                if (matcher.matches()) {
                    return null;
                }
                buffer.setLength(0);
            }
            if (reader.ready() == false) {
                matcher = errorPtn.matcher(buffer.toString());
                if (matcher.matches()) {
                    return null;
                }
                matcher = ptn.matcher(buffer.toString());
                if (matcher.matches()) {
                    // publish(String.valueOf(buffer.substring(0,
                    // buffer.length() - 1)));
                    publish(String.valueOf(buffer.substring(0, buffer.length())));
                    break;
                }
            }
        }

        if (matcher.find(0) && matcher.groupCount() >= 1) {
            return (matcher.group(1));
        }
        return buffer.toString();
    }

    protected String getDisplayWord(Reader reader, String message) throws Exception {

        Pattern pattern = Pattern.compile(message, Pattern.DOTALL);
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = null;

        int c;
        while ((c = reader.read()) > -1) {
            buffer.append((char) c);
            if (reader.ready() == false) {
                matcher = pattern.matcher(buffer.toString());
                if (matcher.matches()) {
                    break;
                }
            }
        }

        if (matcher.find(0) && matcher.groupCount() >= 1) {
            return (matcher.group(1));
        }
        return buffer.toString();
    }

    protected void connection() throws Exception {
        telnet = new TelnetClient();
        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", true, true, true, true);
        WindowSizeOptionHandler wsopt = new WindowSizeOptionHandler(512, 32, true, true, true, true);
        telnet.connect(authAddress);
        telnet.addOptionHandler(ttopt);
        telnet.addOptionHandler(wsopt);
        telnet.registerNotifHandler(new TelnetNotificationHandler() {
            public void receivedNegotiation(int negotiation_code, int option_code) {
                @SuppressWarnings("unused")
                String command = null;
                if (negotiation_code == TelnetNotificationHandler.RECEIVED_DO) {
                    command = "DO";
                } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_DONT) {
                    command = "DONT";
                } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WILL) {
                    command = "WILL";
                } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WONT) {
                    command = "WONT";
                }
            }
        });

        // 通信用の入出力ストリームの生成
        InputStream istream = telnet.getInputStream();
        OutputStream ostream = telnet.getOutputStream();
        reader = new InputStreamReader(istream);
        writer = new OutputStreamWriter(ostream);
    }

    protected void disConnection() throws Exception {
        reader.close();
        writer.close();
        telnet.disconnect();
    }

}
