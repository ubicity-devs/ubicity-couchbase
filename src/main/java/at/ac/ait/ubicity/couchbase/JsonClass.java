package at.ac.ait.ubicity.couchbase;

import java.util.List;

import com.google.gson.Gson;

public class JsonClass {

	private static Gson gson = new Gson();

	private final List<String> channels;

	private final String id;
	private final String user;
	private final String password;
	private final String email;

	public JsonClass(String user, String password, String email,
			List<String> channels) {
		this.id = String.valueOf(System.currentTimeMillis());
		this.user = user;
		this.password = password;
		this.email = email;
		this.channels = channels;
	}

	public String getUser() {
		return this.user;
	}

	public String getPassword() {
		return this.password;
	}

	public String getEmail() {
		return this.email;
	}

	public String getId() {
		return this.id;
	}

	public List<String> getChannels() {
		return this.channels;
	}

	public String toJson() {
		return gson.toJson(this);
	}
}
