package org.bapcraft.discordagent.storage;

import java.util.UUID;

public interface AgentStorage {

	void setDiscordUser(UUID minecraftUuid, UserProfile user);

	UserProfile getDiscordUser(UUID minecraftUuid);

	boolean deleteDiscordUser(UUID minecraftUuid);

	UUID getMinecraftUuidFromDiscord(long snowflake);

}
