package me.profelements.dynatech.registries;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.ChatColor;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.profelements.dynatech.DynaTech;
import me.profelements.dynatech.items.misc.StarDustMeteor;
import me.profelements.dynatech.utils.ItemWrapper;

public final class Researches {

	private static final int RESEARCH_ID_BASE = 536200;
	private static final Set<SlimefunItem> ASSIGNED_ITEMS = new LinkedHashSet<>();

	private Researches() {
	}

	public static void init() {
		int nextResearchId = RESEARCH_ID_BASE;

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"mechanical_foundations",
				8,
				2,
				items(
						Items.WOOD_MACHINE_CORE,
						Items.STONE_MACHINE_CORE,
						Items.IRON_MACHINE_CORE,
						Items.DIAMOND_MACHINE_CORE,
						Items.ENCHANTED_MACHINE_CORE,
						Items.STAINLESS_STEEL_INGOT,
						Items.STAINLESS_STEEL_ROTOR,
						Items.COAL_COKE
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"powered_components",
				12,
				2,
				items(
						Items.ENERGY_STORAGE_COMPONENT,
						Items.ENERGY_INPUT_COMPONENT,
						Items.ENERGY_OUTPUT_COMPONENT,
						Items.FLUID_TANK,
						Items.AUTO_INPUT_UPGRADE,
						Items.AUTO_OUTPUT_UPGRADE
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"arcane_materials",
				18,
				3,
				merge(
						items(
						Items.ANCIENT_MACHINE_CORE,
						Items.MACHINE_SCRAP,
						Items.ADVANCED_MACHINE_SCRAP,
						Items.VEX_GEM,
						Items.GHOSTLY_ESSENCE,
						Items.STAR_DUST,
						Items.TESSERACTING_OBJ
						),
						item(StarDustMeteor.STARDUST_METEOR.getItemId())
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"apiary_studies",
				22,
				4,
				items(
						Items.BEE,
						Items.ROBOTIC_BEE,
						Items.ADVANCED_ROBOTIC_BEE,
						Items.SCOOP,
						Items.MATERIAL_HIVE
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"field_equipment",
				16,
				2,
				items(
						Items.PICNIC_BASKET,
						Items.SOUL_BOUND_PICNIC_BASKET,
						Items.ELECTRICAL_STIMULATOR,
						Items.LIQUID_TANK,
						Items.INVENTORY_FILTER,
						Items.RECIPE_BOOK,
						Items.LAVA_BOTTLE,
						Items.HONEY_BUCKET,
						Items.POTION_BUCKET,
						Items.MILK_BOTTLE
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"dimensional_engineering",
				28,
				4,
				items(
						Items.ANGEL_GEM,
						Items.DIMENSIONAL_HOME,
						Items.ITEM_BAND_HEALTH,
						Items.ITEM_BAND_HASTE,
						Items.TESSERACT_BINDER,
						Items.WITHER_SKELETON_GOLEM
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"industrial_automation",
				26,
				3,
				items(
						Items.COAL_COKE_OVEN,
						Items.KITCHEN_AUTO_CRAFTER,
						Items.WEATHER_CONTROLLER,
						Items.POTION_SPRINKLER,
						Items.BARBED_WIRE,
						Items.SEED_PLUCKER,
						Items.BANDAID_MANAGER,
						Items.EXTERNAL_HEATER
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"cultivation_systems",
				30,
				2,
				items(
						Items.GROWTH_CHAMBER,
						Items.GROWTH_CHAMBER_MK2,
						Items.GROWTH_CHAMBER_END,
						Items.GROWTH_CHAMBER_MK2_END,
						Items.GROWTH_CHAMBER_NETHER,
						Items.GROWTH_CHAMBER_MK2_NETHER,
						Items.GROWTH_CHAMBER_OCEAN,
						Items.GROWTH_CHAMBER_MK2_OCEAN,
						Items.LIVINGROCK,
						Items.LIVINGWOOD,
						Items.PETAL_APOTHECARY,
						Items.ORECHID
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"renewable_generation",
				24,
				2,
				items(
						Items.DEGRADED_WATER_MILL,
						Items.DEGRADED_WATER_MILL_2,
						Items.DEGRADED_WIND_MILL,
						Items.DEGRADED_WIND_MILL_2,
						Items.DEGRADED_EGG_MILL,
						Items.DEGRADED_EGG_MILL_2,
						Items.WATER_MILL,
						Items.WATER_MILL_2,
						Items.WIND_MILL,
						Items.WIND_MILL_2,
						Items.EGG_MILL,
						Items.EGG_MILL_2,
						Items.DURABILITY_GENERATOR,
						Items.FOOD_GENERATOR
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"wireless_logistics",
				40,
				4,
				items(
						Items.WIRELESS_CHARGER,
						Items.WIRELESS_ENERGY_BANK,
						Items.WIRELESS_ENERGY_POINT,
						Items.WIRELESS_ITEM_INPUT,
						Items.WIRELESS_ITEM_OUTPUT,
						Items.TESSERACT
				)
		);

		nextResearchId = registerSequentialResearches(
				nextResearchId,
				"stellar_engineering",
				48,
				8,
				items(
						Items.STARDUST_GENERATOR,
						Items.ANTIGRAVITY_BUBBLE
				)
		);

		registerSequentialResearches(
				nextResearchId,
				"mineralized_apiaries",
				38,
				1,
				group(ItemGroups.HIVES)
		);

		logUnassignedItems();
	}

	private static int registerSequentialResearches(int nextResearchId, String categoryKey, int baseCost, int costStep,
			SlimefunItem... items) {
		LinkedHashSet<SlimefunItem> uniqueItems = new LinkedHashSet<>();

		for (SlimefunItem item : items) {
			if (item != null) {
				uniqueItems.add(item);
			}
		}

		if (uniqueItems.isEmpty()) {
			return nextResearchId;
		}

		int index = 0;

		for (SlimefunItem item : uniqueItems) {
			Research research = new Research(
					new NamespacedKey(DynaTech.getInstance(), categoryKey + "_" + item.getId().toLowerCase(Locale.ROOT)),
					nextResearchId++,
					ChatColor.stripColor(item.getItemName()),
					baseCost + (index++ * costStep)
			);
			item.setResearch(research);
			research.addItems(item);
			research.register();
			ASSIGNED_ITEMS.add(item);
		}

		return nextResearchId;
	}

	private static SlimefunItem[] items(ItemWrapper... wrappers) {
		List<SlimefunItem> items = new ArrayList<>();

		for (ItemWrapper wrapper : wrappers) {
			SlimefunItem item = item(wrapper.stack().getItemId());
			if (item != null) {
				items.add(item);
			}
		}

		return items.toArray(new SlimefunItem[0]);
	}

	private static SlimefunItem[] group(ItemGroup group) {
		return Slimefun.getRegistry()
				.getEnabledSlimefunItems()
				.stream()
				.filter(item -> item.getAddon() == DynaTech.getInstance())
				.filter(item -> group.equals(item.getItemGroup()))
				.toArray(SlimefunItem[]::new);
	}

	private static SlimefunItem[] merge(SlimefunItem[] items, SlimefunItem... extraItems) {
		List<SlimefunItem> mergedItems = new ArrayList<>(List.of(items));

		for (SlimefunItem item : extraItems) {
			if (item != null) {
				mergedItems.add(item);
			}
		}

		return mergedItems.toArray(new SlimefunItem[0]);
	}

	private static SlimefunItem item(String id) {
		return SlimefunItem.getById(id);
	}

	private static void logUnassignedItems() {
		Set<SlimefunItem> allItems = new LinkedHashSet<>();

		Set<ItemGroup> groups = Set.of(
				ItemGroups.RESOURCES,
				ItemGroups.TOOLS,
				ItemGroups.MACHINES,
				ItemGroups.GENERATORS,
				ItemGroups.EXPERIMENTAL,
				ItemGroups.HIVES
		);

		Slimefun.getRegistry()
				.getEnabledSlimefunItems()
				.stream()
				.filter(item -> item.getAddon() == DynaTech.getInstance())
				.filter(item -> groups.contains(item.getItemGroup()))
				.forEach(allItems::add);

		allItems.removeAll(ASSIGNED_ITEMS);

		if (!allItems.isEmpty()) {
			DynaTech.getInstance().getLogger().warning("Items missing research assignments:");
			for (SlimefunItem item : allItems) {
				DynaTech.getInstance().getLogger().warning(" - " + item.getId());
			}
		}
	}
}
