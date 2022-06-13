package cn.uniondrug.dev.ui;

import cn.uniondrug.dev.SpaceDTO;
import cn.uniondrug.dev.SpaceService;
import cn.uniondrug.dev.UserService;
import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.util.StringUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.List;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/6/10
 */
public class TornaIndexForm {

    private static final String TOKEN_KEY = "cn.uniondrug.dev.torna.token";

    private JPanel rootPanel;
    private JLabel spaceLable;
    private JComboBox spaceBox;
    private JComboBox projectBox;
    private JLabel projectLable;
    private JComboBox moduleBox;
    private JLabel moduleLable;
    private JLabel folderLable;
    private JComboBox folderBox;

    private Project project;

    public TornaIndexForm(Project project) {
        this.project = project;
        init();
    }

    private void init() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String url = docSetting.getState().getUrl();
        String username = docSetting.getState().getUsername();
        String password = docSetting.getState().getPassword();
        System.out.println("获取到 torna 密码 : " + password);
        if (StringUtil.isAllEmpty(url, username, password)) {
            // TODO 中断
            return;
        }
        PropertiesComponent properties = PropertiesComponent.getInstance();
        String token = properties.getValue(TOKEN_KEY);
        if (StringUtil.isEmpty(token)) {
            UserService loginService = project.getService(UserService.class);
            try {
                token = loginService.login(url, username, password);
            } catch (Exception e) {
                // TODO 中断
                return;
            }
            properties.setValue(TOKEN_KEY, token);
        }
        SpaceService spaceService = project.getService(SpaceService.class);
        List<SpaceDTO> spaces = spaceService.listMySpace(url, token);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

}
