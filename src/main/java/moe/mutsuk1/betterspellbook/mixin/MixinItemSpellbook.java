package moe.mutsuk1.betterspellbook.mixin;

import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moe.mutsuk1.betterspellbook.ItemBetterSpellBook;
import moe.mutsuk1.betterspellbook.SpellBookStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static moe.mutsuk1.betterspellbook.BetterSpellBook.BETTER_SPELL_BOOK;

@Mixin(ItemSpellbook.class)
public abstract class MixinItemSpellbook{

    @WrapOperation(method = {"arePagesEmpty", "highestPage"}, at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/utils/NBTHelper;getCompound(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"))
    private static CompoundTag patch(ItemStack stack, String key, Operation<CompoundTag> original) {
        if (stack.is(BETTER_SPELL_BOOK)) {
            var storge = SpellBookStorage.get();
            var uuid = ItemBetterSpellBook.getOrCreateUUID(stack);
            return storge.getSpellbookContents(uuid);
        }
        return original.call(stack, key);
    }
}
