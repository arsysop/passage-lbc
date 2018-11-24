package ru.arsysop.passage.lbc.test;

import java.util.ArrayList;
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
import ru.arsysop.passage.lic.transport.RequestClientEngine;
import ru.arsysop.passage.lic.transport.ServerConditionDescriptor;

public class ServerConditionEvaluatorTest {

	private final String HOST_NUM = "localhost";
	private final String PORT_NUM = "8080";
	private final String MODE_ID = "client";

	private CloseableHttpClient httpClient;
	private RequestClientEngine requestEngine;

	@Before
	public void preprocesingTest() {
		httpClient = HttpClients.createDefault();
		requestEngine = new RequestClientEngine();
	}

	@Test
	public void evaluateServerConditionPositiveTest() {
		Map<String, String> requestAttributes = requestEngine.initRequestParams(HOST_NUM, PORT_NUM, MODE_ID);
		Assert.assertNotNull(requestAttributes);

		HttpHost host = HttpHost.create(HOST_NUM + ":" + PORT_NUM);
		Iterable<ServerConditionDescriptor> descriptors = requestEngine.evaluateConditionsRequest(httpClient, host,
				requestAttributes, getConditionsStub());
		List<ConditionDescriptor> conditions = StreamSupport.stream(descriptors.spliterator(), false)
				.collect(Collectors.toList());
		Assert.assertTrue(!conditions.isEmpty());

	}

	private Iterable<ConditionDescriptor> getConditionsStub() {
		ConditionDescriptor someDescriptor = new ServerConditionDescriptor("test.feature.id", "0.3.0", "", "", "");
		ArrayList<ConditionDescriptor> arrayList = new ArrayList<>();
		arrayList.add(someDescriptor);

		return arrayList;
	}
}
