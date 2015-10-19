package jp.co.tabocom.teratermstation.model;

import java.io.Serializable;

public class Gateway implements Serializable {
    private static final long serialVersionUID = -2266986365671637916L;

    private String gwIpAddr;
    private String errPtn;
    private boolean auth;
    private boolean memoryPwd;
    private boolean pwdAutoClear;
    private String pwdGroup;

    public String getGwIpAddr() {
        return gwIpAddr;
    }

    public void setGwIpAddr(String gwIpAddr) {
        this.gwIpAddr = gwIpAddr;
    }

    public String getErrPtn() {
        return errPtn;
    }

    public void setErrPtn(String errPtn) {
        this.errPtn = errPtn;
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

}
