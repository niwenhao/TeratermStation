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
 *   auth: true
 *   password_memory: true
 *   password_autoclear: false
 *   password_group: dev
 *   authcheck:
 *     negotiation: |
 *       wait 'Host name:'
 *       sendln '192.177.238.100'
 *       wait 'Username:'
 *       sendln '${authuser}'
 *       wait 'Password:'
 *       sendln '${authpassword}'
 *     ok: 'login:'
 *     ng: 'Username:'
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
        if (this.gateway == null) {
            return "";
        }
        if (this.gateway.containsKey("ipaddress")) {
            return this.gateway.get("ipaddress").toString();
        }
        return "";
    }

    public boolean isGatewayAuth() {
        if (this.gateway == null) {
            return false;
        }
        if (this.gateway.containsKey("auth")) {
            return Boolean.valueOf(this.gateway.get("auth").toString());
        }
        return false;
    }

    public boolean isGatewayPasswordMemory() {
        if (this.gateway == null) {
            return false;
        }
        if (this.gateway.containsKey("password_memory")) {
            return Boolean.valueOf(this.gateway.get("password_memory").toString());
        }
        return false;
    }

    public boolean isGatewayPasswordAutoclear() {
        if (this.gateway == null) {
            return false;
        }
        if (this.gateway.containsKey("password_autoclear")) {
            return Boolean.valueOf(this.gateway.get("password_autoclear").toString());
        }
        return false;
    }

    public String getGatewayPasswordGroup() {
        if (this.gateway == null) {
            return "";
        }
        if (this.gateway.containsKey("password_group")) {
            return this.gateway.get("password_group").toString();
        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    public String getGatewayAuthCheckNegotiation() {
        if (this.gateway == null) {
            return "";
        }
        if (this.gateway.containsKey("authcheck")) {
            Object obj = this.gateway.get("authcheck");
            if (obj instanceof Map) {
                if (((Map) obj).containsKey("negotiation")) {
                    return ((Map) obj).get("negotiation").toString();
                }
            }
        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    public String getGatewayAuthCheckOkPtn() {
        if (this.gateway == null) {
            return "";
        }
        if (this.gateway.containsKey("authcheck")) {
            Object obj = this.gateway.get("authcheck");
            if (obj instanceof Map) {
                if (((Map) obj).containsKey("ok")) {
                    return ((Map) obj).get("ok").toString();
                }
            }
        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    public String getGatewayAuthCheckNgPtn() {
        if (this.gateway == null) {
            return "";
        }
        if (this.gateway.containsKey("authcheck")) {
            Object obj = this.gateway.get("authcheck");
            if (obj instanceof Map) {
                if (((Map) obj).containsKey("ng")) {
                    return ((Map) obj).get("ng").toString();
                }
            }
        }
        return "";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("connect                    : %s\n", this.connect));
        builder.append(String.format("negotiation                : %s\n", this.negotiation));
        builder.append(String.format("inifile                    : %s\n", this.inifile));
        builder.append(String.format("loginuser                  : %s\n", this.loginuser));
        builder.append(String.format("loginpassword              : %s\n", this.loginpassword));
        builder.append(String.format("gateway_ipaddress          : %s\n", getGatewayIpaddress()));
        builder.append(String.format("gateway_auth               : %s\n", isGatewayAuth()));
        builder.append(String.format("gateway_password_memory    : %s\n", isGatewayPasswordMemory()));
        builder.append(String.format("gateway_password_autoclear : %s\n", isGatewayPasswordAutoclear()));
        builder.append(String.format("gateway_password_group     : %s", getGatewayPasswordGroup()));
        return builder.toString();
    }

}
