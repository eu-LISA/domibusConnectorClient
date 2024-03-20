package eu.domibus.connector.client.link.ws;

import eu.domibus.connector.client.link.ws.configuration.WsLinkAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WsLinkAutoConfiguration.class)
public @interface EnableWsLink {

    /** push mode enabled **/
    boolean push() default false;

}
