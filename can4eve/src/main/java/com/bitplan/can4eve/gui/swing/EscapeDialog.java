/**
 *
 * This file is part of the https://github.com/BITPlan/can4eve open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.can4eve.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * a Dialog which reacts on an escape key
 * @author wf
 *
 */
public class EscapeDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    
    /**
     * create me from the given frame, title and modal setting
     * @param frame
     * @param title
     * @param modal
     */
    public EscapeDialog(JFrame frame, String title, boolean modal) {
        super(frame, title, modal);
    }
    

    /**
     * create me from the given frame
     * @param frame
     * @param modal
     */
    public EscapeDialog(JFrame frame, boolean modal) {
        super(frame, modal);
    }


    /**
     * create a root Pane that will handle the esc key
     */
    protected JRootPane createRootPane() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                dispose();
            }
        };
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

}

