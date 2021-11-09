package net.dandoes.minecraft.nodesupport.serialization.objects;

import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class TextComponentData {
    public static TextComponentData forComponent(final Component text) {
        if (text instanceof TranslatableComponent) {
            return new TranslationTextComponentData((TranslatableComponent) text);
        }

        return new TextComponentData(format(text));
    }

    private static String format(final Component text) {
        final String insertion = text.getStyle().getInsertion();
        if (insertion != null) {
            return insertion;
        }

        final StringBuilder content = new StringBuilder(text.getContents());
        for (final Component sibling : text.getSiblings()) {
            content.append(format(sibling));
        }
        return content.toString();
    }

    public final String content;

    public TextComponentData(final String content) {
        this.content = content;
    }

    public static class TranslationTextComponentData extends TextComponentData {
        protected static Object[] getArgs(final TranslatableComponent text) {
            final List<Object> args = new ArrayList<>();
            for (final Object arg : text.getArgs()) {
                if (arg instanceof TranslatableComponent) {
                    args.add(new TranslationTextComponentData((TranslatableComponent) arg));
                } else if (arg instanceof TextComponent) {
                    args.add(format((BaseComponent)arg));
                } else {
                    args.add(arg.toString());
                }
            }
            return args.toArray();
        }

        public final Object[] args;

        public TranslationTextComponentData(final TranslatableComponent text) {
            super(text.getKey());

            this.args = getArgs(text);
        }
    }

}
