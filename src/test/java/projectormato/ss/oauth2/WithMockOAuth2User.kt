package projectormato.ss.oauth2

import org.springframework.security.test.context.support.WithSecurityContext

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithOAuth2SecurityContextFactory::class)
annotation class WithMockOAuth2User
