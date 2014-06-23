package at.ac.ait.ubicity.couchbase.impl;

import java.util.Arrays;

import org.junit.Test;

import at.ac.ait.ubicity.couchbase.JsonClass;

import com.google.gson.Gson;

public class CouchbaseTest {

	static Gson gson = new Gson();

	@Test
	public void writeTest() {

		CouchbaseAddOnImpl cb = new CouchbaseAddOnImpl();
		cb.init();

		Gson gson = new Gson();

		JsonClass json = new JsonClass("user", "password", "email",
				Arrays.asList("all", "user-23"));

		cb.client.add(json.getId(), json.toJson());

		JsonClass json1 = gson.fromJson(cb.client.get(json.getId()).toString(),
				JsonClass.class);

		System.out.println(json1.toJson());

		cb.shutdown();
	}

}
