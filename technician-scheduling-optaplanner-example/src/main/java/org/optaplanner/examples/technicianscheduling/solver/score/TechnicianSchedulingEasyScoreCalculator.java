/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.technicianscheduling.solver.score;

import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.examples.technicianscheduling.domain.Standstill;
import org.optaplanner.examples.technicianscheduling.domain.Task;
import org.optaplanner.examples.technicianscheduling.domain.Technician;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;
import org.optaplanner.examples.technicianscheduling.domain.timewindowed.TimeWindowedTask;
import org.optaplanner.examples.technicianscheduling.domain.timewindowed.TimeWindowedTechnicianSchedulingSolution;

public class TechnicianSchedulingEasyScoreCalculator implements EasyScoreCalculator<TechnicianSchedulingSolution> {

    @Override
    public HardSoftLongScore calculateScore(TechnicianSchedulingSolution solution) {
        boolean timeWindowed = solution instanceof TimeWindowedTechnicianSchedulingSolution;
        List<Task> taskList = solution.getTaskList();
        long hardScore = 0L;
        long softScore = 0L;
        for (Task task : taskList) {
            Standstill previousStandstill = task.getPreviousStandstill();
            if (previousStandstill != null) {
                Technician technician = task.getTechnician();
                // Score constraint distanceToPreviousStandstill
                softScore -= task.getDistanceFromPreviousStandstill();
                if (task.getNextTask() == null) {
                    // Score constraint distanceFromLastCustomerToDepot
                    softScore -= task.getLocation().getDistanceTo(technician.getLocation());
                }
                if (timeWindowed) {
                    TimeWindowedTask timeWindowedCustomer = (TimeWindowedTask) task;
                    long dueTime = timeWindowedCustomer.getDueTime();
                    Long arrivalTime = timeWindowedCustomer.getArrivalTime();
                    if (dueTime < arrivalTime) {
                        // Score constraint arrivalAfterDueTime
                        hardScore -= (arrivalTime - dueTime);
                    }
                }
            }
        }
        // Score constraint arrivalAfterDueTimeAtDepot is a build-in hard constraint in VehicleRoutingImporter
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
