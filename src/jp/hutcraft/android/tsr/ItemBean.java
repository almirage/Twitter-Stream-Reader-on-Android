package jp.hutcraft.android.tsr;

public class ItemBean {
	private String name = "";
	private String url = "";
	private String tweetIcon = "";
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public String getTweetIcon() {
		return tweetIcon;
	}
	public void setTweetIcon(String tweetIcon) {
		this.tweetIcon = tweetIcon;
	}
}
