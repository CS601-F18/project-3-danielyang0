package cs601.project3.amazonSearch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A review document refers to a review record(one line) in the ?.json file
 * @author yangzun
 *
 */
public class ReviewDocument extends AmazonDocument {
	private String reviewerID;
	private String reviewText;
	private double overall;
	public String getReviewerID() {
		return reviewerID;
	}
	public void setReviewerID(String reviewerID) {
		this.reviewerID = reviewerID;
	}
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public double getOverall() {
		return overall;
	}
	public void setOverall(double overall) {
		this.overall = overall;
	}
	@Override
	public List<String> getOriginalTexts() {
		List<String> l = new ArrayList<>();
		l.add(reviewText);
		return l;
	}
	@Override
	public List<String> summary() {
		List<String> list = new ArrayList<>();
		list.add("ASIN: " + asin);
		list.add("REVIEWERID: " + reviewerID);
		list.add("REVIEWTEXT: " + reviewText);
		list.add("OVERALL"+overall);
		return list;
	}
	@Override
	public String toString() {
		return "ReviewDocument [asin=" + asin + ", reviewerID=" + reviewerID + ", reviewText=" + reviewText + ", overall=" + overall + "]";
	}
}
