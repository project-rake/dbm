package com.github.projectrake.dbm.api.v1;

/**
 * Created on 20.11.2017.
 * <p>
 * SQLUpdate script descriptor class.
 */
public class SQLScript {
    private final String jarfileSource;
    private final String jarfileEntry;

    /**
     * @param jarfileSource The jarfile this script is contained in.
     * @param jarfileEntry  The jarfile entry that is this script.
     */
    public SQLScript(String jarfileSource, String jarfileEntry) {
        this.jarfileSource = jarfileSource;
        this.jarfileEntry = jarfileEntry;
    }

    public String getJarfileSource() {
        return jarfileSource;
    }

    public String getJarfileEntry() {
        return jarfileEntry;
    }

    @Override
    public String toString() {
        return "SQLScript{" +
                "jarfileSource='" + jarfileSource + '\'' +
                ", jarfileEntry='" + jarfileEntry + '\'' +
                '}';
    }
}
