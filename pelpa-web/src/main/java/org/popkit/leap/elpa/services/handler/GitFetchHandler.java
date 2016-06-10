package org.popkit.leap.elpa.services.handler;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
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

import java.io.File;
import java.io.IOException;
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
    public static final int GIT_TIME_OUT = 60 * 1000;    // 60 seconds
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
            update(pkgPath);
        } else {
            create(recipeDo, pkgPath);
        }
    }

    private void update(String localPath) {
        try {
            LeapLogger.info("github fetch update:" + localPath);
            Repository repository = FileRepositoryBuilder.create(new File(localPath + "/.git"));
            //System.out.println("Starting fetch");

            Git git = new Git(repository);
            PullCommand pullCommand = git.pull();
            pullCommand.setTimeout(GIT_TIME_OUT);
            PullResult result = pullCommand.call();

            // http://stackoverflow.com/questions/13399990/usage-of-pull-command-in-jgit
            //FetchResult fetchResult = result.getFetchResult();
            //MergeResult mergeResult = result.getMergeResult();
            //mergeResult.getMergeStatus();  // this should be interesting

            //FetchResult result = git.fetch().setCheckFetchedObjects(true).call();
            //System.out.println("Messages: " + result.getMessages());
        } catch (Exception e) {
            LeapLogger.warn("error update" + localPath, e);
        }
    }

    private void create(RecipeDo recipeDo, String localPathDir) {
        // prepare a new folder for the cloned repository
        LeapLogger.info("github fetch new:" + localPathDir);
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
            Git result = Git.cloneRepository()
                    .setCloneSubmodules(true)
                    .setTimeout(GIT_TIME_OUT_CLONE)
                    .setURI(remote_url)
                    .setDirectory(new File(localPathDir))
                    .call();
            LeapLogger.info("Having repository: " + result.getRepository().getDirectory());
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            result.close();
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
            revWalk.markStart( revWalk.parseCommit(repository.resolve(Constants.HEAD)));
            // revWalk.sort(RevSort.COMMIT_TIME_DESC );
            // revWalk.sort(RevSort.REVERSE, false);
            RevCommit commit = revWalk.next();
            revWalk.dispose();
            return commit.getCommitterIdent().getWhen().getTime();
        } catch (Exception e) {
            LeapLogger.warn("error", e);
        }
        return 0;
    }

    public static void main(String[] args) throws IOException, InvalidRefNameException, GitAPIException {
        int hour = new DateTime().getHourOfDay();
        System.out.print("hour=" + hour);
    }
}
