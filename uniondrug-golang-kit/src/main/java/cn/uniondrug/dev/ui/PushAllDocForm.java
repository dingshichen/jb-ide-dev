package cn.uniondrug.dev.ui;

import cn.hutool.core.util.StrUtil;
import cn.uniondrug.dev.*;
import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.config.TornaKeyService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/6/14
 */
public class PushAllDocForm {

    private JLabel spaceLable;
    private ComboBox<TornaSpaceDTO> spaceBox;
    private ComboBox<TornaProjectDTO> projectBox;
    private ComboBox<TornaModuleDTO> moduleBox;
    private JLabel projectLable;
    private JLabel moduleLable;
    private JPanel rootPanel;

    private Project project;

    private TornaKeyService tornaKeyService;

    public PushAllDocForm(Project project) {
        this.project = project;
        this.tornaKeyService = TornaKeyService.Companion.getInstance(project);
        initListener();
        initValue();
    }

    private void initListener() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String rememberProjectBoxId = docSetting.getState().getRememberProjectBoxId();
        String rememberModuleBoxId = docSetting.getState().getRememberModuleBoxId();
        String token = tornaKeyService.getToken(project, docSetting);
        spaceBox.addItemListener(s -> {
            if (s.getStateChange() == ItemEvent.SELECTED) {
                TornaProjectService tornaProjectService = project.getService(TornaProjectService.class);
                List<TornaProjectDTO> projects = tornaProjectService.listProjectBySpace(token, ((TornaSpaceDTO) s.getItem()).getId());
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
                List<TornaModuleDTO> modules = tornaModuleService.listModuleByProject(token, ((TornaProjectDTO) p.getItem()).getId());
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
    }

    private void initValue() {
        DocSetting docSetting = DocSetting.Companion.getInstance(project);
        String token = tornaKeyService.getToken(project, docSetting);
        TornaSpaceService tornaSpaceService = project.getService(TornaSpaceService.class);
        List<TornaSpaceDTO> spaces = tornaSpaceService.listMySpace(token);
        spaceBox.removeAllItems();
        spaces.forEach(e -> spaceBox.addItem(e));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getProjectId() {
        return projectBox.getItem().getId();
    }

    public String getModuleId() {
        return moduleBox.getItem().getId();
    }
}
