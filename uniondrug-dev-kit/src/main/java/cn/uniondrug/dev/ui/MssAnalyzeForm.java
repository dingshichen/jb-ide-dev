package cn.uniondrug.dev.ui;

import cn.uniondrug.dev.config.DocSetting;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/6/16
 */
public class MssAnalyzeForm {

    private JPanel rootPanel;
    private JLabel workerLabel;
    private JTextField workerField;
    private JTextField projectField;
    private JLabel projectLabel;
    private JLabel authLabel;
    private JTextField authField;

    private Project project;

    private DocSetting docSetting;

    public MssAnalyzeForm(Project project) {
        this.project = project;
        this.docSetting = DocSetting.Companion.getInstance(project);
        init();
    }

    private void init() {
        workerField.setText(docSetting.getState().getMssWorker());
        projectField.setText(docSetting.getState().getMssProjectCode());
        authField.setText(docSetting.getState().getMssToken());
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getWorker() {
        return workerField.getText();
    }

    public String getProjectCode() {
        return projectField.getText();
    }

    public String getToken() {
        return authField.getText();
    }

}
