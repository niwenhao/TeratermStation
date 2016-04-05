package jp.co.tabocom.teratermstation.plugin;

import org.eclipse.jface.preference.PreferenceStore;

public abstract class TeratermStationLifecycle {

    private PreferenceStore preferenceStore;

    public TeratermStationLifecycle(PreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }

    /**
     * Pluginロード後の初期処理です。
     */
    public abstract void initialize() throws Exception;

    /**
     * TeratermStation終了時の処理です。
     * 
     * @param preferenceStore
     */
    public abstract void teminate() throws Exception;

}
