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
				sendMessageToUser(userChatId, "¡Bienvenido a WordBender! Empieza una batalla con /battle");
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
						sendMessageToUser(userChatId, "¡Has comenzado una pelea! Usa /ataque seguido de lo que le quieres decir al monstruo para luchar con tus palabras. CAMBIAR");
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
					sendMessageToUser(userChatId, "¡Cobarde! Huyes de la batalla. Tus racha de victorias es 0");
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
			}

		}

	}
	//		// Se obtiene el mensaje escrito por el usuario
	//		String messageTextReceived = update.getMessage().getText();
	//		// Se obtiene el id de chat del usuario
	//		final long chatId = update.getMessage().getChatId();
	//		// Se crea un objeto mensaje
	//		SendMessage message = new SendMessage().setChatId(chatId).setText("A la aventura!");
	//		Message msg = update.getMessage();
	//		boolean checkPlayerExists = false;
	//		switch(messageTextReceived) {
	//		case Tokens.command1:
	//			message = new SendMessage().setChatId(chatId).setText("Bienvenido! \n Primero, "
	//					+ "deberás crear un personaje, utiliza el comando /crearpj para crearlo. \n Una vez creado, podrás lanzarte a la aventura!");
	//			break;
	//		case Tokens.command2:
	//			String sql = Tokens.createPJ;
	//			try {
	//				PreparedStatement ps = conn.prepareStatement(sql);
	//				ps.setLong(1, chatId);
	//				ps.executeUpdate();
	//				message = new SendMessage().setChatId(chatId).setText("Personaje creado.");
	//			} catch (SQLException e1) {
	//				e1.printStackTrace();
	//			}
	//			break;
	//		case Tokens.command3:
	//			SendMessage answer = new SendMessage();
	//			answer.enableMarkdown(true);
	//			answer.setReplyMarkup(getSettingsKeyboard(Tokens.command31, Tokens.command32));
	//			answer.setReplyToMessageId(msg.getMessageId());
	//			answer.setChatId(msg.getChatId());
	//			answer.setText(Tokens.command1);
	//			message = sendChooseOptionMessage(chatId, msg.getMessageId(), getSettingsKeyboard(Tokens.command31, Tokens.command32));
	//			break;
	//		case Tokens.command31:
	//			checkPlayerExists = checkPlayer(chatId);
	//			if(!checkPlayerExists) {
	//				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
	//			}else {
	//				messageTextReceived = Tokens.command3 + messageTextReceived;
	//				pending.put(chatId, messageTextReceived);
	//			}
	//			break;
	//		case Tokens.command32:
	//			checkPlayerExists = checkPlayer(chatId);
	//			if(!checkPlayerExists) {
	//				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
	//			}else {
	//				messageTextReceived = Tokens.command3 + messageTextReceived;
	//				pending.put(chatId, messageTextReceived);
	//			}
	//			break;
	//		case Tokens.command4:
	//			checkPlayerExists = checkPlayer(chatId);
	//			if(!checkPlayerExists) {
	//				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
	//			}else {
	//				pending.put(chatId, messageTextReceived);
	//			}
	//			break;
	//		case Tokens.command5:
	//			checkPlayerExists = checkPlayer(chatId);
	//			if(!checkPlayerExists) {
	//				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
	//			}else {
	//				pending.put(chatId, messageTextReceived);
	//			}
	//			break;
	//		case Tokens.command6:
	//			checkPlayerExists = checkPlayer(chatId);
	//			if(!checkPlayerExists) {
	//				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
	//			}else {
	//				String[] playerStat= consultarStats(chatId);
	//				message = new SendMessage().setChatId(chatId).setText("Nivel: " + playerStat[0] + "\nAtaque: " + playerStat[1] + "\nDefensa: " 
	//							+ playerStat[2] + "\nExperiencia: " + playerStat[3]+ "\\10\nDefensa activa: " + playerStat[4]);
	//			}
	//			break;
	//		default:
	//			message = new SendMessage().setChatId(chatId).setText("Comando no valido.");				
	//			break;
	//		}
	//		try {
	//			// Se envía el mensaje
	//			execute(message);
	//		} catch (TelegramApiException e) {
	//			e.printStackTrace();
	//		}
	//	}

	/**
	 * Método para consultar los stats de un jugador
	 */
	private String[] consultarStats(long id) {
		String query = "SELECT level,attack,defense,nextlevel,isdefensing FROM characterplayer WHERE id="+id;
		String[] stats = new String[5];
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				stats[0] = String.valueOf(rs.getInt("level"));
				stats[1] = String.valueOf(rs.getInt("attack"));
				stats[2] = String.valueOf(rs.getInt("defense"));
				stats[3] = String.valueOf(rs.getInt("nextlevel"));
				stats[4] = String.valueOf(rs.getInt("isdefensing"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stats;
	}

	/**
	 * Método para comprobar si existe un jugador
	 */
	private boolean checkPlayer(long id) {
		String query = "SELECT id FROM characterplayer WHERE id="+id;
		long idQuery = 0;
		boolean result = false;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				idQuery = rs.getLong("id");
			}
			if(idQuery != 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Método para seleccionar opciones dentro de un teclado
	 */
	//	private static ReplyKeyboardMarkup getSettingsKeyboard(String firstOption, String secondOption) {
	//		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
	//		replyKeyboardMarkup.setSelective(true);
	//		replyKeyboardMarkup.setResizeKeyboard(true);
	//		replyKeyboardMarkup.setOneTimeKeyboard(false);
	//		List<KeyboardRow> keyboard = new ArrayList<>();
	//		KeyboardRow keyboardFirstRow = new KeyboardRow();
	//		keyboardFirstRow.add(firstOption);
	//		keyboardFirstRow.add(secondOption);
	//		keyboard.add(keyboardFirstRow);
	//		replyKeyboardMarkup.setKeyboard(keyboard);
	//		return replyKeyboardMarkup;
	//	}

	/**
	 * Responder al usuario (con el teclado generado en getSettingsKeyboard)
	 */
	//	private static SendMessage sendChooseOptionMessage(Long chatId, Integer messageId, ReplyKeyboard replyKeyboard) {
	//		SendMessage sendMessage = new SendMessage();
	//		sendMessage.enableMarkdown(true);
	//		sendMessage.setChatId(chatId.toString());
	//		sendMessage.setReplyToMessageId(messageId);
	//		sendMessage.setReplyMarkup(replyKeyboard);
	//		sendMessage.setText("Elegir opción");
	//		return sendMessage;
	//	}

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
