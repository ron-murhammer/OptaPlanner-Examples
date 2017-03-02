/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.technicianscheduling.swingui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.technicianscheduling.domain.Task;
import org.optaplanner.examples.technicianscheduling.domain.Technician;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;

public class TechnicianSchedulingListPanel extends JPanel implements Scrollable {

    public static final Dimension PREFERRED_SCROLLABLE_VIEWPORT_SIZE = new Dimension(800, 600);

    public TechnicianSchedulingListPanel() {
        setLayout(new GridLayout(0, 1));
    }

    public void resetPanel(TechnicianSchedulingSolution solution) {
        removeAll();
        for (Technician technician : solution.getTechnicianList()) {
            addTechnicianLine(technician);
            Task task = technician.getNextTask();
            while (task != null) {
                addTaskLine(task);
                task = task.getNextTask();
            }
        }
    }

    protected void addTechnicianLine(Technician technician) {
        JLabel technicianLabel = new JLabel(technician.toString() 
                + " | " + technician.getDepot().toString() 
                + " | " + technician.getDepot().getLocation().toString());
        add(new JLabel(""));
        add(technicianLabel);
    }
    
    protected void addTaskLine(Task task) {
        JLabel locationLabel = new JLabel(task.getLocation().toString() 
                + " | travel-time=" + displayTime(task.getDistanceFromPreviousStandstill())
                + " | scheduled=" + displayTime(task.getArrivalTime()) + "-" + displayTime(task.getDepartureTime())
                + " | window=" + displayTime(task.getReadyTime()) + "-" + displayTime(task.getDueTime())
                + " | duration=" + displayTime(task.getServiceDuration()));
        add(locationLabel);
    }
    
    private String displayTime(Long time) {
        long totalMinutes = time / 1000;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        String hoursString = String.format("%02d", hours);
        String minutesString = String.format("%02d", minutes);
        return hoursString + ":" + minutesString;
    }

    public void updatePanel(TechnicianSchedulingSolution travelingSalesmanTour) {
        resetPanel(travelingSalesmanTour);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

}
