package com.anahoret.vaadinplayground

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@Theme(value = "playground")
class VaadinPlaygroundApplication : AppShellConfigurator

fun main(args: Array<String>) {
	runApplication<VaadinPlaygroundApplication>(*args)
}

@PageTitle("Main")
@Route("/")
class MainView() : HorizontalLayout() {

	private val name: TextField = TextField("Your name")
	private val sayHello: Button = Button("Say hello")

	init {
		sayHello.addClickListener { e -> Notification.show("Hello " + name.value) }
		sayHello.addClickShortcut(Key.ENTER)
		isMargin = true
		setVerticalComponentAlignment(FlexComponent.Alignment.END, name, sayHello)
		sayHello.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
		add(name, sayHello)
	}

}
