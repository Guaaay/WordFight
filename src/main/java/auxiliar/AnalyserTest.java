package auxiliar;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Document.Type;

public class AnalyserTest {
	public static String getSentiment(String text){
		String result = "";
		try (LanguageServiceClient language = LanguageServiceClient.create()) {
			Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
			Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
			System.out.printf("Text: %s%n", text);
		    System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String [] args) {
		String sentiment = "";
		getSentiment(sentiment);
	}
}
