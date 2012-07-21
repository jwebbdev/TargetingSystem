package com.renderjunkies.targetingsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TargetingSystem extends JavaPlugin
{
	// Entity is targeted by Array of Players
	private Map<LivingEntity, List<Player>> targetedBy;
	
	// Player is targeting Entity
	private Map<Player, LivingEntity> targeting;

	
	@Override
	public void onEnable()
	{
		targetedBy = new HashMap<LivingEntity, List<Player>>();
		targeting = new HashMap<Player, LivingEntity>();

		getServer().getPluginManager().registerEvents(new TargetingListener(this),  this);
		getLogger().info("TargetingSystem has been enabled.");
	}

	public void onDisable()
	{
		getLogger().info("TargetingSystem has been disabled.");
		targetedBy.clear();
		targeting.clear();
	}
	
	public List<Player> getTargetedBy(LivingEntity entity)
	{
		List<Player> players = null;
		
		if(targetedBy.containsKey(entity))
			players = targetedBy.get(entity);
		
		return players;
	}
	
	public LivingEntity getTarget(Player player)
	{
		LivingEntity entity = null;
		
		if(targeting.containsKey(player))
			entity = targeting.get(player);
		
		return entity;
	}
	
	public void updateTargetHealth(Player player, LivingEntity target)
	{
		updateTargetHealth(player, target, 0);
	}
	public void updateTargetHealth(Player player, LivingEntity target, int dmg)
	{
		player.setExp((float)(target.getHealth()-dmg) / (float)target.getMaxHealth());
		if(target.getHealth() == 0)
		{
			targeting.remove(player);
			targetedBy.get(target).remove(player);
		}
	}
	
	public void targetEntity(Player player, LivingEntity target)
	{
		// Check to see if the player has a current target
		if(targeting.containsKey(player))
		{
			// Remove the player as a 'targetedBy' for the oldTarget
			LivingEntity oldTarget = getTarget(player);
			if(oldTarget != null)
			{
				targetedBy.get(oldTarget).remove(player);
			}
		}
		// Switch to/insert new target
		targeting.put(player, target);
		
		// Add the player as a targetedBy for the new target
		if(!targetedBy.containsKey(target))
		{
			List<Player> players = new ArrayList<Player>();
			targetedBy.put(target, players);
		}
		targetedBy.get(target).add(player);
	}
	
	public void removeTarget(LivingEntity target)
	{
		if(targetedBy.containsKey(target))
		{
			List<Player> players = targetedBy.get(target);
			if(players != null)
			{
				for(Player curPlayer : players)
				{
					if(targeting.containsKey(curPlayer) && targeting.get(curPlayer) == target)
					{
						targeting.remove(curPlayer);
						curPlayer.setExp(0);
					}
				}
			}
			targetedBy.remove(target);
		}
	}
	
	
}