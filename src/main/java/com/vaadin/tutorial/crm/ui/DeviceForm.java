package com.vaadin.tutorial.crm.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.external.api.cardio.modals.Device;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;

public class DeviceForm extends FormLayout {

	TextField deviceName = new TextField("Device Name");
	TextField serialNumber = new TextField("Serial Number");
	TextField associatedPatientId = new TextField("associatedPatientId");
	DateTimePicker time = new DateTimePicker();

	Button update = new Button("Save");
	Button delete = new Button("Delete");
	Button cancel = new Button("Discard");

	Binder<Device> binder = new Binder<>(Device.class);
	private Device device;

	public DeviceForm() {

		addClassName("contact-form");
		
		getElement().setAttribute("theme", Lumo.LIGHT);
		
        LocalDate today = LocalDate.now();
        LocalDateTime min = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(today.plusWeeks(4), LocalTime.MAX);

        time.setMin(min);
        time.setMax(max);

		binder.forField(deviceName).asRequired("Device Name must be filled!").bind(Device::getDeviceName,Device::setDeviceName);
		binder.forField(serialNumber).bind(Device::getSerialNumber, Device::setSerialNumber);
		binder.forField(associatedPatientId).bind(Device::getAssociatedPatientId,Device::setAssociatedPatientId);
		binder.forField(time).asRequired("Please Pick a Date & Time").bind(Device::getTime, Device::setTime);

		deviceName.addThemeName("bordered");
		serialNumber.addThemeName("bordered");
		associatedPatientId.addThemeName("bordered");
		deviceName.setClearButtonVisible(true);

		time.setDatePlaceholder("Date");
		time.setTimePlaceholder("Time");

		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

		update.addClickListener(click -> validateAndSave());
		delete.addClickListener(click -> fireEvent(new DeleteEvent(this, device)));
		cancel.addClickListener(click -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(evt -> update.setEnabled(binder.isValid()));

		add(serialNumber, deviceName, associatedPatientId, time, update, delete, cancel);

	}

	public void setDevice(Device device) {
		this.device = device;
		binder.readBean(device);
	}

	private void validateAndSave() {
		try {
			binder.writeBean(device);
			fireEvent(new SaveEvent(this, device));
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

	// Events 
	public static abstract class DeviceFormEvent extends ComponentEvent<DeviceForm> {
		private Device device;

		protected DeviceFormEvent(DeviceForm source, Device device) {
			super(source, false);
			this.device = device;
		}

		public Device getDevice() {
			return device;
		}
	}

	public static class SaveEvent extends DeviceFormEvent {
		SaveEvent(DeviceForm source, Device device) {
			super(source, device);
		}
	}

	public static class DeleteEvent extends DeviceFormEvent {
		DeleteEvent(DeviceForm source, Device device) {
			super(source, device);
		}

	}

	public static class CloseEvent extends DeviceFormEvent {
		CloseEvent(DeviceForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
