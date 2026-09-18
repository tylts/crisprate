package com.crisprate.crisprate.service;

import com.crisprate.crisprate.dto.ReportRow;
import com.crisprate.crisprate.entity.Establishment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final EstablishmentService establishmentService;

    public ReportService(EstablishmentService establishmentService) {
        this.establishmentService = establishmentService;
    }

    public List<ReportRow> generateCrispinessReport(String cityFilter) {
        List<Establishment> establishments;

        if (cityFilter != null && !cityFilter.isBlank()) {
            establishments = establishmentService.searchByCityUnpaged(cityFilter);
        } else {
            establishments = establishmentService.getAllUnpaged();
        }

        List<ReportRow> rows = new ArrayList<>();
        for (Establishment e : establishments) {
            double score = establishmentService.getScoreFor(e);
            int count = e.getRatings().size();
            rows.add(new ReportRow(e.getName(), e.getCity(), score, count));
        }

        return rows;
    }
}
