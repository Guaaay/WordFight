package agent.agentExercise;


import java.util.Map;


import org.telegram.telegrambots.*;
import org.telegram.telegrambots.meta.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import auxiliar.TelegramBot;
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
		//ApiContextInitializer.init();
		// Se crea un nuevo Bot API
		
		//Se registra el bot
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new TelegramBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private class Input extends CyclicBehaviour{

		@Override
		public void action() {
			if(TelegramBot.pending.entrySet().iterator().hasNext()) {
				Map.Entry<String,String> entry = TelegramBot.pending.entrySet().iterator().next();
				AgentContainer c = getContainerController();
				AgentController ac;
				String[] args = new String[2];
				args[0] = entry.getKey().toString();
				args[1] = entry.getValue();
				try {
					ac=c.createNewAgent(AgentAnalyzer.NICKNAME+Math.random()*100,AgentAnalyzer.class.getName(), args);
					ac.start();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
				TelegramBot.pending.remove(entry.getKey(), entry.getValue());
			}
		}
	}

}


