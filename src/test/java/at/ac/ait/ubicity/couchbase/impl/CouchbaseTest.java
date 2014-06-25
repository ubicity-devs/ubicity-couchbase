package at.ac.ait.ubicity.couchbase.impl;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import at.ac.ait.ubicity.contracts.test.TestDTO;

import com.google.gson.Gson;

public class CouchbaseTest {

	static Gson gson = new Gson();

	@Test
	@Ignore
	public void writeTest() {

		CouchbasePluginImpl cb = new CouchbasePluginImpl();
		cb.init();

		Gson gson = new Gson();

		TestDTO json = new TestDTO("user", "password", "email", Arrays.asList(
				"all", "user-23"));

		cb.client.add(json.getId(), json.toJson());

		TestDTO json1 = gson.fromJson(cb.client.get(json.getId()).toString(),
				TestDTO.class);

		System.out.println(json1.toJson());

		cb.shutdown();
	}

}
