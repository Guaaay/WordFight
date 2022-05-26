package auxiliar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


public class TelegramBot extends TelegramLongPollingBot{

	public static HashMap<Long,String> pending;
	private Connection conn;

	public TelegramBot() {
		pending = new HashMap<Long, String>();
		conn = SqlConnection.getConnection();
	}

	/**
	 * Esta función se invocará cuando nuestro bot reciba un mensaje
	 */
	@Override
	public void onUpdateReceived(Update update) {
		// Se obtiene el mensaje escrito por el usuario
		String messageTextReceived = update.getMessage().getText();
		// Se obtiene el id de chat del usuario
		final long chatId = update.getMessage().getChatId();
		// Se crea un objeto mensaje
		SendMessage message = new SendMessage().setChatId(chatId).setText("A la aventura!");
		Message msg = update.getMessage();
		boolean checkPlayerExists = false;
		switch(messageTextReceived) {
		case Tokens.command1:
			message = new SendMessage().setChatId(chatId).setText("Bienvenido! \n Primero, "
					+ "deberás crear un personaje, utiliza el comando /crearpj para crearlo. \n Una vez creado, podrás lanzarte a la aventura!");
			break;
		case Tokens.command2:
			String sql = Tokens.createPJ;
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setLong(1, chatId);
				ps.executeUpdate();
				message = new SendMessage().setChatId(chatId).setText("Personaje creado.");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			break;
		case Tokens.command3:
			SendMessage answer = new SendMessage();
			answer.enableMarkdown(true);
			answer.setReplyMarkup(getSettingsKeyboard(Tokens.command31, Tokens.command32));
			answer.setReplyToMessageId(msg.getMessageId());
			answer.setChatId(msg.getChatId());
			answer.setText(Tokens.command1);
			message = sendChooseOptionMessage(chatId, msg.getMessageId(), getSettingsKeyboard(Tokens.command31, Tokens.command32));
			break;
		case Tokens.command31:
			checkPlayerExists = checkPlayer(chatId);
			if(!checkPlayerExists) {
				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
			}else {
				messageTextReceived = Tokens.command3 + messageTextReceived;
				pending.put(chatId, messageTextReceived);
			}
			break;
		case Tokens.command32:
			checkPlayerExists = checkPlayer(chatId);
			if(!checkPlayerExists) {
				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
			}else {
				messageTextReceived = Tokens.command3 + messageTextReceived;
				pending.put(chatId, messageTextReceived);
			}
			break;
		case Tokens.command4:
			checkPlayerExists = checkPlayer(chatId);
			if(!checkPlayerExists) {
				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
			}else {
				pending.put(chatId, messageTextReceived);
			}
			break;
		case Tokens.command5:
			checkPlayerExists = checkPlayer(chatId);
			if(!checkPlayerExists) {
				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
			}else {
				pending.put(chatId, messageTextReceived);
			}
			break;
		case Tokens.command6:
			checkPlayerExists = checkPlayer(chatId);
			if(!checkPlayerExists) {
				message = new SendMessage().setChatId(chatId).setText(Tokens.pjNoExiste);
			}else {
				String[] playerStat= consultarStats(chatId);
				message = new SendMessage().setChatId(chatId).setText("Nivel: " + playerStat[0] + "\nAtaque: " + playerStat[1] + "\nDefensa: " 
							+ playerStat[2] + "\nExperiencia: " + playerStat[3]+ "\\10\nDefensa activa: " + playerStat[4]);
			}
			break;
		default:
			message = new SendMessage().setChatId(chatId).setText("Comando no valido.");				
			break;
		}
		try {
			// Se envía el mensaje
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

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
	private static ReplyKeyboardMarkup getSettingsKeyboard(String firstOption, String secondOption) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		keyboardFirstRow.add(firstOption);
		keyboardFirstRow.add(secondOption);
		keyboard.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboard);
		return replyKeyboardMarkup;
	}

	/**
	 * Responder al usuario (con el teclado generado en getSettingsKeyboard)
	 */
	private static SendMessage sendChooseOptionMessage(Long chatId, Integer messageId, ReplyKeyboard replyKeyboard) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(chatId.toString());
		sendMessage.setReplyToMessageId(messageId);
		sendMessage.setReplyMarkup(replyKeyboard);
		sendMessage.setText("Elegir opción");
		return sendMessage;
	}

	/**
	 * Se devuelve el nombre que dimos al bot al crearlo con el BotFather
	 */
	@Override
	public String getBotUsername() {
		return "BOT_NAME";
	}

	/**
	 * Se devuelve el token que nos generó el BotFather de nuestro bot
	 */
	@Override
	public String getBotToken() {
		return "API_KEY";
	}

	/**
	 * Enviar un mensaje al usuario
	 */
	public void sendMessageToUser(long id, String text) {
		try {
			SendMessage message = new SendMessage().setChatId(id).setText(text);
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
