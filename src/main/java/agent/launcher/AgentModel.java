package main.java.agent.launcher;

public enum AgentModel {

	INPUT("Input"),
	PROCESS("Process"),
	OUTPUT("Output"),
	DESCONOCIDO("Desconocido");

	private final String value;

	AgentModel(String value){ 
		this.value = value; 
	}

	public String getValue(){ 
		return this.value; 
	}

	public static AgentModel getEnum(String value) {
		switch (value) {
		case "Input":
			return INPUT;
		case "Process":
			return PROCESS;
		case "Output":
			return OUTPUT;
		default:
			return DESCONOCIDO;
		}
	}


}
