package com.zbw.gitpic.controller;

import com.jfoenix.controls.*;
import com.zbw.gitpic.exception.AuthorizedException;
import com.zbw.gitpic.exception.TipException;
import com.zbw.gitpic.utils.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * @author zbw
 * @create 2018/2/26 16:36
 */
public class MainController extends StackPane implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    private Stage stage;

    private String uploadImgFilePath;

    private boolean isGitInit = false;

    private Repository repository = null;

    @FXML
    private StackPane root;

    @FXML
    private JFXTextField projectPathTextField;

    @FXML
    private JFXTextField imgPathTextField;

    @FXML
    private JFXTextField rawUrlTextField;

    @FXML
    private JFXButton commitButton;

    @FXML
    private JFXSpinner promptSpinner;

    @FXML
    private Label promptLabel;

    @FXML
    private JFXSpinner gitSpinner;

    @FXML
    private Label gitLabel;

    @FXML
    private JFXDialog dialog;

    @FXML
    private JFXTextField usernameTextField;

    @FXML
    private JFXPasswordField passwordTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.getChildren().remove(dialog); // 先从根组件移除弹窗，否则无法手动初始化弹窗
        stage = new Stage();
        promptSpinner.setVisible(false);
        gitSpinner.setVisible(false);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        String projectPath = Preference.getInstance().getProjectPath();
        if (CastUtil.isNotEmpty(projectPath)) {
            projectPathTextField.setText(projectPath);
            initGit(projectPath);
        }

        imgPathTextField.setText(Preference.getInstance().getPicPath());
    }

    /**
     * 主面板onDragOver事件
     *
     * @param event
     */
    @FXML
    protected void setTransferMode(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * 主面板onDragDropped事件
     *
     * @param event
     */
    @FXML
    protected void setUploadImgPath(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0);
            if (file != null) {
                uploadImgFilePath = file.getAbsolutePath();
            }
        }
        copyAndGenerate();
    }

    /**
     * 选择项目根目录
     */
    @FXML
    protected void chooseProjectPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择项目目录");
        File file = directoryChooser.showDialog(stage);
        if (file == null || !file.isDirectory()) {
            return;
        }
        projectPathTextField.setText(file.getAbsolutePath());
        if (CastUtil.isEmpty(projectPathTextField.getText())) {
            return;
        }
        String projectPath = projectPathTextField.getText();
        Preference.getInstance().saveProjectPath(projectPath);
        initGit(projectPath);
    }


    /**
     * 选择图片保存目录
     */
    @FXML
    protected void chooseImgPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String projectPath = projectPathTextField.getText();
        if (CastUtil.isNotEmpty(projectPath)) {
            File file = new File(projectPath);
            directoryChooser.setInitialDirectory(file);
        }
        directoryChooser.setTitle("选择图片保存目录");
        File file = directoryChooser.showDialog(stage);
        if (file == null || !file.isDirectory()) {
            return;
        }
        imgPathTextField.setText(file.getAbsolutePath());
        Preference.getInstance().savePicPath(imgPathTextField.getText());
    }

    /**
     * 选择要上传的图片
     */
    @FXML
    protected void chooseUploadImg() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (null == file) {
            return;
        }
        uploadImgFilePath = file.getAbsolutePath();
        copyAndGenerate();
    }

    /**
     * 从剪贴板复制
     */
    @FXML
    protected void copyByClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            Image image = clipboard.getImage();
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            try {
                Path tempDirectory = Files.createTempDirectory(Constants.GITPIC_PREFIX);
                String tempFile = tempDirectory.toString() + File.separator + Constants.GITPIC_PREFIX + System.currentTimeMillis() + ".png";
                File file = new File(tempFile);
                ImageIO.write(bImage, "png", file);
                uploadImgFilePath = file.getAbsolutePath();
                copyAndGenerate();
                file.delete();// 删除临时图片
                Files.delete(tempDirectory);//删除临时目录
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                showErrorMessage("从剪切板拷贝图片异常", e);
            }
        } else {
            showErrorMessage("剪切板中没有图片");
        }
    }

    /**
     * commit push 按钮
     */
    @FXML
    protected void commitAndPush() {
        commitButton.setDisable(true);
        ThreadPool.getInstance().execute(() -> {
            try {
                showNormalMessage("commit中...");
                GitUtils.commitAll(repository);
                showSuccessMessage("commit成功!");
                pushGit();
            } catch (TipException e) {
                showErrorMessage(e);
                commitButton.setDisable(false);
            }
        });

    }

    /**
     * Dialog面板Accept
     */
    @FXML
    protected void dialogAccept() {
        if (!usernameTextField.validate() || !passwordTextField.validate()) {
            return;
        }
        String username = this.usernameTextField.getText();
        String password = this.passwordTextField.getText();
        Preference.getInstance().saveGitUsername(username);
        Preference.getInstance().saveGitPassword(password);
        pushGit();
        dialog.close();
    }

    /**
     * Dialog面板Cancel
     */
    @FXML
    protected void dialogCancel() {
        dialog.close();
        commitButton.setDisable(false);
        showErrorMessage("push到github失败");
    }

    /**
     * 加载Git项目
     *
     * @param projectPath
     */
    private void initGit(String projectPath) {
        ThreadPool.getInstance().execute(() -> {
            try {
                Platform.runLater(() -> {
                    gitSpinner.setVisible(true);
                    gitLabel.setText("初始化git项目中...");
                });
                repository = GitUtils.init(projectPath);
                isGitInit = true;
                Platform.runLater(() -> {
                    gitSpinner.setVisible(false);
                    gitLabel.setText("项目已加载");
                    gitLabel.getStyleClass().add("text-success");
                });

                showPromptSpinner();
                showNormalMessage("从Github中pull项目...");
                GitUtils.pull(repository);
                hidePromptSpinner();
                showNormalMessage("Pull项目成功");
                commitButton.setDisable(false);
            } catch (TipException e) {
                showErrorMessage(e);
                Platform.runLater(() -> {
                    gitSpinner.setVisible(false);
                    gitLabel.setText("");
                });
            }
        });
    }

    /**
     * 复制文件到git项目下并生成raw链接
     */
    private void copyAndGenerate() {
        if (!isGitInit) {
            showErrorMessage("请先初始化git");
            return;
        }
        try {
            copyToProject();
        } catch (TipException e) {
            showErrorMessage(e);
            return;
        }
        try {
            generateGitRawPath();
        } catch (TipException e) {
            showErrorMessage(e);
        }
    }

    /**
     * 复制图片到git项目下
     */
    private void copyToProject() {
        if (CastUtil.isEmpty(imgPathTextField.getText())) {
            throw new TipException("请先设置保存图片文件夹");
        }
        File file = new File(imgPathTextField.getText());
        if (!file.isDirectory() || !file.exists()) {
            throw new TipException("保存图片文件夹路径不存在");
        }
        File pic = new File(uploadImgFilePath);
        if (!pic.exists() || !pic.isFile()) {
            throw new TipException("保存图片文件夹路径不存在");
        }
        String gitImgPath = imgPathTextField.getText() + File.separator + pic.getName();
        File gitPic = new File(gitImgPath);
        try {
            Files.copy(pic.toPath(), gitPic.toPath());
        } catch (FileAlreadyExistsException e) {
            throw new TipException("项目中有相同文件名文件");
        } catch (IOException e) {
            e.printStackTrace();
            throw new TipException("复制文件失败");
        }
    }

    /**
     * 生成raw链接
     */
    private void generateGitRawPath() {
        File pic = new File(uploadImgFilePath);
        if (!pic.exists() || pic.isDirectory()) {
            throw new TipException("请选择正确的图片文件");
        }
        if (null == repository) {
            throw new TipException("请先初始化git项目");
        }
        String uri = GitUtils.getRemoteUri(repository);
        String branch = GitUtils.getBranch(repository);
        String folder = imgPathTextField.getText().replace(projectPathTextField.getText(), "");
        String name = uploadImgFilePath.substring(uploadImgFilePath.lastIndexOf(File.separator) + 1);
        String url = GitUtils.createGitBlobUrl(uri, branch, folder, name);
        rawUrlTextField.setText(GitUtils.createGitCdnUrl(url));
        rawUrlTextField.requestFocus();
        rawUrlTextField.selectAll();
        rawUrlTextField.copy();
        showSuccessMessage("已复制图片路径到剪切板");
    }

    /**
     * push到git
     */
    private void pushGit() {
        ThreadPool.getInstance().execute(() -> {
            showPromptSpinner();
            showNormalMessage("push到github中...");
            try {
                String uri = GitUtils.getRemoteUri(repository);
                if (Constants.GIT_SSH.equals(GitUtils.authType(uri))) {
                    GitUtils.push(repository);
                    showSuccessMessage("push成功!");
                    hidePromptSpinner();
                    commitButton.setDisable(false);
                } else {
                    String username = Preference.getInstance().getGitUsername();
                    String password = Preference.getInstance().getGitPassword();
                    if (CastUtil.isNotEmpty(username) && CastUtil.isNotEmpty(password)) {
                        try {
                            GitUtils.push(repository, username, password);
                            showSuccessMessage("push成功!");
                            hidePromptSpinner();
                        } catch (AuthorizedException e) {
                            showErrorMessage(e);
                            hidePromptSpinner();
                            Platform.runLater(() -> dialog.show(root));
                        }
                        commitButton.setDisable(false);
                    } else {
                        Platform.runLater(() -> dialog.show(root));
                    }
                }
            } catch (TipException e) {
                commitButton.setDisable(false);
                hidePromptSpinner();
                showErrorMessage(e);
            }
        });
    }

    /**
     * 显示成功信息
     *
     * @param msg
     */
    private void showSuccessMessage(String msg) {
        this.setGitMessageLabel(msg, "text-success");
    }

    /**
     * 显示错误信息
     *
     * @param msg
     */
    private void showErrorMessage(String msg) {
        this.setGitMessageLabel(msg, "text-error");
    }

    /**
     * 显示异常的信息
     *
     * @param e
     */
    private void showErrorMessage(Exception e) {
        this.setGitMessageLabel(e.getMessage(), "text-dark");
    }

    /**
     * 显示带异常信息的错误信息
     *
     * @param msg
     * @param e
     */
    private void showErrorMessage(String msg, Exception e) {
        this.setGitMessageLabel(msg + e.getMessage(), "text-error");
    }

    /**
     * 显示提示信息
     *
     * @param msg
     */
    private void showNormalMessage(String msg) {
        this.setGitMessageLabel(msg, "text-dark");
    }

    /**
     * 显示信息
     *
     * @param msg
     * @param styleClass
     */
    private void setGitMessageLabel(String msg, String... styleClass) {
        Platform.runLater(() -> {
            promptLabel.getStyleClass().clear();
            promptLabel.getStyleClass().addAll(styleClass);
            promptLabel.setText(msg);
        });
    }

    private void showPromptSpinner() {
        Platform.runLater(() -> promptSpinner.setVisible(true));
    }

    private void hidePromptSpinner() {
        Platform.runLater(() -> promptSpinner.setVisible(false));
    }
}
