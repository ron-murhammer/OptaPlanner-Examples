/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JTabbedPane;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.optaplanner.examples.technicianscheduling.domain.Depot;
import org.optaplanner.examples.technicianscheduling.domain.Task;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;
import org.optaplanner.examples.technicianscheduling.domain.location.AirLocation;
import org.optaplanner.examples.technicianscheduling.domain.location.Location;

public class TechnicianSchedulingPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/technicianscheduling/swingui/vehicleRoutingLogo.png";

    private TechnicianSchedulingWorldPanel vehicleRoutingWorldPanel;

    private Random demandRandom = new Random(37);
    private Long nextLocationId = null;

    public TechnicianSchedulingPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        vehicleRoutingWorldPanel = new TechnicianSchedulingWorldPanel(this);
        vehicleRoutingWorldPanel.setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
        tabbedPane.add("World", vehicleRoutingWorldPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public TechnicianSchedulingSolution getVehicleRoutingSolution() {
        return (TechnicianSchedulingSolution) solutionBusiness.getSolution();
    }

    @Override
    public void resetPanel(Solution solutionObject) {
        TechnicianSchedulingSolution solution = (TechnicianSchedulingSolution) solutionObject;
        vehicleRoutingWorldPanel.resetPanel(solution);
        resetNextLocationId();
    }

    private void resetNextLocationId() {
        long highestLocationId = 0L;
        for (Location location : getVehicleRoutingSolution().getLocationList()) {
            if (highestLocationId < location.getId().longValue()) {
                highestLocationId = location.getId();
            }
        }
        nextLocationId = highestLocationId + 1L;
    }

    @Override
    public void updatePanel(Solution solutionObject) {
        TechnicianSchedulingSolution solution = (TechnicianSchedulingSolution) solutionObject;
        vehicleRoutingWorldPanel.updatePanel(solution);
    }

    public SolverAndPersistenceFrame getWorkflowFrame() {
        return solverAndPersistenceFrame;
    }

    public void insertLocationAndCustomer(double longitude, double latitude) {
        final Location newLocation;
        switch (getVehicleRoutingSolution().getDistanceType()) {
            case AIR_DISTANCE:
                newLocation = new AirLocation();
                break;
            case ROAD_DISTANCE:
                logger.warn("Adding locations for a road distance dataset is not supported.");
                return;
            case SEGMENTED_ROAD_DISTANCE:
                logger.warn("Adding locations for a segmented road distance dataset is not supported.");
                return;
            default:
                throw new IllegalStateException("The distanceType (" + getVehicleRoutingSolution().getDistanceType() + ") is not implemented.");
        }
        newLocation.setId(nextLocationId);
        nextLocationId++;
        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        doProblemFactChange(new ProblemFactChange() {

            @Override
            public void doChange(ScoreDirector scoreDirector) {
                TechnicianSchedulingSolution solution = (TechnicianSchedulingSolution) scoreDirector.getWorkingSolution();
                scoreDirector.beforeProblemFactAdded(newLocation);
                solution.getLocationList().add(newLocation);
                scoreDirector.afterProblemFactAdded(newLocation);
                Task newCustomer = createCustomer(solution, newLocation);
                scoreDirector.beforeEntityAdded(newCustomer);
                solution.getTaskList().add(newCustomer);
                scoreDirector.afterEntityAdded(newCustomer);
                scoreDirector.triggerVariableListeners();
            }
        });
    }

    protected Task createCustomer(TechnicianSchedulingSolution solution, Location newLocation) {
        Task newCustomer;
        if (solution instanceof TechnicianSchedulingSolution) {
            Task newTimeWindowedCustomer = new Task();
            Depot timeWindowedDepot = solution.getDepotList().get(0);
            long windowTime = (timeWindowedDepot.getDueTime() - timeWindowedDepot.getReadyTime()) / 4L;
            long readyTime = RandomUtils.nextLong(demandRandom, windowTime * 3L);
            newTimeWindowedCustomer.setReadyTime(readyTime);
            newTimeWindowedCustomer.setDueTime(readyTime + windowTime);
            newTimeWindowedCustomer.setServiceDuration(Math.min(60000L, windowTime / 2L));
            newCustomer = newTimeWindowedCustomer;
        } else {
            newCustomer = new Task();
        }
        newCustomer.setId(newLocation.getId());
        newCustomer.setLocation(newLocation);
        return newCustomer;
    }

}
