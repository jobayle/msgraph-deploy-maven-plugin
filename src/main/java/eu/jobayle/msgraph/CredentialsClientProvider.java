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

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;

import java.util.List;

/**
 * Creates a Graph client from credentials.
 * Uses the Client credentials provider auth method.
 * The client credential flow enables service applications to run without user interaction.
 *
 * @see <a href="https://learn.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow">
 * this guide</a>
 */
public class CredentialsClientProvider {

	/**
	 * Make a client from the given credentials.
	 *
	 * @param tenantId a non null Tenant ID (usually a UUID)
	 * @param clientId a non null Client ID (usually a UUID)
	 * @param clientSecret a non null Client Secret (an arbitrary string)
	 * @return a non null instance
	 */
	public static GraphServiceClient makeClient(String tenantId, String clientId, String clientSecret) {

		ClientSecretCredential credential = new ClientSecretCredentialBuilder()
		    .clientId(clientId)
		    .clientSecret(clientSecret)
		    .tenantId(tenantId)
		    .build();

		List<String> scopes = List.of("https://graph.microsoft.com/.default");

		TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(scopes, credential);

		GraphServiceClient graphClient = GraphServiceClient.builder()
		    .authenticationProvider(authProvider)
		    .buildClient();

		return graphClient;
	}
}
