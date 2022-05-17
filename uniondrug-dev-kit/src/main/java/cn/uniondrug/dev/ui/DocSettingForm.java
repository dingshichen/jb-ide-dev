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
    private JLabel tornaTokenLabel;
    private JTextField tornaTokenText;
    private JPanel rootPanel;
    private JLabel authorLabel;
    private JTextField authorText;

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
        if (!StringUtils.equals(state.getToken(), tornaTokenText.getText())) {
            return true;
        }
        if (!StringUtils.equals(state.getAuthor(), authorText.getText())) {
            return true;
        }
        return false;
    }

    public void apply() {

        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        DocSetting.TornaState state = docSetting.getState();
        state.setDomain(domainText.getText());
        state.setUrl(tornaText.getText());
        state.setToken(tornaTokenText.getText());
        state.setAuthor(authorText.getText());
    }

    public void reset() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        DocSetting.TornaState state = docSetting.getState();
        domainText.setText(state.getDomain());
        tornaText.setText(state.getUrl());
        tornaTokenText.setText(state.getToken());
        authorText.setText(state.getAuthor());
    }

}
