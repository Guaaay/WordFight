package agent.agentExercise;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import auxiliar.TelegramBot;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentOutput extends AgentBase{
	
	private TelegramBot tb = new TelegramBot();
	
	public static final String NICKNAME = "Output";
	
	protected void setup(){
		super.setup();
		this.type = AgentModel.OUTPUT;
		addBehaviour(new Process());
		registerAgentDF();
	}
	
	public class Process extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage input = receive();
			if(input != null) {
				String[] inputArgument = input.getContent().split("_");
				long id = Long.parseLong(inputArgument[0]);
				String text = inputArgument[1];
				tb.sendMessageToUser(id, text);
			}
			block();
		}
		
	}
}
