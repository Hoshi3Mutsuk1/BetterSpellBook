package moe.mutsuk1.betterspellbook;

import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SpellBookDataCopyRecipe extends CustomRecipe {
    public static final SimpleCraftingRecipeSerializer<SpellBookDataCopyRecipe> SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(SpellBookDataCopyRecipe::new);

    public SpellBookDataCopyRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean isOnlyItem = false;
        for (var stack : container.getItems()) {
            if (stack.is(HexItems.SPELLBOOK)) {
                if (isOnlyItem) return false;
                isOnlyItem = true;
            }
            if (!stack.isEmpty() && !stack.is(HexItems.SPELLBOOK)) return false;
        }
        return isOnlyItem;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack out = ItemStack.EMPTY;
        for (var stack : container.getItems()){
            if (stack.is(HexItems.SPELLBOOK)) {
                var copiedTag = stack.getTag().copy();
                out = new ItemStack(BetterSpellBook.BETTER_SPELL_BOOK);
                out.setTag(copiedTag);
            }
        }
        return out;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
