package com.github.thermoweb.adr;

import javax.swing.JOptionPane;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

public class CreateAdrAction extends AnAction {
    public static final Logger log = Logger.getInstance(CreateAdrAction.class);

    public CreateAdrAction() {
        super("Create Adr", "Create new adr", AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            createNewAdr();
        }
    }

    private void createNewAdr() {
        String adrTitle = JOptionPane.showInputDialog(AdrBundle.message("adrTitleInput"));
        log.info("creating adr: " + adrTitle);
    }
}
