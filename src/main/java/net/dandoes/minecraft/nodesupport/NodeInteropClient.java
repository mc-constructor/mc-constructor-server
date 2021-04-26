package net.dandoes.minecraft.nodesupport;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;

public class NodeInteropClient {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropClient.class);

    private final ChannelWriter writer;
    private final String newline = "\n";

    private final Map<Class<? extends Event>, Consumer<? extends Event>> subscribedEvents = new HashMap<>();

    public NodeInteropClient(ChannelWriter writer) {
        this.writer = writer;
    }

    public void sendEvent(Event event) {
        this.writer.writeMessage(this.buildEventResponse(event));
    }

    public void sendResponse(NodeCommandSource source, ITextComponent message) {
        this.writer.writeMessage(this.buildCommandResponse(source, message));
    }
    public void sendResponse(NodeCommandSource source, Exception ex) {
        this.writer.writeMessage(this.buildCommandResponse(source, ex));
    }

    public <C extends Class<T>, T extends Event> void subscribeToEvent(C eventClass) {
        if (!this.subscribedEvents.containsKey(eventClass)) {
            Consumer<T> consumer = (final T event) -> this.sendEvent(event);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventClass, consumer);
            this.subscribedEvents.put(eventClass, consumer);
        }
    }

    public void unsubscribeFromEvent(Class<? extends Event> eventClass) {
        if (this.subscribedEvents.containsKey(eventClass)) {
            Consumer<? extends Event> consumer = this.subscribedEvents.get(eventClass);
            MinecraftForge.EVENT_BUS.unregister(consumer);
            this.subscribedEvents.remove(eventClass);
        }
    }

    private String buildEventResponse(Event event) {
        List<String> response = new ArrayList<>();
        response.add(UUID.randomUUID().toString());
        response.add(event.getClass().getName());

        if (event instanceof PlayerEvent) {
            PlayerEvent playerEvent = (PlayerEvent) event;
            PlayerEntity player = playerEvent.getPlayer();
            response.add(this.getTextContent(player));

            if (event instanceof PlayerInteractEvent) {
                PlayerInteractEvent intEvent = (PlayerInteractEvent) event;
                response.add(intEvent.getHand().toString());
                response.add(intEvent.getPos().toString());
                response.add(intEvent.getFace().getName());
            }
        } else if (event instanceof EntityEvent) {
            EntityEvent entityEvent = (EntityEvent) event;
            response.add(this.getTextContent(entityEvent.getEntity().getName()));

            if (event instanceof LivingDeathEvent) {
                LivingDeathEvent deathEvent = (LivingDeathEvent) event;
                response.add(this.getTextContent(deathEvent.getSource()));
                final Entity revengeTarget = ((LivingDeathEvent) event).getEntityLiving().getLastHurtByMob();
                if (revengeTarget instanceof PlayerEntity) {
                    response.add("player");
                    response.add(this.getTextContent((PlayerEntity)revengeTarget));
                }
            }
        } else if (event instanceof NodeInteropGameClientEvent) {
            NodeInteropGameClientEvent nodeInteropGameClientEvent = (NodeInteropGameClientEvent) event;
            response.addAll(nodeInteropGameClientEvent.getInteropResponseContent());
        }

        return String.join(newline, response);
    }

    private String buildCommandResponse(NodeCommandSource source, ITextComponent message) {
        List<String> response = this.buildCommandResponse(source, true);

        if (message instanceof TranslationTextComponent) {
            this.addResponseContent((TranslationTextComponent) message, response);
        }

        return String.join(newline, response);
    }

    private void addResponseContent(TranslationTextComponent text, List<String> response) {
        response.add(text.getKey());

        for (Object arg : text.getArgs()) {
            if (arg instanceof TranslationTextComponent) {
                this.addResponseContent((TranslationTextComponent) arg, response);
                continue;
            }
            if (arg instanceof TextComponent) {
                response.add(this.getTextContent((ITextComponent)arg));
                continue;
            }
            response.add(arg.toString());
        }
    }

    private String getTextContent(PlayerEntity player) {
        List<String> content = new ArrayList<>();
        content.add(String.format("%s %s", this.getTextContent(player.getDisplayName()), player.getUUID()));

        // held items
        ItemStack mainHandStack = player.getItemBySlot(EquipmentSlotType.MAINHAND);
        ItemStack offHandStack = player.getItemBySlot(EquipmentSlotType.OFFHAND);
        content.add(this.getTextContent(mainHandStack));
        content.add(this.getTextContent(offHandStack));

        return String.join(newline, content);
    }

    private String getTextContent(ITextComponent text) {
        if (text instanceof TranslationTextComponent) {
            return ((TranslationTextComponent)text).getKey();
        }
        String insertion = text.getStyle().getInsertion();
        if (insertion != null) {
            return insertion;
        }
        StringBuilder content = new StringBuilder(text.getContents());
        for (ITextComponent sibling : text.getSiblings()) {
            content.append(this.getTextContent(sibling));
        }
        return content.toString();
    }

    private String getTextContent(ItemStack stack) {
        Item item = stack.getItem();
        StringBuilder text = new StringBuilder();
        text.append(item.getRegistryName());

        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return text.toString();
        }
        CompoundNBT display = (CompoundNBT) stack.getTag().get("display");
        if (display == null) {
            return text.toString();
        }
        ListNBT name = (ListNBT) display.get("Name");
        if (name == null) {
            return text.toString();
        }

        text.append('|');
        for (INBT part : name) {
            if (part instanceof CompoundNBT) {
                text.append(((CompoundNBT)part).getString("text"));
            } else {
                text.append(part.toString());
            }
        }
        return text.toString();
    }

    private String getTextContent(DamageSource damageSource) {
        List<String> content = new ArrayList<>();

        content.add(damageSource.getMsgId());

//        if (damageSource instanceof EntityDamageSource) {
//            EntityDamageSource entityDamageSource = (EntityDamageSource) damageSource;
//            Entity entitySource = entityDamageSource.getTrueSource();
//            if (entitySource instanceof ServerPlayerEntity) {
//                ServerPlayerEntity playerSource = (ServerPlayerEntity) entitySource;
//                content.add(this.getTextContent(playerSource));
//            }
//        }

        return String.join(newline, content);
    }

    private String buildCommandResponse(NodeCommandSource source, Exception ex) {
        List<String> response = this.buildCommandResponse(source, false);

        response.add(this.getErrorKey(ex));
        response.add(ex.getMessage());

        return String.join(newline, response);
    }

    private List<String> buildCommandResponse(NodeCommandSource source, boolean success) {
        return new ArrayList<>(Arrays.asList(source.getRequestId(), Boolean.toString(success)));
    }

    private String getErrorKey(Exception ex) {
        if (ex instanceof CommandSyntaxException) {
            CommandSyntaxException csEx = (CommandSyntaxException) ex;
            if (csEx.getRawMessage() instanceof TranslationTextComponent) {
                TranslationTextComponent text = (TranslationTextComponent) csEx.getRawMessage();
                return text.getKey();
            }
        }
        if (ex instanceof NodeInteropCommandException) {
            NodeInteropCommandException nicEx = (NodeInteropCommandException) ex;
            return nicEx.getErrorTextKey();
        }
        LOGGER.warn("Unexpected exception type", ex);
        return "";
    }
}
