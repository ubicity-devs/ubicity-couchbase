/**
    Copyright (C) 2014  AIT / Austrian Institute of Technology
    http://www.ait.ac.at

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/agpl-3.0.html
 */
package at.ac.ait.ubicity.couchbase.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.Shutdown;

import org.apache.log4j.Logger;

import at.ac.ait.ubicity.commons.broker.BrokerConsumer;
import at.ac.ait.ubicity.commons.broker.events.EventEntry;
import at.ac.ait.ubicity.commons.broker.events.EventEntry.Property;
import at.ac.ait.ubicity.commons.util.PropertyLoader;
import at.ac.ait.ubicity.couchbase.CouchbasePlugin;

import com.couchbase.client.CouchbaseClient;

@PluginImplementation
public class CouchbasePluginImpl extends BrokerConsumer implements CouchbasePlugin {

	private String name;
	private static HashMap<String, CouchbaseClient> knownBuckets = new HashMap<String, CouchbaseClient>();
	private List<URI> hosts;

	protected static Logger logger = Logger.getLogger(CouchbasePluginImpl.class);

	@Override
	@Init
	public void init() {
		PropertyLoader config = new PropertyLoader(CouchbasePluginImpl.class.getResource("/couchbase.cfg"));
		this.name = config.getString("plugin.couchbase.name");

		try {
			super.init(config.getString("plugin.couchbase.broker.user"), config.getString("plugin.couchbase.broker.pwd"));

			String host = config.getString("plugin.couchbase.host");
			host = host + ":" + config.getString("env.couchbase.host_port");
			host = host + "/pools";

			this.hosts = Arrays.asList(new URI(host));

			setConsumer(this, config.getString("plugin.couchbase.broker.dest"));
		} catch (Exception e) {
			logger.error("During init caught exc.", e);
		}

		logger.info(name + " loaded");
	}

	@Override
	@Shutdown
	public void shutdown() {
		super.shutdown();

		for (CouchbaseClient client : knownBuckets.values()) {
			if (client != null) {
				client.shutdown();
			}
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void onReceived(String destination, EventEntry event) {
		if (event != null) {

			String bucket = event.getProperty(Property.CB_BUCKET);
			CouchbaseClient client = getConnection(bucket);

			if (client != null) {
				client.set(event.getProperty(Property.ID), event.getBody());
			}
		} else {
			logger.warn("EventEntry is null");
		}
	}

	@Override
	protected void onReceivedRaw(String destination, String tmsg) {
		// Not used here
	}

	private CouchbaseClient getConnection(String bucket) {

		CouchbaseClient client = null;

		// Check if bucket connection exists otherwise create it
		if (!knownBuckets.containsKey(bucket)) {
			try {
				client = new CouchbaseClient(hosts, bucket, "");
				knownBuckets.put(bucket, client);
			} catch (IOException e) {
				logger.error("Creation of connection threw an exc.", e);
			}
		} else {
			client = knownBuckets.get(bucket);
		}

		return client;
	}
}
