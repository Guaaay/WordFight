package agent.launcher;

public enum AgentModel {

	INPUT("Input"),
	ANALYZER("Analyzer"),
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
		case "Analyzer":
			return ANALYZER;
		case "Output":
			return OUTPUT;

		default:
			return DESCONOCIDO;
		}
	}


}
