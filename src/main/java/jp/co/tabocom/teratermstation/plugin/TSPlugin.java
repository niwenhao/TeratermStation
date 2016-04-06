package jp.co.tabocom.teratermstation.plugin;

import java.util.ArrayList;
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
        this.subMenuList = new ArrayList<MenuManager>();
        this.actionList = new ArrayList<TeratermStationAction>();
        this.bulkActionList = new ArrayList<TeratermStationBulkAction>();
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
    
    public void addSubmenu(MenuManager subMenu) {
        this.subMenuList.add(subMenu);
    }

    public List<TeratermStationAction> getActionList() {
        return actionList;
    }

    public void setActionList(List<TeratermStationAction> actionList) {
        this.actionList = actionList;
    }
    
    public void addAction(TeratermStationAction action) {
        this.actionList.add(action);
    }

    public List<TeratermStationBulkAction> getBulkActionList() {
        return bulkActionList;
    }

    public void setBulkActionList(List<TeratermStationBulkAction> bulkActionList) {
        this.bulkActionList = bulkActionList;
    }
    
    public void addBulkAction(TeratermStationBulkAction bulkAction) {
        this.bulkActionList.add(bulkAction);
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
