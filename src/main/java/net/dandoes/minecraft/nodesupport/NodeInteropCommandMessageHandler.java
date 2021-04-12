package net.dandoes.minecraft.nodesupport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.command.impl.SetBlockCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NodeInteropCommandMessageHandler implements NodeInteropMessageHandler, Runnable {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropCommandMessageHandler.class);

    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final Set<String> singleThreadedCommands;
    private static final Map<String, ParseResults<CommandSource>> cmdCache = new HashMap<>();

    private NodeClient client;
    private final CommandDispatcher<CommandSource> dispatcher;
    private final ParseResults<CommandSource> parsedCmd;
    private final NodeCommandSource source;
    private final String requestId;
    private final String cmd = "";

    public static NodeInteropCommandMessageHandler forCmd(final MinecraftServer server, final NodeClient client, String requestId, String cmd) {
        LOGGER.debug("parsing command for requestId" + requestId + ": " + cmd);
        NodeCommandSource source = new NodeCommandSource(server, requestId, client);
//        ExecuteCommand
        if (server instanceof DedicatedServer) {
            DedicatedServer dServer = (DedicatedServer) server;
            dServer.handleConsoleInput(cmd, source);
        }
//        try {
//            final ParseResults<CommandSource> parsedCmd = dispatcher.parse(cmd, source);
//            return new NodeInteropCommandMessageHandler(server, client, parsedCmd);
//        } catch (Exception ex) {
//            client.sendResponse(source, ex);
//            return null;
//        }
        return null;
    }

    static {
        final String[] cmds = new String[]{FillCommand.class.toString(), SetBlockCommand.class.toString()};
        singleThreadedCommands = new HashSet<>(Arrays.asList(cmds));
    }

    public NodeInteropCommandMessageHandler(final MinecraftServer server, final NodeClient client, final ParseResults<CommandSource> parsedCmd) {
        this.client = client;
        this.dispatcher = server.getCommandManager().getDispatcher();
        this.parsedCmd = parsedCmd;
        this.source = (NodeCommandSource) parsedCmd.getContext().getSource();
        this.requestId = source.getRequestId();
//        this.cmd = parsedCmd.getContext().get
    }

    @Override
    public void run() {
        try {
            LOGGER.info("dispatching command for requestId" + requestId + ": " + cmd);
            dispatcher.execute(parsedCmd);
            LOGGER.debug("dispatched command: " + cmd);
        } catch (Exception ex) {
            this.client.sendResponse(source, ex);
        }
    }

    public void start() {
        Command<CommandSource> cmd = parsedCmd.getContext().getCommand();
        if (cmd == null) {
            LOGGER.debug("No command for requestId " + this.requestId);
            return;
        }
        String cmdClassName = cmd.getClass().toString();
        String baseCmdClass = cmdClassName.split("\\$\\$Lambda\\$")[0];
        if (singleThreadedCommands.contains(baseCmdClass)) {
            LOGGER.debug("Running command for requestId " + this.requestId + " on the main thread");
            this.run();
            return;
        }

        LOGGER.debug("Starting thread for requestId " + this.requestId);
        pool.execute(this);
    }
}
