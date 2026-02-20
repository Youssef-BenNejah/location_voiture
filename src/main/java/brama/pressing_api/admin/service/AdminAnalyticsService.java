package brama.pressing_api.admin.service;

import brama.pressing_api.admin.dto.response.AdminDashboardResponse;
import brama.pressing_api.admin.dto.response.AdminOverviewResponse;

public interface AdminAnalyticsService {
    AdminOverviewResponse getOverview();

    AdminDashboardResponse getDashboard(int windowDays);
}
