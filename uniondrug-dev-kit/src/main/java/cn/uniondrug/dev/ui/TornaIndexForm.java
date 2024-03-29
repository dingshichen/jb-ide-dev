package cn.uniondrug.dev.ui;

import cn.hutool.core.util.StrUtil;
import cn.uniondrug.dev.*;
import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.config.TornaKeyService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/6/10
 */
public class TornaIndexForm {

    private JPanel rootPanel;
    private JLabel spaceLable;
    private JLabel projectLable;
    private JLabel moduleLable;
    private ComboBox<TornaSpaceDTO> spaceBox;
    private ComboBox<TornaProjectDTO> projectBox;
    private ComboBox<TornaModuleDTO> moduleBox;

    private Project project;

    private TornaKeyService tornaKeyService;

    private DocSetting docSetting;

    /**
     * 目标目录
     */
    private String targetFolder;

    /**
     * 匹配到的目录ID
     */
    @Nullable
    private String folderId;

    private final Function0<String> refreshToken = () -> tornaKeyService.refreshToken(project, docSetting);

    public TornaIndexForm(Project project, String targetFolder) {
        this.project = project;
        this.targetFolder = targetFolder;
        this.tornaKeyService = TornaKeyService.instance(project);
        this.docSetting = DocSetting.instance(project);
        initListener();
        initValue();
    }

    private void initListener() {
        String rememberProjectBoxId = docSetting.getState().getRememberProjectBoxId();
        String rememberModuleBoxId = docSetting.getState().getRememberModuleBoxId();
        String token = tornaKeyService.getToken(project, docSetting);
        spaceBox.addItemListener(s -> {
            if (s.getStateChange() == ItemEvent.SELECTED) {
                TornaProjectService tornaProjectService = project.getService(TornaProjectService.class);
                List<TornaProjectDTO> projects = tornaProjectService.listProjectBySpace(token, ((TornaSpaceDTO) s.getItem()).getId(), refreshToken);
                projectBox.removeAllItems();
                moduleBox.removeAllItems();
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
                TornaModuleService tornaModuleService = project.getService(TornaModuleService.class);
                List<TornaModuleDTO> modules = tornaModuleService.listModuleByProject(token, ((TornaProjectDTO) p.getItem()).getId(), refreshToken);
                moduleBox.removeAllItems();
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
                TornaDocService tornaDocService = project.getService(TornaDocService.class);
                List<TornaDocDTO> docs = tornaDocService.listFolderByModule(token, ((TornaModuleDTO) m.getItem()).getId(), refreshToken);
                if (StrUtil.isNotBlank(targetFolder)) {
                    docs.stream()
                            .filter(d -> d.getName().equals(targetFolder))
                            .findFirst()
                            .ifPresent(d -> folderId = d.getId());
                }
            }
        });
    }

    private void initValue() {
        DocSetting docSetting = DocSetting.instance(project);
        String token = tornaKeyService.getToken(project, docSetting);
        TornaSpaceService tornaSpaceService = project.getService(TornaSpaceService.class);
        List<TornaSpaceDTO> spaces = tornaSpaceService.listMySpace(token, refreshToken);
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

    public @Nullable String getFolderId() {
        return folderId;
    }

    public void refreshFolderId() {
        String token = tornaKeyService.getToken(project, docSetting);
        TornaDocService tornaDocService = project.getService(TornaDocService.class);
        List<TornaDocDTO> docs = tornaDocService.listFolderByModule(token, getModuleId(), refreshToken);
        if (StrUtil.isNotBlank(targetFolder)) {
            docs.stream()
                    .filter(d -> d.getName().equals(targetFolder))
                    .findFirst()
                    .ifPresent(d -> folderId = d.getId());
        }
    }

}
