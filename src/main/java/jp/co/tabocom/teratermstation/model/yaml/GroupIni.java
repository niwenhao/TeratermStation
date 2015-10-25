package jp.co.tabocom.teratermstation.model.yaml;

/**
 * 
 * group.yamlを読み込むためのクラスです。<br>
 * <br>
 * group.yamlの内容は下な感じ<br>
 * 
 * <pre>
 * --------------------------------------------------------------------------------
 * loginuser: kibanusr
 * loginpassword: kibanpwd
 * usemacro: false
 * inifile: AWS.INI
 * --------------------------------------------------------------------------------
 * </pre>
 * 
 * @author turbou
 *
 */
public class GroupIni {
    /**
     * マクロ機能を使用するか否か（任意）<br>
     * 省略時は上の階層の条件に従う。上に設定がない場合はfalse
     */
    private String usemacro;
    /**
     * サーバグループ全体に適用されるTera TermのINIファイル（任意）
     */
    private String inifile;
    /**
     * サーバグループ全体に適用されるサーバログインユーザー（任意）
     */
    private String loginuser;
    /**
     * サーバグループ全体に適用されるサーバログインパスワード（任意）
     */
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
