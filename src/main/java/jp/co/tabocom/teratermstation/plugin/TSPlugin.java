package jp.co.tabocom.teratermstation.plugin;

import java.util.List;

import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferencePage;

public class TSPlugin {

    private String name;

    private String version;

    private TeratermStationLifecycle lifecycle;

    private List<MenuManager> subMenuList;

    private List<TeratermStationAction> actionList;

    private List<TeratermStationBulkAction> bulkActionList;

    private PreferencePage preferencePage;

    public TSPlugin(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public TeratermStationLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(TeratermStationLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public List<MenuManager> getSubMenuList() {
        return subMenuList;
    }

    public void setSubMenuList(List<MenuManager> subMenuList) {
        this.subMenuList = subMenuList;
    }

    public List<TeratermStationAction> getActionList() {
        return actionList;
    }

    public void setActionList(List<TeratermStationAction> actionList) {
        this.actionList = actionList;
    }

    public List<TeratermStationBulkAction> getBulkActionList() {
        return bulkActionList;
    }

    public void setBulkActionList(List<TeratermStationBulkAction> bulkActionList) {
        this.bulkActionList = bulkActionList;
    }

    public PreferencePage getPreferencePage() {
        return preferencePage;
    }

    public void setPreferencePage(PreferencePage preferencePage) {
        this.preferencePage = preferencePage;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

}
