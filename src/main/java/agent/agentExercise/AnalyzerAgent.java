package agent.agentExercise;

import java.io.IOException;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Document.Type;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AnalyzerAgent extends AgentBase{
	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Analyzer";
	protected void setup(){
		super.setup();
		this.type = AgentModel.ANALYZER;
		addBehaviour(new Analyzer());
		registerAgentDF();
	}
	private class Analyzer extends CyclicBehaviour{

		public void reset() {
			super.reset();
		}

		@Override
		public void action() {
			String sentiment = "";
			ACLMessage message = receive();
			if(message!=null) {
				AID senderID = message.getSender();
				sentiment = getSentiment(message.getContent());
				//Creo que tampoco necesitamos
//				try {
//					LocalTime l = LocalTime.now();
//					Gson gson = new GsonBuilder().setPrettyPrinting().create();
//					Files.writeString(Paths.get("Exports\\sentiments"+l.getHour()+l.getMinute()+l.getSecond()+".json"),
//							gson.toJson(output));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				ACLMessage finish = new ACLMessage(ACLMessage.INFORM);
				finish.setSender(getAID());
				finish.addReceiver(senderID);
				finish.setContent(sentiment);
				send(finish);
			}
			block();
		}


		//Creo que no lo necesitamos para nada
//		public JsonObject finalJSON(String json) {
//			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
//			JsonArray tweetsArray = jsonObject.getAsJsonArray("Tweets");
//			JsonObject output = new JsonObject();
//			JsonArray arrayOfTweets = new JsonArray();
//			for(JsonElement tweets : tweetsArray) {
//				ArrayList<Float> result = new ArrayList<Float>();
//				JsonObject tweetsText = tweets.getAsJsonObject();
//				String getText;
//				try {
//					getText = new String(tweetsText.get("Text").getAsString().getBytes(),"UTF-8");
//					result = getSentiment(getText);
//					tweetsText.addProperty("score", result.get(0));
//					tweetsText.addProperty("magnitude", result.get(1));
//					arrayOfTweets.add(tweetsText);
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//			output.add("Tweets", arrayOfTweets);
//			return output;
//		}


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
	}
}