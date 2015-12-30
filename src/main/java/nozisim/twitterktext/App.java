package nozisim.twitterktext;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import scala.collection.Seq;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		String path = "C:\\Users\\jeesim2\\workspace-mars\\twitter-k-text\\src\\main\\java\\docs.txt";
		List<String> docs = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
		for (String doc : docs) {
			processText(doc);
		}
	}

	private static void processText(String text) {
		System.out.println("원문 = " + text);

		// Normalize
		CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
		//System.out.println("노멀라이즈 = " + normalized);
		// 한국어를 처리하는 예시입니다ㅋㅋ #한국어

		// Tokenize
		Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
		//System.out.println("토큰화 = " + TwitterKoreanProcessorJava.tokensToJavaStringList(tokens));
		// [한국어, 를, 처리, 하는, 예시, 입니, 다, ㅋㅋ, #한국어]

		//System.out.println("토큰 품사 = " + TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens));
		// [한국어(Noun: 0, 3), 를(Josa: 3, 1), (Space: 4, 1), 처리(Noun: 5, 2),
		// 하는(Verb: 7, 2), (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2),
		// 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2), (Space: 17, 1),
		// #한국어(Hashtag: 18, 4)]

		// Stemming
		Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
		//System.out.println("스테밍 = " + TwitterKoreanProcessorJava.tokensToJavaStringList(stemmed));
		// [한국어, 를, 처리, 하다, 예시, 이다, ㅋㅋ, #한국어]

		List<KoreanTokenJava> koreanTokenList = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);
		//System.out.println("스테밍 품사= " + koreanTokenList);
		// [한국어(Noun: 0, 3), 를(Josa: 3, 1), (Space: 4, 1), 처리(Noun: 5, 2),
		// 하다(Verb: 7, 2), (Space: 9, 1), 예시(Noun: 10, 2), 이다(Adjective: 12, 3),
		// ㅋㅋ(KoreanParticle: 15, 2), (Space: 17, 1), #한국어(Hashtag: 18, 4)]

		// Phrase extraction
		List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true,
				true);
		//System.out.println("어절 = " + phrases);
		// [한국어(Noun: 0, 3), 처리(Noun: 5, 2), 처리하는 예시(Noun: 5, 7), 예시(Noun: 10,
		// 2), #한국어(Hashtag: 18, 4)]

		for (KoreanPhraseExtractor.KoreanPhrase phrase : phrases) {
			System.out.println(phrase.text() + " " + phrase.pos() + " " + phrase.tokens().size());
			// Seq<KoreanToken> tk = phrase.tokens();
			// List<KoreanTokenJava> list =
			// TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tk);
			// for(KoreanTokenJava tkj : list){
			// System.out.println(tkj.getText() +" "+tkj.getPos());
			// }
		}
	}

	public static List<String> readFile(final String fileName) {
		try {
			return Files.readAllLines(Paths.get(getFromClasspath(fileName)), Charset.defaultCharset());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static URI getFromClasspath(final String fileName) {
		try {
			return findInClasspath(fileName).toURI();
		} catch (final URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static URL findInClasspath(final String fileName) throws IOException {
		final URL url = FileReader.class.getClassLoader().getResource(fileName);
		if (url == null)
			throw new IOException(new StringBuilder(fileName).append(" not within classpath").toString());
		return url;
	}
}
