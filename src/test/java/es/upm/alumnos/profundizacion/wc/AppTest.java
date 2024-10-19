package es.upm.alumnos.profundizacion.wc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import es.upm.grise.profundizacion.wc.App;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemOut;

/*
Documentación de apoyo
- https://github.com/webcompere/system-stubs/blob/main/system-stubs-jupiter/README.md
Ejemplo para implementar el tema que habló el profesor del Sistem.exit()
- https://github.com/webcompere/system-stubs/blob/main/system-stubs-jupiter/src/test/java/uk/org/webcompere/systemstubs/jupiter/examples/SystemExitUseCase.java
Ejemplo para implementar el revisar la salida del programa
- https://www.baeldung.com/java-system-stubs#2-junit-5-example
*/

@ExtendWith(SystemStubsExtension.class)
public class AppTest {
	@SystemStub
	private SystemOut sysOut;
	
	@TempDir
	private static File dir;
	private static File emptyFile;
	
	@BeforeAll
	public static void initFiles() throws IOException
	{
		emptyFile = new File(dir, "emptyFile.txt");
		emptyFile.createNewFile();
	}

	/**
	 * Wrapper method to make calls to the App in a easier way.
	 * */
	private static void callApp ( String... args )
	{
		App.main(args);
	}
	
	private List<String> getOutLines ( )
	{
		return sysOut.getLines().toList();
	}

	@Test
	public void testNoArgs ( )
	{
		callApp();
		assertLinesMatch(List.of("Usage: wc [-clw file]"), getOutLines(), "The program should print how to use it!");
	}
	
	@Test
	public void testOneArg ( )
	{
		callApp("-w");
		assertLinesMatch(List.of("Wrong arguments!"), getOutLines(), "The program only work with 2 arguments!");
	}
	
	@Test
	public void testMoreThanTwoArgs ( )
	{
		callApp("-w", "file.txt", "other.txt");
		assertLinesMatch(List.of("Wrong arguments!"), getOutLines(), "The program only work with 2 arguments!");
	}

	@Test
	public void testAnNonExistingFile ( )
	{
		final String filename = "no_exist_file.txt";
		callApp("-w", filename);
		assertLinesMatch(List.of("Cannot find file.*"), getOutLines(), "The program should report if the file cannot be found!");
		assertLinesMatch(List.of(".*" + filename), getOutLines(), "The program should report the name of the file!");
	}

	@Test
	public void testAnEmptyFile ( )
	{
		final String filename = emptyFile.getAbsolutePath();
		callApp("-cwl", filename);
		assertLinesMatch(List.of("^.*0.*0.*0.*"), getOutLines(), "The program should report zeros for all counts on an empty file!");
		assertLinesMatch(List.of(".*" + filename), getOutLines(), "The program should report the name of the file that had been processed!");
	}
}