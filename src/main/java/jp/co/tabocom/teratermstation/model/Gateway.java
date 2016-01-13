package jp.co.tabocom.teratermstation.model;

import java.io.Serializable;

/**
 * プロキシ認証などに関する情報を保持するクラスです。<br>
 * 
 * @author turbou
 * 
 */
public class Gateway implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * プロキシ認証などゲートウェイのIPアドレス
     */
    private String gwIpAddr;
    /**
     * 認証の必要なゲートウェイか否か
     */
    private boolean auth;
    /**
     * 認証パスワードの記録を可能とするか否か
     */
    private boolean memoryPwd;
    /**
     * PCがロックされた場合に認証パスワードを自動でクリアするか否か
     */
    private boolean pwdAutoClear;
    /**
     * 他のタブと認証パスワードを共有する場合の共有識別子
     */
    private String pwdGroup;
    /**
     * 認証チェックのためのやり取り（任意）
     */
    private String authcheck;

    /**
     * 認証チェックのためのやり取りのOKパターン（任意）
     */
    private String authcheckOkPtn;

    /**
     * 認証チェックのためのやり取りNGパターン（任意）
     */
    private String authcheckNgPtn;

    public String getGwIpAddr() {
        return gwIpAddr;
    }

    public void setGwIpAddr(String gwIpAddr) {
        this.gwIpAddr = gwIpAddr;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isMemoryPwd() {
        return memoryPwd;
    }

    public void setMemoryPwd(boolean memoryPwd) {
        this.memoryPwd = memoryPwd;
    }

    public boolean isPwdAutoClear() {
        return pwdAutoClear;
    }

    public void setPwdAutoClear(boolean pwdAutoClear) {
        this.pwdAutoClear = pwdAutoClear;
    }

    public String getPwdGroup() {
        return pwdGroup;
    }

    public void setPwdGroup(String pwdGroup) {
        this.pwdGroup = pwdGroup;
    }

    public String getAuthcheck() {
        return authcheck;
    }

    public void setAuthcheck(String authcheck) {
        this.authcheck = authcheck;
    }

    public String getAuthcheckOkPtn() {
        return authcheckOkPtn;
    }

    public void setAuthcheckOkPtn(String authcheckOkPtn) {
        this.authcheckOkPtn = authcheckOkPtn;
    }

    public String getAuthcheckNgPtn() {
        return authcheckNgPtn;
    }

    public void setAuthcheckNgPtn(String authcheckNgPtn) {
        this.authcheckNgPtn = authcheckNgPtn;
    }

}
