package agent.agentExercise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import auxiliar.SqlConnection;
import auxiliar.Tokens;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentProcess extends AgentBase {

	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Process";
	private Connection conn;
	private long idTelegram;
	private String text;
	
	protected void setup(){
		super.setup();
		this.type = AgentModel.PROCESS;
		idTelegram = Long.parseLong(this.params[0]);
		text = this.params[1];
		addBehaviour(new Process());
		registerAgentDF();
		conn = SqlConnection.getConnection();
	}

	private class Process extends OneShotBehaviour{
		//Envía una alerta al bot
		@Override
		public void action() {
			checkDefense();
			String toSend = "Nada que reportar";
			if(text.contains(Tokens.command3)) {
				toSend = attackNPC();
			}else if(text.contains(Tokens.command4)) {
				toSend = attackPVP();
			}else{
				defense();
				toSend="Defendiendote";
			}
			checkLevelUp();
			
			ACLMessage finish = new ACLMessage(ACLMessage.INFORM);
			finish.setSender(getAID());
			AID id = new AID("Output@192.168.56.1:1200/JADE", AID.ISGUID);
			finish.addReceiver(id);
			String send = idTelegram + "_" + toSend;
			finish.setContent(send);
			send(finish);
			
			myAgent.doDelete();
		}
		
		
		private String attackNPC() {
			String npc = text.split("/")[2];
			int playerdefense,playerattack,attacker,defenser;
			playerdefense=playerattack=attacker=defenser=0;
			String fightResult = "Ataque fracasado";
			try {
				String query = Tokens.attackerdefenser + idTelegram;
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					playerattack = rs.getInt("attack");
					playerdefense = rs.getInt("defense");
		        }
				query = "SELECT attack,defense FROM npc WHERE name='" + npc +"'";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					attacker = rs.getInt("attack");
					defenser = rs.getInt("defense");
		        }
				boolean result = battle(playerattack, playerdefense, attacker, defenser);
				if(result) {
					String sql = Tokens.updateXP;
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setLong(1, idTelegram);
					ps.executeUpdate();
					fightResult = "Ataque con éxito!";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return fightResult;
		}
		
		private String attackPVP() {
			long idAttack = 4551748;
			int playerdefense,playerattack,attacker,defenser;
			playerdefense=playerattack=attacker=defenser=0;
			String fightResult = "Ataque fracasado";
			try {
				String query = Tokens.attackerdefenser + idTelegram;
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					playerattack = rs.getInt("attack");
					playerdefense = rs.getInt("defense");
		        }
				query = "SELECT * FROM characterplayer WHERE id NOT IN ("+ idTelegram +") ORDER BY RAND() LIMIT 1";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					idAttack = rs.getLong("id");
		        }
				query = Tokens.attackerdefenser + idAttack;
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					attacker = rs.getInt("attack");
					defenser = rs.getInt("defense");
		        }
				boolean result = battle(playerattack, playerdefense, attacker, defenser);
				if(result) {
					String sql = Tokens.updateXP;
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setLong(1, idTelegram);
					ps.executeUpdate();
					fightResult = "Ataque con éxito!";
				}else {
					String sql = Tokens.updateXP;
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setLong(1, idAttack);
					ps.executeUpdate();
					fightResult = "Ataque fracasado";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return fightResult;
		}
		
		private void checkDefense() {
			try {
				String query = Tokens.isDefense + idTelegram;
				boolean isDefensing = false;
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
		            isDefensing = rs.getBoolean("isdefensing");
		        }
				if(isDefensing) {
					String sql = Tokens.quitDefense;
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setLong(1, idTelegram);
					ps.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		private void defense() {
			try {
				String sql = Tokens.defense;
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setBoolean(1, true);
				ps.setLong(2, idTelegram);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		private void checkLevelUp() {
			try {
				String query = Tokens.experience + idTelegram;
				int experience = 0;
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					experience = rs.getInt("nextlevel");
		        }
				if(experience >= 10) {
					String sql = Tokens.nextLvl;
					PreparedStatement ps = conn.prepareStatement(sql);
					experience-=10;
					ps.setInt(1, experience);
					ps.setLong(2, idTelegram);
					ps.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		private boolean battle(int playerattack, int playerdefense, int attacker,int defenser) {
			int roundsWinByAttacker = 0;
			int roundsWinByDefender = 0;
			boolean winner = false;
			for(int i=0;i<=3;i++) {
				int attacker1=(int)(Math.random()*20+playerattack);
				int defenser1=(int)(Math.random()*20+playerdefense);
				int attacker2=(int)(Math.random()*20+attacker);
				int defenser2=(int)(Math.random()*20+defenser);
				int result = attacker1-defenser2;
				int result2 = attacker2-defenser1;
				if(result >= result2) {
					roundsWinByAttacker+=1;
				}else {
					roundsWinByDefender +=1;
				}
			}
			if(roundsWinByAttacker >= roundsWinByDefender) {
				 winner = true;
			}
			return winner;
		}
	}
}
