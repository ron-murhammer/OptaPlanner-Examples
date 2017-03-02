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

package org.optaplanner.examples.technicianscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.technicianscheduling.domain.TechnicianSchedulingSolution;
import org.optaplanner.examples.technicianscheduling.persistence.TechnicianSchedulingDao;
import org.optaplanner.examples.technicianscheduling.persistence.TechnicianSchedulingImporter;
import org.optaplanner.examples.technicianscheduling.swingui.TechnicianSchedulingPanel;

public class TechnicianSchedulingApp extends CommonApp<TechnicianSchedulingSolution> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/technicianscheduling/solver/technicianSchedulingSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TechnicianSchedulingApp().init();
    }

    public TechnicianSchedulingApp() {
        super("Technician Scheduling",
                "Schedule technicians optimally across tasks that have locations and time windows",
                SOLVER_CONFIG,
                TechnicianSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new TechnicianSchedulingPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TechnicianSchedulingDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new TechnicianSchedulingImporter()
        };
    }

}
