package cn.uniondrug.dev.ui;

import cn.hutool.core.util.StrUtil;
import cn.uniondrug.dev.*;
import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.config.TornaKeyService;
import cn.uniondrug.dev.dialog.CreateFolderDialog;
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
    private JButton createFolderButton;

    private Project project;

    private TornaKeyService tornaKeyService;

    private Api api;

    public TornaIndexForm(Project project, Api api) {
        this.project = project;
        this.api = api;
        this.tornaKeyService = TornaKeyService.Companion.getInstance(project);
        initListener();
        initValue();
    }

    private void initListener() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String rememberProjectBoxId = docSetting.getState().getRememberProjectBoxId();
        String rememberModuleBoxId = docSetting.getState().getRememberModuleBoxId();
        String url = docSetting.getState().getUrl();
        String token = tornaKeyService.getToken(project, url, docSetting);
        createFolderButton.addActionListener(e -> {
            CreateFolderDialog dialog = new CreateFolderDialog(project);
            if (dialog.showAndGet()) {
                DocumentService documentService = project.getService(DocumentService.class);
                documentService.saveFolder(url, token, moduleBox.getItem().getId(), dialog.getFolder());
                List<DocumentDTO> docs = documentService.listFolderByModule(url, token, moduleBox.getItem().getId());
                docs.stream()
                        .filter(folder -> folder.getName().equals(dialog.getFolder()))
                        .findFirst()
                        .ifPresent(folder -> {
                            folderBox.addItem(folder);
                            folderBox.setItem(folder);
                        });
            }
        });
        spaceBox.addItemListener(s -> {
            if (s.getStateChange() == ItemEvent.SELECTED) {
                ProjectService projectService = project.getService(ProjectService.class);
                List<ProjectDTO> projects = projectService.listProjectBySpace(url, token, ((SpaceDTO) s.getItem()).getId());
                projectBox.removeAllItems();
                moduleBox.removeAllItems();
                folderBox.removeAllItems();
                projects.forEach(p -> projectBox.addItem(p));
                if (StrUtil.isNotBlank(rememberProjectBoxId)) {
                    projects.stream()
                            .filter(p -> p.getId().equals(rememberProjectBoxId))
                            .findFirst()
                            .ifPresent(p -> projectBox.setItem(p));
                }
            }
        });
        projectBox.addItemListener(p -> {
            if (p.getStateChange() == ItemEvent.SELECTED) {
                ModuleService moduleService = project.getService(ModuleService.class);
                List<ModuleDTO> modules = moduleService.listModuleByProject(url, token, ((ProjectDTO) p.getItem()).getId());
                moduleBox.removeAllItems();
                folderBox.removeAllItems();
                modules.forEach(m -> moduleBox.addItem(m));
                if (StrUtil.isNotBlank(rememberModuleBoxId)) {
                    modules.stream()
                            .filter(m -> m.getId().equals(rememberModuleBoxId))
                            .findFirst()
                            .ifPresent(m -> moduleBox.setItem(m));
                }
            }
        });
        moduleBox.addItemListener(m -> {
            if (m.getStateChange() == ItemEvent.SELECTED) {
                DocumentService documentService = project.getService(DocumentService.class);
                List<DocumentDTO> docs = documentService.listFolderByModule(url, token, ((ModuleDTO) m.getItem()).getId());
                folderBox.removeAllItems();
                docs.forEach(d -> folderBox.addItem(d));
                if (StrUtil.isNotBlank(api.getFolder())) {
                    docs.stream()
                            .filter(d -> d.getName().equals(api.getFolder()))
                            .findFirst()
                            .ifPresent(d -> folderBox.setItem(d));
                }
            }
        });
    }

    private void initValue() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String url = docSetting.getState().getUrl();
        String token = tornaKeyService.getToken(project, url, docSetting);
        SpaceService spaceService = project.getService(SpaceService.class);
        List<SpaceDTO> spaces = spaceService.listMySpace(url, token);
        spaceBox.removeAllItems();
        spaces.forEach(e -> spaceBox.addItem(e));
        String rememberSpaceBoxId = docSetting.getState().getRememberSpaceBoxId();
        if (StrUtil.isNotBlank(rememberSpaceBoxId)) {
            spaces.stream()
                    .filter(e -> e.getId().equals(rememberSpaceBoxId))
                    .findFirst()
                    .ifPresent(e -> spaceBox.setItem(e));
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getSpaceId() {
        return spaceBox.getItem().getId();
    }

    public String getProjectId() {
        return projectBox.getItem().getId();
    }

    public String getModuleId() {
        return moduleBox.getItem().getId();
    }

    public String getFolderId() {
        return folderBox.getItem().getId();
    }
}
