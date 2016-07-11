package jp.co.tabocom.teratermstation.model.yaml;

import java.util.List;
import java.util.Map;

/**
 * 
 * server.yamlを読み込むためのクラスです。<br>
 * <br>
 * server.yamlの内容は下な感じ<br>
 * 
 * <pre>
 * --------------------------------------------------------------------------------
 * id: kibanusr@addbs004
 * ipaddress: 192.177.238.204
 * hostname: addbs004
 * loginuser: kibanusr
 * loginpassword: kibanpwd
 * inifile: AWS.INI
 * procedure: |
 *   wait ']$'
 *   sendln 'date'
 * variable:
 *   APL_DIR: /APL/group01/local
 *   APL_PKG01_DIR: /APL/group01/pkg01
 * --------------------------------------------------------------------------------
 * </pre>
 * 
 * @author turbou
 * 
 */
public class ServerIni {
    /**
     * サーバID（任意）
     */
    private String id;
    /**
     * サーバに適用されるホスト名
     */
    private String hostname;
    /**
     * サーバに適用されるIPアドレス
     */
    private String ipaddress;
    /**
     * サーバに適用されるTera TermのINIファイル（任意）
     */
    private String inifile;
    /**
     * サーバに適用されるサーバログインユーザー（任意）
     */
    private String loginuser;
    /**
     * サーバに適用されるサーバログインパスワード（任意）
     */
    private String loginpassword;
    /**
     * ログイン後の手続き（任意）
     */
    private String procedure;
    /**
     * 変数マップ
     */
    private Map<String, String> variable;
    /**
     * ログインリスト
     */
    private List<Map<String, Object>> login;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
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

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Map<String, String> getVariable() {
        return variable;
    }

    public void setVariable(Map<String, String> variable) {
        this.variable = variable;
    }

    public List<Map<String, Object>> getLogin() {
        return login;
    }

    public void setLogin(List<Map<String, Object>> login) {
        this.login = login;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("id            : %s\n", this.id));
        builder.append(String.format("hostname      : %s\n", this.hostname));
        builder.append(String.format("ipaddress     : %s\n", this.ipaddress));
        builder.append(String.format("inifile       : %s\n", this.inifile));
        builder.append(String.format("loginuser     : %s\n", this.loginuser));
        builder.append(String.format("loginpassword : %s\n", this.loginpassword));
        builder.append(String.format("login          : %s\n", this.login));
        builder.append(String.format("procedure     : %s\n", this.procedure));
        builder.append(String.format("variable      : %s\n", this.variable));
        return builder.toString();
    }

}
