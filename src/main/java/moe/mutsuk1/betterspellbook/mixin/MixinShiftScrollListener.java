package moe.mutsuk1.betterspellbook.mixin;

import at.petrak.hexcasting.client.ShiftScrollListener;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import moe.mutsuk1.betterspellbook.BetterSpellBook;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShiftScrollListener.class)
public class MixinShiftScrollListener {

    @WrapMethod(method = "IsScrollableItem")
    private static boolean patch(Item item, Operation<Boolean> original) {
        return original.call(item) || item == BetterSpellBook.BETTER_SPELL_BOOK;
    }
}
