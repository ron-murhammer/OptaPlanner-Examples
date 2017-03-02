/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.technicianscheduling.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.custom.AbstractCustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.technicianscheduling.domain.Standstill;
import org.optaplanner.examples.technicianscheduling.domain.Task;
import org.optaplanner.examples.technicianscheduling.domain.Technician;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO PLANNER-380 Delete this class. Temporary implementation until BUOY_FIT is implemented as a Construction Heuristic
public class BuoyTechnicianSchedulingSolutionInitializer extends AbstractCustomPhaseCommand {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void changeWorkingSolution(ScoreDirector scoreDirector) {
        TechnicianSchedulingSolution solution = (TechnicianSchedulingSolution) scoreDirector.getWorkingSolution();
        List<Technician> vehicleList = solution.getTechnicianList();
        List<Task> customerList = solution.getTaskList();
        List<Standstill> standstillList = new ArrayList<Standstill>(vehicleList.size() + customerList.size());
        standstillList.addAll(vehicleList);
        standstillList.addAll(customerList);
        logger.info("Starting sorting");
        Map<Standstill, Task[]> nearbyMap = new HashMap<Standstill, Task[]>(standstillList.size());
        for (final Standstill origin : standstillList) {
            Task[] nearbyCustomers = customerList.toArray(new Task[0]);
            Arrays.sort(nearbyCustomers, new Comparator<Standstill>() {

                @Override
                public int compare(Standstill a, Standstill b) {
                    double aDistance = origin.getLocation().getDistanceTo(a.getLocation());
                    double bDistance = origin.getLocation().getDistanceTo(b.getLocation());
                    return Double.compare(aDistance, bDistance);
                }
            });
            nearbyMap.put(origin, nearbyCustomers);
        }
        logger.info("Done sorting");

        List<Standstill> buoyList = new ArrayList<Standstill>(vehicleList);

        int NEARBY_LIMIT = 40;
        while (true) {
            Score stepScore = null;
            int stepBuoyIndex = -1;
            Task stepEntity = null;
            for (int i = 0; i < buoyList.size(); i++) {
                Standstill buoy = buoyList.get(i);

                Task[] nearbyCustomers = nearbyMap.get(buoy);
                int j = 0;
                for (Task customer : nearbyCustomers) {
                    if (customer.getPreviousStandstill() != null) {
                        continue;
                    }
                    scoreDirector.beforeVariableChanged(customer, "previousStandstill");
                    customer.setPreviousStandstill(buoy);
                    scoreDirector.afterVariableChanged(customer, "previousStandstill");
                    scoreDirector.triggerVariableListeners();
                    Score score = scoreDirector.calculateScore();
                    scoreDirector.beforeVariableChanged(customer, "previousStandstill");
                    customer.setPreviousStandstill(null);
                    scoreDirector.afterVariableChanged(customer, "previousStandstill");
                    scoreDirector.triggerVariableListeners();
                    if (stepScore == null || score.compareTo(stepScore) > 0) {
                        stepScore = score;
                        stepBuoyIndex = i;
                        stepEntity = customer;
                    }
                    if (j >= NEARBY_LIMIT) {
                        break;
                    }
                    j++;
                }
            }
            if (stepEntity == null) {
                break;
            }
            Standstill stepValue = buoyList.set(stepBuoyIndex, stepEntity);
            scoreDirector.beforeVariableChanged(stepEntity, "previousStandstill");
            stepEntity.setPreviousStandstill(stepValue);
            scoreDirector.afterVariableChanged(stepEntity, "previousStandstill");
            scoreDirector.triggerVariableListeners();
            logger.debug("    Score ({}), assigned customer ({}) to stepValue ({}).", stepScore, stepEntity, stepValue);
        }
    }

}
