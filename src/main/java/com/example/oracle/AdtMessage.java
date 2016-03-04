package com.example.oracle;

import javax.jms.Message;

/**
 *
 * @author Stephane Nicoll
 */
public interface AdtMessage extends Message {

	Object getAdtPayload();

}
