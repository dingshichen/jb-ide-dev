package cn.uniondrug.dev.ui;

import cn.uniondrug.dev.*;
import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.config.TornaPasswordService;
import cn.uniondrug.dev.util.StringUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.ItemEvent;
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
    private JLabel projectLable;
    private JLabel moduleLable;
    private JLabel folderLable;
    private ComboBox<SpaceDTO> spaceBox;
    private ComboBox<ProjectDTO> projectBox;
    private ComboBox<ModuleDTO> moduleBox;
    private ComboBox<DocumentDTO> folderBox;

    private Project project;

    public TornaIndexForm(Project project) {
        this.project = project;
        initListener();
        initValue();
    }

    private void initListener() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String url = docSetting.getState().getUrl();
        String token = getToken(url, docSetting);
        spaceBox.addItemListener(s -> {
            if (s.getStateChange() == ItemEvent.SELECTED) {
                ProjectService projectService = project.getService(ProjectService.class);
                List<ProjectDTO> projects = projectService.listProjectBySpace(url, token, ((SpaceDTO) s.getItem()).getId());
                projectBox.removeAllItems();
                moduleBox.removeAllItems();
                folderBox.removeAllItems();
                projects.forEach(p -> projectBox.addItem(p));
            }
        });
        projectBox.addItemListener(p -> {
            if (p.getStateChange() == ItemEvent.SELECTED) {
                ModuleService moduleService = project.getService(ModuleService.class);
                List<ModuleDTO> modules = moduleService.listModuleByProject(url, token, ((ProjectDTO) p.getItem()).getId());
                moduleBox.removeAllItems();
                folderBox.removeAllItems();
                modules.forEach(m -> moduleBox.addItem(m));
            }
        });
        moduleBox.addItemListener(m -> {
            if (m.getStateChange() == ItemEvent.SELECTED) {
                DocumentService documentService = project.getService(DocumentService.class);
                List<DocumentDTO> docs = documentService.listFolderByModule(url, token, ((ModuleDTO) m.getItem()).getId());
                folderBox.removeAllItems();
                docs.forEach(d -> folderBox.addItem(d));
            }
        });
    }

    private void initValue() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String url = docSetting.getState().getUrl();
        String token = getToken(url, docSetting);
        SpaceService spaceService = project.getService(SpaceService.class);
        List<SpaceDTO> spaces = spaceService.listMySpace(url, token);
        spaceBox.removeAllItems();
        spaces.forEach(e -> spaceBox.addItem(e));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private String getToken(String url, DocSetting docSetting) {
        String username = docSetting.getState().getUsername();
        TornaPasswordService tornaPasswordService = TornaPasswordService.Companion.getInstance(project);
        String password = tornaPasswordService.getPassword();
        if (StringUtil.isAnyEmpty(url, username, password)) {
            // TODO 需要异常 中断
            return null;
        }
        PropertiesComponent properties = PropertiesComponent.getInstance();
        String token = properties.getValue(TOKEN_KEY);
        if (StringUtil.isEmpty(token)) {
            UserService loginService = project.getService(UserService.class);
            try {
                token = loginService.login(url, username, password);
            } catch (Exception e) {
                // TODO 中断
                return null;
            }
            properties.setValue(TOKEN_KEY, token);
        }
        return token;
    }

}
