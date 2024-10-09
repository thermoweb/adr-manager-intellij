package com.github.thermoweb.adr;

import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;

public class AdrAnnotator implements Annotator {
    public static final Pattern ADR_PATTERN = Pattern.compile("#ADR-(\\w+)");

    public AdrAnnotator() {
        AdrTextAttribute.initializeTextAttributes();
    }

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof PsiComment comment) {
            Matcher matcher = ADR_PATTERN.matcher(comment.getText());

            while (matcher.find()) {
                String adrValue = matcher.group(1);
                TextRange adrIdRange = TextRange.from(comment.getTextRange().getStartOffset() + matcher.start(),
                        matcher.end() - matcher.start());
                annotationHolder.newAnnotation(HighlightSeverity.INFORMATION, "ADR reference: " + adrValue)
                        .range(adrIdRange)
                        .textAttributes(AdrTextAttribute.ADR_REFERENCE_KEY)
                        .create();            }
        }
    }

    private static class AdrTextAttribute {
        public static final TextAttributesKey ADR_REFERENCE_KEY = TextAttributesKey.createTextAttributesKey("ADR_REFERENCE");

        public static void initializeTextAttributes() {
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();

            TextAttributes attributes = new TextAttributes();
            attributes.setForegroundColor(JBColor.MAGENTA);
            attributes.setFontType(Font.BOLD);
            scheme.setAttributes(ADR_REFERENCE_KEY, attributes);
        }
    }
}
