package com.github.thermoweb.adr;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;

public class AdrToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Content content = ContentFactory.getInstance().createContent(getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Adr> adrs = getAdrs();
        JBScrollPane panelFromAdrs = createPanelFromAdrs(adrs);
        panel.add(panelFromAdrs, BorderLayout.CENTER);

        return panel;
    }

    private static List<Adr> getAdrs() {
        return List.of(new Adr("20241009", "my first fake adr !"), new Adr("20240101", "my older adr"));
    }

    private static JBScrollPane createPanelFromAdrs(List<Adr> adrs) {
        JBTable adrTable = new JBTable();
        DefaultTableModel defaultTableModel = new DefaultTableModel(new Object[]{"Id", "Title"}, 0);
        adrs.forEach(adr -> defaultTableModel.addRow(new Object[]{adr.id(), adr.title()}));
        adrTable.setModel(defaultTableModel);
        return new JBScrollPane(adrTable);
    }
}
