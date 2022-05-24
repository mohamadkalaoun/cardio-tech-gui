package com.vaadin.tutorial.crm.ui;

import com.external.api.cardio.modals.Person;
import com.external.api.cardio.service.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
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

@Route(value = "patients", layout = FirstLayout.class)
@PageTitle("PCR Appointement")
public class PatientsView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	PersForm form;
	PersonService service;

	Grid<Person> grid = new Grid<>();
	TextField filterText = new TextField();

	Dialog dialog = new Dialog();

	public PatientsView(PersonService service) {

		addClassName("list-view");

		getElement().setAttribute("theme", Lumo.LIGHT);
		setSizeFull();
		configGrid();

		this.service = service;

		form = new PersForm();
		form.addListener(PersForm.SaveEvent.class, this::savePerson);
		form.addListener(PersForm.DeleteEvent.class, this::deletePerson);
		form.addListener(PersForm.CloseEvent.class, e -> closeEditor());

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

		Button addPersonButton = new Button("Add Person", new Icon(VaadinIcon.PLUS_CIRCLE), click -> addPerson());

		HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonButton);
		toolbar.addClassName("toolbar");
		return toolbar;
	}

	private void addPerson() {
		dialog.setHeight("500px");
		dialog.open();
		form.delete.setVisible(false);
		editPerson(new Person());
	}

	private void savePerson(PersForm.SaveEvent evt) {
		service.save(evt.getPerson());
		updateList();
		closeEditor();
	}

	private void closeEditor() {
		form.setPerson(null);
		dialog.close();
		filterText.focus();
		form.setVisible(false);
		removeClassName("editing");
	}

	private void deletePerson(PersForm.DeleteEvent evt) {
		service.delete(evt.getPerson());
		updateList();
		closeEditor();
	}

	private void updateList() {
		grid.setItems(service.findAll(filterText.getValue()));
	}

	private void configGrid() {

		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.NONE);
		grid.addColumn(Person::getFirstName).setSortable(true).setHeader("First Name");
		grid.addColumn(Person::getLastName).setSortable(true).setHeader("Last Name");
		grid.addColumn(Person::getPhoneNumber).setHeader("Phone Number");
		grid.addColumn(Person::getEmail).setSortable(true).setHeader("Email");
		grid.addColumn(Person::getTime).setHeader("Date & Time");
		grid.addComponentColumn(item -> createEditButton(grid, item)).setHeader("Edit");
		grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COLUMN_BORDERS,
				GridVariant.LUMO_ROW_STRIPES);
		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.addClassName("gridStyle");
	}

	private void editPerson(Person person) {
		if (person == null) {
			closeEditor();
		} else {
			form.setPerson(person);
			form.setVisible(true);
			addClassName("editing");
		}
	}

	private Button createEditButton(Grid<Person> grid, Person item) {
		Button button = new Button(new Icon(VaadinIcon.EDIT), clickEvent -> {
			dialog.setHeight("544px");
			dialog.open();
			form.delete.setVisible(true);
			editPerson(item);
		});
		button.setWidth("10px");
		return button;
	}
}