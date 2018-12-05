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

import static org.junit.Assume.assumeNoException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import ru.arsysop.passage.lic.base.LicensingPaths;
import ru.arsysop.passage.lic.internal.net.NetConditionMiner;
import ru.arsysop.passage.lic.runtime.LicensingCondition;

public class ServerConditionMinerTests {
	private static final String EXTENSION_SERVER_SETTINGS = ".settings";
	private static final String PASSAGE_SERVER_PORT_DEF = "passage.server.port=8080";
	private static final String PASSAGE_SERVER_HOST_DEF = "passage.server.host=localhost";

	private static final String PRODUCT_ID_TEST = "product.test";
	private static ServiceReference<EnvironmentInfo> environmentInfoReference;
	private static EnvironmentInfo environmentInfo;

	/**
	 * Passed through maven-surefire-plugin configuration
	 */
	private static final String MVN_PROJECT_OUTPUT_PROPERTY = "project.build.directory"; //$NON-NLS-1$

	private static final String MVN_PROJECT_OUTPUT_VALUE = "target"; //$NON-NLS-1$

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder(new File(resolveOutputDirName()));

	public static String resolveOutputDirName() {
		String userDir = System.getProperty("user.dir"); //$NON-NLS-1$
		String defaultValue = userDir + File.separator + MVN_PROJECT_OUTPUT_VALUE;
		String outDir = System.getProperty(MVN_PROJECT_OUTPUT_PROPERTY, defaultValue);
		return outDir;
	}

	@BeforeClass
	public void preprocesingTest() {
		Bundle bundle = FrameworkUtil.getBundle(ServerConditionMinerTests.class);
		BundleContext bundleContext = bundle.getBundleContext();
		environmentInfoReference = bundleContext.getServiceReference(EnvironmentInfo.class);
		environmentInfo = bundleContext.getService(environmentInfoReference);
		try {
			createServerConfiguration(PRODUCT_ID_TEST);
		} catch (IOException e) {
			assumeNoException(e.getMessage(), e);
		}
	}

	private void createServerConfiguration(String productId) throws IOException {
		String install = environmentInfo.getProperty(LicensingPaths.PROPERTY_OSGI_INSTALL_AREA);
		Path path = LicensingPaths.resolveConfigurationPath(install, productId);
		Files.createDirectories(path);
		File serverConfigurationFile = path.resolve(productId + EXTENSION_SERVER_SETTINGS).toFile();

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

		@SuppressWarnings("restriction")
		NetConditionMiner serverMiner = new NetConditionMiner();
		Iterable<LicensingCondition> severConditions = serverMiner.extractLicensingConditions(null);

	}

}
