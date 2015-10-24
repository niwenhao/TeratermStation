package jp.co.tabocom.teratermstation.model.yaml;

import java.util.Map;

public class SettingsIni {
    private int width;
    private int height;
    private String inifile;
    private Map<String, Object> initial;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getInifile() {
        return inifile;
    }

    public void setInifile(String inifile) {
        this.inifile = inifile;
    }

    public Map<String, Object> getInitial() {
        return initial;
    }

    public void setInitial(Map<String, Object> initial) {
        this.initial = initial;
    }

    public String getInitialTtpmacroexe() {
        return this.initial.get("ttpmacroexe").toString();
    }

    public String getInitialDirWork() {
        return this.initial.get("dir_work").toString();
    }

    public String getInitialDirLog() {
        return this.initial.get("dir_log").toString();
    }

    public String getInitialDirIni() {
        return this.initial.get("dir_ini").toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("width               : %d\n", this.width));
        builder.append(String.format("height              : %d\n", this.height));
        builder.append(String.format("initial_ttpmacroexe : %s\n", getInitialTtpmacroexe()));
        builder.append(String.format("initial_dir_work    : %s\n", getInitialDirWork()));
        builder.append(String.format("initial_dir_log     : %s\n", getInitialDirLog()));
        builder.append(String.format("initial_dir_ini     : %s\n", getInitialDirIni()));
        return builder.toString();
    }
}
