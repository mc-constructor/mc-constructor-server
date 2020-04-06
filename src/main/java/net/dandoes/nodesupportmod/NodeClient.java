package net.dandoes.nodesupportmod;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NodeClient {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ChannelWriter writer;
    private final String newline = "\n";

    public NodeClient(ChannelWriter writer) {
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

    private String buildEventResponse(Event event) {
        List<String> response = new ArrayList<>();
        response.clear(); // WTF is there a "foo" entry???
        response.add(UUID.randomUUID().toString());
        response.add(event.getClass().getName());

        if (event instanceof PlayerEvent) {
            PlayerEvent playerEvent = (PlayerEvent) event;
            PlayerEntity player = playerEvent.getPlayer();
            response.add(this.getTextContent(player.getDisplayNameAndUUID()));

            // held items
            ItemStack mainHandStack = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            ItemStack offHandStack = player.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
            response.add(this.getTextContent(mainHandStack));
            response.add(this.getTextContent(offHandStack));

            if (event instanceof PlayerInteractEvent) {
                PlayerInteractEvent intEvent = (PlayerInteractEvent) event;
                response.add(intEvent.getHand().toString());
                response.add(intEvent.getPos().toString());
                response.add(intEvent.getFace().getName());
            }
        } else if (event instanceof EntityEvent) {
            EntityEvent entityEvent = (EntityEvent) event;
            response.add(this.getTextContent(entityEvent.getEntity().getName()));
        }

        return String.join(newline, response);
    }

    private String buildCommandResponse(NodeCommandSource source, ITextComponent message) {
        List<String> response = this.buildCommandResponse(source, true);

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

    private String buildCommandResponse(NodeCommandSource source, Exception ex) {
        List<String> response = this.buildCommandResponse(source, false);

        response.add(this.getErrorKey(ex));
        response.add(ex.getMessage());

        return String.join(newline, response);
    }

    private List<String> buildCommandResponse(NodeCommandSource source, boolean success) {
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
