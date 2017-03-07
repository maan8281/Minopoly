package de.wolfi.minopoly.commands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;

import de.wolfi.minopoly.Main;
import de.wolfi.minopoly.components.Minopoly;
import de.wolfi.minopoly.components.Player;
import de.wolfi.minopoly.utils.Messages;

public abstract class CommandInterface implements Listener, TabExecutor {

	private final Main main;
	private final int minArgs;

	public CommandInterface(Main plugin, int minArgs, boolean listener) {
		this.main = plugin;
		this.minArgs = minArgs;
		if (listener)
			Bukkit.getServer().getPluginManager().registerEvents(this, this.main);
	}

	protected abstract void executeCommand(Minopoly board, Player player, String[] args);

	@Nullable
	private Minopoly getGameBoard(CommandSender sender) {
		if (sender instanceof Minopoly)
			return (Minopoly) sender;
		else if (sender instanceof org.bukkit.entity.Player)
			if (this.main.isMinopolyWorld(((org.bukkit.entity.Player) sender).getWorld()))
				return this.main.getMinopoly(((org.bukkit.entity.Player) sender).getWorld());

		return null;
	}

	public Main getMain() {
		return this.main;
	}

	public int getMinArgs() {
		return this.minArgs;
	}

	@Nullable
	private Player getPlayer(Minopoly game, String string) {
		return game.getByPlayerName(string);
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (arg3.length < this.minArgs + 1) {
			Messages.COMMAND_NO_ARGUMENTS.send(arg0);
			return true;
		}
		final Minopoly m = this.getGameBoard(arg0);
		if (m == null) {
			Messages.COMMAND_WRONG_WORLD.send(arg0, String.valueOf(this.minArgs));
			return true;
		}
		final Player player = this.getPlayer(m, arg3[0]);
		if (player == null) {
			Messages.COMMAND_NO_PLAYER.send(arg0, arg3[0]);
			return true;
		}
		this.executeCommand(m, player, Arrays.copyOfRange(arg3, 1, arg3.length));
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

}