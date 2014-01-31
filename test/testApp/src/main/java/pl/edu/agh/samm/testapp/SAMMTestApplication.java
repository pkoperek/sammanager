/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package pl.edu.agh.samm.testapp;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
@Title("SAMM Test Application")
@Push
public class SAMMTestApplication extends UI {

    private static final String START_WORKLOAD = "Start workload";
    private static final String STOP_WORKLOAD = "Stop workload";

    private TextArea generationLogTextArea;

    private Panel createGenerationControlPanel() {
        final VerticalLayout generationControlLayout = new VerticalLayout();
        generationControlLayout.setCaption("Control workload generation");

        Button generationControlButton = createGenerationControlButton();
        generationLogTextArea = createGenerationLogTextArea();
        generationControlLayout.addComponent(generationControlButton);
        generationControlLayout.addComponent(generationLogTextArea);
        generationControlLayout.addComponent(
                wrapHorizontalLayout(
                        createAddSlaveButton(),
                        createRemoveSlaveButton()
                ));
        generationControlLayout.setMargin(true);

        return new Panel(generationControlLayout);
    }

    private Component wrapHorizontalLayout(Button addSlaveButton, Button removeSlaveButton) {
        return new HorizontalLayout(addSlaveButton, removeSlaveButton);
    }

    private Button createRemoveSlaveButton() {
        final Button removeSlaveButton = new Button("Remove slave");

        removeSlaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    WorkloadGenerator.getInstance().removeSlave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return removeSlaveButton;
    }

    private Button createAddSlaveButton() {
        final Button addSlaveButton = new Button("Add slave");

        addSlaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                WorkloadGenerator.getInstance().addSlave();
            }
        });

        return addSlaveButton;
    }

    private TextArea createGenerationLogTextArea() {
        TextArea log = new TextArea();
        log.setWidth("100%");
        log.setHeight("100%");
        return log;
    }

    private Button createGenerationControlButton() {
        final Button generationControl = new Button(START_WORKLOAD);
        generationControl.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    WorkloadGenerator workloadGenerator = WorkloadGenerator.getInstance();
                    if (generationControl.getCaption().equals(START_WORKLOAD)) {
                        publishMessage("Starting expressions generation...");
                        workloadGenerator.startGenerating();
                        publishMessage("Started expressions generation");
                        generationControl.setCaption(STOP_WORKLOAD);
                    } else {
                        publishMessage("Stopping expressions generation...");
                        workloadGenerator.stopGenerating();
                        publishMessage("Stopped expressions generation");
                        generationControl.setCaption(START_WORKLOAD);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return generationControl;
    }

    private void publishMessage(final String message) {
        access(new Runnable() {
            @Override
            public void run() {
                if (!generationLogTextArea.getValue().trim().isEmpty()) {
                    generationLogTextArea.setValue(generationLogTextArea.getValue() + '\n' + message);
                } else {
                    generationLogTextArea.setValue(message);
                }
            }
        });
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(createGenerationControlPanel());
    }
}
