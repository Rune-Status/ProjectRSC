from com.prsc.gs.plugins.listeners.action import ObjectActionListener
from com.prsc.gs.plugins.listeners.executive import ObjectActionExecutiveListener
from com.prsc.gs.plugins import PlugInterface

'''
@author: GORF
Prayer skill
'''
class Prayer(PlugInterface, ObjectActionListener, ObjectActionExecutiveListener):
	
	def onObjectAction(self, gameObject, command, player):
		script = player.getScriptHelper()
		maxPray = script.getMaxLevel(player.SkillType.PRAYER)
		cLevel = script.getCurrentLevel(player.SkillType.PRAYER)
		
		if cLevel == maxPray:
			script.displayMessage("You already have full prayer points")
			return
		
		script.displayMessage("You recharge at the altar.")
		script.sendSound("recharge")
	
		if gameObject.getID() == 200:
			maxPray = script.getMaxLevel(player.SkillType.PRAYER) + 2
		if cLevel < maxPray:
			script.restoreStat(player.SkillType.PRAYER, maxPray)
         	
	def blockObjectAction(self, gameObject, command, player):
		return command == "recharge at"
