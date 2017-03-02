/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.technicianscheduling.persistence.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.technicianscheduling.app.TechnicianSchedulingApp;
import org.optaplanner.examples.technicianscheduling.domain.Standstill;
import org.optaplanner.examples.technicianscheduling.domain.Task;
import org.optaplanner.examples.technicianscheduling.domain.Technician;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;
import org.optaplanner.examples.technicianscheduling.persistence.TechnicianSchedulingDao;

public class TechnicianSchedulingDistanceTypeComparison extends LoggingMain {

    private final ScoreDirectorFactory scoreDirectorFactory;

    public static void main(String[] args) {
        new TechnicianSchedulingDistanceTypeComparison().compare(
                "solved/tmp-p-belgium-n50-k10.xml",
                "solved/tmp-p-belgium-road-km-n50-k10.xml",
                "solved/tmp-p-belgium-road-time-n50-k10.xml");
    }

    protected final TechnicianSchedulingDao vehicleRoutingDao;

    public TechnicianSchedulingDistanceTypeComparison() {
        vehicleRoutingDao = new TechnicianSchedulingDao();
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(TechnicianSchedulingApp.SOLVER_CONFIG);
        scoreDirectorFactory = solverFactory.buildSolver().getScoreDirectorFactory();
    }

    public void compare(String... filePaths) {
        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            File file = new File(vehicleRoutingDao.getDataDir(), filePaths[i]);
            if (!file.exists()) {
                throw new IllegalArgumentException("The file (" + file + ") does not exist.");
            }
            files[i] = file;
        }
        for (File varFile : files) {
            logger.info("  Results for {}:", varFile.getName());
            // Intentionally create a new instance instead of reusing the older one.
            TechnicianSchedulingSolution variablesSolution = (TechnicianSchedulingSolution) vehicleRoutingDao.readSolution(varFile);
            for (File inputFile : files) {
                HardSoftLongScore score;
                if (inputFile == varFile) {
                    score = variablesSolution.getScore();
                } else {
                    TechnicianSchedulingSolution inputSolution = (TechnicianSchedulingSolution) vehicleRoutingDao.readSolution(inputFile);
                    applyVariables(inputSolution, variablesSolution);
                    score = inputSolution.getScore();
                }
                logger.info("    {} (according to {})", score.getSoftScore(), inputFile.getName());
            }
        }
    }

    private void applyVariables(TechnicianSchedulingSolution inputSolution, TechnicianSchedulingSolution varSolution) {
        List<Technician> inputVehicleList = inputSolution.getTechnicianList();
        Map<Long, Technician> inputVehicleMap = new LinkedHashMap<Long, Technician>(inputVehicleList.size());
        for (Technician vehicle : inputVehicleList) {
            inputVehicleMap.put(vehicle.getId(), vehicle);
        }
        List<Task> inputCustomerList = inputSolution.getTaskList();
        Map<Long, Task> inputCustomerMap = new LinkedHashMap<Long, Task>(inputCustomerList.size());
        for (Task customer : inputCustomerList) {
            inputCustomerMap.put(customer.getId(), customer);
        }

        for (Technician varVehicle : varSolution.getTechnicianList()) {
            Technician inputVehicle = inputVehicleMap.get(varVehicle.getId());
            Task varNext = varVehicle.getNextTask();
            inputVehicle.setNextTask(varNext == null ? null : inputCustomerMap.get(varNext.getId()));
        }
        for (Task varCustomer : varSolution.getTaskList()) {
            Task inputCustomer = inputCustomerMap.get(varCustomer.getId());
            Standstill varPrevious = varCustomer.getPreviousStandstill();
            inputCustomer.setPreviousStandstill(varPrevious == null ? null :
                    varPrevious instanceof Technician ? inputVehicleMap.get(((Technician) varPrevious).getId())
                    : inputCustomerMap.get(((Task) varPrevious).getId()));
            Task varNext = varCustomer.getNextTask();
            inputCustomer.setNextTask(varNext == null ? null : inputCustomerMap.get(varNext.getId()));
        }
        ScoreDirector scoreDirector = scoreDirectorFactory.buildScoreDirector();
        scoreDirector.setWorkingSolution(inputSolution);
        scoreDirector.calculateScore();
    }

}
