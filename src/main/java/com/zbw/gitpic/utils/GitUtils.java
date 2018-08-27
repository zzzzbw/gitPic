package com.zbw.gitpic.utils;

import com.zbw.gitpic.exception.AuthorizedException;
import com.zbw.gitpic.exception.TipException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author zbw
 * @create 2018/2/27 20:15
 */
public class GitUtils {

    private static final Logger logger = LoggerFactory.getLogger(GitUtils.class);

    /**
     * 加载git项目
     *
     * @param gitPath
     * @return
     */
    public static Repository init(String gitPath) {
        try {
            return new FileRepositoryBuilder()
                    .setGitDir(findGitRepositoryPath(gitPath))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("初始化git异常");
        }
    }

    /**
     * 获取当前分支
     *
     * @param rep
     * @return
     */
    public static String getBranch(Repository rep) {
        try {
            return rep.getBranch();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("获取Branch异常");
        }
    }

    /**
     * 获取remote URI
     *
     * @param rep
     * @return
     */
    public static String getRemoteUri(Repository rep) {
        Git git = new Git(rep);
        List<RemoteConfig> remoteConfigList;
        try {
            remoteConfigList = git.remoteList().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("获取RemoteUri异常");
        }
        if (null != remoteConfigList && remoteConfigList.size() > 0) {
            if (remoteConfigList.get(0).getURIs().size() <= 0) {
                throw new TipException("该分支不存在远程仓库");
            }
            return remoteConfigList.get(0).getURIs().get(0).toString();
        }
        return "";
    }

    /**
     * 获取验证方式
     *
     * @param uri
     * @return SSH/HTTPS
     */
    public static String authType(String uri) {
        if (uri.contains(Constants.GIT_SSH)) {
            return Constants.GIT_SSH;
        }
        if (uri.contains(Constants.GIT_HTTPS)) {
            return Constants.GIT_HTTPS;
        }
        throw new TipException("不合法的uri");
    }

    /**
     * git commit
     *
     * @param repository
     */
    public static void commitAll(Repository repository) {
        commitAll(repository, Constants.GIT_DEFAULT_COMMIT_MESSAGE);
    }

    /**
     * git commit
     *
     * @param repository
     * @param commitMsg
     */
    public static void commitAll(Repository repository, String commitMsg) {
        Git git = new Git(repository);
        try {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(commitMsg).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("git commit 异常");
        }
    }

    public static void pull(Repository repository) {
        Git git = new Git(repository);
        try {
            PullResult result = git.pull().call();
            FetchResult fetchResult = result.getFetchResult();
            MergeResult mergeResult = result.getMergeResult();
            if (fetchResult.getMessages() != null && !fetchResult.getMessages().isEmpty()) {
                logger.info(fetchResult.getMessages());
            }
            logger.info(mergeResult.getMergeStatus().toString());
            if (!mergeResult.getMergeStatus().isSuccessful()) {
                throw new TipException(mergeResult.getMergeStatus().toString());
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("git commit 异常");
        }
    }

    /**
     * git push
     *
     * @param repository
     */
    public static void push(Repository repository) {
        Git git = new Git(repository);
        try {
            Iterable<PushResult> results = git.push().call();
            PushResult result = results.iterator().next();
            validPushResult(result);
        } catch (GitAPIException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("git push 异常, message:" + e.getMessage());
        }
    }

    /**
     * git push
     *
     * @param repository
     * @param username
     * @param password
     */
    public static void push(Repository repository, String username, String password) {
        Git git = new Git(repository);
        try {
            CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
            Iterable<PushResult> results = git.push().setCredentialsProvider(cp).call();
            PushResult result = results.iterator().next();
            validPushResult(result);
        } catch (TransportException e) {
            logger.error(e.getMessage());
            throw new AuthorizedException("验证失败");
        } catch (GitAPIException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new TipException("git push 异常, message:" + e.getMessage());
        }
    }

    /**
     * 验证push结果
     *
     * @param result
     */
    public static void validPushResult(PushResult result) {
        String msg = "未知原因";
        if (null == result) {
            throw new TipException(("push失败: " + msg));
        }
        RemoteRefUpdate.Status status = result.getRemoteUpdate(Constants.GIT_MASTER_HEAD).getStatus();
        switch (status) {
            case OK:
                return;
            case NOT_ATTEMPTED:
                msg = "Push process hasn't yet attempted to update this ref. This is the default status, prior to push process execution.";
                break;
            case UP_TO_DATE:
                msg = "Remote ref was up to date, there was no need to update anything.";
                break;
            case REJECTED_NONFASTFORWARD:
                msg = "Remote ref update was rejected, as it would cause non fast-forward  update.";
                break;
            case REJECTED_NODELETE:
                msg = "Remote ref update was rejected, because remote side doesn't support/allow deleting refs.";
                break;
            case REJECTED_REMOTE_CHANGED:
                msg = "Remote ref update was rejected, because old object id on remote repository wasn't the same as defined expected old object.";
                break;
            case REJECTED_OTHER_REASON:
                msg = "Remote ref update was rejected for other reason";
                break;
            case NON_EXISTING:
                msg = "Remote ref didn't exist. Can occur on delete request of a non existing ref.";
                break;
            case AWAITING_REPORT:
                msg = "Push process is awaiting update report from remote repository. This is a temporary state or state after critical error in push process.";
                break;
            default:
                msg = "未知原因";
                break;
        }
        throw new TipException("push失败: " + msg);
    }

    /**
     * 获取git文件夹
     *
     * @param projectPath
     * @return
     */
    public static File findGitRepositoryPath(String projectPath) {
        File file = new File(projectPath);
        if (!file.isDirectory()) {
            throw new TipException("git项目必须为文件夹");
        }
        int len = projectPath.length();
        if (Constants.GIT_PATH.equals(projectPath.substring(len - 4, len))) {
            return file;
        } else {//不是.git文件夹则为git项目的文件夹
            projectPath += File.separator + Constants.GIT_PATH;
            file = new File(projectPath);
            if (file.exists() && file.isDirectory()) {
                return file;
            }
        }
        throw new TipException("该目录不存在git项目");
    }

    /**
     * 创建github文件链接
     *
     * @param uri
     * @param branchName
     * @param folder
     * @param fileName
     * @return
     */
    public static String createGitBlobUrl(String uri, String branchName, String folder, String fileName) {

        uri = uri.replace(Constants.GIT_PATH, "");
        if (Constants.GIT_SSH.equals(authType(uri))) {
            uri = uri.replace("git@github.com:", "https://github.com/");
        }
        StringBuilder urlSB = new StringBuilder(uri);
        urlSB.append("/blob/").append(branchName).append(folder).append("/").append(fileName);
        return urlSB.toString().replace("\\", "/");
    }

    /**
     * 创建raw cdn链接
     *
     * @param blobUrl
     * @return
     */
    public static String createGitCdnUrl(String blobUrl) {
        return blobUrl.replace("github", "raw.githubusercontent").replace("blob/", "");
    }


}
