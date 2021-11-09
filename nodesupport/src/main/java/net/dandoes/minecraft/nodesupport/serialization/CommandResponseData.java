package net.dandoes.minecraft.nodesupport.serialization;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dandoes.minecraft.nodesupport.NodeCommandSource;
import net.dandoes.minecraft.nodesupport.NodeCommandSourceStack;
import net.dandoes.minecraft.nodesupport.NodeInteropCommandException;
import net.dandoes.minecraft.nodesupport.serialization.objects.TextComponentData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandResponseData<TDataSource> extends MessageData<TDataSource> {

    private static final Logger LOGGER = LogManager.getLogger(CommandResponseData.class);

    public final String requestId;
    public final boolean success;
    public final TextComponentData message;

    protected CommandResponseData(
        final NodeCommandSource commandSource,
        final TDataSource dataSource,
        final boolean success,
        final TextComponentData message
    ) {
        super(dataSource);

        this.requestId = commandSource.getRequestId();
        this.success = success;
        this.message = message;
    }

    @Override()
    public String[] getTransmissionHeaderParts() {
        return ArrayUtils.addAll(
                super.getTransmissionHeaderParts(),
                this.requestId,
                this.success ? "true" : "false"
        );
    }

    public static class CommandSuccessResponseData extends CommandResponseData<Component> {
        public CommandSuccessResponseData(final NodeCommandSource commandSource, final Component message) {
            super(commandSource, message, true, message == null ? null : TextComponentData.forComponent(message));
        }
    }

    public static class CommandExceptionResponseData extends CommandResponseData<Exception> {
        public static String getErrorKey(final Exception ex) {
            if (ex instanceof CommandSyntaxException) {
                return getErrorKey((CommandSyntaxException) ex);
            }
            if (ex instanceof NodeInteropCommandException) {
                return getErrorKey((NodeInteropCommandException) ex);
            }
            LOGGER.warn("Unexpected exception type", ex);
            return "";
        }

        public static String getErrorKey(final CommandSyntaxException ex) {
            if (ex.getRawMessage() instanceof final TranslatableComponent text) {
                return text.getKey();
            }
            return "";
        }

        public static String getErrorKey(final NodeInteropCommandException ex) {
            return ex.getErrorTextKey();
        }

        public final String errorKey;

        public CommandExceptionResponseData(final NodeCommandSource commandSource, final Exception ex) {
            super(commandSource, ex, false, new TextComponentData(ex.getMessage()));

            this.errorKey = getErrorKey(ex);
        }
    }
}
