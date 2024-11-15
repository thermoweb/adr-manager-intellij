package com.github.thermoweb.adr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
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
        AdrService service = AdrService.getInstance(toolWindow.getProject());

        JPanel panel = new JPanel(new BorderLayout());
        // toolbar
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new CreateAdrAction());
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("AdrToolWindow", actionGroup, true);
        actionToolbar.setTargetComponent(panel);
        panel.add(actionToolbar.getComponent(), BorderLayout.NORTH);

        // adr table
        List<Adr> adrs = service.getAdrs();
        JBScrollPane panelFromAdrs = createPanelFromAdrs(adrs);
        panel.add(panelFromAdrs, BorderLayout.CENTER);

        return panel;
    }

    private static JBScrollPane createPanelFromAdrs(List<Adr> adrs) {
        DefaultTableModel defaultTableModel = new DefaultTableModel(new Object[]{
                AdrBundle.message("adrColumnIdName"),
                AdrBundle.message("adrColumnTitleName")},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adrs.forEach(adr -> defaultTableModel.addRow(new Object[]{adr.shortId(), adr.title()}));

        JBTable adrTable = new JBTable(defaultTableModel);
        adrTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    tableCellRendererComponent.setBackground(table.getSelectionBackground());
                    tableCellRendererComponent.setForeground(table.getSelectionForeground());
                } else {
                    tableCellRendererComponent.setBackground(row % 2 == 0 ? Gray.x4C : Gray.x1C);
                    tableCellRendererComponent.setForeground(JBColor.BLACK);
                }

                tableCellRendererComponent.setFont(new Font("SansSerif", Font.PLAIN, 12));
                if (column == 0) {
                    ((JLabel) tableCellRendererComponent).setHorizontalAlignment(LEFT);
                } else {
                    ((JLabel) tableCellRendererComponent).setHorizontalAlignment(RIGHT);
                }
                return tableCellRendererComponent;
            }
        });

        adrTable.setAutoCreateRowSorter(true);
        adrTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        adrTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        return new JBScrollPane(adrTable);
    }
}
