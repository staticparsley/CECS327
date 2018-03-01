//Ruben Marin
//CECS 327
//Assignment 3 - FTP Client


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.*;
import java.nio.file.*;
import org.apache.commons.net.ftp.*;

class Assn3 {
	public static void main(String[] args) throws IOException, InterruptedException  {
		
		String command = "";
		String commandInput = "";
		String argument = "";
		int port = 21;
		
		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		
		String server = args[0];
		String[] login = args[1].split(":");
		String login1 = login[0];
		String login2 = login[1];
		ftp.connect(server,port);
		ftp.login(login1,login2);
		System.out.println(ftp.getReplyString());
		//ftp.enterLocalPassiveMode();

		for(int i = 2; i < args.length;i++) {
			argument = args[i];
			String emptyString = "";
			
			Pattern pattern = Pattern.compile("^\\w+");
			Matcher matcher = pattern.matcher(argument);
			while(matcher.find()) {
				command = matcher.group(0);
			}

			commandInput = argument.replaceAll("^\\w+\\s*","");
			commandInput = commandInput.replaceAll("\'","");
			
			switch(command) {
				case "ls":
					System.out.println("-------------------------");
					String[] files2 = ftp.listNames();
					printNames(files2);
					break;
				case "cd":
					ftp.changeWorkingDirectory(commandInput);
					break;
				case "mkdir":
					ftp.makeDirectory(commandInput);
					break;
				case "delete":
					ftp.deleteFile(commandInput);
					break;
				case "get":
					get(ftp,commandInput, emptyString);
					break;
				case "put":
					put(ftp, commandInput, emptyString);
					break;
				case "rmdir":
					rmdir(ftp, commandInput, emptyString);
					break;
				 default:
						System.err.println("Command '" + command + "' not found.");
						break;				
			}
		}
		
	}


private static void printNames(String files[]) {
		if (files != null && files.length > 0) {
			for (String aFile: files) {
				System.out.println(aFile);
			}
		}
	}

private static void rmdir(FTPClient ftpClient, String parentDir, String currentDir) {
			
		try {
			String dirToList = parentDir;
			if (!currentDir.equals("")) {
					dirToList += "/" + currentDir;
			}

			FTPFile[] subFiles = ftpClient.listFiles(dirToList);

			if(subFiles != null && subFiles.length > 0) {
				for(FTPFile aFile : subFiles) {
					String currentFileName = aFile.getName();
					if(currentFileName.equals(".") || currentFileName.equals("..")) {
							continue;
					}

					String filePath = parentDir + "/" + currentDir + "/" + currentFileName;
					if(currentDir.equals("")) {
						filePath = parentDir + "/" + currentFileName;
					}
					if(aFile.isDirectory()) {
							rmdir(ftpClient, dirToList, currentFileName);
					}
					else{
						ftpClient.deleteFile(filePath);
					}
				}
			}
	
			ftpClient.removeDirectory(dirToList);
			}
			catch(IOException ex) {
				System.err.println(ex);
			}
	}

	private static void put(FTPClient ftpClient, String parentDir, String currentDir) {
		try {
			String dirToList = parentDir;
			if(!currentDir.equals("")){
				dirToList += "/" + currentDir;
			}

			Path currentRelativePath = Paths.get("");
			String localDirectoryPath = currentRelativePath.toAbsolutePath().toString();
			String localFilePath = localDirectoryPath + "/" + dirToList;
			String currentRemoteDirectoryPath = ftpClient.printWorkingDirectory().replace("\n","");
			String filePath = currentRemoteDirectoryPath + "/" + dirToList;
			File content = new File(localFilePath);

			if(content.isDirectory()){
				String remoteDirectoryPath = currentRemoteDirectoryPath + "/" + dirToList;
				ftpClient.makeDirectory(remoteDirectoryPath);

				File[] files = content.listFiles();

				if(files != null && files.length > 0){
					for(File file : files){
						String currentFile = file.getName();
						if(currentFile.equals(".") || currentFile.equals("..")){
							continue;
						}

						String localPath = parentDir + "/" + currentDir + "/" + currentFile;
						if(currentDir.equals("")){
							filePath = remoteDirectoryPath + "/" + currentFile;
							localPath = parentDir + "/" + currentFile;
						}

						if(file.isDirectory()){
							put(ftpClient, dirToList, currentFile);
						}
						else{
							currentRelativePath = Paths.get("");
							localDirectoryPath = currentRelativePath.toAbsolutePath().toString();
							localFilePath = localDirectoryPath + "/" + localPath;
							filePath = remoteDirectoryPath + "/" + currentFile;
							InputStream inputStream = new FileInputStream(localFilePath);
							if (ftpClient.storeFile(filePath, inputStream)){
								inputStream.close();
							}
							else{
								inputStream.close();
							}
						}
					}
				}
			}
			else{
				currentRelativePath = Paths.get("");
				localDirectoryPath = currentRelativePath.toAbsolutePath().toString();
				localFilePath = localDirectoryPath + "/" + parentDir;
				InputStream inputStream = new FileInputStream(localFilePath);

				if (ftpClient.storeFile(filePath,inputStream)){
					inputStream.close();
				}
				else {
					inputStream.close();
				}
			}
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}
	
	private static void get(FTPClient ftpClient, String parentDir, String currentDir){
		try{
			String dirToList = parentDir;
			if(!currentDir.equals("")){
				dirToList += "/" + currentDir;
			}

			if(ftpClient.changeWorkingDirectory(dirToList) | ftpClient.changeWorkingDirectory(currentDir)) {
				Path currentRelativePath = Paths.get("");
				String localDirectoryPath = currentRelativePath.toAbsolutePath().toString();
				String localFilePath = localDirectoryPath + "/" + dirToList;

				File newDirPath = new File(localFilePath);
				newDirPath.mkdirs();
				String currentRemoteDirectoryPath = ftpClient.printWorkingDirectory().replace("\n","");
				String[] files = ftpClient.listNames();

				if(files != null && files.length > 0){
					for(String file : files){
						String currentFile = file;
						if(currentFile.equals(".") || currentFile.equals("..")){
							continue;
						}

						String filePath = currentRemoteDirectoryPath + "/" + currentFile;
						String localPath = parentDir + "/" + currentDir + "/" + currentFile;
						if(currentDir.equals("")){
							filePath = currentRemoteDirectoryPath + "/" + currentFile;
							localPath = parentDir + "/" + currentFile;
						}

						if(ftpClient.changeWorkingDirectory(file)){
							ftpClient.changeToParentDirectory();
							get(ftpClient, dirToList, currentFile);
						}
						else{
							currentRelativePath = Paths.get("");
							localDirectoryPath = currentRelativePath.toAbsolutePath().toString();
							localFilePath = localDirectoryPath + "/" + localPath;
							OutputStream outputStream = new FileOutputStream(localFilePath);
							if (ftpClient.retrieveFile(filePath, outputStream)){
								outputStream.close();
							}
							else{
								outputStream.close();
							}
						}
					}
				}
				ftpClient.changeToParentDirectory();
			}
			else{
				Path currentRelativePath = Paths.get("");
				String localDirectoryPath = currentRelativePath.toAbsolutePath().toString();
				String localFilePath = localDirectoryPath + "/" + parentDir;

				OutputStream outputStream = new FileOutputStream(localFilePath);
				if (ftpClient.retrieveFile(parentDir,outputStream)){
					outputStream.close();
				}
				else{
					outputStream.close();
				}
			}
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}


	
}