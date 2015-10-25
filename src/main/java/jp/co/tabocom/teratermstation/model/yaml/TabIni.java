package jp.co.tabocom.teratermstation.model.yaml;

import java.util.Map;

/**
 * 
 * tab.yamlを読み込むためのクラスです。<br>
 * <br>
 * tab.yamlの内容は下な感じ<br>
 * 
 * <pre>
 * --------------------------------------------------------------------------------
 * gateway:
 *   ipaddress: 192.177.237.111
 *   errptn: authentication failed
 *   auth: true
 *   password_memory: true
 *   password_autoclear: false
 *   password_group: dev
 * 
 * connect: connect '${gateway_ipaddress} /nossh /T=1 /f="${inifile}"'
 * 
 * negotiation: |
 *   wait 'Host name:'
 *   sendln '${ipaddress}'
 *   wait 'Username:'
 *   sendln '${authuser}'
 *   wait 'Password:'
 *   sendln ${authpassword}
 *   wait 'login:'
 *   sendln '${loginuser}'
 *   waitregex 'Password.*:'
 *   sendln '${loginpassword}'
 * 
 * loginuser: aplusr
 * loginpassword: aplpwd
 * usemacro: true
 * inifile: DEV.INI
 * --------------------------------------------------------------------------------
 * </pre>
 * 
 * @author turbou
 *
 */
public class TabIni {

    /**
     * プロキシを経由する際に必要な情報を保持します。（任意）
     */
    private Map<String, Object> gateway;
    /**
     * Tera Termのconnect文（必須）
     */
    private String connect;
    /**
     * プロキシ認証のやり取りなど、connect後の手続き（任意）
     */
    private String negotiation;
    /**
     * マクロ機能を使用するか否か（任意）<br>
     * 省略時は上の階層の条件に従う。上に設定がない場合はfalse
     */
    private String usemacro;
    /**
     * タブ全体に適用されるTera TermのINIファイル（任意）
     */
    private String inifile;
    /**
     * タブ全体に適用されるサーバログインユーザー（任意）
     */
    private String loginuser;
    /**
     * タブ全体に適用されるサーバログインパスワード（任意）
     */
    private String loginpassword;

    public Map<String, Object> getGateway() {
        return gateway;
    }

    public void setGateway(Map<String, Object> gateway) {
        this.gateway = gateway;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(String negotiation) {
        this.negotiation = negotiation;
    }

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

    public String getGatewayIpaddress() {
        return this.gateway.get("ipaddress").toString();
    }

    public String getGatewayErrptn() {
        return this.gateway.get("errptn").toString();
    }

    public boolean isGatewayAuth() {
        return Boolean.valueOf(this.gateway.get("auth").toString());
    }

    public boolean isGatewayPasswordMemory() {
        return Boolean.valueOf(this.gateway.get("password_memory").toString());
    }

    public boolean isGatewayPasswordAutoclear() {
        return Boolean.valueOf(this.gateway.get("password_autoclear").toString());
    }

    public String getGatewayPasswordGroup() {
        return this.gateway.get("password_group").toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("connect                    : %s\n", this.connect));
        builder.append(String.format("negotiation                : %s\n", this.negotiation));
        builder.append(String.format("usemacro                   : %s\n", this.usemacro));
        builder.append(String.format("inifile                    : %s\n", this.inifile));
        builder.append(String.format("loginuser                  : %s\n", this.loginuser));
        builder.append(String.format("loginpassword              : %s\n", this.loginpassword));
        builder.append(String.format("gateway_ipaddress          : %s\n", getGatewayIpaddress()));
        builder.append(String.format("gateway_errptn             : %s\n", getGatewayErrptn()));
        builder.append(String.format("gateway_auth               : %s\n", isGatewayAuth()));
        builder.append(String.format("gateway_password_memory    : %s\n", isGatewayPasswordMemory()));
        builder.append(String.format("gateway_password_autoclear : %s\n", isGatewayPasswordAutoclear()));
        builder.append(String.format("gateway_password_group     : %s", getGatewayPasswordGroup()));
        return builder.toString();
    }

}
