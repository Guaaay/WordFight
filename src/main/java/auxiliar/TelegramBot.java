package auxiliar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;




public class TelegramBot extends TelegramLongPollingBot{

	public static HashMap<String,String> pending;
	private Connection conn;

	public TelegramBot() {
		pending = new HashMap<String, String>();
		conn = SqlConnection.getConnection();
	}

	/**
	 * Esta función se invocará cuando nuestro bot reciba un mensaje
	 */
	@Override
	public void onUpdateReceived(Update update) {
		String userChatId;
		String userMessage;
		String texto = "";

		if (update.hasMessage() && update.getMessage().hasText()) {
			//sendMessageToUser(update.getMessage().getChatId().toString(), "Preparado para usar tus pa,labras para el bien... o quisas el mal.... ;DDDDDD");
			userMessage = update.getMessage().getText();
			userChatId = update.getMessage().getChatId().toString();
			String [] args = userMessage.split(" ", 2);
			userMessage = args[0];
			if(args.length > 1) {
				texto = args[1];
				System.out.println(texto);
			}

			switch(userMessage) {
			case Tokens.command1:
				sendMessageToUser(userChatId, "¡Bienvenido, WordBender! En este juego tendrás que derrotar a tus enemigos usando solo tus palabras.\n"
						+ "Hay 4 tipos de monstruos, y a cada uno tendrás que derrotarlo de una manera distinta.\n \n"
						+ "Algunos solo te dejarán en paz con halagos y cumplidos. Otros solo serán derrotados con insultos y negatividad. "
						+ "Hay otros que tendrás que agradar, pero sin pasarte, o se pensarán que les mientes, y otros que solo huirán si eres un poco negativo con ellos, pero sin ensañarte.\n \n"
						+ "WordBender_bot usa la API de Natural Language Processing de Google para analizar el sentimiento de tu texto, así que experimenta y juega con ella, en inglés o en español.\n\n"
						+ "¡Buena suerte, WordBender! La vas a necesitar... \n \n"
						+ "Usa /help para ver los comandos disponibles\n");
				try {
					PreparedStatement pst = conn.prepareStatement(Tokens.createUser);
					pst.setString(1, userChatId);
					pst.setInt(2, 0);
					pst.setInt(3, 0);
					pst.setInt(4, 0);
					int res = pst.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Tokens.command2:
				String stats = "";
				try {
					String query = Tokens.consultarStats + userChatId;
					Statement stmt = conn.createStatement();

					ResultSet rs = stmt.executeQuery(query);
					rs.next();
					stats="LLevas: " + rs.getInt("victorias") + " victorias y tu record está en: " + rs.getInt("max_victorias") + " ¡Sigue asi!";
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				sendMessageToUser(userChatId, stats);
				break;
			case Tokens.command3:
				try {
					String query = Tokens.isInBattle + userChatId;
					Statement stmt = conn.createStatement();

					ResultSet rs = stmt.executeQuery(query);
					rs.next();
					if(rs.getInt("isPeleando") == 1) {
						sendMessageToUser(userChatId, "¡Ya estás en medio de una pelea!");
					}
					else {
						//El usuario está peleando
						PreparedStatement pst = conn.prepareStatement(Tokens.setBattle);
						pst.setString(1, userChatId);	
						int res = pst.executeUpdate();

						//Asignamos un monstruo al usuario
						String sql = Tokens.getMonster;
						stmt = conn.createStatement();

						rs = stmt.executeQuery(sql);
						rs.next();
						String monstruo = rs.getString("nombre");
						System.out.println(monstruo);
						pst = conn.prepareStatement(Tokens.setMonster);
						pst.setString(1, monstruo);
						pst.setString(2, userChatId);
						res = pst.executeUpdate();

						//Mandamos los mensajes de introducción de la pelea:
						query = Tokens.getFrasesMonstruo + "\"" +  monstruo + "\"";
						stmt = conn.createStatement();

						rs = stmt.executeQuery(query);
						rs.next();

						String intro = rs.getString(Tokens.FraseIntro);

						sendMessageToUser(userChatId, intro);
						sendMessageToUser(userChatId, "¡Has comenzado una pelea! Usa /ataque seguido de lo que le quieres decir al monstruo para luchar con tus palabras.");
					}
				}
				catch (SQLException e) {
					// TODO Auto-generated catch block
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				break;
			case Tokens.command4:
				//Huir de la batalla
				PreparedStatement pst;
				try {
					pst = conn.prepareStatement(Tokens.noBattle);
					pst.setString(1, userChatId);
					int res = pst.executeUpdate();
					pst = conn.prepareStatement(Tokens.resetVictories);
					pst.setString(1, userChatId);
					res = pst.executeUpdate();
					sendMessageToUser(userChatId, "¡Cobarde! Huyes de la batalla. Tu racha de victorias es 0");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case Tokens.command5:

				try {
					String query = Tokens.isInBattle + userChatId;
					Statement stmt = conn.createStatement();

					ResultSet rs = stmt.executeQuery(query);
					rs.next();
					if(rs.getInt("isPeleando") == 0) {
						sendMessageToUser(userChatId, "¡No estás en una pelea! Usa el comando /battle para entrar en una");
						break;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pending.put(userChatId, userMessage + "_" + texto);
				break;

			case Tokens.command6:
				sendMessageToUser(userChatId, "Comandos disponibles:\n"
						+ "/help: Muestra este mensaje\n"
						+ "/start: Crea tu usuario y muestra el texto de introducción\n"
						+ "/stats: Muestra tu racha y tu record de victorias\n"
						+ "/battle: Comienza una batalla con un monstruo\n"
						+ "/ataque <texto> Analiza el texto que has enviado y calcula el daño realizado. Necesitas estar en batalla para usar este comando.\n"
						+ "/huir Huyes de la batalla, pero tu racha de victorias se va a 0.");
				
				break;
			}

		}

	}

	/**
	 * Se devuelve el nombre que dimos al bot al crearlo con el BotFather
	 */
	public String getBotUsername() {
		return "WordBender_bot";
	}

	/**
	 * Se devuelve el token que nos generó el BotFather de nuestro bot
	 */
	@Override
	public String getBotToken() {
		FileReader file;
		try {
			file = new FileReader("src/main/java/auxiliar/key.txt");
			BufferedReader buffer = new BufferedReader(file);

			//read the 1st line
			String line = buffer.readLine();
			//display the 1st line
			return line;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}


	}


	/**
	 * Enviar un mensaje al usuario
	 */
	public void sendMessageToUser(String id, String text) {
		try {
			SendMessage message = new SendMessage();
			message.setChatId(id);
			message.setText(text);
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
