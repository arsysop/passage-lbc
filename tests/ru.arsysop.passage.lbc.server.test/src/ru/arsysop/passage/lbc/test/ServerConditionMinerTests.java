/*******************************************************************************
 * Copyright (c) 2018 ArSysOp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ArSysOp - initial API and implementation
 *******************************************************************************/
package ru.arsysop.passage.lbc.test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.transport.RequestProducer;
import ru.arsysop.passage.lic.transport.FloatingConditionDescriptor;

public class ServerConditionMinerTests {
	private final String HOST_NUM = "localhost";
	private final String PORT_NUM = "8080";
	private final String MODE_ID = "client";

	private CloseableHttpClient httpClient;
	private RequestProducer requestEngine;

	@Before
	public void preprocesingTest() {
		httpClient = HttpClients.createDefault();
		requestEngine = new RequestProducer();
	}

	@Test
	public void minerServerConditionPositiveTest() {

		Map<String, String> requestAttributes = requestEngine.initRequestParams(HOST_NUM, PORT_NUM, MODE_ID);
		Assert.assertNotNull(requestAttributes);

		HttpHost host = HttpHost.create(HOST_NUM + ":" + PORT_NUM);
		Iterable<FloatingConditionDescriptor> descriptors = requestEngine.extractConditionsRequest(httpClient, host,
				requestAttributes);
		List<ConditionDescriptor> conditions = StreamSupport.stream(descriptors.spliterator(), false)
				.collect(Collectors.toList());
		Assert.assertTrue(!conditions.isEmpty());
	}

}
