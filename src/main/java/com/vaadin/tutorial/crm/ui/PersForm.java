package com.vaadin.tutorial.crm.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.external.api.cardio.modals.Person;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;

public class PersForm extends FormLayout {

	TextField Fname = new TextField("First Name");
	TextField Lname = new TextField("Last Name");
	TextField phoneNumber = new TextField("Phone Number");
	EmailField email = new EmailField("Email");
	DateTimePicker time = new DateTimePicker();

	Button update = new Button("Save");
	Button delete = new Button("Delete");
	Button cancel = new Button("Cancel");

	Binder<Person> binder = new Binder<>(Person.class);
	private Person person;

	public PersForm() {

		addClassName("contact-form");
		
		getElement().setAttribute("theme", Lumo.LIGHT);
		phoneNumber.setPattern("\\d*");
		phoneNumber.setPreventInvalidInput(true);
		
        LocalDate today = LocalDate.now();
        LocalDateTime min = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(today.plusWeeks(4), LocalTime.MAX);

        time.setMin(min);
        time.setMax(max);

		binder.forField(Fname).asRequired("First Name must be filled!").bind(Person::getFirstName,
				Person::setFirstName);
		binder.forField(Lname).asRequired("Last Name must be filled!").bind(Person::getLastName, Person::setLastName);
		binder.forField(phoneNumber).asRequired("Phone Number must be filled!").bind(Person::getPhoneNumber,
				Person::setPhoneNumber);
		binder.forField(email).withValidator(new EmailValidator("This doesn't look like a valid email address"))
				.bind(Person::getEmail, Person::setEmail);
		binder.forField(time).asRequired("Please Pick a Date & Time").bind(Person::getTime, Person::setTime);

		// binder.readBean(person);

		Fname.addThemeName("bordered");
		Lname.addThemeName("bordered");
		phoneNumber.addThemeName("bordered");
		email.setClearButtonVisible(true);
		Fname.setClearButtonVisible(true);
		Lname.setClearButtonVisible(true);
		phoneNumber.setClearButtonVisible(true);
		email.setErrorMessage("Please enter a valid email address");

		time.setDatePlaceholder("Date");
		time.setTimePlaceholder("Time");

		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

		update.addClickListener(click -> validateAndSave());
		delete.addClickListener(click -> fireEvent(new DeleteEvent(this, person)));
		cancel.addClickListener(click -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(evt -> update.setEnabled(binder.isValid()));

		add(Fname, Lname, phoneNumber, email, time, update, delete, cancel);

	}

	public void setPerson(Person person) {
		this.person = person;
		binder.readBean(person);
	}

	private void validateAndSave() {
		try {
			binder.writeBean(person);
			fireEvent(new SaveEvent(this, person));
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

	// Events 
	public static abstract class PersonFormEvent extends ComponentEvent<PersForm> {
		private Person person;

		protected PersonFormEvent(PersForm source, Person person) {
			super(source, false);
			this.person = person;
		}

		public Person getPerson() {
			return person;
		}
	}

	public static class SaveEvent extends PersonFormEvent {
		SaveEvent(PersForm source, Person person) {
			super(source, person);
		}
	}

	public static class DeleteEvent extends PersonFormEvent {
		DeleteEvent(PersForm source, Person person) {
			super(source, person);
		}

	}

	public static class CloseEvent extends PersonFormEvent {
		CloseEvent(PersForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
