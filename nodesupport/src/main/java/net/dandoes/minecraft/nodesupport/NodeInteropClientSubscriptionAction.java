package net.dandoes.minecraft.nodesupport;

public enum NodeInteropClientSubscriptionAction {
    add("add"),
    remove("remove");

    private final String name;

    NodeInteropClientSubscriptionAction(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
