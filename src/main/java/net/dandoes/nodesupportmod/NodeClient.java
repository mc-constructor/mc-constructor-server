package net.dandoes.nodesupportmod;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeClient {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ChannelWriter writer;
    private final String newline = "\n";

    public NodeClient(ChannelWriter writer) {
        this.writer = writer;
    }

    public void sendEvent(Event event) {
        List<String> lines = Arrays.asList(event.getClass().getName());

        this.writer.writeMessage(String.join(newline, lines));
    }

    public void sendResponse(NodeCommandSource source, ITextComponent message) {
        this.writer.writeMessage(this.buildResponse(source, message));
    }
    public void sendResponse(NodeCommandSource source, Exception ex) {
        this.writer.writeMessage(this.buildResponse(source, ex));
    }

    private String buildResponse(NodeCommandSource source, ITextComponent message) {
        List<String> response = this.buildResponse(source, true);

        if (message instanceof TranslationTextComponent) {
            TranslationTextComponent text = (TranslationTextComponent) message;
            response.add(text.getKey());

            for (Object arg : text.getFormatArgs()) {
                if (arg instanceof TextComponent) {
                    response.add(this.getTextContent((ITextComponent)arg));
                    continue;
                }
                response.add(arg.toString());
            }
        }

        return String.join(newline, response);
    }

    private String getTextContent(ITextComponent text) {
        if (text instanceof TranslationTextComponent) {
            return ((TranslationTextComponent)text).getKey();
        }
        String insertion = text.getStyle().getInsertion();
        if (insertion != null) {
            return insertion;
        }
        StringBuilder content = new StringBuilder(text.getUnformattedComponentText());
        for (ITextComponent sibling : text.getSiblings()) {
            content.append(this.getTextContent(sibling));
        }
        return content.toString();
    }

    private String buildResponse(NodeCommandSource source, Exception ex) {
        List<String> response = this.buildResponse(source, false);

        response.add(this.getErrorKey(ex));
        response.add(ex.getMessage());

        return String.join(newline, response);
    }

    private List<String> buildResponse(NodeCommandSource source, boolean success) {
        return new ArrayList<>(Arrays.asList(source.requestId, Boolean.toString(success)));
    }

    private String getErrorKey(Exception ex) {
        if (ex instanceof CommandSyntaxException) {
            CommandSyntaxException csEx = (CommandSyntaxException) ex;
            if (csEx.getRawMessage() instanceof TranslationTextComponent) {
                TranslationTextComponent text = (TranslationTextComponent) csEx.getRawMessage();
                return text.getKey();
            }
        }
        LOGGER.warn("Unexpected exception type", ex);
        return "";
    }

}
