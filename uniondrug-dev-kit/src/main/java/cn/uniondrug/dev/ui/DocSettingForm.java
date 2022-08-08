package cn.uniondrug.dev.ui;

import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.config.TornaKeyService;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/3/29
 */
public class DocSettingForm {

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
        DocSetting docSetting = DocSetting.instance(project);
        DocSetting.TornaState state = docSetting.getState();
        if (!StringUtils.equals(state.getUsername(), tornaUsernameText.getText())) {
            return true;
        }
        TornaKeyService tornaKeyService = TornaKeyService.instance(project);
        if (!StringUtils.equals(tornaKeyService.getPassword(), new String(tornaPasswordField.getPassword()))) {
            return true;
        }
        return false;
    }

    public void apply() {
        DocSetting docSetting = DocSetting.instance(project);
        DocSetting.TornaState state = docSetting.getState();
        state.setUsername(tornaUsernameText.getText());

        TornaKeyService tornaKeyService = TornaKeyService.instance(project);
        tornaKeyService.setPassword(new String(tornaPasswordField.getPassword()));
    }

    public void reset() {
        DocSetting docSetting = DocSetting.instance(project);
        DocSetting.TornaState state = docSetting.getState();
        tornaUsernameText.setText(state.getUsername());

        TornaKeyService tornaKeyService = TornaKeyService.instance(project);
        tornaPasswordField.setText(tornaKeyService.getPassword());
    }

}
