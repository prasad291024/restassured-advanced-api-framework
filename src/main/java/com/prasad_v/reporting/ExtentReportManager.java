package com.prasad_v.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;
import com.prasad_v.logging.LogManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the Extent Reports configuration and lifecycle.
 * This class is responsible for initializing the report, configuring its settings,
 * and ensuring it is properly flushed at the end of test execution.
 */
public class ExtentReportManager {
    private static final CustomLogger logger = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static String reportFilePath;
    private static final String DEFAULT_REPORT_PATH = "test-output/ExtentReports/";
    private static final String DEFAULT_REPORT_NAME = "API-Automation-Report.html";
    private static final String DEFAULT_DOCUMENT_TITLE = "API Automation Test Results";
    private static final String DEFAULT_REPORT_THEME = "STANDARD";

    // Used to track information about the test environment
    private static final Map<String, String> systemInfo = new HashMap<>();

    /**
     * Initialize the ExtentReports instance
     *
     * @return The ExtentReports instance
     */
    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            createInstance();
        }
        return extentReports;
    }

    /**
     * Create a new ExtentReports instance with configured settings
     *
     * @return The ExtentReports instance
     */
    public static synchronized ExtentReports createInstance() {
        if (extentReports == null) {
            try {
                // Set up the directory for reports
                setupReportDirectory();

                // Create and configure the reporter
                ExtentSparkReporter sparkReporter = configureReporter();

                // Create the ExtentReports instance
                extentReports = new ExtentReports();
                extentReports.attachReporter(sparkReporter);

                // Add system information
                addDefaultSystemInfo();

                logger.info("ExtentReports initialized successfully at: " + reportFilePath);
            } catch (Exception e) {
                logger.error("Failed to initialize ExtentReports", e);
                throw new RuntimeException("Failed to initialize ExtentReports", e);
            }
        }
        return extentReports;
    }

    /**
     * Configure the ExtentSparkReporter with settings from configuration manager
     *
     * @return The configured ExtentSparkReporter
     */
    private static ExtentSparkReporter configureReporter() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        String reportName = config.getProperty("extent.report.name", DEFAULT_REPORT_NAME);

        // Generate timestamped report filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportFileName = reportName.replace(".html", "") + "_" + timestamp + ".html";
        reportFilePath = DEFAULT_REPORT_PATH + reportFileName;

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);

        // Configure report settings
        sparkReporter.config().setDocumentTitle(config.getProperty("extent.report.title", DEFAULT_DOCUMENT_TITLE));
        sparkReporter.config().setReportName(config.getProperty("extent.report.name", DEFAULT_REPORT_NAME));
        sparkReporter.config().setTheme(Theme.valueOf(config.getProperty("extent.report.theme", DEFAULT_REPORT_THEME)));
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss a");

        // Configure the report view order
        sparkReporter.viewConfigurer().viewOrder().as(new ViewName[] {
                ViewName.DASHBOARD, ViewName.TEST, ViewName.CATEGORY, ViewName.DEVICE, ViewName.EXCEPTION
        }).apply();

        // Apply any custom CSS if provided
        String cssPath = config.getProperty("extent.report.css", "");
        if (!cssPath.isEmpty() && new File(cssPath).exists()) {
            sparkReporter.config().setCss(cssPath);
        }

        // Apply any custom JavaScript if provided
        String jsPath = config.getProperty("extent.report.js", "");
        if (!jsPath.isEmpty() && new File(jsPath).exists()) {
            sparkReporter.config().setJs(jsPath);
        }

        return sparkReporter;
    }

    /**
     * Create the directory for storing reports if it doesn't exist
     */
    private static void setupReportDirectory() {
        File reportDir = new File(DEFAULT_REPORT_PATH);
        if (!reportDir.exists()) {
            boolean created = reportDir.mkdirs();
            if (created) {
                logger.info("Created report directory: " + reportDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create report directory: " + reportDir.getAbsolutePath());
            }
        }
    }

    /**
     * Add default system information to the report
     */
    private static void addDefaultSystemInfo() {
        ConfigurationManager config = ConfigurationManager.getInstance();

        // Add environment information
        String env = config.getProperty("environment", "N/A");
        systemInfo.put("Environment", env);
        extentReports.setSystemInfo("Environment", env);

        // Add base URL
        String baseUrl = config.getProperty("api.base.url", "N/A");
        systemInfo.put("Base URL", baseUrl);
        extentReports.setSystemInfo("Base URL", baseUrl);

        // Add Java version
        systemInfo.put("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));

        // Add OS information
        systemInfo.put("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));

        // Add execution timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        systemInfo.put("Execution Timestamp", timestamp);
        extentReports.setSystemInfo("Execution Timestamp", timestamp);
    }

    /**
     * Add or update a system information entry
     *
     * @param key The information key
     * @param value The information value
     */
    public static void setSystemInfo(String key, String value) {
        systemInfo.put(key, value);
        if (extentReports != null) {
            extentReports.setSystemInfo(key, value);
        }
    }

    /**
     * Get the path to the generated report file
     *
     * @return The report file path
     */
    public static String getReportFilePath() {
        return reportFilePath;
    }

    /**
     * Flush the reports to disk
     * This should be called at the end of test execution
     */
    public static synchronized void flush() {
        if (extentReports != null) {
            logger.info("Flushing ExtentReports to disk");
            extentReports.flush();
            logger.info("ExtentReports saved to: " + reportFilePath);
        }
    }

    /**
     * Attach screenshot to the report if desired
     *
     * @param screenshotPath Path to the screenshot file
     * @param title Title for the screenshot
     * @return True if screenshot was attached, false otherwise
     */
    public static boolean attachScreenshot(String screenshotPath, String title) {
        try {
            File screenshot = new File(screenshotPath);
            if (screenshot.exists()) {
                // Screenshot handling typically managed by ExtentTestManager
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot", e);
        }
        return false;
    }

    /**
     * Generate a report for a historic trend
     *
     * @param outputDir Directory to store trend reports
     */
    public static void generateTrendReport(String outputDir) {
        try {
            logger.info("Generating trend report in: " + outputDir);
            // Create KlovReporter or other trend reporter if needed
            // This would need additional configuration and is optional
        } catch (Exception e) {
            logger.error("Failed to generate trend report", e);
        }
    }
}