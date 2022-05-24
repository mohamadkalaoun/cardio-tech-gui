package com.vaadin.tutorial.crm.ui;

import com.external.api.cardio.modals.UrgentNotification;
import com.external.api.cardio.service.UrgentNotificationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "notifications", layout = FirstLayout.class)
@PageTitle("Notifications")
public class NotificationsView extends VerticalLayout{
	
	private static final long serialVersionUID = -3665191592840090872L;

	private UrgentNotificationService urgentNotificationService;
	private Grid<UrgentNotification> grid = new Grid<>();
	TextField filterText = new TextField();
	
	public NotificationsView(UrgentNotificationService urgentNotificationService) {
		
		addClassName("list-view");
		getElement().setAttribute("theme", Lumo.LIGHT);
		setSizeFull();
		configGrid();
		
		this.urgentNotificationService = urgentNotificationService;
		
		filterText.setPlaceholder("Filter...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
		add(filterText , grid);
		updateList();
	}
	
	private void configGrid() {
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.NONE);
		grid.addComponentColumn(item -> {
			Icon wrg = VaadinIcon.WARNING.create();
			wrg.getStyle().set("color", "red");
			wrg.getStyle().set("align", "center");
			return wrg;
		}).setSortable(false);
		grid.addColumn(UrgentNotification::getMsg).setSortable(true).setHeader("Notification Message");
		grid.addColumn(UrgentNotification::getPatientName).setHeader("Patient Name");
		grid.addColumn(UrgentNotification::getSerialNumber).setSortable(true).setHeader("Device Serial Number");
		grid.addColumn(UrgentNotification::getTime).setHeader("Sent On");
		grid.addComponentColumn(item -> {
			Button clear = new Button(VaadinIcon.CLOSE_BIG.create(), listener -> {
				urgentNotificationService.delete(item);
				updateList();
			});
			return clear;
		}).setSortable(false).setHeader("Clear Notification");
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.MATERIAL_COLUMN_DIVIDERS, GridVariant.LUMO_ROW_STRIPES);
		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.addClassName("gridStyle");
	}
	
	private void updateList() {
		grid.setItems(urgentNotificationService.findAll(filterText.getValue()));
	}
	
}
