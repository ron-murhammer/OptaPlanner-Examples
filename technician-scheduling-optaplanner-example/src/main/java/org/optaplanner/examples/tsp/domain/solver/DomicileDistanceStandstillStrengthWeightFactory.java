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

package org.optaplanner.examples.tsp.domain.solver;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;

public class DomicileDistanceStandstillStrengthWeightFactory implements SelectionSorterWeightFactory<TravelingSalesmanTour, Standstill> {

    public Comparable createSorterWeight(TravelingSalesmanTour tour, Standstill standstill) {
        Domicile domicile = tour.getDomicile();
        long domicileRoundTripDistance = domicile.getDistanceTo(standstill) + standstill.getDistanceTo(domicile);
        return new DomicileDistanceVisitDifficultyWeight(standstill, domicileRoundTripDistance);
    }

    public static class DomicileDistanceVisitDifficultyWeight implements Comparable<DomicileDistanceVisitDifficultyWeight> {

        private final Standstill standstill;
        private final long domicileRoundTripDistance;

        public DomicileDistanceVisitDifficultyWeight(Standstill standstill, long domicileRoundTripDistance) {
            this.standstill = standstill;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        public int compareTo(DomicileDistanceVisitDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(other.domicileRoundTripDistance, domicileRoundTripDistance) // Decreasing: closer to depot is stronger
                    .append(standstill.getLocation().getLatitude(), other.standstill.getLocation().getLatitude())
                    .append(standstill.getLocation().getLongitude(), other.standstill.getLocation().getLongitude())
                    .toComparison();
        }

    }

}
