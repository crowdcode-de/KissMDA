package de.crowdcode.kissmda.core.file;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.core.Context;

/**
 * @author idueppe
 * @since 1.0
 */
public class FileWriterTest {

	private FileWriter fileWriter;

	@Before
	public void setUp() throws Exception {
		fileWriter = new FileWriter();
	}

	@Test
	public void testCreateFile() throws IOException {
		String fileContent = "Hello Junit Test.";
		String directory = "de/kissmda/test";
		String fileName = "readme.txt";

		Context context = mock(Context.class);
		when(context.getTargetModel()).thenReturn(
				"target/generated-test-sources/resources");

		fileWriter.createFile(context, directory, fileName, fileContent);

		verify(context).getTargetModel();
	}
}
