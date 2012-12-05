package com.prsc.gs.event.impl;

import com.prsc.config.Constants;
import com.prsc.config.Formulae;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Path;
import com.prsc.gs.model.Player;
import com.prsc.gs.states.CombatState;
import com.prsc.gs.tools.DataConversions;

import java.util.ArrayList;

public final class FightEvent extends DelayedEvent {

    private Mob affectedMob;
    private int firstHit, hits;
    private boolean attacked = false, invincibleMode = false;
    boolean delay = false;
    int dela = 0;

    public FightEvent(Player owner, Mob affectedMob) {
        this(owner, affectedMob, false);
    }

    /**
     * Slowed down a fight if PvP in wildy. - not in use atm
     */
    public static int getDelay(Mob owner, Mob affectedMob) {
        if (owner instanceof Player && affectedMob instanceof Player) {
            if (((Player) owner).getLocation().inWilderness()) {
                return 1400;
            }
        }
        return 1100;
    }

    public FightEvent(Player owner, Mob affectedMob, boolean attacked) {
        super(owner, 1400);
        this.affectedMob = affectedMob;
        firstHit = attacked ? 1 : 0;
        this.attacked = attacked;
        hits = 0;
    }

    public boolean equals(Object o) {
        if (o instanceof FightEvent) {
            FightEvent e = (FightEvent) o;
            return e.belongsTo(owner) && e.getAffectedMob().equals(affectedMob);
        }
        return false;
    }

    public Mob getAffectedMob() {
        return affectedMob;
    }
    
    public void setOpponentInvincible(boolean invincibleMode) {
    	this.invincibleMode = invincibleMode;
    }

    public void run() {
        if (!owner.loggedIn() || (affectedMob instanceof Player && !((Player) affectedMob).loggedIn())) {
            owner.resetCombat(CombatState.ERROR);
            affectedMob.resetCombat(CombatState.ERROR);
            return;
        }
        /* Disabled due to overturned
        if (owner.isSleeping() && !owner.isPrayerActivated(12)) {
        	owner.getActionSender().sendWakeUp(false, false);
        	owner.getActionSender().sendFatigue(owner.getFatigue());
        } */

        Mob attacker, opponent;

        if (hits++ % 2 == firstHit) {
            attacker = owner;
            opponent = affectedMob;
        } else {
            attacker = affectedMob;
            opponent = owner;
        }

        if (attacker instanceof Npc) {
            Npc n = (Npc) attacker;
            if (attacker.getHits() <= 0) {
                n.resetCombat(CombatState.ERROR);
            }
        }
        if (opponent instanceof Npc) {
            Npc n = (Npc) opponent;
            if (opponent.getHits() <= 0) {
                n.resetCombat(CombatState.ERROR);
            }
        }

        attacker.incHitsMade();
        if (attacker instanceof Npc && opponent.isPrayerActivated(12)) {
            return;
        }
        int damage = (attacker instanceof Player && opponent instanceof Player ? Formulae.calcFightHit(attacker, opponent) : Formulae.calcFightHitWithNPC(attacker, opponent));
        if (opponent instanceof Npc && attacker instanceof Player) {
            Npc n = (Npc) opponent;
            Player p = ((Player) attacker);
            n.addCombatDamage(p, damage);
        } else if (opponent instanceof Player && attacker instanceof Npc) {
            Npc n = (Npc) attacker;
            Player p = ((Player) opponent);
            n.addCombatDamage(p, damage);
        }
        if (attacker instanceof Npc && opponent instanceof Player && attacker.getHitsMade() >= (attacked ? 4 : 3)) {
            Npc npc = (Npc) attacker;
            Player player = (Player) opponent;
            if (npc.getCurHits() <= npc.getDef().hits * 0.10 && npc.getCurHits() > 0) {
                if (!npc.getLocation().inWilderness() && npc.getDef().attackable && !npc.getDef().aggressive) {
                    if (DataConversions.inArray(Constants.GameServer.NPCS_THAT_DONT_RETREAT, npc.getID())) {
                        player.getActionSender().sendSound("retreat");
                        npc.unblock();
                        npc.resetCombat(CombatState.RUNNING);
                        player.resetCombat(CombatState.WAITING);
                        npc.setRan(true);
                        npc.setPath(new Path(attacker.getX(), attacker.getY(), DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()), DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY())));
                        player.resetAll();
                        player.getActionSender().sendMessage("Your opponent is retreating");
                        return;
                    }
                }
            }
        }

        opponent.setLastDamage(damage);
        int newHp = opponent.getHits() - damage;
        opponent.setHits(newHp);
        String combatSound = null;
        combatSound = damage > 0 ? "combat1b" : "combat1a";
        if (opponent instanceof Player) {
            if (attacker instanceof Npc) {
                Npc n = (Npc) attacker;
                if (n.hasArmor) {
                    combatSound = damage > 0 ? "combat2b" : "combat2a";
                } else if (n.undead) {
                    combatSound = damage > 0 ? "combat3b" : "combat3a";
                } else {
                    combatSound = damage > 0 ? "combat1b" : "combat1a";
                }
            }
            Player opponentPlayer = ((Player) opponent);
            opponentPlayer.getActionSender().sendStat(3);
            opponentPlayer.getActionSender().sendSound(combatSound);
        }
        if (attacker instanceof Player) {
            if (opponent instanceof Npc) {
                Npc n = (Npc) opponent;
                if (n.hasArmor) {
                    combatSound = damage > 0 ? "combat2b" : "combat2a";
                } else if (n.undead) {
                    combatSound = damage > 0 ? "combat3b" : "combat3a";
                } else {
                    combatSound = damage > 0 ? "combat1b" : "combat1a";
                }
                //
            }
            Player attackerPlayer = (Player) attacker;
            attackerPlayer.getActionSender().sendSound(combatSound);
        }

        if (newHp <= 0) {
            if (opponent instanceof Player) {
                Player p = (Player) opponent;

                if (p.isMod()) {
                    p.setHits(p.getMaxStat(3));
                    p.getActionSender().sendStat(3);
                    return;
                }
            }
            if (opponent instanceof Npc) {
            	Npc n = (Npc) opponent;
            	
            	if(invincibleMode) {
                	n.setHits(n.getDef().getHits());
                	return;
                }
            }
            opponent.killedBy(attacker, false);

            attacker.resetCombat(CombatState.WON);
            opponent.resetCombat(CombatState.LOST);
        } else { // show hits
            ArrayList<Player> playersToInform = new ArrayList<Player>();
            playersToInform.addAll(opponent.getViewArea().getPlayersInView());
            playersToInform.addAll(attacker.getViewArea().getPlayersInView());
            for (Player p : playersToInform) {
                p.informOfModifiedHits(opponent);
            }
        }
    }
}