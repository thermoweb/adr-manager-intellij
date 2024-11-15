package com.github.thermoweb.adr;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;

public class AdrReference extends PsiReferenceBase<PsiElement> {
    public final String adrId;

    public AdrReference(PsiElement element, TextRange rangeInElement, String adrId) {
        super(element, rangeInElement);
        this.adrId = adrId;
    }

    @Override
    public @Nullable PsiElement resolve() {
        Project project = myElement.getProject();
        return AdrService.getInstance(project)
                .getAdrFromId(adrId)
                .map(Adr::file)
                .map(adrFile -> PsiManager.getInstance(project).findFile(adrFile))
                .orElse(null);
    }
}
