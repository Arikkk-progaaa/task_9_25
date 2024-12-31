package util;

import org.apache.commons.cli.*;

import java.io.File;

public class CliUtils {
    public static class CmdParams {
        public String inputFile;
        public String outputFile;
        public boolean test;
        public boolean window;
    }

    public static CmdParams parseCmdArgs(String[] args) {
        CmdParams params = new CmdParams();
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        options.addOption(output);

        Option window = new Option("w", "window", false, "windowed");
        options.addOption(window);

        Option test = new Option("t", "test", false, "tests");
        options.addOption(test);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Main", options);

            System.exit(70);
            return null;
        }
        String moduleDir = getModuleDirectory();
        params.inputFile = cmd.hasOption("input")
                ? getFullPath(cmd.getOptionValue("input"), moduleDir)
                : moduleDir + File.separator + "src" + File.separator + "input.txt";
        params.outputFile = cmd.hasOption("output")
                ? getFullPath(cmd.getOptionValue("output"), moduleDir)
                : moduleDir + File.separator + "src" + File.separator + "output.txt";
        params.window = cmd.hasOption("window");
        params.test = cmd.hasOption("test");
        return params;
    }

    public static String getModuleDirectory() {
        try {
            String classPath = CliUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            String moduleName = new File(classPath).getName();

            return System.getProperty("user.dir") + File.separator + moduleName;
        } catch (Exception e) {
            System.err.println("Failed to determine module directory: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private static String getFullPath(String filePath, String moduleDir) {
        File file = new File(filePath);
        return file.isAbsolute() ? filePath : moduleDir + File.separator + "src" + File.separator + filePath;
    }
}
