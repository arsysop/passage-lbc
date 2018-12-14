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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import ru.arsysop.passage.lic.base.LicensingConfigurations;
import ru.arsysop.passage.lic.base.LicensingPaths;
import ru.arsysop.passage.lic.net.RequestProducer;
import ru.arsysop.passage.lic.runtime.LicensingCondition;
import ru.arsysop.passage.lic.runtime.LicensingConfiguration;

public class ServerConditionMinerTests {
	private static final String EXTENSION_SERVER_SETTINGS = ".settings";
	private static final String PASSAGE_SERVER_PORT_DEF = "passage.server.port=8080";
	private static final String PASSAGE_SERVER_HOST_DEF = "passage.server.host=localhost";
	private static final String HOST_KEY = "passage.server.host";
	private static final String PORT_KEY = "passage.server.port";
	private static final String HOST_PORT = "%s:%s";
	private static final String PRODUCT_ID_TEST = "product.test";
	private static ServiceReference<EnvironmentInfo> environmentInfoReference;
	private static EnvironmentInfo environmentInfo;

	/**
	 * Passed through maven-surefire-plugin configuration
	 */
	private static final String MVN_PROJECT_OUTPUT_PROPERTY = "project.build.directory"; //$NON-NLS-1$
	private static final String MINER_LICENSING_CONDITION_TYPE = "extractConditions";
	private static final String MVN_PROJECT_OUTPUT_VALUE = "target"; //$NON-NLS-1$

	public static String resolveOutputDirName() {
		String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
		String defaultValue = userDir + File.separator + MVN_PROJECT_OUTPUT_VALUE;
		String outDir = System.getProperty(MVN_PROJECT_OUTPUT_PROPERTY, defaultValue);
		return outDir;
	}

	@BeforeClass
	public static void preprocesingTest() {
		Bundle bundle = FrameworkUtil.getBundle(ServerConditionMinerTests.class);
		BundleContext bundleContext = bundle.getBundleContext();
		environmentInfoReference = bundleContext.getServiceReference(EnvironmentInfo.class);
		environmentInfo = bundleContext.getService(environmentInfoReference);
		try {
			createServerConfiguration(LicensingConfigurations.create(PRODUCT_ID_TEST, null));
		} catch (IOException e) {
			assumeNoException(e.getMessage(), e);
		}
	}

	private static void createServerConfiguration(LicensingConfiguration configuration) throws IOException {
		String install = environmentInfo.getProperty(LicensingPaths.PROPERTY_OSGI_INSTALL_AREA);
		Path path = LicensingPaths.resolveConfigurationPath(install, configuration);
		Files.createDirectories(path);
		String fileName = LicensingPaths.composeFileName(configuration, EXTENSION_SERVER_SETTINGS);
		File serverConfigurationFile = path.resolve(fileName).toFile();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(serverConfigurationFile));) {
			bw.write(PASSAGE_SERVER_HOST_DEF);
			bw.newLine();
			bw.write(PASSAGE_SERVER_PORT_DEF);
			bw.newLine();
			bw.flush();
		} catch (Exception e) {
			assumeNoException(e.getMessage(), e);
		}

	}

	@Test
	public void mineConditionFromServerPositiveTest() {

		assertNotNull(environmentInfo);
		String areaValue = environmentInfo.getProperty(LicensingPaths.PROPERTY_OSGI_INSTALL_AREA);

		LicensingConfiguration configuration = LicensingConfigurations.create(PRODUCT_ID_TEST, null);
		Path configurationPath = LicensingPaths.resolveConfigurationPath(areaValue, configuration);
		assertTrue(Files.isDirectory(configurationPath));

		List<Path> settinsFiles = new ArrayList<>();
		try {
			Files.walkFileTree(configurationPath, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.toString().toLowerCase().endsWith(EXTENSION_SERVER_SETTINGS)) {
						settinsFiles.add(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			assumeNoException(e);
		}
		Map<String, String> settingsMap = new HashMap<>();
		for (Path path : settinsFiles) {
			try {
				Map<String, String> loadedSettings = loadIstallationAreaSettings(Files.readAllLines(path));
				settingsMap.putAll(loadedSettings);

			} catch (Exception e) {
				assumeNoException(e);
			}
		}

		RequestProducer requestProducer = new RequestProducer();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String hostValue = settingsMap.get(HOST_KEY);
		assertNotNull(hostValue);
		assertFalse(hostValue.isEmpty());

		String portValue = settingsMap.get(PORT_KEY);
		assertNotNull(portValue);
		assertFalse(portValue.isEmpty());

		Map<String, String> requestAttributes = requestProducer.initRequestParams(hostValue, portValue, "client",
				"product1.id", "1.0.0");
		HttpHost host = HttpHost.create(String.format(HOST_PORT, hostValue, portValue));
		URIBuilder requestBulder = requestProducer.createRequestURI(httpClient, host, requestAttributes,
				MINER_LICENSING_CONDITION_TYPE);

		assertNotNull(requestBulder);

		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(requestBulder.build());
		} catch (URISyntaxException e) {
			assumeNoException(e);
		}

		ResponseHandler<Iterable<LicensingCondition>> responseHandler = new ResponseHandler<Iterable<LicensingCondition>>() {

			@Override
			public Iterable<LicensingCondition> handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				assertNotNull(entity);
				assertNotNull(entity.getContent());
				return null;
			}
		};
		try {
			httpClient.execute(host, httpPost, responseHandler);
		} catch (IOException e) {
			assumeNoException(e);
		}

	}

	private Map<String, String> loadIstallationAreaSettings(List<String> readAllLines) {
		Map<String, String> settings = new HashMap<>();
		for (String iter : readAllLines) {
			String[] setting = iter.split("=");
			if (setting.length == 2) {
				settings.put(setting[0], setting[1]);
			}
		}
		return settings;
	}
}
