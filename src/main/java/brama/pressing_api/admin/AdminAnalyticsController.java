package brama.pressing_api.admin;

import brama.pressing_api.admin.dto.response.AdminDashboardResponse;
import brama.pressing_api.admin.dto.response.AdminOverviewResponse;
import brama.pressing_api.admin.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin analytics endpoints for dashboard metrics.
 */
@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "Admin dashboard analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {
    private final AdminAnalyticsService adminAnalyticsService;

    /**
     * Returns an overview of key platform metrics.
     */
    @GetMapping("/overview")
    public AdminOverviewResponse getOverview() {
        return adminAnalyticsService.getOverview();
    }

    /**
     * Returns a decision-focused dashboard in one payload.
     */
    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard(@RequestParam(defaultValue = "30") int windowDays) {
        return adminAnalyticsService.getDashboard(windowDays);
    }
}
