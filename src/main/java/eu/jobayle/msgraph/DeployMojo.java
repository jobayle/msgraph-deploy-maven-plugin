/*
	Copyright 2022 Jon Bayle

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package eu.jobayle.msgraph;

import com.microsoft.graph.requests.DriveRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven configuration and entry point for goal {@code deploy}.
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true)
public class DeployMojo extends AbstractMojo {

	/**
	 * Flag whether Maven is currently in online/offline mode.
	 */
	@Parameter(defaultValue = "${settings.offline}", readonly = true)
	private boolean offline;

	/**
	 * Built artifact name (usually ${artifactId}-${version}.jar).
	 */
	@Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.jar")
	private String artifact;

	/**
	 * Azure AD credentials: Tenant ID (usually a UUID).
	 */
	@Parameter(required = true)
	private String tenantId;

	/**
	 * Azure AD credentials: Client ID (usually a UUID).
	 */
	@Parameter(required = true)
	private String clientId;

	/**
	 * Azure AD credentials: Client Secret (an arbitrary string).
	 */
	@Parameter(required = true)
	private String clientSecret;

	/**
	 * Path to drive to deploy to.
	 */
	@Parameter(required = true)
	private String drive;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		failIfOffline();
		getLog().info("Deploying artifact '" + artifact + "' using the MS Graph API.");

		GraphServiceClient graphClient = CredentialsClientProvider.makeClient(tenantId, clientId, clientSecret);
		String svcRoot = graphClient.getServiceRoot();
		String apiEndpoint = svcRoot + '/' + drive;
		Deployer deployer = new Deployer(graphClient, c -> new DriveRequestBuilder(apiEndpoint, c, List.of()));
		try {
			deployer.deploy(Paths.get(artifact));
		} catch (IOException ex) {
			throw new MojoFailureException("Deploy failed", ex);
		}
	}

	private void failIfOffline() throws MojoFailureException {
		if (offline) {
			throw new MojoFailureException("Cannot deploy artifacts when Maven is in offline mode");
		}
	}

}
