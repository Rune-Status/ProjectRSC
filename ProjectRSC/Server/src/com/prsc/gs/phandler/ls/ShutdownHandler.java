package com.prsc.gs.phandler.ls;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.World;


public class ShutdownHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested shutdown");
        World.getWorld().getServer().kill();
    }

}