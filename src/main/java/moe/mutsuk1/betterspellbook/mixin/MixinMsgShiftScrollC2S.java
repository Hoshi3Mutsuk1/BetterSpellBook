package moe.mutsuk1.betterspellbook.mixin;

import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.msgs.MsgShiftScrollC2S;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moe.mutsuk1.betterspellbook.BetterSpellBook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MsgShiftScrollC2S.class)
public class MixinMsgShiftScrollC2S {
    @WrapOperation(method = "handleForHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item patch(ItemStack instance, Operation<Item> original) {
        if (instance.getItem() == BetterSpellBook.BETTER_SPELL_BOOK){
            return HexItems.SPELLBOOK;
        }
        return original.call(instance);
    }
}
