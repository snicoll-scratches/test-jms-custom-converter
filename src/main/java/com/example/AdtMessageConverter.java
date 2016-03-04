package com.example;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.example.oracle.AdtMessage;
import com.example.oracle.MyFakeAdtMessage;
import com.example.oracle.ORAData;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

/**
 *
 * @author Stephane Nicoll
 */
class AdtMessageConverter implements MessageConverter {

	private final SimpleMessageConverter messageConverter = new SimpleMessageConverter();

	/**
	 * Depending on your use case, you may want to configure this as well if you are handling
	 * some low-level API to send the message. If you are casting the Session in some Oracle
	 * specific thing to create the AdtMessage from a payload, you may want to do this here and
	 * simply pass the payload in your jmsTemplate.
	 * <p>
	 * If you expose a `MessageConverter` in the context, it will be linked to the default listener
	 * container and the default message template. If you need several of those for your app, then
	 * you can create your own JmsTemplate.
	 * <p>
	 * In this case, I am delegating to the default. You could do so as well if the payload is not
	 * a type you handle.
	 */
	@Override
	public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException {
		return messageConverter.toMessage(o, session);
	}

	/**
	 * The first if is what you should implement based on the "real" API. I've created a fake
	 * {@link AdtMessage} that is supposed to represent the basics of the Oracle API. Instead
	 * of dealing with the payload and the type in your code, you can do this here and return
	 * the payload that you actually expect. That should allow you to replace your signature
	 * to accept an ORAData object that may be easier to mock. You can go even more advanced
	 * by looking at that ORAData object and extract what you _really_ need.
	 * <p>
	 * The second if is my actual scenario to prove that it works if you start the app. You
	 * shouldn't care in your own app obviously. The last part is to fallback on the default
	 * behaviour if the incoming message is not the expected type.
	 */
	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		if (message instanceof AdtMessage) {
			AdtMessage adtMessage = (AdtMessage) message;
			Object payload = adtMessage.getAdtPayload();
			if (!(payload instanceof ORAData)) {
				throw new MessageConversionException("Invalid payload for " + message + " - " + payload);
			}
			return payload;
		}
		if (message instanceof ObjectMessage) {
			Serializable object = ((ObjectMessage) message).getObject();
			return ((MyFakeAdtMessage) object).getAdtPayload();
		}
		return messageConverter.fromMessage(message);
	}

}
