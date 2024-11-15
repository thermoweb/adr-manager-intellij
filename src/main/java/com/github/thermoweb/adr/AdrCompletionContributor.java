package com.github.thermoweb.adr;

import java.util.List;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;

public class AdrCompletionContributor extends CompletionContributor {
    public static final Pattern ADR_PATTERN = Pattern.compile("#ADR-(\\w+)");

    public AdrCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<>() {

                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters,
                                                  @NotNull ProcessingContext processingContext,
                                                  @NotNull CompletionResultSet completionResultSet) {
                        if (!(completionParameters.getPosition() instanceof PsiComment comment)) {
                            return;
                        }

                        String commentText = getCleanText(comment);
                        int prefixIndex = commentText.indexOf("#ADR-");
                        if (prefixIndex == -1) {
                            return;
                        }

                        String alreadyTyped = commentText.substring(prefixIndex + 5);
                        AdrService adrService = AdrService.getInstance(completionParameters.getPosition().getProject());
                        List<Adr> adrs = adrService.getAdrs();

                        boolean hasMatches = false;
                        for (Adr adr : adrs) {
                            if (adr.id().toLowerCase().startsWith(alreadyTyped)) {
                                hasMatches = true;
                                completionResultSet.addElement(LookupElementBuilder.create(adr.id())
                                        .withBoldness(true)
                                        .withTypeText(adr.title())
                                );
                            }
                        }

                        if (!hasMatches) {
                            completionResultSet.restartCompletionOnAnyPrefixChange();
                        }
                    }
                });
    }

    private static String getCleanText(PsiElement comment) {
        String text = comment.getText();
        if (text.endsWith(CompletionUtilCore.DUMMY_IDENTIFIER)) {
            text = text.substring(0, text.length() - CompletionUtilCore.DUMMY_IDENTIFIER.length());
        }
        return text;
    }
}
