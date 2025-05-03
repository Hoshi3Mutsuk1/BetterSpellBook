package moe.mutsuk1.betterspellbook;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.RegisterClientStuff;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.lib.HexItems;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class BetterSpellBook implements ModInitializer, ClientModInitializer {
    public static final String MODID = "betterspellbook";
    private static MinecraftServer currentServer = null;
    public static final ItemBetterSpellBook BETTER_SPELL_BOOK = new ItemBetterSpellBook(HexItems.unstackable());

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static ResourceLocation modLoc(String s) {
        return new ResourceLocation(MODID, s);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var storge = SpellBookStorage.get();
            ServerPlayNetworking.send(handler.getPlayer(), MsgSpellBookContentsS2C.ID,
                    new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true))
                            .writeUUID(UUID.randomUUID())
                            .writeNbt(storge.save(new CompoundTag())));
        });
        Registry.register(BuiltInRegistries.ITEM, modLoc("betterspellbook"), BETTER_SPELL_BOOK);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, modLoc("copy_spell_data"), SpellBookDataCopyRecipe.SERIALIZER);
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MsgSpellBookContentsS2C.ID,
                makeClientBoundHandler(MsgSpellBookContentsS2C::deserialize, MsgSpellBookContentsS2C::handle));
        ColorProviderRegistry.ITEM.register(RegisterClientStuff.makeIotaStorageColorizer(BETTER_SPELL_BOOK::getColor), BETTER_SPELL_BOOK);
        ItemProperties.register(BETTER_SPELL_BOOK, ItemFocus.OVERLAY_PRED,
                (stack, clientLevel, livingEntity, i) -> {
                if (BETTER_SPELL_BOOK.readIotaTag(stack) == null && !NBTHelper.hasString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY)) {
                    return 0;
                }
                if (!ItemSpellbook.isSealed(stack)) {
                    return 1;
                }
                return 2;
        });
        ItemProperties.register(BETTER_SPELL_BOOK, ItemFocus.VARIANT_PRED,
                (stack, clientLevel, livingEntity, i) -> BETTER_SPELL_BOOK.getVariant(stack));
    }

    private static <T> ClientPlayNetworking.PlayChannelHandler makeClientBoundHandler(
            Function<FriendlyByteBuf, T> decoder, Consumer<T> handler) {
        return (_client, _handler, buf, _responseSender) -> handler.accept(decoder.apply(buf));
    }
}
