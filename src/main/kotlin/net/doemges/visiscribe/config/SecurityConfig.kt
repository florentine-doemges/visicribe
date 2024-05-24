package net.doemges.visiscribe.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import reactor.core.publisher.Mono
import java.net.URI

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Value("\${okta.oauth2.issuer}")
    private val issuer: String? = null

    @Value("\${okta.oauth2.client-id}")
    private val clientId: String? = null

    @Value("\${okta.oauth2.client-secret}")
    private val clientSecret: String? = null

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/", "/images/**").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2Login { oauth2LoginSpec -> oauth2LoginSpec }
            .logout { logoutSpec ->
                logoutSpec
                    .logoutHandler(logoutHandler())
                    .logoutSuccessHandler(logoutSuccessHandler())
            }
            .securityContextRepository(WebSessionServerSecurityContextRepository())
        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(): ReactiveClientRegistrationRepository {
        val clientRegistration = ClientRegistration.withRegistrationId("okta")
            .clientId(clientId!!)
            .clientSecret(clientSecret!!)
            .issuerUri(issuer!!)
            .authorizationUri("$issuer/oauth2/v1/authorize")
            .tokenUri("$issuer/oauth2/v1/token")
            .userInfoUri("$issuer/oauth2/v1/userinfo")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/okta")  // Set the redirectUri template
            .build()

        return InMemoryReactiveClientRegistrationRepository(clientRegistration)
    }

    @Bean
    fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository {
        return WebSessionServerOAuth2AuthorizedClientRepository()
    }

    private fun logoutHandler(): ServerLogoutHandler {
        return ServerLogoutHandler { exchange, _ ->
            Mono.fromRunnable { /* Custom logout logic if needed */ }
        }
    }

    private fun logoutSuccessHandler(): ServerLogoutSuccessHandler {
        return ServerLogoutSuccessHandler { exchange, _ ->
            val request = exchange.exchange.request
            val response = exchange.exchange.response

            val baseUrl = "${request.uri.scheme}://${request.uri.authority}"
            val redirectUrl = "$issuer/v2/logout?client_id=$clientId&returnTo=$baseUrl"
            response.statusCode = HttpStatus.FOUND
            response.headers.location = URI.create(redirectUrl)
            Mono.empty<Void>()
        }
    }
}