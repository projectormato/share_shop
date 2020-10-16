package projectormato.ss.oauth2

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithOAuth2SecurityContextFactory : WithSecurityContextFactory<WithMockOAuth2User> {

    override fun createSecurityContext(user: WithMockOAuth2User): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val attributes = mapOf<String, Any>(
                "sub" to "1",
                "email" to "tomato@example.com"
        )
        val authorities = listOf(OAuth2UserAuthority("ROLE_USER", attributes))
        val oAuth2User = DefaultOAuth2User(authorities, attributes, "sub")
        val token = OAuth2AuthenticationToken(oAuth2User, authorities, "Google")
        context.authentication = token
        return context
    }
}
