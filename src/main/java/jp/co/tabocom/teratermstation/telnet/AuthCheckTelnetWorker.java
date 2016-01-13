package jp.co.tabocom.teratermstation.telnet;

public class AuthCheckTelnetWorker extends AbstractTelnetWorker {

    private String authCheck;
    private String okPtn;
    private String ngPtn;

    public AuthCheckTelnetWorker(String str, String okPtn, String ngPtn) {
        super(str.split("\n")[0]);
        this.authCheck = str;
        this.okPtn = okPtn;
        this.ngPtn = ngPtn;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        try {
            connection();
            if (!login()) {
                disConnection();
                return Boolean.FALSE;
            }
            disConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }

    private boolean login() throws Exception {
        for (String line : this.authCheck.split("\n")) {
            String str = line.replaceAll("wait '", "").replaceAll("sendln '", "").replaceAll("'$", "");
            if (line.startsWith("wait")) {
                System.out.println("wait: " + str);
                String rtn = readMessage(reader, ".*" + str + " $");
                if (rtn == null) {
                    return false;
                }
            } else if (line.startsWith("sendln")) {
                System.out.println("send: " + str);
                writer.write(str + LF);
                writer.flush();
            }
        }
        String rtn = readMessage(reader, ".*" + okPtn + ".*", ".*" + ngPtn + ".*");
        if (rtn == null) {
            return false;
        }
        return true;
    }

}
