package jp.co.tabocom.teratermstation.model.yaml;

public class GroupIni {
    private String usemacro;
    private String inifile;
    private String loginuser;
    private String loginpassword;

    public String getUsemacro() {
        return usemacro;
    }

    public void setUsemacro(String usemacro) {
        this.usemacro = usemacro;
    }

    public String getInifile() {
        return inifile;
    }

    public void setInifile(String inifile) {
        this.inifile = inifile;
    }

    public String getLoginuser() {
        return loginuser;
    }

    public void setLoginuser(String loginuser) {
        this.loginuser = loginuser;
    }

    public String getLoginpassword() {
        return loginpassword;
    }

    public void setLoginpassword(String loginpassword) {
        this.loginpassword = loginpassword;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("usemacro      : %s\n", this.usemacro));
        builder.append(String.format("inifile       : %s\n", this.inifile));
        builder.append(String.format("loginuser     : %s\n", this.loginuser));
        builder.append(String.format("loginpassword : %s\n", this.loginpassword));
        return builder.toString();
    }

}
