package com.builtbroken.sbmgrowmeal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GrowmealFabric implements ModInitializer {

	public static final String MOD_ID = "sbmgrowmeal";
	public static final String MOD_NAME = "SBM Growmeal";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static final Item GROWMEAL = new GrowmealItem(new Item.Settings());

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "growmeal"), GROWMEAL);
	}
}
