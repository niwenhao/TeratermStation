package jp.co.tabocom.teratermstation.model;

public class Initial {

    private String ttpMacroExe;
    private String workDir;
    private String logDir;
    private String iniFileDir;

    public String getTtpMacroExe() {
        return ttpMacroExe;
    }

    public void setTtpMacroExe(String ttpMacroExe) {
        this.ttpMacroExe = ttpMacroExe;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getIniFileDir() {
        return iniFileDir;
    }

    public void setIniFileDir(String iniFileDir) {
        this.iniFileDir = iniFileDir;
    }
}
