package org.bapcraft.discordagent.cmd;

import org.bapcraft.discordagent.LinkManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class LinkExecutor implements CommandExecutor {

	private LinkManager links;

	public LinkExecutor(LinkManager linkMan) {
		this.links = linkMan;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (src instanceof Player) {

			Player p = (Player) src;
			
			String token = (String) args.getOne("token").get();
			
			boolean succ = this.links.redeemToken(token, p.getUniqueId());

			if (succ) {
				src.sendMessage(Text.of("Token redeemed."));
				return CommandResult.success();
			} else {
				src.sendMessage(Text.of("Token redemption failed, perhaps was already used or expired?"));
			}
			
		}
		
		return CommandResult.empty();

	}

}
