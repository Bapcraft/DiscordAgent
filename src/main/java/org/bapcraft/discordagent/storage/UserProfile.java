package org.bapcraft.discordagent.storage;

public class UserProfile {

	public long snowflakeId;
	public String discordUsername;
	public String discordDiscrim;

	public UserProfile(long snowflake, String username, String discrim) {
		this.snowflakeId = snowflake;
		this.discordUsername = username;
		this.discordDiscrim = discrim;
	}
	
}
