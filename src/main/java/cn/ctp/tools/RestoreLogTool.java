package cn.ctp.tools;

import java.util.Set;

import cn.ctp.task.RestoreLogTask;

import com.google.common.collect.Sets;

public class RestoreLogTool {

	public static void main(String[] args) {
		if (args.length < 4) {
			throw new RuntimeException("not enough arguments");
		}

		String logFile = args[1];
		String mapFile = args[2];
		Set<String> basePackages = Sets.newHashSet();
		for (int i = 3; i < args.length; i++) {
			basePackages.add(args[i]);
		}

		RestoreLogTask restoreLogTask = new RestoreLogTask(logFile, mapFile, basePackages);
		restoreLogTask.restore();
	}

}
