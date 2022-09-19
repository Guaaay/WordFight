package agent.agentExercise;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Document.Type;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import auxiliar.SqlConnection;
import auxiliar.Tokens;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentAnalyzer extends AgentBase {

	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Process";
	private Connection conn;
	private String idTelegram;
	private String text;

	protected void setup(){
		super.setup();
		this.type = AgentModel.ANALYZER;
		idTelegram = this.params[0];
		text = this.params[1];
		addBehaviour(new Process());
		registerAgentDF();
		conn = SqlConnection.getConnection();
	}

	private class Process extends OneShotBehaviour{
		//Envía una alerta al bot
		@Override
		public void action() {
			String toSend = "";
			String [] args = text.split("_", 2);
			String comando = args[0];
			String texto = args[1];
			double sentiment = 0;
			double magnitude = 0;
			if(text.contains(Tokens.command5)) {
				String senti = getSentiment(texto);
				String [] sent_mag = senti.split("_", 2);
				sentiment = Double.parseDouble(sent_mag[0]);
				magnitude = Double.parseDouble(sent_mag[1]);

				toSend = calcularBatalla(sentiment, magnitude);
			}

			ACLMessage finish = new ACLMessage(ACLMessage.INFORM);
			finish.setSender(getAID());
			AID id = new AID("Output@192.168.0.244:1200/JADE", AID.ISGUID);
			finish.addReceiver(id);
			String send = idTelegram + "_" + toSend;
			finish.setContent(send);
			send(finish);

			myAgent.doDelete();
		}

		private String getSentiment(String text){
			String result = "";
			try (LanguageServiceClient language = LanguageServiceClient.create()) {
				Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
				Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
				result = result + sentiment.getScore() + "_";
				result = result + sentiment.getMagnitude();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		private String calcularBatalla(double sentiment, double magnitude) {
			//Conseguimos los datos del monstruo contra el que pelea el usuario:

			String nombre_monstruo;
			String output = "";
			int tipo = 0;
			double vida_total = 0;
			int turnos_max = 0;
			int turnos_actual = 0;
			int victorias = 0;
			int max_victorias = 0;
			double daño_pelea = 0;
			String hurt = "";
			String notHurt_pos = "";
			String notHurt_neg = "";
			String derrota = "";
			String daño_mensaje = "";
			String respuesta = "";
			String briefing = "";

			Statement stmt;
			try {
				//Cogemos los atributos del monstruo
				String query = Tokens.getMonsterStats + idTelegram;
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				rs.next();
				nombre_monstruo = rs.getString("monstruo.nombre");
				tipo = rs.getInt("monstruo.tipo");
				vida_total = rs.getDouble("monstruo.vida_total");
				turnos_max = rs.getInt("monstruo.turnos");
				turnos_actual = rs.getInt("usuario.turnos_pelea");
				daño_pelea = rs.getDouble("usuario.daño_pelea");
				victorias = rs.getInt("usuario.victorias");
				max_victorias = rs.getInt("usuario.max_victorias");

				
				//Cogemos las frases del monstruo;
				query = Tokens.getFrasesMonstruo + "\"" +  nombre_monstruo + "\"";
				stmt = conn.createStatement();

				rs = stmt.executeQuery(query);
				rs.next();

				hurt = rs.getString(Tokens.FraseHurt);
				notHurt_pos = rs.getString(Tokens.FraseNotHurt_pos);
				notHurt_neg = rs.getString(Tokens.FraseNotHurt_neg);
				derrota = rs.getString(Tokens.FraseDerrota);


				//Calculamos el daño realizado
				double daño = calcularDaño(sentiment,  magnitude, tipo);
				
				if(daño == -10) {
					//Significa que no le hacemos nada de daño
					if(tipo == 1 || tipo == 2) 
						respuesta = notHurt_neg;
					else
						respuesta = notHurt_pos;
					daño = 0;
					
				}
				else if(daño < 0) {
					//Significa que le hacemos poco daño
					if(tipo == 1 || tipo == 2) 
						respuesta = notHurt_pos;
					else 
						respuesta = notHurt_neg;
				}
				else {
					//Significa que le hacemos mucho daño
					respuesta = hurt;
				}
				
				PreparedStatement pst;
				pst = conn.prepareStatement(Tokens.incDaño);
				pst.setDouble(1, Math.abs(daño));
				pst.setString(2, idTelegram);
				pst.executeUpdate();
				
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				
				daño_mensaje = "Tu texto ha sido evaluado con un sentimento de " + sentiment + " y una magnitud de " + magnitude +
						". El daño realizado es " + df.format(Math.abs(daño)) + ". Al monstruo le quedan " +  df.format((vida_total - daño_pelea - Math.abs(daño))) + " puntos de vida.";
				
				//Miramos si hemos ganado o perdido.
				if(daño + daño_pelea >= vida_total) {
					//Hemos ganado
					respuesta = derrota;
					//Tenemos que actualizar los datos:
					pst = conn.prepareStatement(Tokens.incVictories);
					pst.setString(1, idTelegram);
					pst.executeUpdate();
					if(victorias + 1 > max_victorias) {
						//actualizamos el record de victorias
						pst = conn.prepareStatement(Tokens.incMaxVictories);
						pst.setString(1, idTelegram);
						pst.executeUpdate();
					}
					//Reseteamos los turnos
					pst = conn.prepareStatement(Tokens.resetTurno);
					pst.setString(1, idTelegram);
					pst.executeUpdate();

					//Ya no estamos peleando
					pst = conn.prepareStatement(Tokens.noBattle);
					pst.setString(1, idTelegram);
					pst.executeUpdate();
					
					//Reseteamos el daño de la pelea
					pst = conn.prepareStatement(Tokens.resetDaño);
					pst.setString(1, idTelegram);
					pst.executeUpdate();

					briefing = "¡Has derrotado a " + nombre_monstruo + "! Estás en una racha de " + (victorias + 1) 
							+ " victorias. Tu record personal son " + (max_victorias + 1) 
							+ " victorias seguidas. Vuelve a hacer /battle para entrar en otra pelea.";
				}
				else {
					if(turnos_actual + 1 > turnos_max) {
						//Hemos perdido
						pst = conn.prepareStatement(Tokens.resetVictories);
						pst.setString(1, idTelegram);
						pst.executeUpdate();

						pst = conn.prepareStatement(Tokens.resetTurno);
						pst.setString(1, idTelegram);
						pst.executeUpdate();

						//Ya no estamos peleando
						pst = conn.prepareStatement(Tokens.noBattle);
						pst.setString(1, idTelegram);
						pst.executeUpdate();
							
						//Reseteamos el daño de la pelea
						pst = conn.prepareStatement(Tokens.resetDaño);
						pst.setString(1, idTelegram);
						pst.executeUpdate();
						briefing = "¡No has conseguido derrotar a " + nombre_monstruo + " en los turnos máximos! Estás en una racha de 0 victorias (pringao). Tu record personal son " + (max_victorias) + " victorias seguidas.";


					}
					else {
						//Continua la batalla
						pst = conn.prepareStatement(Tokens.incTurno);
						pst.setString(1, idTelegram);
						pst.executeUpdate();
						
						briefing = "Para continuar la pelea con " + nombre_monstruo + " vuelve a usar /ataque seguido de tu texto! Te quedan " + (turnos_max - (turnos_actual+1)) + " turnos para ganar la batalla.";
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			output = daño_mensaje + "_" + respuesta + "_" + briefing;
			return output;
		}


		private double calcularDaño(double sentiment, double magnitude, int tipo) {
			double daño;
			switch(tipo) {
			case 1:
				//Diva
				if(sentiment < 0) {
					daño = -10;
				}
				else {
					if(sentiment < 0.5) {
						daño = sentiment;
						daño = daño * -1;
					}
					else {
						daño = sentiment * 10 + magnitude;
					}
				}
				break;
			case 2:
				//inseguro
				if(sentiment < 0) {
					daño = -10;
				}
				else {
					if(sentiment > 0.5) {
						daño = sentiment-0.5;
						daño = daño * -1;
					}
					else {
						daño = sentiment * 20 + magnitude;
					}
				}
				break;
			case 3:
				//Troll
				if(sentiment > 0) {
					daño = -10;
				}
				else {
					if(sentiment < -0.5) {
						daño = Math.abs(sentiment)-0.5;
						daño = daño*-1;
					}
					else {
						daño =  Math.abs(sentiment) * 20 + magnitude;
					}
				}
				break;
			case 4:
				if(sentiment > 0) {
					daño = -10;
				}
				else {
					if(sentiment > -0.5) {
						daño = Math.abs(sentiment);
						daño = daño*-1;
					}
					else {
						daño =  Math.abs(sentiment) * 10 + magnitude;
					}
				}
				break;
			default:
				daño = 0;
			}
			return daño;
		}

	}
}
