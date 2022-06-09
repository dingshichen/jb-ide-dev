package cn.uniondrug.dev.ui;

import cn.uniondrug.dev.config.DocSetting;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/3/29
 */
public class DocSettingForm {

    private JLabel domainLabel;
    private JTextField domainText;
    private JLabel tornaLabel;
    private JTextField tornaText;
    private JLabel tornaUsernameLabel;
    private JTextField tornaUsernameText;
    private JPanel rootPanel;
    private JLabel tornaPasswordLabel;
    private JPasswordField tornaPasswordField;

    private Project project;

    public DocSettingForm(Project project) {
        this.project = project;
    }

    public JComponent getRootPanel() {
        return rootPanel;
    }

    public boolean isModified() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        DocSetting.TornaState state = docSetting.getState();
        if (!StringUtils.equals(state.getDomain(), domainText.getText())) {
            return true;
        }
        if (!StringUtils.equals(state.getUrl(), tornaText.getText())) {
            return true;
        }
        if (!StringUtils.equals(state.getUsername(), tornaUsernameText.getText())) {
            return true;
        }
        if (!StringUtils.equals(state.getPassword(), new String(tornaPasswordField.getPassword()))) {
            return true;
        }
        return false;
    }

    public void apply() {

        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        DocSetting.TornaState state = docSetting.getState();
        state.setDomain(domainText.getText());
        state.setUrl(tornaText.getText());
        state.setUsername(tornaUsernameText.getText());
        state.setPassword(new String(tornaPasswordField.getPassword()));
    }

    public void reset() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        DocSetting.TornaState state = docSetting.getState();
        domainText.setText(state.getDomain());
        tornaText.setText(state.getUrl());
        tornaUsernameText.setText(state.getUsername());
        tornaPasswordField.setText(state.getPassword());
    }

}
