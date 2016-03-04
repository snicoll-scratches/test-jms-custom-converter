package com.example;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import com.example.oracle.MyFakeAdtMessage;
import com.example.oracle.ORAData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	/**
	 * Ths configuration overrides the default container factory with our custom
	 * message converter. In your case, you should enrich your existing configuration
	 * to include the custom message converter.
	 */
	@Configuration
	static class JmsConfig {

		@Autowired
		private DefaultJmsListenerContainerFactoryConfigurer configurer;

		@Autowired
		private ConnectionFactory connectionFactory;

		@Bean
		public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
			DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
			this.configurer.configure(factory, this.connectionFactory);
			factory.setMessageConverter(adtMessageConverter());
			return factory;
		}

		@Bean
		public MessageConverter adtMessageConverter() {
			return new AdtMessageConverter();
		}

	}

	@Service
	static class Startup {

		private final JmsTemplate jmsTemplate;

		@Autowired
		public Startup(JmsTemplate jmsTemplate) {
			this.jmsTemplate = jmsTemplate;
		}

		@PostConstruct
		public void send() {
			this.jmsTemplate.send("testQueue",
					s -> s.createObjectMessage(new MyFakeAdtMessage(new ORAData("Hello World"))));
		}
	}


	@Service
	static class Listener {

		/**
		 * That JMS listener no longer takes an Oracle message but takes the actual
		 * payload instead. All the logic to parse the message has been moved to the
		 * message converter.
		 */
		@JmsListener(destination = "testQueue")
		public void handleFoo(ORAData oraData) {
			System.out.println("=======================================================");
			System.out.println("Received " + oraData + " with name " + oraData.getName());
			System.out.println("=======================================================");
		}

	}
}
