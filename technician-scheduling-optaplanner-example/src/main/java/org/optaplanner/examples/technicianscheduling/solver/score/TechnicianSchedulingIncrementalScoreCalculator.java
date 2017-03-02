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

package org.optaplanner.examples.technicianscheduling.solver.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.technicianscheduling.domain.Standstill;
import org.optaplanner.examples.technicianscheduling.domain.Task;
import org.optaplanner.examples.technicianscheduling.domain.Technician;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;
import org.optaplanner.examples.technicianscheduling.domain.timewindowed.TimeWindowedTask;
import org.optaplanner.examples.technicianscheduling.domain.timewindowed.TimeWindowedTechnicianSchedulingSolution;

public class TechnicianSchedulingIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<TechnicianSchedulingSolution> {

    private boolean timeWindowed;
    private Map<Technician, Integer> vehicleDemandMap;

    private long hardScore;
    private long softScore;

    @Override
    public void resetWorkingSolution(TechnicianSchedulingSolution solution) {
        timeWindowed = solution instanceof TimeWindowedTechnicianSchedulingSolution;
        List<Technician> vehicleList = solution.getTechnicianList();
        vehicleDemandMap = new HashMap<Technician, Integer>(vehicleList.size());
        for (Technician vehicle : vehicleList) {
            vehicleDemandMap.put(vehicle, 0);
        }
        hardScore = 0L;
        softScore = 0L;
        for (Task customer : solution.getTaskList()) {
            insertPreviousStandstill(customer);
            insertVehicle(customer);
            // Do not do insertNextCustomer(customer) to avoid counting distanceFromLastCustomerToDepot twice
            if (timeWindowed) {
                insertArrivalTime((TimeWindowedTask) customer);
            }
        }
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(Object entity) {
        if (entity instanceof Technician) {
            return;
        }
        insertPreviousStandstill((Task) entity);
        insertVehicle((Task) entity);
        // Do not do insertNextCustomer(customer) to avoid counting distanceFromLastCustomerToDepot twice
        if (timeWindowed) {
            insertArrivalTime((TimeWindowedTask) entity);
        }
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        if (entity instanceof Technician) {
            return;
        }
        if (variableName.equals("previousStandstill")) {
            retractPreviousStandstill((Task) entity);
        } else if (variableName.equals("vehicle")) {
            retractVehicle((Task) entity);
        } else if (variableName.equals("nextCustomer")) {
            retractNextCustomer((Task) entity);
        } else if (variableName.equals("arrivalTime")) {
            retractArrivalTime((TimeWindowedTask) entity);
        } else {
            throw new IllegalArgumentException("Unsupported variableName (" + variableName + ").");
        }
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        if (entity instanceof Technician) {
            return;
        }
        if (variableName.equals("previousStandstill")) {
            insertPreviousStandstill((Task) entity);
        } else if (variableName.equals("vehicle")) {
            insertVehicle((Task) entity);
        } else if (variableName.equals("nextCustomer")) {
            insertNextCustomer((Task) entity);
        } else if (variableName.equals("arrivalTime")) {
            insertArrivalTime((TimeWindowedTask) entity);
        } else {
            throw new IllegalArgumentException("Unsupported variableName (" + variableName + ").");
        }
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        if (entity instanceof Technician) {
            return;
        }
        retractPreviousStandstill((Task) entity);
        retractVehicle((Task) entity);
        // Do not do retractNextCustomer(customer) to avoid counting distanceFromLastCustomerToDepot twice
        if (timeWindowed) {
            retractArrivalTime((TimeWindowedTask) entity);
        }
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insertPreviousStandstill(Task customer) {
        Standstill previousStandstill = customer.getPreviousStandstill();
        if (previousStandstill != null) {
            // Score constraint distanceToPreviousStandstill
            softScore -= customer.getDistanceFromPreviousStandstill();
        }
    }

    private void retractPreviousStandstill(Task customer) {
        Standstill previousStandstill = customer.getPreviousStandstill();
        if (previousStandstill != null) {
            // Score constraint distanceToPreviousStandstill
            softScore += customer.getDistanceFromPreviousStandstill();
        }
    }

    private void insertVehicle(Task customer) {
        Technician vehicle = customer.getTechnician();
        if (vehicle != null) {
            // Score constraint vehicleCapacity
            if (customer.getNextTask() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore -= customer.getLocation().getDistanceTo(vehicle.getLocation());
            }
        }
    }

    private void retractVehicle(Task customer) {
        Technician vehicle = customer.getTechnician();
        if (vehicle != null) {
            // Score constraint vehicleCapacity
            if (customer.getNextTask() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore += customer.getLocation().getDistanceTo(vehicle.getLocation());
            }
        }
    }

    private void insertNextCustomer(Task customer) {
        Technician vehicle = customer.getTechnician();
        if (vehicle != null) {
            if (customer.getNextTask() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore -= customer.getLocation().getDistanceTo(vehicle.getLocation());
            }
        }
    }

    private void retractNextCustomer(Task customer) {
        Technician vehicle = customer.getTechnician();
        if (vehicle != null) {
            if (customer.getNextTask() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore += customer.getLocation().getDistanceTo(vehicle.getLocation());
            }
        }
    }

    private void insertArrivalTime(TimeWindowedTask customer) {
        Long arrivalTime = customer.getArrivalTime();
        if (arrivalTime != null) {
            long dueTime = customer.getDueTime();
            if (dueTime < arrivalTime) {
                // Score constraint arrivalAfterDueTime
                hardScore -= (arrivalTime - dueTime);
            }
        }
        // Score constraint arrivalAfterDueTimeAtDepot is a build-in hard constraint in VehicleRoutingImporter
    }

    private void retractArrivalTime(TimeWindowedTask customer) {
        Long arrivalTime = customer.getArrivalTime();
        if (arrivalTime != null) {
            long dueTime = customer.getDueTime();
            if (dueTime < arrivalTime) {
                // Score constraint arrivalAfterDueTime
                hardScore += (arrivalTime - dueTime);
            }
        }
    }

    @Override
    public HardSoftLongScore calculateScore() {
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
