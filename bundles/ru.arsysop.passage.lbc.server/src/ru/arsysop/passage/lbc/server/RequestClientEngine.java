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
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     ArSysOp - initial API and implementation
 *******************************************************************************/
package ru.arsysop.passage.lbc.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestClientEngine {

	private static final String ACT_CHECKOUT_CONDITIONS = "checkoutConditions";
	private final Logger logger = Logger.getLogger(RequestClientEngine.class.getName());

	public Map<String, String> initRequestParams(String host, String port, String modeId) {
		Map<String, String> requestAttributes = new HashMap<>();
		requestAttributes.put(ServerRuntimeRequestParameters.HOST, host);
		requestAttributes.put(ServerRuntimeRequestParameters.PORT, port);

		requestAttributes.put(ServerRuntimeRequestParameters.CLIENT_TRUSTED_ID, "12345678");
		requestAttributes.put(ServerRuntimeRequestParameters.SERVER_ACCESS_MODE_ID, modeId);
		return requestAttributes;
	}

	public Iterable<ServerConditionDescriptor> requestServerConditions(CloseableHttpClient httpClient, HttpHost host,
			Map<String, String> requestAttributes) {
		Iterable<ServerConditionDescriptor> descriptors = new ArrayList<>();

		try {
			requestAttributes.put(ServerRuntimeRequestParameters.SERVER_ACTION_ID, ACT_CHECKOUT_CONDITIONS);
			URIBuilder builder = RequestCreator.createRequestUriBuilder(requestAttributes);
			ServerTransferObject transferObject = requestConditions(httpClient, host, builder);
			descriptors = transferObject.getDescriptors();
		} catch (Exception e) {
			Logger.getLogger(RequestClientEngine.class.getName()).info(e.getMessage());
		}
		return descriptors;
	}

	private ServerTransferObject requestConditions(CloseableHttpClient httpClient, HttpHost host, URIBuilder builder)
			throws URISyntaxException, IOException, ClientProtocolException {

		HttpPost httpPost = new HttpPost(builder.build());
		ResponseHandler<ServerTransferObject> responseHandler = new ResponseHandler<ServerTransferObject>() {

			@Override
			public ServerTransferObject handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				ServerTransferObject transferObject = null;
				HttpEntity entity = response.getEntity();

				ObjectMapper mapper = new ObjectMapper();
				try (InputStream inputContext = entity.getContent()) {
					transferObject = mapper.readValue(inputContext, ServerTransferObject.class);
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
				return transferObject;
			}
		};
		return httpClient.execute(host, httpPost, responseHandler);
	}
}
