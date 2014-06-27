package de.sydsoft.sg_gol.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

import de.sydsoft.sg_gol.gui.javafx.GuiGameOfLife;
import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.system.OSValidator;

public class Main {
	public static final String	COMPANY		= "Sydsoft Games";
	public static final String	ABOUT_URL	= "https://gol.games.sydsoft.com/";
	
	public static String		Name		= Localizer.get("dialog.header");
	public static String		Version		= Localizer.get("version");
	public static String		HKey			= "[HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + COMPANY + " - " + Name + "]";
	public static String		jarName;
	public static String		path;
	static {
		File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String[] splitted = jar.toString().replace(".jar", "").split("\\\\");
		jarName = splitted[splitted.length - 1].replace(".jar", "");
		path = jar.getParent();
		if (!path.endsWith(jarName.split("-")[0])) {
			path += "\\" + jarName.split("-")[0];
		}
	}

	public static void main(String[] args) throws Exception {
		for (String string : args) {
			if (string.equals("-HKEY")) {
				System.out.println(HKey);
				return;
			}
		}
		copyToCorrectFolder();
		ExecutorService executor = Executors.newCachedThreadPool();
		FutureTask<Void> future = new FutureTask<Void>(new Callable<Void>() {
			public Void call() throws Exception {
				if (OSValidator.isWindows()) checkRegistryDependencies();
				return null;
			}
		});
		executor.execute(future);
		GuiGameOfLife.main(args);
		executor.shutdown();
	}

	private static void copyToCorrectFolder() throws IOException {
		File newExecPath = new File(path);
		if (!newExecPath.exists()) {
			newExecPath.mkdir();
			File oldExecPath = null;
			String scriptFileContent = "";
			String scriptFileName = "/cp.";
			if (OSValidator.isWindows()) {
				oldExecPath = new File(newExecPath.getParent() + "\\" + jarName + ".exe");
				newExecPath = new File(newExecPath.toString() + "\\" + jarName + ".exe");
				scriptFileContent = "@ECHO OFF\r\nSETLOCAL\r\nMV " + oldExecPath.toString() + " " + newExecPath.toString() + "\r\n" + newExecPath.toString() + "\r\nDEL \"%~f0\"\r\n";
				scriptFileName+="bat";
			} else {
				oldExecPath = new File(newExecPath.getParent() + "\\" + jarName + ".jar");
				newExecPath = new File(newExecPath.toString() + "\\" + jarName + ".jar");
				scriptFileContent = "#!/bin/bash\rSETLOCAL\rmv " + oldExecPath.toString() + " " + newExecPath.toString() + "\r" + newExecPath.toString() + "\rrm -- \"$0\"";
				scriptFileName+="sh";
			}
			File batFile = new File(path + scriptFileName);
			Files.write(batFile.toPath(), scriptFileContent.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			try {
				new ProcessBuilder("chmod", "+x", path+scriptFileName).start().waitFor();
				new ProcessBuilder(path + scriptFileName).start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	private static void checkRegistryDependencies() throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append("Windows Registry Editor Version 5.00\r\n");
		sb.append("\r\n");
		sb.append(HKey + "\r\n");
		sb.append("\"DisplayIcon\"=\"" + path.replace("\\", "\\\\") + "\\\\" + jarName + ".exe" + "\"\r\n");
		sb.append("\"DisplayName\"=\"" + Name + "\"\r\n");
		sb.append("\"DisplayVersion\"=\"" + Version + "\"\r\n");
		sb.append("\"InstallLocation\"=\"" + path.replace("\\", "\\\\") + "\"\r\n");
		sb.append("\"UninstallString\"=\"" + path.replace("\\", "\\\\") + "\\\\uninst.exe\"\r\n");
		sb.append("\"Publisher\"=\"" + COMPANY + "\"\r\n");
		sb.append("\"URLInfoAbout\"=\"https://github.com/Sythelux/SG_GoL/blob/master/README.md\"\r\n");
		sb.append("\"EstimatedSize\"=dword:" + String.format("%08X", getFolderSize(path)).toLowerCase() + "\r\n");
		sb.append("\"NoModify\"=dword:00000001\r\n");
		sb.append("\"NoRepair\"=dword:00000001\r\n");
		sb.append("\"QuietUninstallString\"=\"\\\"" + path.replace("\\", "\\\\") + "\\\\uninst.exe\\\" /SILENT\"\r\n");

		File regFile = new File(path + "/reg.reg");
		Files.write(regFile.toPath(), sb.toString().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

		// System.out.println("reg import \"" + regFile.toString() + "\"");
		// System.out.println("reg delete \"" + HKEY + "\"");
		ProcessBuilder pb = new ProcessBuilder("reg", "import", "\"" + regFile.toString() + "\"");
		pb.redirectErrorStream(true);

		Process p = pb.start();
		ByteBuffer bb = ByteBuffer.allocate(10);
		try (InputStream stdout = p.getInputStream()) {
			for (int d = stdout.read(); d > -1; d = stdout.read()) {
				bb.putInt(d);
			}
		}
		System.out.println(new String(bb.array()));
		p.waitFor();

		regFile.delete();
	}

	private static String date() {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		return dtf.format(currentDate);
	}

	public static long getFolderSize(String startPath) throws IOException {
		final AtomicLong size = new AtomicLong(0);
		Path path = Paths.get(startPath);

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				size.addAndGet(attrs.size());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// Skip folders that can't be traversed
				System.out.println("skipped: " + file + "e=" + exc);
				return FileVisitResult.CONTINUE;
			}
		});

		return size.get();
	}
}
