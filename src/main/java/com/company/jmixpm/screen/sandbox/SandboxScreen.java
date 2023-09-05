package com.company.jmixpm.screen.sandbox;

import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("SandboxScreen")
@UiDescriptor("sandbox-screen.xml")
public class SandboxScreen extends Screen {
    @Autowired
    private JavaScriptComponent quill;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onInit(final InitEvent event) {
        QuillState state = new QuillState();
        state.theme = "snow";
        state.placeholder = "Compose an epic...";

        quill.setState(state);

        quill.addFunction("valueChange", javaScriptCallbackEvent -> {
            String value = javaScriptCallbackEvent.getArguments().getString(0);
            notifications.create()
                    .withCaption("Value: " + value)
                    .withType(Notifications.NotificationType.TRAY)
                    .show();
        });
    }

    @Subscribe("deleteTextBtn")
    public void onDeleteTextBtnClick(final Button.ClickEvent event) {
        quill.callFunction("deleteText");
    }

    class QuillState {
        public String theme;
        public String placeholder;
    }
}