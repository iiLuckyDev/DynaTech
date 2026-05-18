package me.profelements.dynatech;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.bakedlibs.dough.updater.BlobBuildUpdater;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.profelements.dynatech.items.backpacks.PicnicBasket;
import me.profelements.dynatech.items.misc.DimensionalHomeDimension;
import me.profelements.dynatech.items.tools.ElectricalStimulator;
import me.profelements.dynatech.listeners.BlockBreakBlockListener;
import me.profelements.dynatech.listeners.CoalCokeListener;
import me.profelements.dynatech.listeners.ElectricalStimulatorListener;
import me.profelements.dynatech.listeners.ExoticGardenIntegrationListener;
import me.profelements.dynatech.listeners.GastronomiconIntegrationListener;
import me.profelements.dynatech.listeners.InventoryFilterListener;
import me.profelements.dynatech.listeners.PicnicBasketListener;
import me.profelements.dynatech.listeners.RegistryListeners;
import me.profelements.dynatech.listeners.UpgradesListener;
import me.profelements.dynatech.registries.ItemGroups;
import me.profelements.dynatech.registries.Items;
import me.profelements.dynatech.registries.RecipeTypes;
import me.profelements.dynatech.registries.Recipes;
import me.profelements.dynatech.registries.Researches;
import me.profelements.dynatech.registries.Registries;
import me.profelements.dynatech.setup.DynaTechItemsSetup;
import me.profelements.dynatech.tasks.ItemBandTask;
import me.profelements.dynatech.utils.Liquid;
import me.profelements.dynatech.utils.LiquidRegistry;
import me.profelements.dynatech.utils.RecipeRegistry;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class DynaTech extends JavaPlugin implements SlimefunAddon {

	private static final String CONFIG_PATH = "config.yml";
	private static final String CONFIG_VERSION_KEY = "config-version";
	private static final DateTimeFormatter BACKUP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	private static DynaTech instance;
	private static boolean exoticGardenInstalled;
	private static boolean infinityExpansionInstalled;
	private static RecipeRegistry rRegistry;
	private static LiquidRegistry lRegistry;

	private int tickInterval;

	@Override
	public void onEnable() {
		setInstance(this);
		rRegistry = RecipeRegistry.init();
		lRegistry = LiquidRegistry.init();
		setExoticGardenInstalled(Bukkit.getPluginManager().isPluginEnabled("ExoticGarden"));
		setInfinityExpansionInstalled(Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion"));

		final int TICK_TIME = Slimefun.getTickerTask().getTickRate();

		ensureConfigUpToDate();

		new Metrics(this, 9689);

		if (!getConfig().getBoolean("options.disable-dimensionalhome-world")) {
			WorldCreator worldCreator = new WorldCreator("dimensionalhome");
			worldCreator.generator(new DimensionalHomeDimension());
			worldCreator.createWorld();
		}
		DynaTechLiquids.registerLiquids(DynaTech.getLiquidRegistry());

		DynaTechItemsSetup.setup(this);
		new PicnicBasketListener(this, (PicnicBasket) Items.PICNIC_BASKET.stack().getItem());
		new ElectricalStimulatorListener(this, (ElectricalStimulator) Items.ELECTRICAL_STIMULATOR.stack().getItem());
		new InventoryFilterListener(this);
		new UpgradesListener(this);
		new CoalCokeListener(this);
		new BlockBreakBlockListener(this);
		new RegistryListeners(this);
		try {
			Class.forName("io.github.schntgaispock.gastronomicon.api.items.FoodItemStack");
			new GastronomiconIntegrationListener(this);
		} catch (ClassNotFoundException ex) {

		}

		try {
			Class.forName("io.github.thebusybiscuit.exoticgarden.items.CustomFood");
			new ExoticGardenIntegrationListener(this);
		} catch (ClassNotFoundException ex) {
		}

		// Tasks
		getServer().getScheduler().runTaskTimerAsynchronously(DynaTech.getInstance(), new ItemBandTask(), 0L, 5 * 20L);
		getServer().getScheduler().runTaskTimer(DynaTech.getInstance(), () -> this.tickInterval++, 0, TICK_TIME);

		if (getConfig().getBoolean("options.auto-update", true) && getDescription().getVersion().startsWith("Main")) {
			new BlobBuildUpdater(this, getFile(), "DynaTech", "Main").start();
		}

		if (!Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_19)) {
			getLogger().warning("DynaTech only support 1.19+, disabling.");
			getServer().getPluginManager().disablePlugin(this);
		}

		setupRegistries();
	}

	private static void setupRegistries() {
		ItemGroups.init(Registries.ITEM_GROUPS);
		RecipeTypes.init(Registries.RECIPE_TYPES);
		Recipes.init(Registries.RECIPES);
		if (getInstance().getConfig().getBoolean("options.enable-researches", true)) {
			Researches.init();
		} else {
			getInstance().getLogger().info("Addon researches are disabled in config.yml.");
		}
		Registries.ITEMS.freeze();
		Registries.ITEM_GROUPS.freeze();
		Registries.RECIPE_TYPES.freeze();
		Registries.RECIPES.freeze();

	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);

		setInstance(null);
	}

	@Override
	public String getBugTrackerURL() {
		return "https://github.com/ProfElements/DynaTech/issues";
	}

	@Nonnull
	@Override
	public JavaPlugin getJavaPlugin() {
		return this;
	}

	@Nonnull
	public static DynaTech getInstance() {
		return instance;
	}

	@Nonnull
	public static RecipeRegistry getRecipeRegistry() {
		return RecipeRegistry.getInstance();
	}

	@Nonnull
	public static LiquidRegistry getLiquidRegistry() {
		return LiquidRegistry.getInstance();
	}

	public int getTickInterval() {
		return tickInterval;
	}

	public static boolean isExoticGardenInstalled() {
		return exoticGardenInstalled;
	}

	public static boolean isInfinityExpansionInstalled() {
		return infinityExpansionInstalled;
	}

	public static void setInstance(DynaTech inst) {
		instance = inst;
	}

	public static void setExoticGardenInstalled(boolean isExoticGardenInstalled) {
		exoticGardenInstalled = isExoticGardenInstalled;
	}

	public static void setInfinityExpansionInstalled(boolean isInfinityExpansionInstalled) {
		infinityExpansionInstalled = isInfinityExpansionInstalled;
	}

	private void ensureConfigUpToDate() {
		int bundledVersion = getBundledConfigVersion();
		File configFile = new File(getDataFolder(), CONFIG_PATH);

		if (!configFile.exists()) {
			saveDefaultConfig();
			reloadConfig();
			return;
		}

		FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);
		int currentVersion = currentConfig.getInt(CONFIG_VERSION_KEY, 0);

		if (currentVersion == bundledVersion) {
			reloadConfig();
			return;
		}

		if (currentVersion > bundledVersion) {
			getLogger().warning("Detected a newer config.yml version (" + currentVersion + ") than this build expects (" + bundledVersion + "). Keeping the existing config.");
			reloadConfig();
			return;
		}

		try {
			File backupFile = new File(getBackupDirectory(), buildBackupName(currentVersion));
			Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			saveResource(CONFIG_PATH, true);
			reloadConfig();
			getLogger().warning("config.yml was updated from version " + currentVersion + " to " + bundledVersion + ".");
			getLogger().warning("A backup of the previous config was saved to " + backupFile.getPath() + ".");
		} catch (IOException ex) {
			getLogger().severe("Failed to back up config.yml. The existing config was kept untouched.");
			getLogger().severe(ex.getMessage());
			reloadConfig();
		}
	}

	private int getBundledConfigVersion() {
		try (InputStream stream = getResource(CONFIG_PATH)) {
			if (stream == null) {
				getLogger().warning("Bundled config.yml was not found, defaulting config version to 1.");
				return 1;
			}

			YamlConfiguration bundledConfig = YamlConfiguration.loadConfiguration(
					new InputStreamReader(stream, StandardCharsets.UTF_8)
			);

			return bundledConfig.getInt(CONFIG_VERSION_KEY, 1);
		} catch (IOException ex) {
			getLogger().warning("Failed to read bundled config.yml version, defaulting config version to 1.");
			return 1;
		}
	}

	private File getBackupDirectory() throws IOException {
		File backupDirectory = new File(getDataFolder(), "config-backups");

		if (!backupDirectory.exists() && !backupDirectory.mkdirs()) {
			throw new IOException("Could not create config backup directory at " + backupDirectory.getPath());
		}

		return backupDirectory;
	}

	private String buildBackupName(int version) {
		String versionLabel = version > 0 ? "v" + version : "legacy";
		return "config-" + versionLabel + "-" + LocalDateTime.now().format(BACKUP_TIMESTAMP) + ".yml";
	}

	@Nullable
	public static BukkitTask runSync(@Nonnull Runnable runnable) {
		Preconditions.checkNotNull(runnable, "Cannot run null");

		if (instance == null || !instance.isEnabled()) {
			return null;
		}

		return instance.getServer().getScheduler().runTask(getInstance(), runnable);
	}

}
