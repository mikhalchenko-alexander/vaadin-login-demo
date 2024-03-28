package com.anahoret.vaadinplayground

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext
import com.vaadin.flow.spring.security.VaadinWebSecurity
import jakarta.annotation.security.PermitAll
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@SpringBootApplication
class VaadinPlaygroundApplication

fun main(args: Array<String>) {
    runApplication<VaadinPlaygroundApplication>(*args)
}

@Configuration
@EnableWebSecurity
class SecurityConfig: VaadinWebSecurity() {

    override fun configure(http: HttpSecurity) {
        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/public/**")
            ).permitAll()
        }
        super.configure(http)
        setLoginView(http, MainView::class.java)
    }

    @Bean
    fun users(): UserDetailsService {
        val user: UserDetails = User.builder()
            .username("user") // password = password with this hash, don't tell anybody :-)
            .password("{bcrypt}$2a$10\$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
            .roles("USER")
            .build()
        val admin: UserDetails = User.builder()
            .username("admin")
            .password("{bcrypt}$2a$10\$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
            .roles("USER", "ADMIN")
            .build()
        return InMemoryUserDetailsManager(user, admin)
    }

}

class MainLayout(private val authContext: AuthenticationContext) : AppLayout() {
    init {
        val logo = H1("Vaadin CRM")
        logo.addClassName("logo")
        val header: HorizontalLayout =
            authContext.getAuthenticatedUser(UserDetails::class.java)
                .map { user ->
                    val logout = Button("Logout") { click: ClickEvent<Button?>? -> this.authContext.logout() }
                    val loggedUser = Span("Welcome " + user.getUsername())
                    HorizontalLayout(logo, loggedUser, logout)
                }.orElseGet { HorizontalLayout(logo) }


        // Other page components omitted.
        addToNavbar(header)
    }
}


@PageTitle("Main")
@Route("", layout = MainLayout::class)
@AnonymousAllowed
class MainView : VerticalLayout(), BeforeEnterObserver {

    private val login = LoginForm()

    init {
        addClassName("login-view")
        setSizeFull()

        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = JustifyContentMode.CENTER

        login.action = "login"

        add(H1("Vaadin CRM"), login)
    }

    override fun beforeEnter(beforeEnterEvent: BeforeEnterEvent) {
        // inform the user about an authentication error
        if (beforeEnterEvent.location
                .queryParameters
                .parameters
                .containsKey("error")
        ) {
            login.isError = true
        }
    }

}

@PageTitle("Private page")
@Route("private", layout = MainLayout::class)
@PermitAll
class PrivateView() : HorizontalLayout() {
    private val label: Span = Span("This is private page")

    init {
        add(label)
    }
}

@PageTitle("Public page")
@Route("public", layout = MainLayout::class)
@AnonymousAllowed
class PublicView() : HorizontalLayout() {
    private val label: Span = Span("This is public page")

    init {
        add(label)
    }
}
