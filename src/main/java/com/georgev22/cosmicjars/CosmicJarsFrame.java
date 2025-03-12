package com.georgev22.cosmicjars;

import com.georgev22.cosmicjars.gui.ConfigPopup;
import com.georgev22.cosmicjars.gui.HistoryTextField;
import com.georgev22.cosmicjars.gui.SmartScroller;
import com.georgev22.cosmicjars.helpers.MinecraftServer;
import com.georgev22.cosmicjars.utilities.ConsoleOutputHandler;
import com.georgev22.cosmicjars.utilities.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

public class CosmicJarsFrame extends JFrame {
    private final ConsoleOutputHandler outputHandler;
    private final HistoryTextField commandTextField;
    private final JTextArea infoPanelTextArea;
    private static CosmicJarsFrame instance;
    private final List<Long> processIds = new ArrayList<>();
    private final CosmicJars main = CosmicJars.getInstance();

    public static CosmicJarsFrame getInstance() {
        return instance;
    }

    public CosmicJarsFrame() {
        instance = this;
        setTitle("CosmicJars");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 600);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        JMenuItem configItem = new JMenuItem("Config");
        configItem.addActionListener(e -> ConfigPopup.showConfigPopup());
        fileMenu.add(configItem);

        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> {
            String version = Utils.getManifestValue("Version");
            String branch = Utils.getManifestValue("Git-Branch");
            String commit = Utils.getManifestValue("Git-Hash");
            String repoUrl = Utils.getManifestValue("Git-Repo");

            String aboutMessage = String.format(
                    """
                            CosmicJars %s
                            Developed by GeorgeV22
                            
                            Branch: %s
                            Commit: %s
                            GitHub: %s
                            
                            Special thanks to JetBrains for their amazing tools!""",
                    version, branch, commit, repoUrl
            );

            JOptionPane.showMessageDialog(this, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel consolePanel = new JPanel(new BorderLayout());
        JTextPane consoleTextPane = new JTextPane();
        consoleTextPane.setEditable(false);
        consoleTextPane.setBackground(Color.BLACK);
        consoleTextPane.setForeground(Color.WHITE);
        consoleTextPane.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextPane);
        consolePanel.add(consoleScrollPane, BorderLayout.CENTER);
        new SmartScroller(consoleScrollPane);
        mainPanel.add(consolePanel, BorderLayout.CENTER);

        JPanel commandPanel = new JPanel(new BorderLayout());
        commandTextField = new HistoryTextField();
        JButton sendButton = new JButton("Send");
        Action action = new AbstractAction("send") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] command = commandTextField.getText().split(" ");
                MinecraftServer server = main.getMinecraftServer();
                if (server != null && (server.getMinecraftServerProcess() != null && server.getMinecraftServerProcess().isAlive())) {
                    server.sendCommandToServer(command);
                } else {
                    main.sendCommand(command);
                }
                commandTextField.setText("");
            }
        };
        commandTextField.setAction(action);
        sendButton.setAction(action);
        sendButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
        sendButton.getActionMap().put("send", action);

        commandPanel.add(commandTextField, BorderLayout.CENTER);
        commandPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(commandPanel, BorderLayout.SOUTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanelTextArea = new JTextArea();
        infoPanelTextArea.setEditable(false);
        infoPanel.add(new JLabel("Info"));
        mainPanel.add(infoPanel, BorderLayout.EAST);

        getContentPane().add(mainPanel);

        outputHandler = new ConsoleOutputHandler(consoleTextPane, main.getLogger());
        Runtime.getRuntime().addShutdownHook(new Thread(outputHandler::stop));
        PrintStream consolePrintStream = new PrintStream(outputHandler);
        new Thread(outputHandler).start();
        System.setOut(consolePrintStream);
        System.setErr(consolePrintStream);

        JLabel cpuUsageLabel = new JLabel("CPU Usage: ");
        JLabel memoryUsageLabel = new JLabel("Memory Usage: ");
        JLabel systemFreeMemoryLabel = new JLabel("Total Free Memory: ");

        SystemInfo sysInfo = new SystemInfo();
        OperatingSystem os = sysInfo.getOperatingSystem();

        StandardChartTheme darkTheme = getStandardChartTheme(mainPanel);
        ChartFactory.setChartTheme(darkTheme);
        DefaultCategoryDataset cpuDataset = new DefaultCategoryDataset();
        JFreeChart cpuChart = createChart("CPU Usage", "Time", "Usage (%)", cpuDataset);
        ChartPanel cpuChartPanel = createChartPanel(cpuChart);

        DefaultCategoryDataset memoryDataset = new DefaultCategoryDataset();
        JFreeChart memoryChart = createChart("Memory Usage", "Time", "Usage (MB)", memoryDataset);
        ChartPanel memoryChartPanel = createChartPanel(memoryChart);

        Timer timer = new Timer(1000, e -> {
            DecimalFormat df = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            double totalCpuUsage = 0;
            double totalMemoryUsage = 0;

            for (Iterator<Long> it = processIds.iterator(); it.hasNext(); ) {
                try {
                    long pid = it.next();
                    OSProcess process = os.getProcess((int) pid);
                    if (process == null) {
                        it.remove();
                        continue;
                    }
                    totalCpuUsage += process.getProcessCpuLoadCumulative();
                    totalMemoryUsage += process.getResidentSetSize();
                } catch (Exception ex) {
                    CosmicJars.getInstance().getLogger().error("Error getting process CPU usage: {}", ex.getMessage());
                    ((Timer) e.getSource()).stop();
                    break;
                }
            }

            double averageCpuUsage = processIds.isEmpty() ? 0 : totalCpuUsage / processIds.size();
            cpuUsageLabel.setText("CPU Usage: " + df.format(averageCpuUsage) + "%");
            cpuDataset.addValue(averageCpuUsage, "Usage", String.valueOf(System.currentTimeMillis()));

            double averageMemoryUsage = processIds.isEmpty() ? 0 : totalMemoryUsage / (1024 * 1024 * processIds.size());
            memoryUsageLabel.setText("Memory Usage: " + df.format(averageMemoryUsage) + "MB");
            memoryDataset.addValue(averageMemoryUsage, "Usage", String.valueOf(System.currentTimeMillis()));
            systemFreeMemoryLabel.setText("Free Memory: " + sysInfo.getHardware().getMemory().getAvailable() / (1024 * 1024) + "MB");
        });
        timer.start();

        infoPanel.add(new JLabel("OS: " + os.getVersionInfo().toString()));
        infoPanel.add(new JLabel("Java: " + System.getProperty("java.version")));
        infoPanel.add(new JLabel("Total Memory: " + sysInfo.getHardware().getMemory().getTotal() / (1024 * 1024) + "MB"));
        infoPanel.add(systemFreeMemoryLabel);
        infoPanel.add(new JLabel("Virtual Memory: " + sysInfo.getHardware().getMemory().getVirtualMemory().getVirtualMax() / (1024 * 1024) + "MB"));
        infoPanel.add(new JLabel("CPU: " + sysInfo.getHardware().getProcessor().getProcessorIdentifier().getName()));
        infoPanel.add(new JLabel("CPU Cores: " + sysInfo.getHardware().getProcessor().getPhysicalProcessorCount()));
        infoPanel.add(cpuUsageLabel);
        infoPanel.add(cpuChartPanel);
        infoPanel.add(memoryUsageLabel);
        infoPanel.add(memoryChartPanel);
        setVisible(true);
    }

    private static @NotNull StandardChartTheme getStandardChartTheme(@NotNull JPanel mainPanel) {
        StandardChartTheme darkTheme = new StandardChartTheme("Dark Theme");
        darkTheme.setAxisLabelPaint(mainPanel.getForeground());
        darkTheme.setItemLabelPaint(mainPanel.getForeground());
        darkTheme.setTickLabelPaint(mainPanel.getForeground());
        darkTheme.setTitlePaint(mainPanel.getForeground());
        darkTheme.setSubtitlePaint(mainPanel.getForeground());
        darkTheme.setLegendBackgroundPaint(mainPanel.getBackground());
        darkTheme.setLegendItemPaint(mainPanel.getForeground());
        darkTheme.setPlotBackgroundPaint(mainPanel.getBackground());
        darkTheme.setChartBackgroundPaint(mainPanel.getBackground());
        darkTheme.setDomainGridlinePaint(mainPanel.getForeground());
        darkTheme.setRangeGridlinePaint(mainPanel.getForeground());
        darkTheme.setRegularFont(mainPanel.getFont());
        return darkTheme;
    }

    /**
     * Adds CPU and memory usage monitoring for specified process IDs.
     *
     * @param processIds An array of process IDs to monitor.
     */
    public void addCpuAndMemoryUsage(Long... processIds) {
        this.processIds.addAll(Arrays.asList(processIds));
    }

    /**
     * Creates a line chart.
     *
     * @param title      The chart title.
     * @param xAxisLabel The label for the X-axis.
     * @param yAxisLabel The label for the Y-axis.
     * @param dataset    The dataset for the chart.
     * @return A JFreeChart object representing the line chart.
     */
    @Contract("_, _, _, _ -> new")
    private @NotNull JFreeChart createChart(String title, @SuppressWarnings("SameParameterValue") String xAxisLabel, String yAxisLabel, DefaultCategoryDataset dataset) {
        return ChartFactory.createLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a panel for displaying a chart.
     *
     * @param chart The chart to be displayed in the panel.
     * @return A ChartPanel object containing the specified chart.
     */
    private @NotNull ChartPanel createChartPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        return chartPanel;
    }

    public void printToConsole(String text) {
        outputHandler.addToQueue(text + "\n");
    }

    public void addToInfoPanel(String text) {
        infoPanelTextArea.append(text + "\n");
    }

}
