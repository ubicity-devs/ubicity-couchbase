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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.Shutdown;

import org.apache.log4j.Logger;

import at.ac.ait.ubicity.commons.broker.BrokerConsumer;
import at.ac.ait.ubicity.commons.broker.events.EventEntry;
import at.ac.ait.ubicity.commons.util.PropertyLoader;
import at.ac.ait.ubicity.couchbase.CouchbaseAddOn;

import com.couchbase.client.CouchbaseClient;

@PluginImplementation
public class CouchbaseAddOnImpl extends BrokerConsumer implements
		CouchbaseAddOn {

	private String name;
	CouchbaseClient client;

	protected static Logger logger = Logger.getLogger(CouchbaseAddOnImpl.class);

	@Init
	public void init() {
		PropertyLoader config = new PropertyLoader(
				CouchbaseAddOnImpl.class.getResource("/couchbase.cfg"));
		this.name = config.getString("addon.couchbase.name");

		try {
			List<URI> hosts = Arrays.asList(new URI(config
					.getString("addon.couchbase.host")));
			String bucket = config.getString("addon.couchbase.bucket.name");
			String password = config.getString("addon.couchbase.bucket.pwd");
			client = new CouchbaseClient(hosts, bucket, password);
		} catch (Exception e) {
			logger.error("During init caught exc.", e);
		}

		logger.info(name + " loaded");
	}

	@Override
	@Shutdown
	public void shutdown() {
		super.shutdown();
		if (client != null) {
			client.shutdown();
		}
	}

	public String getName() {
		return this.name;
	}

	@Override
	public void onReceived(EventEntry event) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
