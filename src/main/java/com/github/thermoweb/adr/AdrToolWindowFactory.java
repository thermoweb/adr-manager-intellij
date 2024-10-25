package com.github.thermoweb.adr;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
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
        Content content = ContentFactory.getInstance().createContent(getPanel(toolWindow), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static JPanel getPanel(ToolWindow toolWindow) {
        AdrService service = toolWindow.getProject().getService(AdrService.class);

        JPanel panel = new JPanel(new BorderLayout());
        // toolbar
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new CreateAdrAction());
        ActionToolbar adrToolWindow = ActionManager.getInstance().createActionToolbar("AdrToolWindow", actionGroup, true);
        adrToolWindow.setTargetComponent(panel);
        panel.add(adrToolWindow.getComponent(), BorderLayout.NORTH);

        // adr table
        List<Adr> adrs = service.getAdrs();
        JBScrollPane panelFromAdrs = createPanelFromAdrs(adrs);
        panel.add(panelFromAdrs, BorderLayout.CENTER);

        return panel;
    }

    private static JBScrollPane createPanelFromAdrs(List<Adr> adrs) {
        JBTable adrTable = new JBTable();
        DefaultTableModel defaultTableModel = new DefaultTableModel(new Object[]{
                AdrBundle.message("adrColumnIdName"),
                AdrBundle.message("adrColumnTitleName")},
                0);
        adrs.forEach(adr -> defaultTableModel.addRow(new Object[]{adr.shortId(), adr.title()}));
        adrTable.setModel(defaultTableModel);
        return new JBScrollPane(adrTable);
    }
}
