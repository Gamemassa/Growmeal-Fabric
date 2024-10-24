package com.builtbroken.sbmgrowmeal;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class GrowmealFabric implements ModInitializer {

    public static final String MOD_ID = "sbmgrowmeal";
    public static final String MOD_NAME = "SBM Growmeal";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final Item GROWMEAL = register("growmeal", GrowmealItem::new);

    @Override
    public void onInitialize() {

    }

    private static <T extends Item> T register(String id, Function<Item.Settings, T> factory) {
        Identifier guid = Identifier.of(MOD_ID, id);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, guid);
        Item.Settings settings = (new Item.Settings()).registryKey(key);
        T item = factory.apply(settings);
        return Registry.register(Registries.ITEM, guid, item);
    }
}
