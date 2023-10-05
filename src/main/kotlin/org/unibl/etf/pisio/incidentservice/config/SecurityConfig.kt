package org.unibl.etf.pisio.incidentservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val jwksUri: String? = null

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        configureOAuth2ResourceServer(http)
        configureExchange(http)
        configureCors(http)
        configureCsrf(http)

        return http.build()
    }

    private fun configureOAuth2ResourceServer(http: ServerHttpSecurity) =
        http.oauth2ResourceServer { oAuth2ResourceServerSpec ->
            oAuth2ResourceServerSpec.jwt { jwtSpec ->
                val jwtAuthenticationConverter = JwtAuthenticationConverter()
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(GrantedAuthoritiesExtractor())

                jwtSpec.jwkSetUri(jwksUri)
                    .jwtDecoder(NimbusReactiveJwtDecoder(jwksUri))
                    .jwtAuthenticationConverter(ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter))
            }
        }


    private fun configureExchange(http: ServerHttpSecurity) = http.authorizeExchange {
        it.pathMatchers(GET, "/api/v1/incidents/**", "/api/v1/types/**").permitAll()
        it.pathMatchers(POST, "/api/v1/incidents").hasAuthority("ROLE_USER")
        it.pathMatchers(PATCH, "/api/v1/incidents/*").hasAuthority("ROLE_MODERATOR")
        it.anyExchange().authenticated()
    }

    private fun configureCsrf(http: ServerHttpSecurity) = http.csrf {
        it.disable()
    }


    private fun configureCors(http: ServerHttpSecurity) = http.cors {
        it.configurationSource {
            val corsConfig = CorsConfiguration()
            corsConfig.allowedOrigins = listOf("http://localhost:4200")
            corsConfig.allowedMethods = listOf(
                GET.name(), PUT.name(), POST.name(), DELETE.name(), PATCH.name()
            )
            corsConfig.allowedHeaders = listOf("*")
            corsConfig
        }
    }


}

class GrantedAuthoritiesExtractor : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(source: Jwt): Collection<GrantedAuthority>? {
        val realmAccess = source.getClaim("realm_access") as Map<String, Any>?
        val roles = realmAccess?.get("roles") as List<String>?
        val authorities = roles?.map {
            SimpleGrantedAuthority("ROLE_$it")
        }
        return authorities
    }
}
