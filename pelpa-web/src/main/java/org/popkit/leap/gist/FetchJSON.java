package org.popkit.leap.gist;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-20:22:17
 */
public class FetchJSON {
    private String status;
    private String info;
    private long lastCommit;
    private String lastCommitFormat;
    private String pkgFile;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(long lastCommit) {
        this.lastCommit = lastCommit;
    }

    public String getLastCommitFormat() {
        return lastCommitFormat;
    }

    public void setLastCommitFormat(String lastCommitFormat) {
        this.lastCommitFormat = lastCommitFormat;
    }

    public String getPkgFile() {
        return pkgFile;
    }

    public void setPkgFile(String pkgFile) {
        this.pkgFile = pkgFile;
    }
}
