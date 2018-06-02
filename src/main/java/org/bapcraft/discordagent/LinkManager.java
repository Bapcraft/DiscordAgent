package org.bapcraft.discordagent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bapcraft.discordagent.storage.AgentStorage;
import org.bapcraft.discordagent.storage.UserProfile;
import org.slf4j.Logger;

public class LinkManager {

	public static final String tokenStrs = "aeioujklty"; // make it short because it's cool

	private Logger logger;
	private Map<String, Long> unredeemedTokens = new HashMap<>();
	private AgentStorage storage;

	private Map<Long, String> discordNames = new HashMap<>();
	private Map<Long, String> discordDiscrims = new HashMap<>();
	private Map<Long, String> discordMention = new HashMap<>();

	public LinkManager(Logger logger, AgentStorage storage) {
		this.logger = logger;
		this.storage = storage;
	}
	
	public String createNewToken(long discordId, String name, String discrim, String mention) {

		UUID oldUuid = this.storage.getMinecraftUuidFromDiscord(discordId);
		boolean deleted = false;
		if (oldUuid != null) {
			boolean succ = this.storage.deleteDiscordUser(oldUuid);
			deleted = true;
		}
		
		Long did = Long.valueOf(discordId);

		String token = getTokenFor(discordId);
		if (token == null) {
			token = generateToken();
			this.unredeemedTokens.put(token, did);
		}

		String prettyName = String.format("@%s#%s", name, discrim);
		this.logger.info("Created new Discord token for " + prettyName + ": " + token);
		if (deleted) {
			this.logger.warn("Had to unlink" + prettyName + " from Minecraft user " + oldUuid + "!");
		}

		// Caching of stuff to use for the profiles.
		this.discordNames.put(did, name);
		this.discordDiscrims.put(did, discrim);
		this.discordMention.put(did, mention);

		return token;

	}

	private String getTokenFor(long discordId) {

		for (Map.Entry<String, Long> e : this.unredeemedTokens.entrySet()) {
			if (e.getValue().longValue() == discordId) {
				return e.getKey();
			}
		}

		return null;

	}

	public boolean redeemToken(String token, UUID uuid) {

		Long discordId = this.unredeemedTokens.get(token);

		if (discordId == null) {
			return false;
		}

		UserProfile prof = this.storage.getDiscordUser(uuid);

		long did = discordId.longValue();
		String name = this.discordNames.get(discordId);
		String discrim = this.discordDiscrims.get(discordId);
		String mention = this.discordMention.get(discordId);

		if (prof == null) {
			prof = new UserProfile(did, name, discrim, mention);
		} else {
			prof.snowflakeId = did;
			prof.discordUsername = name;
			prof.discordDiscrim = discrim;
		}
		
		// Remove all the old data cached.
		this.discordNames.remove(discordId);
		this.discordDiscrims.remove(discordId);
		this.unredeemedTokens.remove(token);

		// Actually store the player's ID.
		this.storage.setDiscordUser(uuid, prof);

		// And since we didn't get an exception here we can return success.
		return true;

	}

	private static String generateToken() {

		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for (int i = 0; i < 24; i++) {
			sb.append(tokenStrs.charAt(r.nextInt(tokenStrs.length())));
		}

		return sb.toString();

	}

}
