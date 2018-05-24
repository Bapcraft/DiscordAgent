package org.bapcraft.discordagent.storage;

public class UserProfile {

	public long snowflakeId;
	public String discordUsername;
	public int discordDiscrim;

	public UserProfile(long snowflake, String username, int discrim) {
		this.snowflakeId = snowflake;
		this.discordUsername = username;
		this.discordDiscrim = discrim;
	}
	
}
