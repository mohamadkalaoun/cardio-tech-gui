package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 2555605721222826493L;

	LoginForm login = new LoginForm();
	Button signUp = new Button();
	
	public LoginView() {
		addClassName("login-view");
		getElement().setAttribute("theme", Lumo.LIGHT);
		setSizeFull();
		signUp.setText("Register");
		
		signUp.addClickListener(e -> signUp.getUI().ifPresent(ui -> ui.navigate(SignUpView.class)));
		
		//		Anchor signup = new Anchor();
		//      signup.add(new RouterLink("Register", SignUpView.class));
		
		TextArea textArea = new TextArea("HINT :");
		textArea.setPlaceholder("Username : user " + "Password : upass");
		
		setJustifyContentMode(JustifyContentMode.CENTER);
		setAlignItems(Alignment.CENTER);
		
		//      Set the LoginForm action to "login" to post the login form to Spring Security
		login.setAction("login");
		login.setForgotPasswordButtonVisible(false);
		
		add(new H1("Nurses"), login, textArea);
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error"))
		{
			login.setError(true);
		}
	}
}
