package cn.uniondrug.dev.ui;

import cn.uniondrug.dev.Api;
import cn.uniondrug.dev.TornaDocService;
import cn.uniondrug.dev.MbsEvent;
import cn.uniondrug.dev.config.DocSetting;
import cn.uniondrug.dev.config.DocSettingConfigurable;
import cn.uniondrug.dev.config.TornaKeyService;
import cn.uniondrug.dev.dialog.TornaIndexDialog;
import cn.uniondrug.dev.service.DocService;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.WindowMoveListener;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.intellij.plugins.markdown.settings.MarkdownApplicationSettings;
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel;
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanelProvider;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.uniondrug.dev.notifier.CommonNotifierKt.notifyError;
import static cn.uniondrug.dev.notifier.CommonNotifierKt.notifyInfo;

/**
 * @author liuzhihang
 * @date 2020/2/26 19:40
 */
@Slf4j
public class PreviewForm {

    @NonNls
    public static final String DOC_VIEW_POPUP = "com.intellij.docview.popup";
    public static final AtomicBoolean myIsPinned = new AtomicBoolean(false);
    private static final AtomicBoolean previewIsHtml = new AtomicBoolean(true);

    private final Document markdownDocument = EditorFactory.getInstance().createDocument("");

    private final Project project;
    private final PsiFile psiFile;

    private JPanel previewParent;
    private JPanel rootPanel;
    private JPanel viewPanel;

    private JPanel previewPanel;
    private JBScrollPane markdownSourceScrollPanel;
    private MarkdownHtmlPanel markdownHtmlPanel;

    private JPanel headToolbarPanel;
    private JPanel previewToolbarPanel;
    private JLabel docNameLabel;
    private EditorEx markdownEditor;

    private Api api;
    private MbsEvent mbsEvent;
    private JBPopup popup;
    // 如果是 API 文档预览，这里设置为 true ，如果是 MBS 文档预览，这里设置为 false
    private boolean isApi;

    public PreviewForm(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull Api api) {
        this.project = project;
        this.psiFile = psiFile;
        this.api = api;
        this.isApi = true;
        // UI调整
        initUI();
        initHeadToolbar();
        // 右侧文档
        initMarkdownSourceScrollPanel();
        initMarkdownHtmlPanel();
        initPreviewPanel();
        initPreviewLeftToolbar();
        initPreviewRightToolbar();
        // 生成文档
        buildDoc();
        addMouseListeners();
    }

    public PreviewForm(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull MbsEvent mbsEvent) {
        this.project = project;
        this.psiFile = psiFile;
        this.mbsEvent = mbsEvent;
        this.isApi = false;
        // UI调整
        initUI();
        initHeadToolbar();
        // 右侧文档
        initMarkdownSourceScrollPanel();
        initMarkdownHtmlPanel();
        initPreviewPanel();
        initPreviewLeftToolbar();
        initPreviewRightToolbar();
        // 生成文档
        buildDoc();
        addMouseListeners();
    }

    private void addMouseListeners() {
        WindowMoveListener windowMoveListener = new WindowMoveListener(rootPanel);
        rootPanel.addMouseListener(windowMoveListener);
        rootPanel.addMouseMotionListener(windowMoveListener);
        headToolbarPanel.addMouseListener(windowMoveListener);
        headToolbarPanel.addMouseMotionListener(windowMoveListener);

    }

    public static PreviewForm getInstance(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull Api api) {
        return new PreviewForm(project, psiFile, api);
    }

    public static PreviewForm getInstance(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull MbsEvent mbsEvent) {
        return new PreviewForm(project, psiFile, mbsEvent);
    }

    public void popup() {

        // dialog 改成 popup, 第一个为根面板，第二个为焦点面板
        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(rootPanel, previewToolbarPanel)
                .setProject(project)
                .setResizable(true)
                .setMovable(true)

                .setModalContext(false)
                .setRequestFocus(true)
                .setBelongsToGlobalPopupStack(true)
                .setDimensionServiceKey(null, DOC_VIEW_POPUP, true)
                .setLocateWithinScreenBounds(false)
                // 鼠标点击外部时是否取消弹窗 外部单击, 未处于 pin 状态则可关闭
                .setCancelOnMouseOutCallback(event -> event.getID() == MouseEvent.MOUSE_PRESSED && !myIsPinned.get())

                // 单击外部时取消弹窗
                .setCancelOnClickOutside(true)
                // 在其他窗口打开时取消
                .setCancelOnOtherWindowOpen(true)
                .setCancelOnWindowDeactivation(true)
                .createPopup();
        popup.showCenteredInCurrentWindow(project);
    }


    private void initUI() {
        GuiUtils.replaceJSplitPaneWithIDEASplitter(rootPanel, true);
        // 边框
        rootPanel.setBorder(JBUI.Borders.empty());
        previewToolbarPanel.setBorder(JBUI.Borders.empty());
        previewPanel.setBorder(JBUI.Borders.empty());
        viewPanel.setBorder(JBUI.Borders.empty());
        docNameLabel.setBorder(JBUI.Borders.emptyLeft(5));
        // 设置滚动条, 总是隐藏
        JBScrollBar jbScrollBar = new JBScrollBar();
        jbScrollBar.setBackground(UIUtil.getTextFieldBackground());
        jbScrollBar.setAutoscrolls(true);
    }

    private void initHeadToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AnAction("Setting", "Doc view settings", AllIcons.General.GearPlain) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                popup.cancel();
                ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), DocSettingConfigurable.class);
            }
        });

        group.addSeparator();

        group.add(new ToggleAction("Pin", "Pin window", AllIcons.General.Pin_tab) {

            @Override
            public boolean isDumbAware() {
                return true;
            }

            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return myIsPinned.get();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                myIsPinned.set(state);
            }
        });

        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("DocViewRootToolbar", group, true);
        toolbar.setTargetComponent(headToolbarPanel);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        headToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);
        docNameLabel.setText("Uniondrug Dev Kit");
    }


    private void initMarkdownSourceScrollPanel() {
        // 会使用 velocity 渲染模版
        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("md");

        final EditorHighlighter editorHighlighter =
                HighlighterFactory.createHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), project);

        markdownEditor = (EditorEx) EditorFactory.getInstance().createEditor(markdownDocument, project, fileType, true);

        EditorSettings editorSettings = markdownEditor.getSettings();
        editorSettings.setAdditionalLinesCount(0);
        editorSettings.setAdditionalColumnsCount(0);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setLineNumbersShown(false);
        editorSettings.setVirtualSpace(false);
        editorSettings.setFoldingOutlineShown(false);

        editorSettings.setLanguageSupplier(() -> Language.findLanguageByID("Markdown"));

        markdownEditor.setHighlighter(editorHighlighter);
        markdownEditor.setBorder(JBUI.Borders.emptyLeft(5));
        markdownSourceScrollPanel = new JBScrollPane(markdownEditor.getComponent());
    }

    private void initMarkdownHtmlPanel() {
        MarkdownApplicationSettings settings = MarkdownApplicationSettings.getInstance();
        MarkdownHtmlPanelProvider.ProviderInfo providerInfo = settings.getMarkdownPreviewSettings().getHtmlPanelProviderInfo();
        MarkdownHtmlPanelProvider provider = MarkdownHtmlPanelProvider.createFromInfo(providerInfo);
        // xx
        if (!JBCefApp.isSupported()) {
            // Fallback to an alternative browser-less solution
            // https://plugins.jetbrains.com/docs/intellij/jcef.html#jbcefapp
            log.info("当前不支持 JCEF");
            return;
        }
        markdownHtmlPanel = provider.createHtmlPanel();
    }


    private void initPreviewPanel() {

        if (previewIsHtml.get() && JBCefApp.isSupported()) {
            previewPanel.add(markdownHtmlPanel.getComponent(), BorderLayout.CENTER);
        } else {
            // 展示源码
            previewPanel.add(markdownSourceScrollPanel, BorderLayout.CENTER);
        }

    }


    private void initPreviewLeftToolbar() {

        DefaultActionGroup leftGroup = new DefaultActionGroup();

        leftGroup.add(new ToggleAction("Preview", "Preview markdown", AllIcons.Actions.Preview) {

            @Override
            public boolean isDumbAware() {
                return true;
            }

            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return previewIsHtml.get();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {

                if (!JBCefApp.isSupported()) {
                    // 不支持 JCEF 不允许预览
                    previewIsHtml.set(false);
                    notifyInfo(project, "不支持 JCEF 无法预览");
                } else {
                    previewIsHtml.set(state);
                    if (state) {
                        previewPanel.removeAll();
                        previewPanel.repaint();
                        previewPanel.add(markdownHtmlPanel.getComponent(), BorderLayout.CENTER);
                        previewPanel.revalidate();
                    } else {
                        // 展示源码
                        previewPanel.removeAll();
                        previewPanel.repaint();
                        previewPanel.add(markdownSourceScrollPanel, BorderLayout.CENTER);
                        previewPanel.revalidate();
                    }
                }
            }
        });

        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("DocViewEditorLeftToolbar", leftGroup, true);
        toolbar.setTargetComponent(previewToolbarPanel);
        toolbar.getComponent().setBackground(markdownEditor.getBackgroundColor());

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        previewToolbarPanel.setBackground(markdownEditor.getBackgroundColor());
        previewToolbarPanel.add(toolbar.getComponent(), BorderLayout.WEST);
    }

    private void initPreviewRightToolbar() {
        DefaultActionGroup rightGroup = new DefaultActionGroup();

        if (isApi) {
            rightGroup.add(new AnAction("Upload", "Upload To Torna", AllIcons.Actions.Upload) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {

                    DocSetting apiSettings = DocSetting.Companion.getInstance(project);
                    DocSetting.TornaState state = apiSettings.getState();
                    if (StringUtils.isBlank(state.getUsername())) {
                        // 说明没有配置 Torna, 跳转到配置页面
                        notifyError(project, "请先完成 Torna 账号配置");
                        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), DocSettingConfigurable.class);
                        popup.cancel();
                        return;
                    }
                    // 上传到 torna
                    TornaIndexDialog dialog = new TornaIndexDialog(project, api);
                    if (dialog.showAndGet()) {
                        TornaDocService service = project.getService(TornaDocService.class);
                        TornaKeyService tornaKeyService = TornaKeyService.Companion.getInstance(project);
                        try {
                            String token = tornaKeyService.getToken(project, apiSettings);
                            service.saveDoc(token, dialog.getProjectId(), dialog.getModuleId(), dialog.getFolderId(), api,
                                    () -> tornaKeyService.refreshToken(project, apiSettings));
                            notifyInfo(project, "文档上传成功");
                        } catch (Exception ex) {
                            notifyError(project, "文档上传失败：" + ex.getMessage());
                        }
                        // 记住选择
                        state.setRememberSpaceBoxId(dialog.getSpaceId());
                        state.setRememberProjectBoxId(dialog.getProjectId());
                        state.setRememberModuleBoxId(dialog.getModuleId());
                    }
                }
            });
            rightGroup.addSeparator();
        }

        rightGroup.add(new AnAction("Export", "Export markdown", AllIcons.ToolbarDecorator.Export) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                popup.cancel();
                DocService service = ApplicationManager.getApplication().getService(DocService.class);
                service.export(project,
                        isApi ? api.getFileName() : mbsEvent.getFileName(),
                        isApi ? api.getMarkdownText() : mbsEvent.getMarkdownText());
            }
        });

        rightGroup.add(new AnAction("Copy", "Copy to clipboard", AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                StringSelection selection = new StringSelection(isApi ? api.getMarkdownText() : mbsEvent.getMarkdownText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                notifyInfo(project, "复制成功");
            }
        });

        // init toolbar
        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("DocViewEditorRightToolbar", rightGroup, true);
        toolbar.setTargetComponent(previewToolbarPanel);
        toolbar.getComponent().setBackground(markdownEditor.getBackgroundColor());

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        previewToolbarPanel.setBackground(markdownEditor.getBackgroundColor());
        previewToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);

    }

    private void buildDoc() {
        if (JBCefApp.isSupported()) {
            markdownHtmlPanel.setHtml(MarkdownUtil.INSTANCE.generateMarkdownHtml(psiFile.getVirtualFile(),
                    isApi ? api.getMarkdownText() : mbsEvent.getMarkdownText(), project), 0);
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 光标放在顶部
            markdownDocument.setText(isApi ? api.getMarkdownText() : mbsEvent.getMarkdownText());
        });
    }
}
