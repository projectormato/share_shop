package projectormato.ss.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import javax.servlet.SessionTrackingMode

@Configuration
@EnableWebSecurity
@ConfigurationProperties("ss.security")
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    // application.ymlからhttpsを使用しているかどうか判定している。trueにしたらlocalhostでは動かない。本番はtrueにする。
    var useHttps: Boolean = false

    @Bean
    open fun servletContextInitializer(): ServletContextInitializer {
        return ServletContextInitializer { servletContext ->
            // ここでhttponlyをつけてる httponlyとは、CookieへのアクセスはHTTPプロトコルに限定するもの（SSHとかはNG）
            servletContext.sessionCookieConfig.isHttpOnly = true
            // httpsならばセキュアモードにする
            if (useHttps) {
                servletContext.sessionCookieConfig.isSecure = true
            }
            servletContext.setSessionTrackingModes(
                    setOf(SessionTrackingMode.COOKIE)
            )
        }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .and()
                .csrf()
    }
}
