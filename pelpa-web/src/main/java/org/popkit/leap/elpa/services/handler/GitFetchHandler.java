package org.popkit.leap.elpa.services.handler;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.joda.time.DateTime;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:10:09
 */
@Service
public class GitFetchHandler implements FetchHandler {
    public static final String GITHUB_HTTPS_ROOT = "https://github.com/";
    public static final String GITLAB_HTTPS_ROOT = "https://gitlab.com/";
    public static final int GIT_TIME_OUT = 10 * 60 * 1000;    // 10分钟
    public static final int GIT_TIME_OUT_CLONE = 10 * 60 * 1000;    // 10 minutes

    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {
        if (recipeDo.getFetcherEnum() == FetcherEnum.GITHUB ||
                recipeDo.getFetcherEnum() == FetcherEnum.GITLAB) {
            return true;
        }

        if (recipeDo.getFetcherEnum() == FetcherEnum.GIT
                && (!recipeDo.getUrl().contains("https://gist.github.com"))) {   // non gist.github repo
            return true;
        }

        return false;
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {
        String pkgPath = (String) extra.get("pkgPath");
        File pkgPathFile = new File(pkgPath);
        if (pkgPathFile.exists() && pkgPathFile.isDirectory()
                && new File(pkgPath + "/.git").exists()) {
            doExecute(recipeDo, pkgPath, false);
        } else {
            doExecute(recipeDo, pkgPath, true);
        }
    }



    private void doExecute(RecipeDo recipeDo, String localPathDir, boolean isCreate) {
        // prepare a new folder for the cloned repository
        LeapLogger.info(isCreate ? "github fetch new:" + localPathDir : "github update:" + localPathDir);
        String remote_url = getRemoteGitUrl(recipeDo);
        if (StringUtils.isBlank(remote_url)) {
            LeapLogger.info("remote url is blank, pkgName:" + recipeDo.getPkgName());
            return;
        }

        try {
            File localPath = File.createTempFile(localPathDir, "");
            localPath.delete();
        } catch (Exception e) {
            LeapLogger.warn("error create!");
        }

        // then clone
        LeapLogger.info("Cloning from " + remote_url + " to " + localPathDir);
        try {
            String command = isCreate ? "git clone --depth=1 " + remote_url + " " + localPathDir : "git pull";
            String workingPath = isCreate ? PelpaUtils.getWorkingPath("") : PelpaUtils.getWorkingPath(recipeDo.getPkgName());
            Process p = Runtime.getRuntime().exec(command, null, new File(workingPath));
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuffer result = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result);
        } catch (Exception e) {
            LeapLogger.warn("error create" + localPathDir, e);
        } finally {
            //
        }
    }

    private String getRemoteGitUrl(RecipeDo recipeDo) {
        if (recipeDo.getFetcherEnum() == FetcherEnum.GITHUB) {
            return GITHUB_HTTPS_ROOT + PelpaUtils.unwrap(recipeDo.getRepo()) + ".git";
        } else if (recipeDo.getFetcherEnum() == FetcherEnum.GITLAB) {
            return GITLAB_HTTPS_ROOT + PelpaUtils.unwrap(recipeDo.getRepo()) + ".git";
        } else if (recipeDo.getFetcherEnum() == FetcherEnum.GIT) {
            return recipeDo.getUrl();
        } else  {
            return null;
        }
    }

    public static long getLastCommiterTime(String pkgName) {
        try {
            String workingPath = PelpaUtils.getWorkingPath(pkgName);
            Repository repository = FileRepositoryBuilder.create(new File(workingPath + "/.git"));
            RevWalk revWalk = new RevWalk( repository );
            revWalk.markStart(revWalk.parseCommit(repository.resolve(Constants.HEAD)));
            // revWalk.sort(RevSort.COMMIT_TIME_DESC );
            // revWalk.sort(RevSort.REVERSE, false);
            RevCommit commit = revWalk.next();
            revWalk.dispose();
            return commit.getCommitterIdent().getWhen().getTime();
        } catch (Exception e) {
            LeapLogger.warn("error in getLastCommiterTime", e);
        }
        return 0;
    }

    public static void main(String[] args) throws IOException, InvalidRefNameException, GitAPIException {
        int hour = new DateTime().getHourOfDay();
        System.out.print("hour=" + hour);
    }
}
