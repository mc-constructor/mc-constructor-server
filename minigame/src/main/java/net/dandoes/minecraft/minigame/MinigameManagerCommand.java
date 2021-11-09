package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dandoes.minecraft.nodesupport.NodeCommandSourceStack;
import net.dandoes.minecraft.nodesupport.NodeInteropClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class MinigameManagerCommand {

//    public static void title(CommandDispatcher<CommandSource> dispatcher) {
//        dispatcher.register(
//            Commands.literal("title")
//                .requires((p_198847_0_) -> p_198847_0_.hasPermissionLevel(2))
//                .then(Commands.argument("targets", EntityArgument.players())
//                    .then(Commands.literal("clear")
//                        .executes((p_198838_0_) -> clear(p_198838_0_.getSource(), EntityArgument.getPlayers(p_198838_0_, "targets")))
//                    )
//                        .then(Commands.literal("reset").executes((p_198841_0_) -> {
//            return reset(p_198841_0_.getSource(), EntityArgument.getPlayers(p_198841_0_, "targets"));
//        })).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.component()).executes((p_198837_0_) -> {
//            return show(p_198837_0_.getSource(), EntityArgument.getPlayers(p_198837_0_, "targets"), ComponentArgument.getComponent(p_198837_0_, "title"), STitlePacket.Type.TITLE);
//        }))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.component()).executes((p_198842_0_) -> {
//            return show(p_198842_0_.getSource(), EntityArgument.getPlayers(p_198842_0_, "targets"), ComponentArgument.getComponent(p_198842_0_, "title"), STitlePacket.Type.SUBTITLE);
//        }))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.component()).executes((p_198836_0_) -> {
//            return show(p_198836_0_.getSource(), EntityArgument.getPlayers(p_198836_0_, "targets"), ComponentArgument.getComponent(p_198836_0_, "title"), STitlePacket.Type.ACTIONBAR);
//        }))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((p_198843_0_) -> {
//            return setTimes(p_198843_0_.getSource(), EntityArgument.getPlayers(p_198843_0_, "targets"), IntegerArgumentType.getInteger(p_198843_0_, "fadeIn"), IntegerArgumentType.getInteger(p_198843_0_, "stay"), IntegerArgumentType.getInteger(p_198843_0_, "fadeOut"));
//        })))))));
//    }

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
//        final ScoreboardCommand
        final LiteralArgumentBuilder<CommandSourceStack> register = Commands.literal("register")
            .then(Commands.argument("key", StringArgumentType.word())
                .then(Commands.argument("title", ComponentArgument.textComponent())

                    // no description
                    .executes(context -> registerGame(context, false))));

                        // TODO: implement description command
//                    // description
//                    .then(Commands.argument("description", ComponentArgument.component())
//                        .executes(context -> registerGame(context, true))

//                )));

        dispatcher.register(register);

        final LiteralArgumentBuilder<CommandSourceStack> unregister =
            Commands.literal("unregister")
                .then(Commands.argument("minigame", MinigameArgumentType.minigameName())
                    .suggests(MinigameArgumentType.SUGGEST_MINIGAME_NAMES)
                    .executes(MinigameManagerCommand::unregisterGame));
        dispatcher.register(unregister);
    }

    private static int registerGame(final CommandContext<CommandSourceStack> context, final boolean hasDescription) {
        final String key = StringArgumentType.getString(context, "key");
        final Component title = ComponentArgument.getComponent(context, "title");
        Component description = null;
        if (hasDescription) {
            description = ComponentArgument.getComponent(context, "description");
        }
        final NodeInteropClient interopClient = ((NodeCommandSourceStack) context.getSource()).getInteropClient();
        try {
            MinigameManager.registerGame(interopClient, key, title, description);
            context.getSource().sendSuccess(new TextComponent(key), false);
            return 1;
        } catch (MinigameRegistrationKeyConflictException ex) {
            ((NodeCommandSourceStack) context.getSource()).sendErrorMessage(ex);
            return 0;
        }
    }

    private static int unregisterGame(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final NodeInteropClient interopClient = ((NodeCommandSourceStack) context.getSource()).getInteropClient();
        final Minigame game = MinigameArgumentType.getMinigame(context, "minigame");
        MinigameManager.unregisterGame(interopClient, game);

        context.getSource().sendSuccess(new TextComponent(game.getKey()), false);
        return 1;
    }

}
