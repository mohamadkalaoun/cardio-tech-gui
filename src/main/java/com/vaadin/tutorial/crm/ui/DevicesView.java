package com.vaadin.tutorial.crm.ui;

import java.util.List;

import com.external.api.cardio.modals.Device;
import com.external.api.cardio.service.DeviceService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "devices", layout = FirstLayout.class)
@PageTitle("Devices")
public class DevicesView extends VerticalLayout{
	private static final long serialVersionUID = -3665191592840090872L;

	DeviceForm form;
	DeviceService service;

	Grid<Device> grid = new Grid<>();
	TextField filterText = new TextField();

	Dialog dialog = new Dialog();

	public DevicesView(DeviceService service) {

		addClassName("list-view");

		getElement().setAttribute("theme", Lumo.LIGHT);
		setSizeFull();
		configGrid();

		this.service = service;
		
		form = new DeviceForm();
		form.addListener(DeviceForm.SaveEvent.class, this::saveDevice);
		form.addListener(DeviceForm.DeleteEvent.class, this::deleteDevice);
		form.addListener(DeviceForm.CloseEvent.class, e -> closeEditor());

		dialog.add(form);
		dialog.setWidth("510px");
		dialog.setHeight("544px");
		dialog.setResizable(false);
		dialog.setDraggable(false);
		dialog.addDialogCloseActionListener(outside -> prevent());

		add(getToolBar(), grid);
		updateList();
		closeEditor();

	}

	private void prevent() {
		Notification notification = new Notification("You Can't Leave Without Saving , Or Discard Changes!", 2600,
				Position.TOP_CENTER);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		notification.open();
	}

	private HorizontalLayout getToolBar() {
		filterText.setPlaceholder("Filter...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());

		Button addDeviceButton = new Button("Add Device", new Icon(VaadinIcon.PLUS_CIRCLE), click -> addDevice());

		HorizontalLayout toolbar = new HorizontalLayout(filterText, addDeviceButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private void addDevice() {
		dialog.setHeight("500px");
		dialog.open();
		form.delete.setVisible(false);
		editDevice(new Device());
	}

	private void saveDevice(DeviceForm.SaveEvent evt) {
		service.save(evt.getDevice());
		updateList();
		closeEditor();
	}

	private void closeEditor() {
		form.setDevice(null);
		dialog.close();
		filterText.focus();
		form.setVisible(false);
		removeClassName("editing");
	}

	private void deleteDevice(DeviceForm.DeleteEvent evt) {
		service.delete(evt.getDevice());
		updateList();
		closeEditor();
	}

	private void updateList() {
		grid.setItems(service.findAll(filterText.getValue()));
	}

	private void configGrid() {

		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.NONE);
		grid.addColumn(Device::getSerialNumber).setSortable(true).setHeader("Serial Number");
		grid.addColumn(Device::getDeviceName).setSortable(true).setHeader("Device Name");
		grid.addColumn(Device::getAssociatedPatientId).setHeader("Associated Patient Id");
		grid.addColumn(Device::getTime).setHeader("Date & Time");
		grid.addComponentColumn(item -> createEditButton(grid, item)).setHeader("Edit");
		grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COLUMN_BORDERS,
				GridVariant.LUMO_ROW_STRIPES);
		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.addClassName("gridStyle");
	}

	private void editDevice(Device person) {
		if (person == null) {
			closeEditor();
		} else {
			form.setDevice(person);
			form.setVisible(true);
			addClassName("editing");
		}
	}

	private Button createEditButton(Grid<Device> grid, Device item) {
		Button button = new Button(new Icon(VaadinIcon.EDIT), clickEvent -> {
			dialog.setHeight("544px");
			dialog.open();
			form.delete.setVisible(true);
			editDevice(item);
		});
		button.setWidth("10px");
		return button;
	}
}
