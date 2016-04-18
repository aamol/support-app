/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.module.mail;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.module.trigger.TriggerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.mail.transformer.MailToStringTransformer;
import org.springframework.integration.mail.transformer.MimeToDetatchedTransformer;
import org.springframework.integration.scheduling.PollerMetadata;


/**
 * A source module that listens for HTTP requests and emits the body as a message payload.
 * If the Content-Type matches 'text/*' or 'application/json', the payload will be a String,
 * otherwise the payload will be a byte array.
 *
 * @author Eric Bottard
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
@EnableBinding(Source.class)
//@EnableConfigurationProperties({MailSourceProperties.class})
@Import({TriggerConfiguration.class})
public class MailSource {

	@Autowired
	@Qualifier("defaultPoller")
	PollerMetadata defaultPoller;

	@Autowired
	@Bindings(MailSource.class)
	Source source;

	@Bean
	public IntegrationFlow mailInboundFlow() {
		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		javaMailProperties.setProperty("mail.imap.socketFactory.fallback", "false");
		javaMailProperties.setProperty("mail.store.protocol", "imaps");
		javaMailProperties.setProperty("mail.debug", "true");
		//String imapUrl = "imaps://amol.rentacoder%40gmail.com:poisoniv82%21@imap.gmail.com:993/INBOX";
		String imapUrl = "imaps://mnsreport:winter%4015@webmail.sapient.com:993/Amol";
		
//		javaMailProperties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		javaMailProperties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		javaMailProperties.setProperty("mail.pop3.socketFactory.fallback", "false");
//		javaMailProperties.setProperty("mail.pop3.port",  "995");
//		javaMailProperties.setProperty("mail.pop3.socketFactory.port", "995");
//		javaMailProperties.setProperty("mail.debug", "false");
//		String imapUrl = "pop3://amol.rentacoder%40gmail.com:poisoniv82%21@pop.gmail.com:995/INBOX";

		
				
		
		IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(Mail.imapInboundAdapter(imapUrl).javaMailProperties(javaMailProperties).shouldMarkMessagesAsRead(true)
				, new Consumer<SourcePollingChannelAdapterSpec>() {


			@Override
			public void accept(SourcePollingChannelAdapterSpec sourcePollingChannelAdapterSpec) {
				sourcePollingChannelAdapterSpec
						.poller(defaultPoller);
			}
		});
		
		System.out.println("Amol Test----------------");
		return flowBuilder.transform(new MimeToDetatchedTransformer()).channel(source.output()).get();
	}

}
