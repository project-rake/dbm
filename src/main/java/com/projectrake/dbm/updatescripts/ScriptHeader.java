package com.projectrake.dbm.updatescripts;

import java.util.List;

/**
 * Created on 20.11.2017.
 * <p>
 * This class represents an SQLScript header for later dependency resolution.
 */
public class ScriptHeader {
    private List<Integer> from;
    private List<Integer> to;
    private List<String> dialect;

    public ScriptHeader() {
    }

    public ScriptHeader(List<Integer> from, List<Integer> to, List<String> dialect) {
        this.from = from;
        this.to = to;
        this.dialect = dialect;
    }

    public List<Integer> getFrom() {
        return from;
    }

    public void setFrom(List<Integer> from) {
        this.from = from;
    }

    public List<Integer> getTo() {
        return to;
    }

    public void setTo(List<Integer> to) {
        this.to = to;
    }

    public List<String> getDialect() {
        return dialect;
    }

    public void setDialect(List<String> dialect) {
        this.dialect = dialect;
    }

    @Override
    public String toString() {
        return "ScriptHeader{" +
                "from=" + from +
                ", to=" + to +
                ", dialect=" + dialect +
                '}';
    }
}
