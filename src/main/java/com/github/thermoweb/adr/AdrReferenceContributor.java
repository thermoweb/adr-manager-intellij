package com.github.thermoweb.adr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.util.ProcessingContext;

public class AdrReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiDocToken.class), new AdrReferenceProvider());
        registrar.registerReferenceProvider(PlatformPatterns.psiComment(), new AdrReferenceProvider());
    }

    private static class AdrReferenceProvider extends PsiReferenceProvider {
        private static final Pattern PATTERN = Pattern.compile("#ADR-(\\d+)");

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
            List<PsiReference> references = new ArrayList<>();
            String text = psiElement.getText();
            Matcher matcher = PATTERN.matcher(text);

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String adrId = matcher.group(1);
                TextRange rangeInElement = new TextRange(start, end).shiftRight(psiElement.getTextRange().getStartOffset() - psiElement.getTextOffset());
                references.add(new AdrReference(psiElement, rangeInElement, adrId));
            }
            return references.toArray(PsiReference.EMPTY_ARRAY);
        }
    }
}
