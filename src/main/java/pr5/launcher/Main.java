package pr5.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.cli.*;
import pr5.control.Controller;
import pr5.view.SimWindow;
import pr5.exception.SimulatorError;
import pr5.ini.Ini;

/**
 * Main class.
 */
public class Main {

    private enum modesAvailable {
        GUI, BATCH
    }
    private final static Integer _timeLimitDefaultValue = 10;
    private final static String _modeDefaultValue = "batch";
    private static Integer _timeLimit = null;
    private static String _inFile = null;
    private static String _outFile = null;
    private static modesAvailable _mode = null;
    private static String split = System.getProperty("file.separator");

    /**
     * Parse a list of arguments given. The method creates a cmdLineOptions to
     * store the different options available. Then it parses the list of
     * arguments given setting values to the simulator configuration if they
     * match the options available.
     */
    private static void parseArgs(String[] args) {

        // define the validd command line options
        //
        Options cmdLineOptions = buildOptions();

        // parse the command line as provided in args
        //
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parseHelpOption(line, cmdLineOptions);
            parseModeOption(line);
            parseInFileOption(line);
            parseOutFileOption(line);
            parseStepsOption(line);

            // if there are some remaining arguments, then something wrong is
            // provided in the command line!
            //
            String[] remaining = line.getArgs();
            if (remaining.length > 0) {
                String error = "Illegal arguments:";
                for (String o : remaining) {
                    error += (" " + o);
                }
                throw new ParseException(error);
            }

        } catch (ParseException e) {
            // new Piece(...) might throw GameError exception
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }

    }

    /**
     * This method creates the different options for the commandLine. It adds to
     * an object Options the different values it could be and the information of
     * how they work just in case the user ask for it using the command help.
     *
     */
    private static Options buildOptions() {
        Options cmdLineOptions = new Options();

        cmdLineOptions.addOption(Option.builder("h").longOpt("help")
                .desc("Print this message").build());
        cmdLineOptions.addOption(Option.builder("i").longOpt("input")
                .hasArg().desc("Events input file").build());
        cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
                .desc("’batch’ for batch mode and ’gui’ for GUI mode\n"
                        + "(default value is ’batch’)").build());
        cmdLineOptions.addOption(
                Option.builder("o").longOpt("output").hasArg()
                        .desc("Output file, where reports are written.").build());
        cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg()
                .desc("Ticks to execute the simulator's main loop (default value"
                        + " is " + _timeLimitDefaultValue + ").")
                .build());

        return cmdLineOptions;
    }

    /**
     * Parse in the command line the option "help". It shows the information for
     * the usage of the commands.
     */
    private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
            System.exit(0);
        }
    }

    /**
     * Parse in the command line the option "mode". It sets the value of _Mode
     * to the value given by the user (Typically: GUI or Batch)
     *
     * @throws ParseException Exception thrown when not valid mode given.
     */
    private static void parseModeOption(CommandLine line) throws ParseException {
        String s = line.getOptionValue("m", _modeDefaultValue);
        if ("gui".equals(s)) {
            _mode = modesAvailable.GUI;
        } else if ("batch".equals(s)) {
            _mode = modesAvailable.BATCH;
        } else {
            throw new ParseException("Mode not valid given, check help command "
                    + "to see which modes are available");
        }
    }

    /**
     * Parse in the command line the option "inFile". It sets the value of
     * _inFile to the expected value for the user for the name of the inputFile.
     *
     * @throws ParseException Exception thrown when invalid value for time
     * limit.
     */
    private static void parseInFileOption(CommandLine line) throws ParseException {
        _inFile = line.getOptionValue("i");
        if (_inFile == null && _mode != modesAvailable.GUI) {
            throw new ParseException("An events file is missing");
        }
    }

    /**
     * Parse in the command line the option "outputFile". It sets the value of
     * _outFile to the expected value for the user for the name of the
     * outputFile.
     *
     * @throws ParseException Exception throwed when invalid value for time
     * limit.
     */
    private static void parseOutFileOption(CommandLine line) throws ParseException {
        _outFile = line.getOptionValue("o");
    }

    /**
     * Parse in the command line the option "numberOfTicks". It sets the value
     * _timeLimit to the given value for the user for the number of ticks in the
     * simulation.
     *
     * @throws ParseException Exception throwed when invalid value for time
     * limit.
     */
    private static void parseStepsOption(CommandLine line) throws ParseException {
        String t = line.getOptionValue("t", _timeLimitDefaultValue.toString());
        try {
            _timeLimit = Integer.parseInt(t);
            assert (_timeLimit < 0);

        } catch (Exception e) {
            throw new ParseException("Invalid value for time limit: " + t);
        }
    }

    /**
     * This method run the simulator on all files that ends with .ini if the
     * given path, and compares that output to the expected output. It assumes
     * that for example "example.ini" the expected output is stored in
     * "example.ini.eout". The simulator's output will be stored in
     * "example.ini.out"
     *
     * @throws IOException
     */
    public static void test(String path) throws Exception {

        File dir = new File(path);

        if (!dir.exists()) {
            throw new FileNotFoundException(path);
        }

        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".ini");
            }
        });

        for (File file : files) {
            test(file.getAbsolutePath(), file.getAbsolutePath() + ".out",
                    file.getAbsolutePath() + ".eout", 10);
        }

    }

    /**
     * Generates a file using the simulator running during a given number of
     * ticks. Then it compares between what the value of the file should be and
     * what it is.
     *
     * @param inFile Name of the inputfile to be processed during the
     * simulation.
     * @param outFile Name of the outputfile after proccesing the input file.
     * @param expectedOutFile Name of the expected file to be compared.
     * @param timeLimit Number of ticks for the simulator to run.
     */
    private static void test(String inFile, String outFile,
            String expectedOutFile, int timeLimit) throws Exception {
        _outFile = outFile;
        _inFile = inFile;
        _timeLimit = timeLimit;
        startBatchModeTest();
        boolean equalOutput = (new Ini(_outFile)).equals(new Ini(expectedOutFile));
        System.out.println("Result for: '" + _inFile + "' : "
                + (equalOutput ? "OK!" : ("not equal to expected output +'"
                        + expectedOutFile + "'")));
    }

    /**
     * Run the simulator in batch test mode. Exceptions are not caught during
     * the execution of the method so a test method can analyse wether or not
     * the exception was expected or not according to the configuration given to
     * the traffic simulator to start simulating.
     *
     */
    private static void startBatchModeTest() throws Exception {
        Controller control = new Controller(_outFile == null ? System.out
                : new FileOutputStream(_outFile));
        control.run(_inFile, _timeLimit == null ? _timeLimitDefaultValue
                : _timeLimit);
    }

    /**
     * Run the simulator in batch mode.
     *
     */
    private static void startBatchMode() {
        try {
            Controller control = new Controller(_outFile == null ? System.out
                    : new FileOutputStream(_outFile));
            control.run(_inFile, _timeLimit == null ? _timeLimitDefaultValue
                    : _timeLimit);
        } catch (FileNotFoundException e) {
            System.err.println("Error with output file: " + _outFile);
        } catch (SimulatorError e) {
            System.err.println("Error during controller execution...");
        }
    }

    /**
     * Run the simulator in GUI mode.
     *
     */
    private static void startGUIMode() {
        try {
            new SimWindow(null == _inFile ? "" : _inFile,
                    new Controller(_timeLimit == null ? _timeLimitDefaultValue
                            : _timeLimit,
                            _outFile == null ? System.out
                                    : new FileOutputStream(_outFile)));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found!");
        }
    }

    /**
     * Start the simulator choosing one of the modes available: GUI or Batch.
     *
     */
    private static void start(String[] args) throws IOException {
        parseArgs(args);
        switch (_mode) {
            case GUI:
                startGUIMode();
                break;
            case BATCH:
                startBatchMode();
                break;
        }
    }

    public static void main(String[] args) throws IOException,
            InvocationTargetException, InterruptedException {
        start(args);
    }
}
