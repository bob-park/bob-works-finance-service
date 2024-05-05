package org.bobpark.finance.configure.oauth2;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import org.bobpark.finance.configure.oauth2.propreties.BobWorksOAuth2Properties;

@Configuration
@EnableConfigurationProperties(BobWorksOAuth2Properties.class)
public class BobWorksOAuth2Configuration {
}
