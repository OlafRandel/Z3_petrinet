import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

public class Z3_PetriNet {

	public static void main(String[] args) {
		/*for(int i = 0; args[i] != null; i++) {
			System.out.println("1-safety of " + args[i]);
			interpreter.interpret(args[i], 0);
		}*/
		
		String dirName = "examplefiles";
        File file = new File(dirName);
        //interpreter.interpret("/vol/home/s1696149/eclipse-workspace/pdr-ic3/examplefiles/referendum-50.pnml", 0);
		
		try {
		Files.walkFileTree(file.toPath(), Collections.emptySet(), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("Is " + file.toUri().toString().substring(7) + " 1-safe?");
    			try {
    				if(interpreter.interpret(file.toUri().toString().substring(7), 0)) {
    					System.out.println("yes");
    				}else {
    					System.out.println("no");
    				}
    			}
    			catch(Exception e) {
    				System.out.println(e.getMessage());
    				System.out.println(e.getStackTrace());
    			}
                return FileVisitResult.CONTINUE;
            }
        });       
        }catch(Exception e) {
        	System.out.println(e.getLocalizedMessage());
		}
		System.out.println("end");
	}

}
