package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.config.Formulae;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.event.impl.FightEvent;
import com.prsc.gs.event.impl.RangeEvent;
import com.prsc.gs.event.impl.WalkToMobEvent;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.PathGenerator;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.world.World;


public class AttackHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		
		player.resetAll();
		Mob affectedMob = null;
		int serverIndex = p.readShort();
		
		if (pID == 57) {
			affectedMob = world.getPlayer(serverIndex);
		} else if (pID == 73) {
			affectedMob = world.getNpc(serverIndex);
		}
		
		if (affectedMob == null || affectedMob.equals(player)) {
			player.resetPath();
			return;
		}
		if (affectedMob instanceof Player) {
			Player pl = (Player) affectedMob;
			if (pl.inCombat() && player.getRangeEquip() < 0) {
				return;
			}
			if (pl.getLocation().inWilderness() && System.currentTimeMillis() - pl.getLastRun() < 3000) {
				return;
			}
			// Determine who gets the attack
			/*for(Player attackingPlayer : pl.getViewArea().getPlayersInView()) { 
				if(attackingPlayer.getFollowing() == pl) {
					return;
				}
			} */
		} 
		
		player.setFollowing(affectedMob);
		player.setStatus(Action.ATTACKING_MOB);

		if (player.getRangeEquip() < 0) {
			World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, affectedMob, affectedMob instanceof Npc ? 1 : 2) {
				public void arrived() {
					owner.resetPath();
					owner.resetFollowing();
					
					if (owner.isBusy() || affectedMob.isBusy() || !owner.nextTo(affectedMob) || !owner.checkAttack(affectedMob, false) || owner.getStatus() != Action.ATTACKING_MOB) {
						return;
					}
					if (affectedMob instanceof Npc) {
						if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerAttackNpc", new Object[]{owner, (Npc)affectedMob})) {
							return;
						}
					}
					if (affectedMob instanceof Player) {
						if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerAttack", new Object[]{owner, affectedMob})) {
							return;
						}
					}
					
					if (affectedMob.getID() == 35) {
						owner.getActionSender().sendMessage("Delrith can not be attacked without the Silverlight sword");
						return;
					}
					
					owner.resetAll();
					owner.setStatus(Action.FIGHTING_MOB);
					if (affectedMob instanceof Player) {
						Player affectedPlayer = (Player) affectedMob;
						owner.setSkulledOn(affectedPlayer);
						affectedPlayer.resetAll();
						affectedPlayer.setStatus(Action.FIGHTING_MOB);
						affectedPlayer.getActionSender().sendSound("underattack");
						affectedPlayer.getActionSender().sendMessage("You are under attack!");
						
						if (affectedPlayer.isSleeping()) {
							affectedPlayer.getActionSender().sendWakeUp(false, false);
							affectedPlayer.getActionSender().sendFatigue(affectedPlayer.getFatigue());
						}
						//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(owner.getUsername() + " attacked " + affectedPlayer.getUsername() + " at location " + affectedPlayer.getLocation()));
					} else if (affectedMob instanceof Npc) {
						Npc npc = (Npc) affectedMob;
						//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(owner.getUsername() + " attacked npc " + npc.getDef().getName() + " at location " + npc.getLocation()));
					}
					affectedMob.resetPath();

					owner.setLocation(affectedMob.getLocation(), true);

					for (Player p : owner.getViewArea().getPlayersInView()) {
						p.removeWatchedPlayer(owner);
					}

					owner.setBusy(true);
					owner.setSprite(9);
					owner.setOpponent(affectedMob);
					owner.setCombatTimer();
					affectedMob.setBusy(true);
					affectedMob.setSprite(8);
					affectedMob.setOpponent(owner);
					affectedMob.setCombatTimer();
					FightEvent fighting = new FightEvent(owner, affectedMob);
					fighting.setLastRun(0);
					World.getWorld().getDelayedEventHandler().add(fighting);
				}
			});
		} else {
			if (!new PathGenerator(player.getX(), player.getY(), affectedMob.getX(), affectedMob.getY()).isValid()) {
				player.getActionSender().sendMessage("I can't get a clear shot from here");
				player.resetPath();
				player.resetFollowing();
				return;
			}
			
			int radius = 7;
			if (player.getRangeEquip() == 59 || player.getRangeEquip() == 60)
				radius = 5;
			if (player.getRangeEquip() == 189)
				radius = 4;
			
			World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, affectedMob, radius) {
				public void arrived() {
					owner.resetPath();
					if (owner.isBusy() || !owner.checkAttack(affectedMob, true) || owner.getStatus() != Action.ATTACKING_MOB) {
						return;
					}

					if (!new PathGenerator(owner.getX(), owner.getY(), affectedMob.getX(), affectedMob.getY()).isValid()) {
						owner.getActionSender().sendMessage("I can't get a clear shot from here");
						owner.resetPath();
						return;
					}

					if (affectedMob instanceof Npc) {
						Npc npc = (Npc) affectedMob;
						//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(owner.getUsername() + " ranged npc " + npc.getDef().getName() + " at location " + npc.getLocation()));
					}
					
					if (affectedMob.getID() == 35) {
						owner.getActionSender().sendMessage("Delrith can not be attacked without the Silverlight sword");
						return;
					}
					owner.resetAll();
					owner.setStatus(Action.RANGING_MOB);
					
					if (affectedMob instanceof Player) {
						Player affectedPlayer = (Player) affectedMob;
						owner.setSkulledOn(affectedPlayer);
						affectedPlayer.resetTrade();
						if (affectedPlayer.getMenuHandler() != null) {
							affectedPlayer.resetMenuHandler();
						}
						if (affectedPlayer.accessingBank()) {
							affectedPlayer.resetBank();
						}
						if (affectedPlayer.accessingShop()) {
							affectedPlayer.resetShop();
						}
						if (affectedPlayer.getNpc() != null) {
							affectedPlayer.getNpc().unblock();
							affectedPlayer.setNpc(null);
						}	
						//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(owner.getUsername() + " ranged " + affectedPlayer.getUsername() + " at location " + affectedPlayer.getLocation()));
					}
					if (Formulae.getRangeDirection(owner, affectedMob) != -1)
						owner.setSprite(Formulae.getRangeDirection(owner, affectedMob));

					owner.setRangeEvent(new RangeEvent(owner, affectedMob));
				}
			});
		}
	}
}