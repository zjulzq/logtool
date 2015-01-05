package cn.ctp.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	private BiMap<String, String> classnames = HashBiMap.create();
	private Table<String, String, String> methodnames = HashBasedTable.create();
	private Table<String, String, String> fieldnames = HashBasedTable.create();

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
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(mapFile)))) {
			String line = null;
			String latestClassName = null;
			Splitter splitter = Splitter.on("->").trimResults();
			while ((line = bufferedReader.readLine()) != null) {
				if (isClassNameMap(line)) {
					line = line.replaceAll(COLON, StringUtils.EMPTY);
					List<String> list = splitter.splitToList(line);
					latestClassName = list.get(0);
					classnames.put(list.get(0), list.get(1));
				} else if (isMethodNameMap(line)) {
					String temp = new String(line.substring(line.lastIndexOf(COLON) + 1));
					List<String> list = splitter.splitToList(temp);
					methodnames.put(latestClassName, list.get(0), list.get(1));
				} else {
					List<String> list = splitter.splitToList(line);
					fieldnames.put(latestClassName, list.get(0), list.get(1));
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

	}
}
