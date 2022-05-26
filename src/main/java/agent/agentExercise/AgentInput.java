package main.java.agent.agentExercise;

import java.util.Map;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import main.java.agent.launcher.AgentBase;
import main.java.agent.agent.launcher.AgentModel;
import main.java.agent.auxiliar.TelegramBot;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AgentInput extends AgentBase{

	private static final long serialVersionUID = 1L;

	public static final String NICKNAME = "Input";

	protected void setup(){
		super.setup();
		this.type = AgentModel.INPUT;
		addBehaviour(new Input());
		registerAgentDF();
		// Se inicializa el contexto de la API
		ApiContextInitializer.init();
		// Se crea un nuevo Bot API
		final TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		//Se registra el bot
		try {
			telegramBotsApi.registerBot(new TelegramBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private class Input extends CyclicBehaviour{

		@Override
		public void action() {
			if(TelegramBot.pending.entrySet().iterator().hasNext()) {
				Map.Entry<Long,String> entry = TelegramBot.pending.entrySet().iterator().next();
				AgentContainer c = getContainerController();
				AgentController ac;
				String[] args = new String[2];
				args[0] = entry.getKey().toString();
				args[1] = entry.getValue();
				try {
					ac=c.createNewAgent(AgentProcess.NICKNAME+Math.random()*100, AgentProcess.class.getName(), args);
					ac.start();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
				TelegramBot.pending.remove(entry.getKey(), entry.getValue());
			}
		}
	}

}


