package moe.mutsuk1.betterspellbook.mixin;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IotaHolderItem.class)
public interface MixinIotaHolderItem {

    @ModifyExpressionValue(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z"))
    private static boolean hideAdvancedInfo(boolean original) {
        return original && Screen.hasShiftDown();
    }
}
