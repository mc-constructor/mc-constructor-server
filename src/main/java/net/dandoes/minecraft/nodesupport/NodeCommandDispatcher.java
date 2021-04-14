package net.dandoes.minecraft.nodesupport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class NodeCommandDispatcher extends CommandDispatcher<NodeCommandSource> {
    public CommandDispatcher<CommandSource> asCommandDispatcher() {
        return (CommandDispatcher<CommandSource>) (Object) this;
    }
}
