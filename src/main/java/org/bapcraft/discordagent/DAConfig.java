package org.bapcraft.discordagent;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DAConfig {

	@Setting(value = "bot_token")
	public String botAuthToken = "";

}
