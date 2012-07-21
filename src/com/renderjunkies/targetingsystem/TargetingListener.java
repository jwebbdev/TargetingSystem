package com.renderjunkies.targetingsystem;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class TargetingListener implements Listener
{
	TargetingSystem ts;
	
	public TargetingListener(TargetingSystem ts)
	{
		this.ts = ts;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof LivingEntity)
		{
			LivingEntity damagedEntity = (LivingEntity) event.getEntity();

			if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) event;
				Entity damager = edee.getDamager();
				
				if(damager instanceof Player)
				{
					Player pDmgr = (Player) damager;
					ts.targetEntity(pDmgr, damagedEntity);
				}	
				else if(damager instanceof Projectile)
				{
					Projectile proj = (Projectile) damager;
					
					// Could be a skeleton
					if(proj.getShooter() instanceof Player)
					{
						Player pDmgr = (Player) proj.getShooter();
						ts.targetEntity(pDmgr, damagedEntity);
					}
				}
			}
			
			List<Player> players = ts.getTargetedBy(damagedEntity);
			if(players != null)
			{
				for(Player player : players)
				{
					ts.updateTargetHealth(player, damagedEntity, event.getDamage());
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		event.setDroppedExp(0);
		ts.removeTarget((LivingEntity) event.getEntity());
	}
}