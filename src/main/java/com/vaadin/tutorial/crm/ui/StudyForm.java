package com.vaadin.tutorial.crm.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.external.api.cardio.modals.Device;
import com.external.api.cardio.modals.Person;
import com.external.api.cardio.modals.Study;
import com.external.api.cardio.service.DeviceService;
import com.external.api.cardio.service.PersonService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;

public class StudyForm extends FormLayout {
	
	ComboBox<String> patientName = new ComboBox<String>("Patient Name");
	ComboBox<String> serialNumber = new ComboBox<String>("Device Serial Number");
	ComboBox<String> studyStaus = new ComboBox<String>("Study Status");
	DateTimePicker time = new DateTimePicker("Start Time");
	TextField duration = new TextField("Duration");
	
	PersonService personService;
	DeviceService deviceService;
	
	Button update = new Button("Save");
	Button delete = new Button("Delete");
	Button cancel = new Button("Cancel");
	
	Binder<Study> binder = new Binder<>(Study.class);
	private Study study;
	
	public StudyForm(PersonService personService, DeviceService deviceService) {
		
		addClassName("contact-form");
		getElement().setAttribute("theme", Lumo.LIGHT);

		this.personService = personService;
		this.deviceService = deviceService;
		
		LocalDate today = LocalDate.now();
		LocalDateTime min = LocalDateTime.of(today, LocalTime.MIN);
		LocalDateTime max = LocalDateTime.of(today.plusWeeks(4), LocalTime.MAX);
		duration.setHelperText("Accepted formats : 1h | 1d | 1w | 1y -> corresponding for h : hour , w : week , d : day , y : year");
		
		time.setMin(min);
		time.setMax(max);
		
		// fill patients combobox
		List<Person> patients = personService.findAll();
		List<String> patientsName = new ArrayList<>();
		patients.forEach(p -> patientsName.add(p.getFirstName()));
		patientName.setItems(patientsName);
		
		// fill the unused devices sn
		List<Device> unusedDevices = deviceService.findUnusedDevices();
		List<String> devicesSerialNumber = new ArrayList<>();
		unusedDevices.forEach(d -> devicesSerialNumber.add(d.getSerialNumber()));
		serialNumber.setItems(devicesSerialNumber);
		
		// fill status combo
		studyStaus.setItems("Ongoing", "Ended");
		
		binder.forField(patientName).asRequired("Patient Name must be filled!").bind(Study::getPatientName, Study::setPatientName);
		binder.forField(serialNumber).asRequired("Device Serial Number must be filled!").bind(Study::getDeviceSerialNumber, Study::setDeviceSerialNumber);
		binder.forField(studyStaus).asRequired("Status must be filled!").bind(Study::getStudyStatus, Study::setStudyStatus);
		binder.forField(time).asRequired("Please Pick a Date & Time").bind(Study::getStartTime, Study::setStartTime);
		binder.forField(duration).withValidator(duration -> duration.matches("\\d{1,2}[hHdDwWyY]"),"Wrong Format! maximum 2 numbers accepted").asRequired("Duration must be filled!").bind(Study::getDuration, Study::setDuration);
		
		// binder.readBean(study);
		
		patientName.setClearButtonVisible(true);
		duration.setClearButtonVisible(true);
		time.setDatePlaceholder("Date");
		time.setTimePlaceholder("Time");
		
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		
		update.addClickListener(click -> validateAndSave());
		delete.addClickListener(click -> fireEvent(new DeleteEvent(this, study)));
		cancel.addClickListener(click -> fireEvent(new CloseEvent(this)));
		
		binder.addStatusChangeListener(evt -> update.setEnabled(binder.isValid()));
		
		add(serialNumber, patientName,studyStaus , time, duration, update, delete, cancel);
	}
	
	public void setStudy(Study study) {
		this.study = study;
		binder.readBean(study);
	}
	
	private void validateAndSave() {
		try
		{
			binder.writeBean(study);
			fireEvent(new SaveEvent(this, study));
		}
		catch(ValidationException e)
		{
			e.printStackTrace();
		}
	}
	
	// Events 
	public static abstract class StudyFormEvent extends ComponentEvent<StudyForm> {
		private Study study;
		
		protected StudyFormEvent(StudyForm source, Study study) {
			super(source, false);
			this.study = study;
		}
		
		public Study getStudy() {
			return study;
		}
	}
	
	public static class SaveEvent extends StudyFormEvent {
		SaveEvent(StudyForm source, Study study) {
			super(source, study);
		}
	}
	
	public static class DeleteEvent extends StudyFormEvent {
		DeleteEvent(StudyForm source, Study study) {
			super(source, study);
		}
		
	}
	
	public static class CloseEvent extends StudyFormEvent {
		CloseEvent(StudyForm source) {
			super(source, null);
		}
	}
	
	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
