package com.company.jmixpm.app;

import org.springframework.context.ApplicationEvent;

public class TaskChangedEvent extends ApplicationEvent {

    public TaskChangedEvent(Object source) {
        super(source);
    }
}
