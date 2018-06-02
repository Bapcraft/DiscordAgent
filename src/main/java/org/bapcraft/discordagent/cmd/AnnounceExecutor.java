package org.bapcraft.discordagent.cmd;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class AnnounceExecutor implements CommandExecutor {

	private DiscordAgentService serv;

	public AnnounceExecutor(DiscordAgentService serv) {
		this.serv = serv;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		String msg = (String) args.getOne("msg").get();
		this.serv.broadcast(msg);

		src.sendMessage(Text.of("Message sent: " + msg));

		return CommandResult.success();

	}

}
