package cn.uniondrug.dev.ui;

import javax.swing.*;

/**
 * @author dingshichen
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 * @date 2022/8/8
 */
public class IssueForm {

    private JTextArea contentTextArea;
    private JPanel rootPanel;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getContent() {
        return contentTextArea.getText();
    }

}
