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
 * auth:
 *   memory: true
 *   autoclear: true
 *   group: oyoyo
 *   check: |
 *     show -1
 *     connect '192.177.237.12 /nossh /T=1 /f="${inifile}"'
 *     showtt -1
 *     wait 'Host name:'
 *     sendln '192.177.238.204'
 *     wait 'Username:'
 *     sendln '${authuser}'
 *     wait 'Password:'
 *     sendln '${authpassword}'
 *     wait 'login:' 'autentication failed'
 *     if result=1 then
 *         messagebox '認証に成功しました。' '認証成功'
 *     endif
 *     if result=2 then
 *         messagebox 'ユーザーまたはパスワードが正しくないようです。' '認証エラー'
 *     endif
 *     closett
 *     end
 * 
 * connect: |
 *   connect '${gateway_ipaddress} /nossh /T=1 /f="${inifile}"'
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
    private Map<String, Object> auth;
    /**
     * Tera Termのconnect文（必須）
     */
    private String connect;
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

    public Map<String, Object> getAuth() {
        return auth;
    }

    public void setAuth(Map<String, Object> auth) {
        this.auth = auth;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
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

    public boolean isAuthMemory() {
        if (this.auth == null) {
            return false;
        }
        if (this.auth.containsKey("memory")) {
            return Boolean.valueOf(this.auth.get("memory").toString());
        }
        return false;
    }

    public boolean isAuthAutoclear() {
        if (this.auth == null) {
            return false;
        }
        if (this.auth.containsKey("autoclear")) {
            return Boolean.valueOf(this.auth.get("autoclear").toString());
        }
        return false;
    }

    public String getAuthGroup() {
        if (this.auth == null) {
            return "";
        }
        if (this.auth.containsKey("group")) {
            return this.auth.get("group").toString();
        }
        return "";
    }

    public String getAuthCheck() {
        if (this.auth == null) {
            return null;
        }
        if (this.auth.containsKey("check")) {
            return this.auth.get("check").toString();
        }
        return null;
    }

    public String validate() {
        StringBuilder builder = new StringBuilder();
        if (this.connect == null || this.connect.isEmpty()) {
            builder.append("- connect: がありません。");
        }
        if (builder.length() > 0) {
            return builder.toString();
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("connect        : \n%s\n", this.connect));
        builder.append(String.format("inifile        : %s\n", this.inifile));
        builder.append(String.format("loginuser      : %s\n", this.loginuser));
        builder.append(String.format("loginpassword  : %s\n", this.loginpassword));
        builder.append(String.format("auth           : %s\n", this.auth == null ? "false" : "true"));
        builder.append(String.format("auth_memory    : %s\n", isAuthMemory()));
        builder.append(String.format("auth_autoclear : %s\n", isAuthAutoclear()));
        builder.append(String.format("auth_group     : %s\n", getAuthGroup()));
        builder.append(String.format("auth_check     : \n%s", getAuthCheck()));
        return builder.toString();
    }

}
