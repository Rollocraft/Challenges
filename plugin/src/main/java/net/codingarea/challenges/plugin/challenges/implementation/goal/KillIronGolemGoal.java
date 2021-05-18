package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.commons.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.KillEntityGoal;
import net.codingarea.challenges.plugin.language.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
@Since("2.0")
public class KillIronGolemGoal extends KillEntityGoal {

	public KillIronGolemGoal() {
		super(EntityType.IRON_GOLEM);
		this.killerNeeded = true;
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.IRON_INGOT, Message.forName("item-iron-golem-goal"));
	}

	@Nonnull
	@Override
	public SoundSample getStartSound() {
		return new SoundSample().addSound(Sound.ENTITY_IRON_GOLEM_HURT, 1);
	}

}
