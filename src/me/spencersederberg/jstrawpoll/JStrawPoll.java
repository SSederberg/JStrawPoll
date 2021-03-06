package me.spencersederberg.jstrawpoll;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JStrawPoll {

	public String rawJSON;
	
	JSONObject obj = new JSONObject();
	JSONArray arr = new JSONArray();
	
	String titlePoll;
	boolean multiPoll = false;
	boolean captchaPoll = false;
	DupCheck dupcheck;
	
	/**
	 * Sets the title of your strawpoll. The API REQUIRES that this is filled in some way.
	 * 
	 * @param title - The name of the poll being created.
	 * @return - The instance of the class.
	 */
	
	public JStrawPoll withTitle(String title) {
		this.titlePoll = title;
		return this;
	}
	
	/**
	 * Determines if the poll will allow multiple votes from a single user/IP address.
	 * 
	 * @param multi - The boolean that determines yes or no.
	 * @return - The instance of the class.
	 */
	
	public JStrawPoll withMulti(boolean multi) {
		this.multiPoll = multi;
		return this;
	}
	
	/**
	 * Determines if the poll requires users to verify that they are 
	 * human via a Captcha test.
	 * 
	 * @param captcha - The boolean that determines yes or no.
	 * @return - The instance of the class.
	 */
	
	public JStrawPoll withCaptcha(boolean captcha) {
		this.captchaPoll = captcha;
		return this;
	}
	
	/**
	 * Determines what kind of duplication checking should be in play.
	 * 
	 * 
	 * @param dup - What kind of duplication checking is being used.
	 * @return - The instance of the class.
	 */
	
	public JStrawPoll withDupcheck(DupCheck dup) {
		this.dupcheck = dup;
		return this;
	}
	
	/**
	 * Determines what the options for the StrawPoll will be
	 * in {@code ArrayList} form.
	 * 
	 * @param options - The options of the poll
	 * @return - The instance of the class.
	 */
	
	@SuppressWarnings("unchecked")
	public JStrawPoll withOptions(ArrayList<String> list) {
		for(String s : list) {
			arr.add(s);
		}
		
		return this;
	}
	
	/**
	 * Determines what the options for the StrawPoll will be 
	 * in {@code String} array form.
	 * 
	 * @param options - The options of the poll.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JStrawPoll withOptions(String... options) {
		for(String s : options) {
			arr.add(s);
		}
		return this;
	}
	
	/**
	 * Builds the raw JSON output for your own manipulation.
	 * <b>JStrawPoll does not provide a way to extract the poll id. </b>
	 * 
	 * @return - The raw JSON output from strawpoll.me 
	 * @throws MalformedURLException - The URL was incorrect.
	 * @throws IOException - Something else happened trying to communicate with strawpoll.me.
	 */
	
	@SuppressWarnings("unchecked")
	public String buildForRawJSON() throws MalformedURLException, IOException {
		
		obj.put("title", this.titlePoll);
		obj.put("multi", this.multiPoll);
		obj.put("captcha", this.captchaPoll);
		obj.put("dupcheck", this.dupcheck.getDupCheck());
		obj.put("options", arr);
		
		String urlS = obj.toString();
		
		HttpURLConnection c = (HttpURLConnection) (new URL("http://strawpoll.me/api/v2/polls").openConnection());

		c.setRequestMethod("POST");
		c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
		c.setRequestProperty("Content-Length", Integer.toString(urlS.length()));

		c.setDoOutput(true);
		c.setDoInput(true);
		c.setUseCaches(false);
		
		DataOutputStream wr = new DataOutputStream(c.getOutputStream());
		wr.writeBytes(urlS);

		wr.flush();
		wr.close();

		InputStream data = null;
		try {
			data = c.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			data = c.getErrorStream();
		}

		BufferedReader b = new BufferedReader(new InputStreamReader(data));
		
		return b.readLine();
		
	}
}
