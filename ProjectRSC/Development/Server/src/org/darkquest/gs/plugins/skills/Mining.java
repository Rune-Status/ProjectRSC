package org.darkquest.gs.plugins.skills;

import org.darkquest.config.Constants;
import org.darkquest.config.Formulae;
import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.external.ObjectMiningDef;
import org.darkquest.gs.model.*;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;

import java.util.Arrays;


public class Mining implements ObjectActionListener {

	static int[] ids;

	static {
		ids = new int[]{176, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211};
		Arrays.sort(ids);
	}

	@Override
	public void onObjectAction(final GameObject object, String command, Player owner) {

		if (Arrays.binarySearch(ids, object.getID()) >= 0) {
			handleMining(object, owner, owner.click);
			return;
		}
	}

	/**
	 * Picks the best axe the player can use
	 *
	 * @param p
	 * @return
	 */
	
	public int getAxe(Player p) {
		int lvl = p.getCurStat(14);
		
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
				if (lvl >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				}
			}
		} 
		return -1; 
	} 

	public void handleMining(final GameObject object, Player owner, int click) {
		if (owner.isBusy() || owner.inCombat()) {
			return;
		}
		if (!owner.withinRange(object, 1))
			return;


		final GameObject newobject = World.getWorld().getTile(object.getX(), object.getY()).getGameObject();
		final ObjectMiningDef def = EntityHandler.getObjectMiningDef(newobject.getID());
		if (def == null || def.getRespawnTime() < 1) {
			owner.getActionSender().sendMessage("There is currently no ore available in this rock.");
			return;
		}
		final InvItem ore = new InvItem(def.getOreId());
		if (owner.click == 1) {
			owner.setBusy(true);
			owner.getActionSender().sendMessage("You examine the rock for ores");
			World.getWorld().getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
				@Override
				public void action() {
					owner.getActionSender().sendMessage("This rock contains " + ore.getDef().getName() + ".");
					owner.setBusy(false);
				}
			});
			return;
		}
		if (owner.getCurStat(14) < def.getReqLevel()) {
			owner.getActionSender().sendMessage("You need a mining level of " + def.getReqLevel() + " to mine this rock.");
			return;
		}
		final int axeId = getAxe(owner);

		if (axeId < 0) {
			owner.getActionSender().sendMessage("You need a pickaxe to mine this rock.");
			return;
		}

		int retrytimes = -1;
		final int mineLvl = owner.getCurStat(14);
		int reqlvl = 1;
		switch (axeId) {
		case 1258:
			retrytimes = 2;
			break;
		case 1259:
			retrytimes = 3;
			reqlvl = 6;
			break;
		case 1260:
			retrytimes = 5;
			reqlvl = 21;
			break;
		case 1261:
			retrytimes = 8;
			reqlvl = 31;
			break;
		case 1262:
			retrytimes = 12;
			reqlvl = 41;
			break;
		}
		
		final int shots = retrytimes;

		if (reqlvl > mineLvl) {
			owner.getActionSender().sendMessage("You need to be level " + reqlvl + " to use this pick.");
			return;
		}
		if(owner.getFatigue() >= 7500) {
        	owner.getActionSender().sendMessage("You are too tired to mine this rock");
        	return;
        }
		owner.setBusy(true);
		if(owner.lastMineTries == -1) {
			owner.lastMineTries = 0;
		}
		owner.lastMineTries++;
		owner.getActionSender().sendSound("mine");
		Bubble bubble = new Bubble(owner, 1258); // default back to iron only (verified http://www.youtube.com/watch?v=CZtVBarTMjY)
		for (Player p : owner.getViewArea().getPlayersInView()) {
			p.informOfBubble(bubble);
		}

		owner.getActionSender().sendMessage("You swing your pick at the rock...");
		World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
			public void action() {
				if (Formulae.getOre(def, owner.getCurStat(14), axeId)) {
					if (DataConversions.random(0, 200) == 0) {
						InvItem gem = new InvItem(Formulae.getGem(), 1);
						owner.incExp(14, 100, true);
						owner.getInventory().add(gem);
						owner.getActionSender().sendMessage("You found a gem!");
						owner.lastMineTries = -1;
					} else {
						owner.getInventory().add(ore);
						owner.getActionSender().sendMessage("You manage to obtain some " + ore.getDef().getName() + ".");
						owner.incExp(14, def.getExp(), true);
						owner.getActionSender().sendStat(14);
						owner.lastMineTries = -1;
						world.registerGameObject(new GameObject(object.getLocation(), 98, object.getDirection(), object.getType()));
						world.delayedSpawnObject(newobject.getLoc(), def.getRespawnTime() * 1000);
					}
					owner.getActionSender().sendInventory();
					if (!owner.getInventory().full()) {
						world.getDelayedEventHandler().add(new SingleEvent(owner, 500) {
							public void action() {
								if(Constants.GameServer.BATCH_EVENTS)
									handleMining(object, owner, owner.click);
							}
						});
					}
				} else {
					owner.getActionSender().sendMessage("You only succeed in scratching the rock.");
					owner.isMining(false);
						world.getDelayedEventHandler().add(new SingleEvent(owner, 500) {
							public void action() {
								if(owner.lastMineTries < shots)
									handleMining(object, owner, owner.click);
							}
						});
					
				}
				owner.setBusy(false);
			}
		});
	}
}
