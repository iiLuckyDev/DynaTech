package me.profelements.dynatech.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public final class HeadTextures {

	private static final String TEXTURE_BASE_URL = "http://textures.minecraft.net/texture/";

	private HeadTextures() {
	}

	public static ItemStack fromHash(String hash) {
		return fromUrl(TEXTURE_BASE_URL + hash);
	}

	public static ItemStack fromUrl(String url) {
		try {
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			PlayerProfile profile = Bukkit.createPlayerProfile(
					UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8)),
					"DynaTechHead"
			);
			PlayerTextures textures = profile.getTextures();
			textures.setSkin(new URL(url));
			profile.setTextures(textures);
			meta.setOwnerProfile(profile);
			head.setItemMeta(meta);
			return head;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid skin URL: " + url, e);
		}
	}
}
