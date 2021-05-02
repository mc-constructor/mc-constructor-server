package net.dandoes.minecraft.nodesupport;

public class NodeInteropCommandException extends Exception {

    private static final String ERROR_TEXT_KEY = "net.dandoes.minecraft.nodeinterop.error.command";

    public String getErrorTextKey() {
        return ERROR_TEXT_KEY;
    }

}
