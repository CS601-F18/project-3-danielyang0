package cs601.project3.amazonSearch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A QAdocument refers to a question/answer  record(one line) in the ?.json file
 * @author yangzun
 *
 */
public class QADocument extends AmazonDocument{
	private String question;
	private String answer;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	@Override
	protected List<String> getOriginalTexts() {
		List<String> l = new ArrayList<>();
		l.add(question);
		l.add(answer);
		return l;
	}
	@Override
	public List<String> summary() {
		List<String> list = new ArrayList<>();
		list.add("ASIN: " + asin);
		list.add("QUESTION: "+question);
		list.add("ANSWER: "+answer);
		return list;
	}
	@Override
	public String toString() {
		return "QADocument [asin=" + asin + ", question=" + question + ", answer=" + answer + "]";
	}
}