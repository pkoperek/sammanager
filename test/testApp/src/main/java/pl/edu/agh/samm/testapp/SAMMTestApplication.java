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
import pl.edu.agh.samm.testapp.core.WorkloadGeneratorFacade;
import pl.edu.agh.samm.testapp.core.WorkloadGeneratorListener;
import pl.edu.agh.samm.testapp.flot.FlotChart;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
@Title("SAMM Test Application")
@Push
public class SAMMTestApplication extends UI {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final String START_WORKLOAD = "Start workload";
    private static final String STOP_WORKLOAD = "Stop workload";

    private TextField expressionsPerMinuteTextField;
    private TextField slavesCountTextField;
    private FlotChart slavesCountChart;
    private FlotChart queueLengthCountChart;
    private FlotChart processedExpressionsCountChart;
    private TextArea logTextArea;

    private Layout createContentPanel() {
        Panel controlPanel = createControlPanel();
        Panel chartsPanel = createChartsPanel();

        VerticalLayout panels = new VerticalLayout(controlPanel, chartsPanel);
        panels.setMargin(true);
        return panels;
    }

    private Panel createChartsPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(slavesCountChart = new FlotChart("Slaves count", "#0062FF"));
        verticalLayout.setComponentAlignment(slavesCountChart, Alignment.TOP_CENTER);
        verticalLayout.addComponent(queueLengthCountChart = new FlotChart("Queue length", "#D11739"));
        verticalLayout.setComponentAlignment(queueLengthCountChart, Alignment.TOP_CENTER);
        verticalLayout.addComponent(processedExpressionsCountChart = new FlotChart("Processed expressions", "#1ABD28"));
        verticalLayout.setComponentAlignment(processedExpressionsCountChart, Alignment.TOP_CENTER);
        return new Panel("Charts", verticalLayout);
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
                    WorkloadGeneratorFacade.getInstance().removeSlave();
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
                WorkloadGeneratorFacade.getInstance().addSlave();
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
                    WorkloadGeneratorFacade workloadGeneratorFacade = WorkloadGeneratorFacade.getInstance();
                    if (generationControl.getCaption().equals(START_WORKLOAD)) {
                        publishMessage("Starting expressions generation...");
                        workloadGeneratorFacade.startGenerating(Long.parseLong(expressionsPerMinuteTextField.getValue()));
                        publishMessage("Started expressions generation");
                        generationControl.setCaption(STOP_WORKLOAD);
                    } else {
                        publishMessage("Stopping expressions generation...");
                        workloadGeneratorFacade.stopGenerating();
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

    private void publishSlavesCountChart(final int dataPointIndex, final int slavesCount) {
        access(new Runnable() {
            @Override
            public void run() {
                slavesCountChart.addPoint(dataPointIndex, slavesCount);
            }
        });
    }

    private void publishExpressionsQueueLengthChart(final int dataPointIndex, final long queueLength) {
        access(new Runnable() {
            @Override
            public void run() {
                queueLengthCountChart.addPoint(dataPointIndex, queueLength);
            }
        });
    }

    private void publishProcessedExpressionsChart(final int dataPointIndex, final long processedExpressionsCount) {
        access(new Runnable() {
            @Override
            public void run() {
                processedExpressionsCountChart.addPoint(dataPointIndex, processedExpressionsCount);
            }
        });
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        registerWorkloadGeneratorListener();
        setContent(createContentPanel());

        startMonitoringTasks();
    }

    private void startMonitoringTasks() {
        executorService.scheduleAtFixedRate(new ChartUpdatingTask(WorkloadGeneratorFacade.getInstance()), 0l, 5l, TimeUnit.SECONDS);
    }

    private void registerWorkloadGeneratorListener() {
        WorkloadGeneratorFacade.getInstance().addWorkloadGeneratorListener(new UIUpdatingWorkloadGeneratorListener());
    }

    private class UIUpdatingWorkloadGeneratorListener implements WorkloadGeneratorListener {

        @Override
        public void handleSlavesCountChangedEvent(int count) {
            publishSlavesCount(count);
        }
    }

    private class ChartUpdatingTask implements Runnable {
        private final WorkloadGeneratorFacade workloadGenerator;
        private int dataPoint = 0;

        public ChartUpdatingTask(WorkloadGeneratorFacade workloadGenerator) {
            this.workloadGenerator = workloadGenerator;
        }

        @Override
        public void run() {
//            publishMessage("Logging: " + System.currentTimeMillis());
            publishSlavesCountChart(dataPoint++, workloadGenerator.getSlavesCount());
            publishExpressionsQueueLengthChart(dataPoint++, workloadGenerator.getExpressionsQueueLength());
            publishProcessedExpressionsChart(dataPoint++, workloadGenerator.getProcessedExpressionsCount());
        }

    }
}
