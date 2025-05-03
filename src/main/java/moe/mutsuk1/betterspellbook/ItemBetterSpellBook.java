package moe.mutsuk1.betterspellbook;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemBetterSpellBook extends ItemSpellbook {
    public static String TAG_BOOK_UUID = "book_uuid";

    public ItemBetterSpellBook(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (NBTHelper.contains(stack, TAG_PAGES)){
            if (!pLevel.isClientSide) {
                ItemBetterSpellBook.broadcastChanges(getOrCreateUUID(stack), NBTHelper.getCompound(stack, TAG_PAGES));
            }
            NBTHelper.remove(stack, TAG_PAGES);
        }
        super.inventoryTick(stack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public @Nullable CompoundTag readIotaTag(ItemStack stack) {
        int idx = getPage(stack, 1);
        var key = String.valueOf(idx);
        var storge = SpellBookStorage.get();
        var uuid = getOrCreateUUID(stack);
        var pages = storge.getSpellbookContents(uuid);
        if (pages != null && pages.contains(key, Tag.TAG_COMPOUND)) {
            return pages.getCompound(key);
        } else {
            return null;
        }
    }

    @Override
    public void writeDatum(ItemStack stack, Iota datum) {
        if (datum != null && isSealed(stack)) {
            return;
        }

        int idx = getPage(stack, 1);
        var key = String.valueOf(idx);
        var storge = SpellBookStorage.get();
        var uuid = getOrCreateUUID(stack);
        var pages = storge.getSpellbookContents(uuid);
        if (pages != null) {
            if (datum == null) {
                pages.remove(key);
                NBTHelper.remove(NBTHelper.getCompound(stack, TAG_SEALED), key);
            } else {
                pages.put(key, IotaType.serialize(datum));
            }
            if (pages.isEmpty()) {
                pages = null;
            }
        } else if (datum != null) {
            pages = new CompoundTag();
            pages.put(key, IotaType.serialize(datum));
        } else {
            NBTHelper.remove(NBTHelper.getCompound(stack, TAG_SEALED), key);
        }
        broadcastChanges(uuid, pages);
    }

    public static UUID getOrCreateUUID(ItemStack stack) {
        if (NBTHelper.getString(stack, TAG_BOOK_UUID) == null) {
            var uuid = UUID.randomUUID();
            NBTHelper.putString(stack, TAG_BOOK_UUID, uuid.toString());
        }
        return UUID.fromString(NBTHelper.getString(stack, TAG_BOOK_UUID));
    }

    public static void broadcastChanges(UUID uuid, CompoundTag change) {
        SpellBookStorage.get().setSpellbookContents(uuid, change);
        PlayerLookup.all(BetterSpellBook.getCurrentServer()).forEach(
                p -> ServerPlayNetworking.send(p, MsgSpellBookContentsS2C.ID,
                        new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false))
                                .writeUUID(uuid)
                                .writeNbt(change))
        );
    }
}
