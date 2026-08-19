package kudos

import org.springframework.context.annotation.Configuration
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession

/**
 * Spring Session JDBC, enabled explicitly rather than by auto-configuration.
 *
 * Grails excludes Spring Boot's DataSourceAutoConfiguration and registers its own
 * `dataSource` bean through the GORM plugin. That happens after Boot evaluates
 * conditions, so JdbcSessionConfiguration's @ConditionalOnBean(DataSource) never
 * matches and sessions silently fall back to Tomcat's in-memory manager — which
 * drops every login on restart.
 *
 * Declaring the configuration ourselves sidesteps condition ordering: beans are
 * injected at refresh time, when Grails' dataSource exists.
 *
 * The SPRING_SESSION tables come from Liquibase (changelog-session.yaml), so no
 * schema initializer runs here.
 */
@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 2592000)   // 30 days, matching server.servlet.session.timeout
class SessionConfig {
}
