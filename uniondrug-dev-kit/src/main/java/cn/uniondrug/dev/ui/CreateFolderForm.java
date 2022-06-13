package cn.uniondrug.dev.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/6/13
 */
public class CreateFolderForm {

    private JTextField folderText;
    private JPanel rootPanel;

    private Project project;

    public CreateFolderForm(Project project) {
        this.project = project;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getFolderText() {
        return folderText.getText();
    }

}
