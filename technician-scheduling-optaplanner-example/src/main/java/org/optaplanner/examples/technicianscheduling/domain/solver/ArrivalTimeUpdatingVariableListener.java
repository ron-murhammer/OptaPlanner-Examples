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

package org.optaplanner.examples.technicianscheduling.domain.solver;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.technicianscheduling.domain.Standstill;
import org.optaplanner.examples.technicianscheduling.domain.Task;

// TODO When this class is added only for TimeWindowedCustomer, use TimeWindowedCustomer instead of Customer
public class ArrivalTimeUpdatingVariableListener implements VariableListener<Task> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Task customer) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Task customer) {
            updateArrivalTime(scoreDirector, customer);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Task customer) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Task customer) {
            updateArrivalTime(scoreDirector, customer);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Task customer) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Task customer) {
        // Do nothing
    }

    protected void updateArrivalTime(ScoreDirector scoreDirector, Task sourceCustomer) {
        Standstill previousStandstill = sourceCustomer.getPreviousStandstill();
        Long departureTime = (previousStandstill instanceof Task) ? ((Task) previousStandstill).getDepartureTime() : null;
        Task shadowCustomer = sourceCustomer;
        Long arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        while (shadowCustomer != null && ObjectUtils.notEqual(shadowCustomer.getArrivalTime(), arrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "arrivalTime");
            shadowCustomer.setArrivalTime(arrivalTime);
            scoreDirector.afterVariableChanged(shadowCustomer, "arrivalTime");
            departureTime = shadowCustomer.getDepartureTime();
            shadowCustomer = shadowCustomer.getNextTask();
            arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        }
    }

    private Long calculateArrivalTime(Task customer, Long previousDepartureTime) {
        if (customer == null || customer.getPreviousStandstill() == null) {
            return null;
        }
        if (previousDepartureTime == null) {
            // PreviousStandstill is the Vehicle, so we leave from the Depot at the best suitable time
            return Math.max(customer.getReadyTime(), customer.getDistanceFromPreviousStandstill());
        }
        return previousDepartureTime + customer.getDistanceFromPreviousStandstill();
    }

}
