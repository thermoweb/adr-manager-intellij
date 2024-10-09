package com.github.thermoweb.adr;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class AdrToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Content content = ContentFactory.getInstance().createContent(getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static JPanel getPanel() {
        return new JPanel(new BorderLayout());
    }
}
