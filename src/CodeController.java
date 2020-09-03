import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CodeController extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private static final String BASE_PATH = "D:/EasyCodeFiles/";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.print("Hello");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String code = request.getParameter("code");
		code = code.trim();
		HttpSession session = request.getSession();
		
		try {
			String jFileDir = BASE_PATH+session.getId();
			String jFilePath = BASE_PATH + session.getId() + "/MainClass.java";
			String cFilePath = BASE_PATH + session.getId() + "/MainClass";
			String jOutputFilePath = BASE_PATH + session.getId() + "/output";
			File userDir = new File(jFileDir);
			if(userDir.mkdir()) {
				System.out.println("Folder created");
			}
			File javaFile = new File(jFilePath);
			javaFile.createNewFile();
			FileOutputStream jFileCodeSteam = new FileOutputStream(jFilePath);
			jFileCodeSteam.write(code.getBytes());
			
			File outputFile = new File(jOutputFilePath);
			outputFile.createNewFile();
			
			System.out.println("Wrote to a java file");
			jFileCodeSteam.close();
			
			if(compileIt(jFilePath, jFileDir)) {
				// Compilation success
				// Run the program
				System.out.println("Running java file...");
				runIt(jFileDir, jOutputFilePath);
				System.out.println("Wrote to output file");
				// Output is in "output" file
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
		
		session.setAttribute("output", code);
		session.setAttribute("code", code);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
	
	public boolean compileIt(String jFilePath, String jFileDir) throws IOException {
		StringBuilder compileLog = new StringBuilder();
		File somethingJavaFile = new File(jFilePath);
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

		List<String> optionList = new ArrayList<String>();
		optionList.add("-classpath");
		optionList.add(System.getProperty("java.class.path"));

		Iterable<? extends JavaFileObject> compilationUnit = fileManager
				.getJavaFileObjectsFromFiles(Arrays.asList(somethingJavaFile));
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				compilationUnit);
		if (task.call()) {
			System.out.println("Compilation success");
			fileManager.close();
			return true;
		} else {
			System.out.println("Compilation failed");
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				compileLog.append(diagnostic.toString().substring(jFileDir.length()+1));
				compileLog.append("\n");
			}
			writeOutput(compileLog.toString(), new FileWriter(jFileDir + "/output"));
		}
		fileManager.close();
		return false;
	}
	
	public void runIt(String userDir, String jOutputFilePath) throws IOException {
		
		System.out.println(jOutputFilePath);
		System.out.println(userDir);
		ProcessBuilder builder = new ProcessBuilder("java","-cp", userDir, "MainClass");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		
		writeOutput(p.getInputStream(), new FileOutputStream(jOutputFilePath));		
	}
	
	void writeOutput(InputStream is, FileOutputStream fos) throws IOException {
		int c;
		while((c = is.read()) != -1){
			fos.write(c);
		}
		fos.close();
	}
	void writeOutput(String compileLog, FileWriter fw) {
		System.out.println("Writing compile log to file...");
		try {
			fw.write(compileLog);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
