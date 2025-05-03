package moe.mutsuk1.betterspellbook;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record MsgSpellBookContentsS2C(boolean isFullData, UUID spellbookUuid, CompoundTag spellbookContents){
    public static final ResourceLocation ID = BetterSpellBook.modLoc("sbc_sc");

    public static MsgSpellBookContentsS2C deserialize(FriendlyByteBuf buf) {
        return new MsgSpellBookContentsS2C(buf.readBoolean(), buf.readUUID(), buf.readNbt());
    }

    public static void handle (MsgSpellBookContentsS2C self) {
        Minecraft.getInstance().execute(() -> {
            if (self.isFullData()) {
                SpellBookStorage.get().readSpellbookContents(self.spellbookContents);
            } else {
                SpellBookStorage.get().setSpellbookContents(self.spellbookUuid(), self.spellbookContents());
            }
        });
    }
}
