from org.darkquest.gs.plugins.listeners.action import TalkToNpcListener
from org.darkquest.gs.plugins.listeners.executive import TalkToNpcExecutiveListener
from org.darkquest.gs.plugins import PlugInterface
from org.darkquest.gs.model import Player, Npc

'''
@author: xEnt
This is the Banana collector script!
'''
class Luthas(PlugInterface, TalkToNpcListener, TalkToNpcExecutiveListener):

    NPC_ID = 164
    BANANA_ID = 249

    def onTalkToNpc(self, player, npc):
        player.setActiveNpc(npc)
        player.sendNpcChat("Hello, i am after 20 Bananas, do you have 20 you can sell?")
        opt = player.pickOption(["Yes i will sell you 20 bananas", "No sorry, i don't have any"])
        if opt == 0:
            player.sendNpcChat("I will give you 30gp for your 20 bananas is that ok?")
            opt = player.pickOption(["Sure", "Sorry, i would rather eat them"])
            if opt == 0:
                if player.hasItem(self.BANANA_ID, 20):
                    player.removeItem(self.BANANA_ID, 20)
                    player.addItem(10, 30)
                    player.displayMessage("You receive 30gp")
                else:
                    player.sendNpcChat("It looks like you don't have enough Bananas, don't waste my time.")
        elif opt == 1:
            player.sendNpcChat("Come back when you do!")
            
        player.release()
        
    def blockTalkToNpc(self, player, npc):
        return npc.getID() == self.NPC_ID
    
        
