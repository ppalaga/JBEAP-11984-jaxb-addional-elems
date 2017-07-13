package org.wildfly.issues.jbeap11984;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class EventHandlerInjector extends AbstractPhaseInterceptor<Message> {
    public EventHandlerInjector() {
        super(Phase.PRE_UNMARSHAL);
    }

    public void handleMessage(Message message) throws Fault {
        message.put("jaxb-validation-event-handler", new IgnoringUnexpectedElementsEventHandler());
    }
}