package org.bapcraft.discordagent.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileAgentStorage implements AgentStorage {

	private File root;
	private Logger logger;

	public FileAgentStorage(File root, Logger logger) {
		this.root = root;
		this.logger = logger;

		if (!root.exists()) {
			root.mkdirs();
		}

	}

	@Override
	public void setDiscordUser(UUID minecraftUuid, UserProfile user) {

		File userFile = this.getUserFile(minecraftUuid);
		if (!userFile.exists()) {
			try {
				userFile.getParentFile().mkdirs();
				userFile.createNewFile();
			} catch (IOException e) {
				this.logger.error("Failed to create Discord user file!", e);
			}
		}

		try {
			FileWriter fw = new FileWriter(userFile);
			Gson gson = this.createGson();
			gson.toJson(user, fw);
			fw.close();
		} catch (IOException e) {
			this.logger.error("Failed to write Discord user!", e);
		}

	}

	@Override
	public UserProfile getDiscordUser(UUID minecraftUuid) {
		File userFile = this.getUserFile(minecraftUuid);
		try (FileReader fr = new FileReader(userFile)) {
			Gson gson = this.createGson();
			return gson.fromJson(fr, UserProfile.class);
		} catch (IOException e) {
			return null; // There wasn't a user profile.
		}
	}

	@Override
	public void deleteDiscordUser(UUID minecraftUuid) {
		File userFile = this.getUserFile(minecraftUuid);
		userFile.delete();
	}

	@Override
	public UUID getMinecraftUuidFromDiscord(long snowflake) {

		File userDir = new File(this.root, "users");
		Gson gson = this.createGson();
		
		// TODO Make this not suck.
		for (File f : userDir.listFiles()) {
			
			try (FileReader fr = new FileReader(f)) {
				UserProfile p = gson.fromJson(fr, UserProfile.class);
				if (p.snowflakeId == snowflake) {
					return UUID.fromString(f.getName().split("\\.")[0]); // Just the part of the UUID before the '.'.
				}
			} catch (IOException e) {
				this.logger.warn("Error reading " + f + ", this probably isn't a big deal though.");
			}
			
		}

		return null;

	}

	private Gson createGson() {
		return new GsonBuilder().setLenient().setPrettyPrinting().create();
	}

	private File getUserFile(UUID uuid) {
		return new File(new File(this.root, "users"), uuid.toString() + ".json");
	}

}
