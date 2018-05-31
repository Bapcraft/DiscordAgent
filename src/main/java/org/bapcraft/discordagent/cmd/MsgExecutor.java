package org.bapcraft.discordagent.cmd;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;

public class MsgExecutor implements CommandExecutor {

	private DiscordAgentService serv;

	public MsgExecutor(DiscordAgentService serv) {
		this.serv = serv;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		User user = (User) args.getOne("user").get();
		String msg = (String) args.getOne("msg").get();

		this.serv.sendMessage(user.getUniqueId(), msg);

		return CommandResult.success();

	}

}
