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

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCreateUploadSessionParameterSet;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.DriveRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.LargeFileUploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class does the upload.
 */
public class Deployer {

	private final Function<GraphServiceClient, DriveRequestBuilder> driveRequestBuilderSupplier;
	private final GraphServiceClient graphClient;

	/**
	 * instantiate a new Deployer using the given client and a functor to get the Drive to deploy to.
	 *
	 * @param graphClient a non null, well configured client
	 * @param driveRequestBuilderSupplier a non null functor to get a driveRequestBuilder using the {@code graphClient}
	 */
	public Deployer(GraphServiceClient graphClient, Function<GraphServiceClient, DriveRequestBuilder> driveRequestBuilderSupplier) {
		Objects.requireNonNull(driveRequestBuilderSupplier);
		Objects.requireNonNull(graphClient);
		this.driveRequestBuilderSupplier = driveRequestBuilderSupplier;
		this.graphClient = graphClient;
	}

	/**
	 * Deploy the given data at the given location.
	 *
	 * @param input input stream to push to the Drive
	 * @param streamLength length of input
	 * @param itemPath path on the Drive
	 * @throws IOException could not deploy
	 */
	public void deploy(InputStream input, long streamLength, String itemPath) throws IOException {
		Objects.requireNonNull(input);
		Objects.requireNonNull(itemPath);

		DriveItemCreateUploadSessionParameterSet uploadParams = DriveItemCreateUploadSessionParameterSet.newBuilder()
		    .withItem(new DriveItemUploadableProperties())
		    .build();

		UploadSession uploadSession = driveRequestBuilderSupplier.apply(graphClient)
		    .root()
		    .itemWithPath(itemPath)
		    .createUploadSession(uploadParams)
		    .buildRequest()
		    .post();

		LargeFileUploadTask<DriveItem> largeFileUploadTask =
		    new LargeFileUploadTask<>(uploadSession, graphClient, input, streamLength, DriveItem.class);

		largeFileUploadTask.upload();
	}

	/**
	 * Helper method to deploy the file at the given path.
	 *
	 * @param input a non null path to a readable file to deploy
	 * @throws IOException could not deploy
	 */
	public void deploy(Path input) throws IOException {
		Objects.requireNonNull(input);
		if (!Files.exists(input) || !Files.isRegularFile(input) || !Files.isReadable(input)) {
			throw new IOException(input + " does not exist or is not readable");
		}
		deploy(Files.newInputStream(input), Files.size(input), input.getFileName().toString());
	}

}
