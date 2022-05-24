/*
 * package com.vaadin.tutorial.crm.ui;
 * 
 * import java.io.IOException; import java.io.InputStream; import
 * java.nio.charset.StandardCharsets; import java.nio.file.Files; import
 * java.nio.file.Path; import java.util.ArrayList; import java.util.List; import
 * java.util.Objects;
 * 
 * import com.vaadin.flow.component.button.Button; import
 * com.vaadin.flow.component.charts.Chart; import
 * com.vaadin.flow.component.charts.model.ChartType; import
 * com.vaadin.flow.component.charts.model.Configuration; import
 * com.vaadin.flow.component.charts.model.Cursor; import
 * com.vaadin.flow.component.charts.model.DataSeries; import
 * com.vaadin.flow.component.charts.model.DataSeriesItem; import
 * com.vaadin.flow.component.charts.model.PlotOptionsPie; import
 * com.vaadin.flow.component.charts.model.Tooltip; import
 * com.vaadin.flow.component.charts.util.ChartSerialization; import
 * com.vaadin.flow.component.orderedlayout.HorizontalLayout; import
 * com.vaadin.flow.router.Route; import
 * com.vaadin.flow.server.frontend.FrontendTools; import
 * com.vaadin.flow.server.frontend.FrontendUtils;
 * 
 * @Route("chart") public class PieWithLegend extends HorizontalLayout {
 * 
 * protected Chart chart;
 * 
 * private Button exp; private static final String INTERNAL_BUNDLE_PATH =
 * "/META-INF/frontend/generated/jsdom-exporter-bundle.js"; private final Path
 * tempDirPath; private final Path bundleTempPath; private static final String
 * SCRIPT_TEMPLATE = "const exporter = require('%s');\n" + "exporter({\n" +
 * "chartConfiguration: %s,\n" + "outFile: '%s',\n" + "exportOptions: %s,\n" +
 * "})";
 * 
 * public PieWithLegend() throws IOException {
 * 
 * exp = new Button(); exp.setText("Export"); exp.setSizeFull();
 * 
 * tempDirPath = Files.createTempDirectory("svg-export"); bundleTempPath =
 * tempDirPath.resolve("export-svg-bundle.js");
 * 
 * InputStream is = getClass().getResourceAsStream(INTERNAL_BUNDLE_PATH);
 * Files.copy(getClass().getResourceAsStream(INTERNAL_BUNDLE_PATH),
 * bundleTempPath);
 * 
 * exp.addClickListener(listener -> {
 * 
 * try { String hala2Svg = generate(chart.getConfiguration());
 * System.out.println("hala2 why not : "+hala2Svg); } catch (IOException e) { //
 * TODO Auto-generated catch block e.printStackTrace(); } catch
 * (InterruptedException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); }
 * 
 * });
 * 
 * 
 * setSizeFull(); add(exp , initDemo()); }
 * 
 * public Chart initDemo() { chart = new Chart(ChartType.PIE);
 * 
 * Configuration conf = chart.getConfiguration(); conf.setExporting(true);
 * conf.setTitle("Browser market shares in January, 2018");
 * 
 * Tooltip tooltip = new Tooltip(); tooltip.setValueDecimals(1);
 * conf.setTooltip(tooltip);
 * 
 * PlotOptionsPie plotOptions = new PlotOptionsPie();
 * plotOptions.setAllowPointSelect(true); plotOptions.setCursor(Cursor.POINTER);
 * plotOptions.setShowInLegend(true); conf.setPlotOptions(plotOptions);
 * 
 * DataSeries series = new DataSeries(); DataSeriesItem chrome = new
 * DataSeriesItem("Chrome", 61.41); chrome.setSliced(true);
 * chrome.setSelected(true); series.add(chrome); series.add(new
 * DataSeriesItem("Internet Explorer", 11.84)); series.add(new
 * DataSeriesItem("Firefox", 10.85)); series.add(new DataSeriesItem("Edge",
 * 4.67)); series.add(new DataSeriesItem("Safari", 4.18)); series.add(new
 * DataSeriesItem("Sogou Explorer", 1.64)); series.add(new
 * DataSeriesItem("Opera", 6.2)); series.add(new DataSeriesItem("QQ", 1.2));
 * series.add(new DataSeriesItem("Others", 2.61)); conf.setSeries(series);
 * chart.setVisibilityTogglingDisabled(true);
 * 
 * return chart; }
 * 
 * public String generate(Configuration chartConfiguration) throws IOException,
 * InterruptedException { if (isClosed()) { throw new IllegalStateException(
 * "This generator is already closed."); } Configuration config =
 * Objects.requireNonNull(chartConfiguration,
 * "Chart configuration must not be null."); String jsonConfig =
 * ChartSerialization.toJSON(config);
 * 
 * Path chartFilePath = Files.createTempFile(tempDirPath, "chart", ".svg");
 * String chartFileName = chartFilePath.toFile().getName(); String script =
 * String.format( SCRIPT_TEMPLATE, bundleTempPath.toFile().getAbsolutePath()
 * .replaceAll("\\\\", "/"), jsonConfig, chartFileName);
 * 
 * runJavascript(script); // when script completes, the chart svg file should
 * exist try { return new String(Files.readAllBytes(chartFilePath),
 * StandardCharsets.UTF_8); } finally { Files.delete(chartFilePath); } }
 * 
 * public boolean isClosed() { return !tempDirPath.toFile().exists(); }
 * 
 * int runJavascript(String script) throws InterruptedException, IOException {
 * FrontendTools tools = new FrontendTools("", () ->
 * FrontendUtils.getVaadinHomeDirectory().getAbsolutePath()); String node =
 * tools.getNodeExecutable(); List<String> command = new ArrayList<>();
 * command.add(node); command.add("-e"); // this check is necessary since
 * running a script on windows eats up // double quotes if
 * (FrontendUtils.isWindows()) { command.add(script.replace("\"", "\\\"")); }
 * else { command.add(script); } ProcessBuilder builder =
 * FrontendUtils.createProcessBuilder(command); builder.inheritIO(); Process
 * process = builder.start(); return process.waitFor(); }
 * 
 * }
 */