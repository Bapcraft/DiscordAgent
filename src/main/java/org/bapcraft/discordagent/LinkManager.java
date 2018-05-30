package org.bapcraft.discordagent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bapcraft.discordagent.storage.AgentStorage;
import org.bapcraft.discordagent.storage.UserProfile;

public class LinkManager {

	public static final String tokenStrs = "aeioujklty"; // make it short because it's cool

	private Map<String, Long> unredeemedTokens = new HashMap<>();
	private AgentStorage storage;

	private Map<Long, String> discordNames = new HashMap<>();
	private Map<Long, String> discordDiscrims = new HashMap<>();

	public LinkManager(AgentStorage storage) {
		this.storage = storage;
	}
	
	public String createNewToken(long discordId, String name, String discrim) {
		
		Long did = Long.valueOf(discordId);

		String token = getTokenFor(discordId);
		if (token == null) {
			token = generateToken();
			this.unredeemedTokens.put(token, did);
		}

		// Caching of stuff to use for the profiles.
		this.discordNames.put(did, name);
		this.discordDiscrims.put(did, discrim);

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

		if (prof == null) {
			prof = new UserProfile(did, name, discrim);
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
