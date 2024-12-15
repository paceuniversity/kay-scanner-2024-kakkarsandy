


import java.nio.file.Files;
import java.nio.file.Paths;

public class ParserDemo {

    public static void main(String[] args) {
        try {
            // Path to the KAY program file
            String filePath = "C:\\Users\\sande\\Desktop\\prog1.kay";

            // To check if file exists 
            if (!Files.exists(Paths.get(filePath))) {
                System.err.println("Error: File not found at " + filePath);
                return;
            }

            // File reading
            TokenStream tStream = new TokenStream(filePath); // Generate tokens from the file
            ConcreteSyntax cSyntax = new ConcreteSyntax(tStream); // Create a parser instance
            Program p = cSyntax.program(); // Parse the program
            
            
            System.out.println(p.display());
        } catch (RuntimeException e) {
            // Catch and display syntax errors
            System.err.println(e.getMessage());
        } catch (Exception e) {
            // Catch and display any unexpected errors
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
