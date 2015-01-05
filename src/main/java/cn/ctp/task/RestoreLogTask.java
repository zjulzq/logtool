package cn.ctp.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class RestoreLogTask {
	private static final String COLON = ":";
	private String logFile;
	private String mapFile;
	private Set<String> basePackages = Sets.newHashSet();
	private BiMap<String, String> classNames = HashBiMap.create();
	private Table<String, String, String> methodNames = HashBasedTable.create();
	private Table<String, String, String> fieldNames = HashBasedTable.create();

	public RestoreLogTask(String logFile, String mapFile, Set<String> basePackages) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(logFile), "the log file name can't be empty");
		Preconditions.checkArgument(StringUtils.isNotEmpty(mapFile), "the map file name can't be empty");
		Preconditions.checkArgument(basePackages.size() > 0, "there must be at least 1 base package");
		this.logFile = logFile;
		this.mapFile = mapFile;
		this.basePackages.addAll(basePackages);
	}

	public void restore() {
		loadMapFile();
		restoreLogFile();
	}

	private void loadMapFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(mapFile)))) {
			String line = null;
			String latestClassName = null;
			Splitter splitter = Splitter.on("->").trimResults();
			while ((line = reader.readLine()) != null) {
				if (isClassNameMap(line)) {
					line = line.replaceAll(COLON, StringUtils.EMPTY);
					List<String> list = splitter.splitToList(line);
					latestClassName = list.get(0);
					classNames.put(list.get(0), list.get(1));
				} else if (isMethodNameMap(line)) {
					String temp = new String(line.substring(line.lastIndexOf(COLON) + 1));
					List<String> list = splitter.splitToList(temp);
					methodNames.put(latestClassName, list.get(0), list.get(1));
				} else {
					List<String> list = splitter.splitToList(line);
					fieldNames.put(latestClassName, list.get(0), list.get(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isClassNameMap(String line) {
		if (!line.endsWith(COLON)) {
			return false;
		}

		for (String basePackage : basePackages) {
			if (line.startsWith(basePackage)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMethodNameMap(String line) {
		return line.contains("(");
	}

	private void restoreLogFile() {
		int lastIndex = logFile.lastIndexOf(".");
		String newLogFile = logFile.substring(0, lastIndex) + "-restore" + logFile.substring(lastIndex);
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(logFile)));
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(newLogFile)))) {
			String line = null;
			Set<String> badNames = classNames.values();
			BiMap<String, String> bad2good = classNames.inverse();
			while ((line = reader.readLine()) != null) {
				for (String badName : badNames) {
					if (line.contains(badName)) {
						line = line.replace(badName, bad2good.get(badName));
						break;
					}
				}
				writer.write(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
