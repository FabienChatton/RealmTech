package ch.realmtech.registry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.mod.items.IronOreItemEntry;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RegistryTest {

    @Test
    public void createRoot() {
        Registry<?> rootRegistry = Registry.createRoot();

        assertEquals(rootRegistry.toFqrn(), "");
        assertEquals(rootRegistry.getName(), ".");
    }

    @Test
    public void addSubRegistry() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        assertEquals(subRegistry.getName(), "subRegistry");
        assertEquals(subRegistry.toFqrn(), "subRegistry");

        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        assertEquals(subSubRegistry.getName(), "subSubRegistry");
        assertEquals(subSubRegistry.toFqrn(), "subRegistry.subSubRegistry");
    }

    @Test
    public void addEntryAndGetFqrn() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        Registry<ItemEntry> items = Registry.createRegistry(subSubRegistry, "items");

        ItemEntry itemTestEntry = new ItemEntry("ItemTest", null, ItemBehavior.builder().build()) {
        };
        items.addEntry(itemTestEntry);

        assertSame(RegistryUtils.findEntry(rootRegistry, "subRegistry.subSubRegistry.items.ItemTest").orElseThrow(), itemTestEntry);
    }

    @Test
    public void addEntryAndGetByName() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        Registry<ItemEntry> items = Registry.createRegistry(subSubRegistry, "items");

        ItemEntry itemTestEntry = new ItemEntry("ItemTest", null, ItemBehavior.builder().build()) {
        };
        items.addEntry(itemTestEntry);

        assertSame(RegistryUtils.findEntry(rootRegistry, "ItemTest").orElseThrow(), itemTestEntry);
    }

    @Test
    public void addEntryAndGetByTags() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        Registry<ItemEntry> items = Registry.createRegistry(subSubRegistry, "items", "itemsTags");

        ItemEntry itemTestEntry = new ItemEntry("ItemTest", null, ItemBehavior.builder().build()) {
        };
        items.addEntry(itemTestEntry);

        List<? extends Entry> itemsTags = RegistryUtils.findEntries(rootRegistry, "#itemsTags");

        assertSame(itemsTags.get(0), itemTestEntry);
    }

    @Test
    public void addEntryAndGetBySubTags() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        Registry<?> items = Registry.createRegistry(subSubRegistry, "items", "itemsTags");
        Registry<ItemEntry> itemsSub = Registry.createRegistry(items, "items");

        ItemEntry itemTestEntry = new ItemEntry("ItemTest", null, ItemBehavior.builder().build()) {
        };
        itemsSub.addEntry(itemTestEntry);

        List<? extends Entry> itemsTags = RegistryUtils.findEntries(rootRegistry, "#itemsTags");

        assertSame(itemsTags.get(0), itemTestEntry);
    }

    @Test
    public void addEntryAndGetById() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        Registry<ItemEntry> items = Registry.createRegistry(subSubRegistry, "items", "itemsTags");

        ItemEntry itemTestEntry = new ItemEntry("ItemTest", null, ItemBehavior.builder().build()) {
            @Override
            public int getId() {
                return 123456789;
            }
        };
        items.addEntry(itemTestEntry);

        Optional<Entry> entryOptional = RegistryUtils.findEntry(rootRegistry, 123456789);

        assertSame(entryOptional.orElseThrow(), itemTestEntry);
    }

    @Test
    public void addEntryAndGetByClass() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<?> subRegistry = Registry.createRegistry(rootRegistry, "subRegistry");
        Registry<?> subSubRegistry = Registry.createRegistry(subRegistry, "subSubRegistry");
        Registry<ItemEntry> items = Registry.createRegistry(subSubRegistry, "items", "itemsTags");

        IronOreItemEntry ironOreItemEntry = new IronOreItemEntry();
        items.addEntry(ironOreItemEntry);

        Optional<IronOreItemEntry> ironOreItemEntryOptional = RegistryUtils.findEntry(rootRegistry, IronOreItemEntry.class);

        assertSame(ironOreItemEntryOptional.orElseThrow(), ironOreItemEntry);
    }

    @Test
    public void twoRootSubRegistriesAndGetByFqrn() {
        Registry<?> rootRegistry = Registry.createRoot();

        Registry<Entry> modOne = Registry.createRegistry(rootRegistry, "modOne");
        Registry<Entry> modTwo = Registry.createRegistry(rootRegistry, "modTwo");

        Registry<Entry> items = Registry.createRegistry(modTwo, "items");
        items.addEntry(new ItemEntry("itemTest", null, ItemBehavior.builder().build()) {
        });

        assertTrue(RegistryUtils.findEntry(rootRegistry, "modTwo.items.itemTest").isPresent());
    }
}
