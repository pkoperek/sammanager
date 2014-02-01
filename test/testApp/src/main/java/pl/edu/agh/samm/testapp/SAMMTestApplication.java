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
import pl.edu.agh.samm.testapp.core.WorkloadGenerator;
import pl.edu.agh.samm.testapp.core.WorkloadGeneratorListener;

@SuppressWarnings("serial")
@Title("SAMM Test Application")
@Push
public class SAMMTestApplication extends UI {

    private static final String START_WORKLOAD = "Start workload";
    private static final String STOP_WORKLOAD = "Stop workload";

    private TextField expressionsPerMinuteTextField;
    private TextField slavesCountTextField;
    private TextArea logTextArea;

    private Layout createContentPanel() {
        Panel controlPanel = createControlPanel();
        Panel chartsPanel = createChartsPanel();

        VerticalLayout panels = new VerticalLayout(controlPanel, chartsPanel);
        panels.setMargin(true);
        return panels;
    }

    private Panel createChartsPanel() {
        return new Panel("Charts");
    }

    private Panel createControlPanel() {
        final VerticalLayout generationControlLayout = new VerticalLayout();

        generationControlLayout.addComponent(createGenerationControlButton());
        generationControlLayout.addComponent(
                wrapHorizontalLayout(
                        createExpressionsPerMinuteLabel(),
                        expressionsPerMinuteTextField = createExpressionsPerMinuteTextField()
                )
        );

        generationControlLayout.addComponent(logTextArea = createGenerationLogTextArea());
        generationControlLayout.addComponent(
                wrapHorizontalLayout(
                        new Label("Slaves count: "),
                        slavesCountTextField = createSlavesCountTextField(),
                        createAddSlaveButton(),
                        createRemoveSlaveButton()
                )
        );

        generationControlLayout.setMargin(true);

        return new Panel("Control workload generation", generationControlLayout);
    }

    private TextField createSlavesCountTextField() {
        TextField slavesCount = new TextField();
        slavesCount.setValue("1");
        slavesCount.setEnabled(false);
        slavesCount.setColumns(3);
        return slavesCount;
    }

    private Label createExpressionsPerMinuteLabel() {
        return new Label("Number of expressions generated per minute (negative value - as many as possible)");
    }

    private TextField createExpressionsPerMinuteTextField() {
        TextField textField = new TextField();
        textField.setValue("60");
        return textField;
    }

    private Component wrapHorizontalLayout(Component... componentsToAdd) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        for (Component component : componentsToAdd) {
            horizontalLayout.addComponent(component);
        }

        return horizontalLayout;
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
                        workloadGenerator.startGenerating(Long.parseLong(expressionsPerMinuteTextField.getValue()));
                        publishMessage("Started expressions generation");
                        generationControl.setCaption(STOP_WORKLOAD);
                    } else {
                        publishMessage("Stopping expressions generation...");
                        workloadGenerator.stopGenerating();
                        publishMessage("Stopped expressions generation");
                        generationControl.setCaption(START_WORKLOAD);
                    }
                } catch (InterruptedException e) {
                    publishMessage("Error! " + e.getMessage());
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
                if (!logTextArea.getValue().trim().isEmpty()) {
                    logTextArea.setValue(logTextArea.getValue() + '\n' + message);
                } else {
                    logTextArea.setValue(message);
                }
            }
        });
    }

    private void publishSlavesCount(final int slavesCount) {
        access(new Runnable() {
            @Override
            public void run() {
                slavesCountTextField.setValue(Integer.toString(slavesCount));
            }
        });
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        registerWorkloadGeneratorListener();
        setContent(createContentPanel());
    }

    private void registerWorkloadGeneratorListener() {
        WorkloadGenerator.getInstance().addWorkloadGeneratorListener(new UIUpdatingWorkloadGeneratorListener());
    }

    private class UIUpdatingWorkloadGeneratorListener implements WorkloadGeneratorListener {

        @Override
        public void handleSlavesCountChangedEvent(int count) {
            publishSlavesCount(count);
        }
    }
}
