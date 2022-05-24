package com.vaadin.tutorial.crm.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.external.api.cardio.modals.Study;
import com.external.api.cardio.service.DeviceService;
import com.external.api.cardio.service.PersonService;
import com.external.api.cardio.service.StudyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.AxisType;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.Exporting;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Component
@Scope("prototype")
@Route(value = "", layout = FirstLayout.class)
@PageTitle("Studies")
public class StudiesView extends VerticalLayout {
	
	private static final long serialVersionUID = 1L;
	
	StudyForm form;
	
	StudyService service;
	PersonService personService;
	DeviceService deviceService;
	
	Grid<Study> grid = new Grid<>();
	TextField filterText = new TextField();
	
	Dialog dialog = new Dialog();
	
	public StudiesView(StudyService service, PersonService personService, DeviceService deviceService) {
		
		addClassName("list-view");
		
		getElement().setAttribute("theme", Lumo.LIGHT);
		setSizeFull();
		configGrid();
		
		this.service = service;
		this.deviceService = deviceService;
		this.personService = personService;
		
		form = new StudyForm(personService, deviceService);
		form.addListener(StudyForm.SaveEvent.class, this::saveStudy);
		form.addListener(StudyForm.DeleteEvent.class, this::deleteStudy);
		form.addListener(StudyForm.CloseEvent.class, e -> closeEditor());
		
		dialog.add(form);
		dialog.setWidth("530px");
		dialog.setHeight("640px");
		dialog.setResizable(false);
		dialog.setDraggable(false);
		dialog.addDialogCloseActionListener(outside -> prevent());
		
		add(getToolBar(), grid);
		updateList();
		closeEditor();
		
	}
	
	private void prevent() {
		Notification notification = new Notification("You Can't Leave Without Saving , Or Discard Changes!", 2600, Position.TOP_CENTER);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		notification.open();
	}
	
	private HorizontalLayout getToolBar() {
		filterText.setPlaceholder("Filter...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
		
		Button addStudyButton = new Button("Add Study", new Icon(VaadinIcon.PLUS_CIRCLE), click -> addStudy());
		
		HorizontalLayout toolbar = new HorizontalLayout(filterText, addStudyButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}
	
	private void addStudy() {
		dialog.setHeight("500px");
		dialog.open();
		form.delete.setVisible(false);
		editStudy(new Study());
	}
	
	private void saveStudy(StudyForm.SaveEvent evt) {
		service.save(evt.getStudy());
		updateList();
		closeEditor();
	}
	
	private void closeEditor() {
		form.setStudy(null);
		dialog.close();
		filterText.focus();
		form.setVisible(false);
		removeClassName("editing");
	}
	
	private void deleteStudy(StudyForm.DeleteEvent evt) {
		service.delete(evt.getStudy());
		updateList();
		closeEditor();
	}
	
	private void updateList() {
		grid.setItems(service.findAll(filterText.getValue()));
	}
	
	private void configGrid() {
		
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.NONE);
		grid.addComponentColumn(item -> {
			H4 d = new H4();
			d.getStyle().set("color", "white");
			d.getStyle().set("text-align", "center");
			if(item.getStudyStatus().equalsIgnoreCase("ongoing"))
			{
				d.setText("Ongoing");
				d.getStyle().set("background-color", "green");
			}
			else if(item.getStudyStatus().equalsIgnoreCase("ended"))
			{
				d.setText("Ended");
				d.getStyle().set("background-color", "grey");
			}
			return d;
		}).setSortable(false).setHeader("Status");
		grid.addColumn(Study::getPatientName).setSortable(true).setHeader("Patient Name");
		grid.addColumn(Study::getDeviceSerialNumber).setHeader("Device Serial Number");
		grid.addColumn(Study::getStartTime).setHeader("Start Time");
		grid.addColumn(Study::getDuration).setSortable(true).setHeader("Duration");
		grid.addComponentColumn(item -> {
			Button showChart = new Button(VaadinIcon.CHART_LINE.create(), listener -> {
				showHeartBeatChart(item);
			});
			showChart.getStyle().set("color", "red");
			if(item.getStudyStatus().equalsIgnoreCase("ended"))
			{
				showChart.getStyle().set("color", "grey");
				//				showChart.setEnabled(false);
			}
			return showChart;
		}).setSortable(false).setHeader("Heart Beats Chart");
		grid.addComponentColumn(item -> createEditButton(grid, item)).setHeader("Edit");
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.MATERIAL_COLUMN_DIVIDERS, GridVariant.LUMO_ROW_STRIPES);
		
		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.addClassName("gridStyle");
	}
	
	private void showHeartBeatChart(Study item) {
		
		int a1 = item.getId();
		Study item2 = service.findById(a1);
		List<Study> a132 = service.findAll();
		
		if(item.getHeartBeatsValues() != null)
		{
			String heartBeatsAsString = item2.getHeartBeatsValues();
			HashMap<Integer, Integer> map = getBPMSeries(heartBeatsAsString);
			Dialog chartDialog = new Dialog();
			chartDialog.setWidth("540px");
			chartDialog.setHeight("510px");
			
			H3 title = new H3(item.getStudyStatus().equalsIgnoreCase("ongoing") ? "Ongoing Study :" : "Ended Study :");
			
			// chart
			Chart chart = new Chart(ChartType.SPLINE);
			
			Configuration configuration = chart.getConfiguration();
			configuration.getTitle().setText("Latest Heart Beats Chart for " + item.getPatientName());
			configuration.getxAxis().setType(AxisType.LINEAR);
			Exporting export = new Exporting(true);
			export.setFilename(item.getPatientName() + "-heart-beats");
			configuration.setExporting(export);
			
			DataSeries mappedData = new DataSeries(map.entrySet().stream().map(e -> new DataSeriesItem(e.getKey(), e.getValue())).collect(Collectors.toList()));
			mappedData.setId("dataseries");
			configuration.addSeries(mappedData);
			
			chartDialog.add(title, chart);
			chartDialog.open();
		}
		else
			Notification.show("No Heart Beats received yet!", 2600, Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
	}
	
	private HashMap<Integer, Integer> getBPMSeries(String heartBeatsAsString) {
		List<String> heartBeatsList = Arrays.asList(heartBeatsAsString.split(","));
		List<Integer> values = heartBeatsList.stream().map(Integer::parseInt).collect(Collectors.toList());
		HashMap<Integer, Integer> map = new HashMap<>();
		int i = 1;
		for (Integer bp : values)
		{
			map.put(i, bp);
			i++;
		}
		return map;
	}
	
	private void editStudy(Study study) {
		if(study == null)
		{
			closeEditor();
		}
		else
		{
			form.setStudy(study);
			form.setVisible(true);
			addClassName("editing");
		}
	}
	
	private Button createEditButton(Grid<Study> grid, Study item) {
		Button button = new Button(new Icon(VaadinIcon.EDIT), clickEvent -> {
			dialog.setHeight("544px");
			dialog.open();
			form.delete.setVisible(true);
			editStudy(item);
		});
		button.setWidth("10px");
		return button;
	}
}