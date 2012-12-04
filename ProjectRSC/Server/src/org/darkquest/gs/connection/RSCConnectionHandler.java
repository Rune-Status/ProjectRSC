package org.darkquest.gs.connection;

import org.darkquest.gs.core.GameEngine;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.util.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;

public final class RSCConnectionHandler extends IdleStateAwareChannelHandler {

    private final GameEngine engine;

    public RSCConnectionHandler(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Invoked whenever an exception is caught
     *
     * @param ctx The channel chandler context
     * @param e   The message event
     */
//    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
////        Player p = (Player) ctx.getAttachment();
////        if (p != null)
////            p.getActionSender().sendLogout();
////        ctx.getChannel().close();
//        e.getCause().getStackTrace();
//    }


    /**
     * Invoked whenever a packet is ready to be added to the queue.
     *
     * @param ctx The channel chandler context
     * @param e   The message event
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel ch = ctx.getChannel();
        Player player = (Player) ch.getAttachment();
        
        if (ch.isConnected() && !player.destroyed()) {
            RSCPacket p = (RSCPacket) e.getMessage();
            player.addPacket(p); // Used to log packets for macro detection
            engine.addPacket(p); // This one actually results in the packet being processed!
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	//e.getCause().printStackTrace();
    }

    /**
     * Invoked whenever a a channel is closed. This must handle unregistering
     * the disconnecting world from the engine.
     *
     * @param ctx The channel chandler context
     * @param e   The state event
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Player player = (Player) ctx.getAttachment();
        if (player != null && !player.destroyed()) {
            player.destroy(false);
        }
    }

    /**
     * Invoked whenever a channel is connected
     *
     * @param ctx The channel chandler context
     * @param e   The state event
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel session = ctx.getChannel();
        session.setAttachment(new Player(session));
        //session.setIdleTime(IdleStatus.BOTH_IDLE, 30);
        //session.getConfig().getConnectTimeoutMillis(30);
    }

    /**
     * Invoked whenever a channel is opened
     *
     * @param ctx The channel chandler context
     * @param e   The state event
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new RSCCodecFactory()));
        Logger.println("Connection from: " + ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress()
        		+ " - Hostname: " + ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getHostName());
    }

    /**
     * Invoked whenever a channel is idled
     *
     * @param ctx The channel chandler context
     * @param e   The state event
     */
    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        Player player = (Player) ctx.getAttachment();
        if (!player.destroyed()) {
            player.destroy(false);
        }
        ctx.getChannel().close();
    }

    /**
     * Invoked whenever a channel is unbinded
     *
     * @param ctx The channel chandler context
     * @param e   The state event
     */
    @Override
    public void unbindRequested(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	System.out.println("unBind Requested");
    	 Player player = (Player) ctx.getAttachment();
         if (player != null && !player.destroyed()) {
             player.destroy(false);
         }
        ctx.getChannel().disconnect();
    }
}
