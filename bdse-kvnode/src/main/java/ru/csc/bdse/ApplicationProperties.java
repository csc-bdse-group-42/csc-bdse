package ru.csc.bdse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("bdse")
public class ApplicationProperties {
    private String dbfile;
    private String nodes;
    private int nodeTimeout;
    private int nodeWCL;
    private int noreRCL;

    public String getDbfile() {
        return dbfile;
    }

    public void setDbfile(String dbfile) {
        this.dbfile = dbfile;
    }

    public String[] getNodes() {
        // Splits by whitespace and commas.
        // Example: "test, test2,test3" => { "test", "test2", "test3" }
        return this.nodes.split("(\\s|,)+");
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public int getNodeTimeout() {
        return nodeTimeout;
    }

    public void setNodeTimeout(int nodeTimeout) {
        this.nodeTimeout = nodeTimeout;
    }

    public int getNodeWCL() {
        return nodeWCL;
    }

    public void setNodeWCL(int nodeWCL) {
        this.nodeWCL = nodeWCL;
    }

    public int getNoreRCL() {
        return noreRCL;
    }

    public void setNoreRCL(int noreRCL) {
        this.noreRCL = noreRCL;
    }
}
