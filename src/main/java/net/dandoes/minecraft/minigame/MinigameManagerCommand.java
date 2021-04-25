package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dandoes.minecraft.nodesupport.NodeCommandSource;
import net.dandoes.minecraft.nodesupport.NodeInteropClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
//        final ScoreboardCommand
        LiteralArgumentBuilder<CommandSource> register = Commands.literal("register")
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

        LiteralArgumentBuilder<CommandSource> unregister =
            Commands.literal("unregister")
                .then(Commands.argument("minigame", MinigameArgument.minigameArgument())
                    .suggests(MinigameArgument.SUGGEST_MINIGAMES)
                    .executes(MinigameManagerCommand::unregisterGame));
        dispatcher.register(unregister);
    }

    private static int registerGame(final CommandContext<CommandSource> context, boolean hasDescription) {
        final String key = StringArgumentType.getString(context, "key");
        final ITextComponent title = ComponentArgument.getComponent(context, "title");
        ITextComponent description = null;
        if (hasDescription) {
            description = ComponentArgument.getComponent(context, "description");
        }
        final NodeInteropClient interopClient = ((NodeCommandSource) context.getSource()).getInteropClient();
        try {
            MinigameManager.registerGame(interopClient, key, title, description);
            context.getSource().sendSuccess(new StringTextComponent(key), false);
            return 1;
        } catch (MinigameRegistrationKeyConflictException ex) {
            ((NodeCommandSource) context.getSource()).sendErrorMessage(ex);
            return 0;
        }
    }

    private static int unregisterGame(CommandContext<CommandSource> context) throws CommandSyntaxException {
        final NodeInteropClient interopClient = ((NodeCommandSource) context.getSource()).getInteropClient();
        Minigame game = MinigameArgument.getMinigame(context, "minigame");
        MinigameManager.unregisterGame(interopClient, game);

        context.getSource().sendSuccess(new StringTextComponent(game.getKey()), false);
        return 1;
    }

}
