package functions;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class PrintFiles extends SimpleFileVisitor<Path>{
	
	public static int counter = 1;
	public static ArrayList<Path> files = new ArrayList<Path>();
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
		
		System.out.println("(" + Integer.toString(counter) + ") " + file);
		counter++;
		files.add(file);
		
		return CONTINUE; 
	}
	
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		
		System.err.println("PROBLEM READING " + file);
		
		return CONTINUE;
	}
}
