package org.bapcraft.discordagent.storage;

public class UserProfile {

	public long snowflakeId;
	public String discordUsername;
	public String discordDiscrim;
	public String discordMention;

	public UserProfile(long snowflake, String username, String discrim, String mention) {
		this.snowflakeId = snowflake;
		this.discordUsername = username;
		this.discordDiscrim = discrim;
		this.discordMention = mention;
	}
	
}
