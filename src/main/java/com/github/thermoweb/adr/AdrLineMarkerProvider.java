package com.github.thermoweb.adr;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;

public class AdrLineMarkerProvider implements LineMarkerProvider {
    public static final Pattern ADR_PATTERN = Pattern.compile("#ADR-(\\w+)") ;

    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (psiElement instanceof PsiComment comment) {
            Optional<Adr> adr = findAdrIfAny(comment);
            if (adr.isPresent()) {
                Adr currentAdr = adr.get();
                String tooltip = "ADR #" + currentAdr.id();
                return new LineMarkerInfo<>(comment,
                        comment.getTextRange(),
                        AllIcons.FileTypes.Manifest,
                        e -> tooltip,
                        null,
                        GutterIconRenderer.Alignment.RIGHT,
                        () -> tooltip);
            }
        }
        return null;
    }

    private Optional<Adr> findAdrIfAny(PsiComment element) {
        Matcher matcher = ADR_PATTERN.matcher(element.getText());
        if (matcher.find()) {
            AdrService service = AdrService.getInstance(element.getProject());
            return service.getAdrFromId(matcher.group(1));
        }

        return Optional.empty();
    }
}
