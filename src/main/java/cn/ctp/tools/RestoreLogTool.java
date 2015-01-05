package cn.ctp.tools;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.ctp.task.RestoreLogTask;

import com.google.common.collect.Sets;

public class RestoreLogTool {

	public static void main(String[] args) {
		String logFile = null;
		String mapFile = null;
		Set<String> basePackages = Sets.newHashSet();
		if (args.length >= 4) {
			logFile = args[1];
			mapFile = args[2];
			for (int i = 3; i < args.length; i++) {
				basePackages.add(args[i]);
			}
		} else if (args.length == 3) {
			logFile = args[1];
			mapFile = args[2];
			basePackages.add("cn.ctp");
		} else if (args.length == 2) {
			logFile = args[1];
			String[] fileNames = loadFileNamesFromRootPath();
			mapFile = searchFileName(fileNames, ".map");
			basePackages.add("cn.ctp");
		} else {
			String[] fileNames = loadFileNamesFromRootPath();
			logFile = searchFileName(fileNames, ".log");
			mapFile = searchFileName(fileNames, ".map");
			for (int i = 0; i < fileNames.length; i++) {
				String fileName = fileNames[i];
				if (StringUtils.isNotEmpty(fileName) && fileName.endsWith(".map")) {
					mapFile = fileName;
					break;
				}
			}
			basePackages.add("cn.ctp");
		}

		if (StringUtils.isEmpty(logFile) || StringUtils.isEmpty(mapFile)) {
			throw new RuntimeException("not enough arguments");
		}

		RestoreLogTask restoreLogTask = new RestoreLogTask(logFile, mapFile, basePackages);
		restoreLogTask.restore();
	}

	private static String[] loadFileNamesFromRootPath() {
		File file = new File(System.getProperty("user.dir"));
		return file.list();
	}

	private static String searchFileName(String[] fileNames, String suffix) {
		if (fileNames == null) {
			return null;
		}

		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			if (StringUtils.isNotEmpty(fileName) && fileName.endsWith(suffix)) {
				return fileName;
			}
		}
		return null;
	}

}
