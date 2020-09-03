<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Editor</title>
</head>
<body>
	<%@ page import='java.io.FileReader' %>
	<% 	HttpSession s = request.getSession(); 
		String code = (String) s.getAttribute("code");
		String output = "";
		StringBuffer sb = new StringBuffer();
		try(FileReader fr= new FileReader("D:/EasyCodeFiles/" + s.getId() + "/output")){
			char buff[] = new char[1000];
			while(fr.read(buff) != -1){
				sb.append(String.valueOf(buff));
			}
			output = sb.toString();
		}catch(Exception e){
			System.out.println(e);
		}
		if(code == null)
			code = "";
	%>
	<center><h1>Coding Editor</h1></center>
	Enter code here:
	<form action="run" method="POST">
		
		<textarea name="code" rows="25" cols="150"><%= code %></textarea>
		<br>
		<input name="submit" type="submit" value="Run">
		<br>
	</form>
	Output:
	<br>
	<pre><%= output %></pre>
</body>
</html>