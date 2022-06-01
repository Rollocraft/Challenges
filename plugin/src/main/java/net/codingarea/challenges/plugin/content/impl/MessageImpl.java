package net.codingarea.challenges.plugin.content.impl;

import net.anweisen.utilities.common.collection.IRandom;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.ItemDescription;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.misc.FontUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public class MessageImpl implements Message {

	protected final String name;
	protected String[] value;

	public MessageImpl(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	@Override
	public String asString(@Nonnull Object... args) {
		if (value == null) return Message.NULL;
		return String.join("\n", asArray(args));
	}

	@Nonnull
	@Override
	public String asRandomString(@Nonnull Object... args) {
		return asRandomString(defaultRandom(), args);
	}

	@Nonnull
	@Override
	public String asRandomString(@Nonnull IRandom random, @Nonnull Object... args) {
		String[] array = asArray(args);
		if (array.length == 0) return Message.unknown(name);
		return random.choose(array);
	}

	@Nonnull
	@Override
	public BaseComponent asRandomComponent(@NotNull IRandom random, @Nonnull Prefix prefix, @NotNull Object... args) {
		BaseComponent[] array = asComponentArray(prefix, args);
		if (array.length == 0) return new TextComponent(Message.unknown(name));
		return random.choose(array);
	}

	@Nonnull
	@Override
	public String[] asArray(@Nonnull Object... args) {
		if (value == null) return new String[]{ Message.unknown(name) };
		args = BukkitStringUtils.replaceArgumentStrings(args, true);
		LanguageLoader loader = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class);
		boolean capsFont = false;
		if (loader != null) capsFont = loader.isSmallCapsFont();
		return capsFont ? FontUtils.toSmallCaps(StringUtils.format(value, args)) : StringUtils.format(value, args);
	}

	@Nonnull
	@Override
	public BaseComponent[] asComponentArray(@Nonnull Prefix prefix, @NotNull Object... args) {
		if (value == null) return new TextComponent[] { new TextComponent(Message.unknown(name)) };
		return BukkitStringUtils.format(prefix, value, args);
	}

	@Nonnull
	@Override
	public ItemDescription asItemDescription(@Nonnull Object... args) {
		if (value == null) {
			Message.unknown(name);
			return ItemDescription.empty();
		}
		return new ItemDescription(asArray(args));
	}

	@Override
	public void send(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLines(component -> target.spigot().sendMessage(component), prefix, asComponentArray(prefix, args));
	}

	@Override
	public void sendRandom(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		sendRandom(defaultRandom(), target, prefix, args);
	}

	@Override
	public void sendRandom(@Nonnull IRandom random, @Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLine(components -> target.spigot().sendMessage(components), prefix, asRandomComponent(random, prefix, args));
	}

	@Override
	public void broadcast(@Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLines(components -> Bukkit.spigot().broadcast(components), prefix, asComponentArray(prefix, args));
	}

	@Override
	public void broadcastRandom(@Nonnull Prefix prefix, @Nonnull Object... args) {
		broadcastRandom(defaultRandom(), prefix, args);
	}

	@Override
	public void broadcastRandom(@Nonnull IRandom random, @Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLine(component -> Bukkit.spigot().broadcast(component), prefix, asRandomComponent(random, prefix, args));
	}

	private void doSendLines(@Nonnull Consumer<? super BaseComponent> sender, @Nonnull Prefix prefix, @Nonnull BaseComponent[] components) {
		for (BaseComponent line : components) {
			doSendLine(sender, prefix, line);
		}
	}

	private void doSendLine(@Nonnull Consumer<? super BaseComponent> sender, @Nonnull Prefix prefix, @Nonnull BaseComponent components) {
//		if (components.length == 0 || components[0].toLegacyText().isEmpty()) {
//			sender.accept(components);
//		} else {
//			ArrayList<BaseComponent> list = new ArrayList<>();
//			list.add(0, new TextComponent(prefix.toString()));
//			list.addAll(Arrays.asList(components));
//			sender.accept(list.toArray(new BaseComponent[0]));
//		}
		sender.accept(components);
	}

	@Override
	public void broadcastTitle(@Nonnull Object... args) {
		String[] title = asArray(args);
		Bukkit.getOnlinePlayers().forEach(player -> doSendTitle(player, title));
	}

	@Override
	public void sendTitle(@Nonnull Player player, @Nonnull Object... args) {
		doSendTitle(player, asArray(args));
	}

	@Override
	public void sendTitleInstant(@Nonnull Player player, @Nonnull Object... args) {
		doSendTitleInstant(player, asArray(args));
	}

	protected void doSendTitle(@Nonnull Player player, @Nonnull String[] title) {
		sendTitle(title, (line1, line2) -> Challenges.getInstance().getTitleManager().sendTitle(player, line1, line2));
	}

	protected void doSendTitleInstant(@Nonnull Player player, @Nonnull String[] title) {
		sendTitle(title, (line1, line2) -> Challenges.getInstance().getTitleManager().sendTitleInstant(player, line1, line2));
	}

	protected void sendTitle(@Nonnull String[] title, @Nonnull BiConsumer<String, String> send) {
		if (title.length == 0) send.accept("", "");
		else if (title.length == 1) send.accept(title[0], "");
		else send.accept(title[0], title[1]);
	}

	@Override
	public void setValue(@Nonnull String[] value) {
		this.value = value;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return asString();
	}

	@Nonnull
	protected static IRandom defaultRandom() {
		return IRandom.threadLocal();
	}

}
