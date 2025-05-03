package moe.mutsuk1.betterspellbook;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpellBookStorage extends SavedData {
    private final Map<UUID, CompoundTag> spellbookContents = new HashMap<>();
    public static final SpellBookStorage clientStorageCopy = new SpellBookStorage();

    public SpellBookStorage() {}

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        spellbookContents.forEach((u, p) -> {
            if (p != null && !p.isEmpty()) compoundTag.put(u.toString(), p);
        });
        return compoundTag;
    }

    public static SpellBookStorage load(CompoundTag nbt) {
        var storage = new SpellBookStorage();
        storage.readSpellbookContents(nbt);
        return storage;
    }

    public static SpellBookStorage get() {
        var server = BetterSpellBook.getCurrentServer();
        if (server != null && server.isSameThread()) {
            var dataStorage = server.getLevel(Level.OVERWORLD).getDataStorage();
            var storage = dataStorage.computeIfAbsent(SpellBookStorage::load, SpellBookStorage::new, BetterSpellBook.MODID);
            storage.setDirty();
            return storage;
        }
        return clientStorageCopy;
    }

    protected void readSpellbookContents(CompoundTag nbt) {
        nbt.getAllKeys().forEach(k -> spellbookContents.put(UUID.fromString(k), nbt.getCompound(k)));
    }

    public void setSpellbookContents(UUID uuid, CompoundTag contents) {
        if (contents == null || contents.isEmpty()) {
            spellbookContents.remove(uuid);
        }else {
            spellbookContents.put(uuid, contents);
        }
        setDirty();
    }

    @Nullable
    public CompoundTag getSpellbookContents(UUID spellbookUuid) {
        return spellbookContents.get(spellbookUuid);
    }

}
