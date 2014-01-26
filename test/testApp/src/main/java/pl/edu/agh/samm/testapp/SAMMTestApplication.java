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

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import pl.edu.agh.samm.testapp.core.ExpressionGenerator;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class SAMMTestApplication extends Application {

    private static final String START_WORKLOAD = "Start workload";
    private static final String STOP_WORKLOAD = "Stop workload";
    private static final int MAX_LVL = 10;

    private Window mainWindow;

    private Thread expressionGeneratorThread;
    private ExpressionGenerator expressionGenerator;
    private TextArea generationLogTextArea;

    @Override
    public void init() {
        expressionGenerator = new ExpressionGenerator(MAX_LVL);
        expressionGeneratorThread = new Thread(expressionGenerator);
        expressionGeneratorThread.start();

        mainWindow = new Window("SAMM Test Application");

        mainWindow.addComponent(createGenerationControlPanel());

        setMainWindow(mainWindow);
    }

    private Panel createGenerationControlPanel() {
        final Panel generationControlPanel = new Panel("Data generation control:");

        Button generationControlButton = createGenerationControlButton();
        generationLogTextArea = createGenerationLogTextArea();
        generationControlPanel.addComponent(generationControlButton);
        generationControlPanel.addComponent(generationLogTextArea);

        return generationControlPanel;
    }

    private void stopGenerating() {
        expressionGenerator.stopGeneration();
    }

    private void startGenerating() {
        expressionGenerator.startGeneration();
    }

    private TextArea createGenerationLogTextArea() {
        TextArea log = new TextArea();
        log.setWidth("100%");
        log.setHeight("100%");
        return log;
    }

    private Button createGenerationControlButton() {
        final Button generationControl = new Button(START_WORKLOAD);
        generationControl.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (generationControl.getCaption().equals(START_WORKLOAD)) {
                    startGenerating();
                    generationControl.setCaption(STOP_WORKLOAD);
                } else {
                    stopGenerating();
                    generationControl.setCaption(START_WORKLOAD);
                }
            }
        });
        return generationControl;
    }
}
