package com.vaadin.tutorial.crm.ui;

import javax.xml.bind.Marshaller.Listener;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;

@PWA(name = "SB-Vaadin Web-App", shortName = "Vaadin App", description = "This is an example Vaadin and Spring Boot Web App", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
public class FirstLayout extends AppLayout {

	private static final long serialVersionUID = 1L;
	
	public HorizontalLayout header = new HorizontalLayout();
	public Button swit = new Button();

	public FirstLayout() {

		getElement().setAttribute("theme", Lumo.LIGHT);
		Paragraph title = new Paragraph("Monitoring Platform");
		title.addClassName("title");

		Button patients = new Button(VaadinIcon.USERS.create() , Listener -> {
			UI.getCurrent().navigate(PatientsView.class);
		});
		patients.setText("Patients");
		patients.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		Button devices = new Button(VaadinIcon.BARCODE.create());
		devices.setText("Devices");
		devices.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		devices.addClickListener(listener -> {
			UI.getCurrent().navigate(DevicesView.class);
		});
		
		Button notifications = new Button(VaadinIcon.BELL.create(), Listener -> {
			UI.getCurrent().navigate(NotificationsView.class);
		});
		notifications.setText("Notifications");
		notifications.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		Button studies = new Button(VaadinIcon.CLIPBOARD_PULSE.create(), Listener -> {
			UI.getCurrent().navigate(StudiesView.class);
		});
		studies.setText("Studies");
		studies.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		swit.setText("Light Theme");
		swit.setIcon(new Icon(VaadinIcon.ADJUST));
		swit.addClickListener(click -> toggle());

		Icon logmeout = new Icon(VaadinIcon.SIGN_OUT);
		logmeout.getStyle().set("cursor", "pointer");
		Icon pcr = new Icon(VaadinIcon.WORKPLACE);
		Anchor logout = new Anchor("/logout", "Log out");
		header.getStyle().set("background-color", "#DDE4ED");
		header.add(pcr, title, studies,patients , devices , notifications, swit, logmeout, logout);
		header.addClassName("header");
		header.setWidth("100%");
		header.expand(title);
		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		addToNavbar(true, header);

		RouterLink listLink = new RouterLink("List", StudiesView.class);
		listLink.setHighlightCondition(HighlightConditions.sameLocation());

	}

	public void toggle() {
		String theme = getElement().getAttribute("theme");
		if (theme.equalsIgnoreCase("dark")) {
			swit.setText("Light Theme");
			header.getStyle().set("background-color", "#DDE4ED");
			getElement().setAttribute("theme", Lumo.LIGHT);
		} else if (theme.equalsIgnoreCase("light")) {
			swit.setText("Dark Theme");
			header.getStyle().set("background-color", "#182638");
			getElement().setAttribute("theme", Lumo.DARK);
		}
	}

}