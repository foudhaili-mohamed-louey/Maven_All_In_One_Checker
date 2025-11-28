package services.intelligence.reporting;

import services.intelligence.models.EmailIntelligenceProfile;
import services.intelligence.models.PersonaScore;
import services.intelligence.models.SecurityScore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates HTML reports from email intelligence profiles
 */
public class HTMLReportGenerator {

  /**
   * Generates a complete HTML report with dashboard and individual profiles
   */
  public String generateReport(List<EmailIntelligenceProfile> profiles) {
    if (profiles == null || profiles.isEmpty()) {
      return generateEmptyReport();
    }

    StringBuilder html = new StringBuilder();

    // HTML header
    html.append(generateHeader());

    // Dashboard summary
    html.append(generateDashboard(profiles));

    // Individual profiles
    html.append(generateProfilesSection(profiles));

    // Footer and scripts
    html.append(generateFooter(profiles));

    return html.toString();
  }

  private String generateHeader() {
    return "<!DOCTYPE html>\n" +
        "<html lang=\"en\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\">\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "    <title>Email Intelligence Report</title>\n" +
        "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
        "    <style>\n" +
        generateCSS() +
        "    </style>\n" +
        "</head>\n" +
        "<body>\n";
  }

  private String generateCSS() {
    return "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
        "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif; background: #f5f7fa; color: #2c3e50; line-height: 1.6; }\n"
        +
        "        .container { max-width: 1400px; margin: 0 auto; padding: 20px; }\n" +
        "        h1 { font-size: 32px; margin-bottom: 10px; color: #2c3e50; }\n" +
        "        h2 { font-size: 24px; margin: 30px 0 15px; color: #34495e; }\n" +
        "        h3 { font-size: 18px; margin: 15px 0 10px; color: #34495e; }\n" +
        "        .dashboard { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }\n"
        +
        "        .summary-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 30px 0; }\n"
        +
        "        .card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; border-radius: 10px; text-align: center; }\n"
        +
        "        .card.green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }\n" +
        "        .card.orange { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }\n" +
        "        .card.blue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }\n" +
        "        .card-value { font-size: 36px; font-weight: bold; margin-bottom: 5px; }\n" +
        "        .card-label { font-size: 14px; opacity: 0.9; }\n" +
        "        .chart-container { margin: 30px 0; max-width: 800px; }\n" +
        "        .profiles { display: grid; gap: 20px; }\n" +
        "        .profile-card { background: white; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n"
        +
        "        .profile-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 2px solid #ecf0f1; padding-bottom: 15px; }\n"
        +
        "        .profile-email { font-size: 20px; font-weight: 600; color: #2c3e50; }\n" +
        "        .persona-badge { display: inline-block; background: #3498db; color: white; padding: 8px 16px; border-radius: 20px; font-size: 14px; font-weight: 500; }\n"
        +
        "        .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0; }\n"
        +
        "        .metric { padding: 15px; background: #f8f9fa; border-radius: 8px; }\n" +
        "        .metric-label { font-size: 12px; color: #7f8c8d; text-transform: uppercase; margin-bottom: 5px; }\n" +
        "        .metric-value { font-size: 20px; font-weight: 600; color: #2c3e50; }\n" +
        "        .score-LOW { color: #27ae60; }\n" +
        "        .score-MEDIUM { color: #f39c12; }\n" +
        "        .score-HIGH { color: #e74c3c; }\n" +
        "        .section { margin: 20px 0; }\n" +
        "        .section-title { font-size: 16px; font-weight: 600; color: #34495e; margin-bottom: 10px; }\n" +
        "        ul { list-style: none; }\n" +
        "        ul li { padding: 8px 0; padding-left: 20px; position: relative; }\n" +
        "        ul li:before { content: '•'; position: absolute; left: 0; color: #3498db; font-weight: bold; }\n" +
        "        .tag { display: inline-block; background: #ecf0f1; color: #34495e; padding: 4px 12px; border-radius: 12px; font-size: 13px; margin: 4px; }\n"
        +
        "        .tag-container { margin: 10px 0; }\n";
  }

  private String generateDashboard(List<EmailIntelligenceProfile> profiles) {
    int totalAnalyzed = profiles.size();
    long highRisk = profiles.stream()
        .filter(p -> p.getSecurityScore() != null && "HIGH".equals(p.getSecurityScore().getRiskLevel()))
        .count();

    Map<String, Long> segmentCounts = profiles.stream()
        .filter(p -> p.getPersonaScore() != null)
        .collect(Collectors.groupingBy(
            p -> p.getPersonaScore().getSegment() != null ? p.getPersonaScore().getSegment() : "Unknown",
            Collectors.counting()));

    int uniqueSegments = segmentCounts.size();

    StringBuilder html = new StringBuilder();
    html.append("    <div class=\"container\">\n");
    html.append("        <div class=\"dashboard\">\n");
    html.append("            <h1>📊 Email Intelligence Analysis Report</h1>\n");
    html.append("            <p>Comprehensive marketing insights and digital footprint analysis</p>\n");
    html.append("            <div class=\"summary-cards\">\n");
    html.append("                <div class=\"card blue\">\n");
    html.append("                    <div class=\"card-value\">").append(totalAnalyzed).append("</div>\n");
    html.append("                    <div class=\"card-label\">Total Analyzed</div>\n");
    html.append("                </div>\n");
    html.append("                <div class=\"card orange\">\n");
    html.append("                    <div class=\"card-value\">").append(highRisk).append("</div>\n");
    html.append("                    <div class=\"card-label\">High Risk</div>\n");
    html.append("                </div>\n");
    html.append("                <div class=\"card green\">\n");
    html.append("                    <div class=\"card-value\">").append(uniqueSegments).append("</div>\n");
    html.append("                    <div class=\"card-label\">Unique Segments</div>\n");
    html.append("                </div>\n");
    html.append("                <div class=\"card\">\n");
    html.append("                    <div class=\"card-value\">").append(calculateAvgQuality(profiles))
        .append("%</div>\n");
    html.append("                    <div class=\"card-label\">Avg Quality Score</div>\n");
    html.append("                </div>\n");
    html.append("            </div>\n");

    // Chart container
    html.append("            <div class=\"chart-container\">\n");
    html.append("                <canvas id=\"segmentChart\"></canvas>\n");
    html.append("            </div>\n");
    html.append("        </div>\n");

    return html.toString();
  }

  private String generateProfilesSection(List<EmailIntelligenceProfile> profiles) {
    StringBuilder html = new StringBuilder();
    html.append("        <h2>📋 Individual Email Profiles</h2>\n");
    html.append("        <div class=\"profiles\">\n");

    for (EmailIntelligenceProfile profile : profiles) {
      html.append(generateProfileCard(profile));
    }

    html.append("        </div>\n");
    return html.toString();
  }

  private String generateProfileCard(EmailIntelligenceProfile profile) {
    StringBuilder html = new StringBuilder();
    html.append("            <div class=\"profile-card\">\n");
    html.append("                <div class=\"profile-header\">\n");
    html.append("                    <div class=\"profile-email\">").append(escapeHtml(profile.getEmail()))
        .append("</div>\n");

    PersonaScore persona = profile.getPersonaScore();
    if (persona != null && persona.getSegment() != null) {
      html.append("                    <span class=\"persona-badge\">").append(escapeHtml(persona.getSegment()))
          .append("</span>\n");
    }

    html.append("                </div>\n");

    // Metrics grid — SECURITY SCORE, RISK LEVEL, ENGAGEMENT, CONFIDENCE REMOVED
    html.append("                <div class=\"metrics-grid\">\n");
    html.append("                </div>\n");

    // Interests
    if (persona != null && persona.getInterests() != null && !persona.getInterests().isEmpty()) {
      html.append("                <div class=\"section\">\n");
      html.append("                    <div class=\"section-title\">Interests</div>\n");
      html.append("                    <div class=\"tag-container\">\n");
      for (String interest : persona.getInterests()) {
        html.append("                        <span class=\"tag\">").append(escapeHtml(interest)).append("</span>\n");
      }
      html.append("                    </div>\n");
      html.append("                </div>\n");
    }

    // Marketing Recommendations
    if (persona != null && persona.getMarketingRecommendations() != null
        && !persona.getMarketingRecommendations().isEmpty()) {
      html.append("                <div class=\"section\">\n");
      html.append("                    <div class=\"section-title\">📈 Marketing Recommendations</div>\n");
      html.append("                    <ul>\n");
      for (String rec : persona.getMarketingRecommendations()) {
        html.append("                        <li>").append(escapeHtml(rec)).append("</li>\n");
      }
      html.append("                    </ul>\n");
      html.append("                </div>\n");
    }

    // Security Recommendations
    SecurityScore security = profile.getSecurityScore();
    if (security != null && security.getRecommendations() != null && !security.getRecommendations().isEmpty()) {
      html.append("                <div class=\"section\">\n");
      html.append("                    <div class=\"section-title\">🔒 Security Recommendations</div>\n");
      html.append("                    <ul>\n");
      for (String rec : security.getRecommendations()) {
        html.append("                        <li>").append(escapeHtml(rec)).append("</li>\n");
      }
      html.append("                    </ul>\n");
      html.append("                </div>\n");
    }

    html.append("            </div>\n");
    return html.toString();
  }
  // private String generateProfileCard(EmailIntelligenceProfile profile) {
  // StringBuilder html = new StringBuilder();
  // html.append(" <div class=\"profile-card\">\n");
  // html.append(" <div class=\"profile-header\">\n");
  // html.append(" <div
  // class=\"profile-email\">").append(escapeHtml(profile.getEmail())).append("</div>\n");
  //
  // PersonaScore persona = profile.getPersonaScore();
  // if (persona != null && persona.getSegment() != null) {
  // html.append(" <span
  // class=\"persona-badge\">").append(escapeHtml(persona.getSegment())).append("</span>\n");
  // }
  //
  // html.append(" </div>\n");
  //
  // // Metrics grid
  // html.append(" <div class=\"metrics-grid\">\n");
  //
  // SecurityScore security = profile.getSecurityScore();
  // if (security != null) {
  // html.append(" <div class=\"metric\">\n");
  // html.append(" <div class=\"metric-label\">Security Score</div>\n");
  // html.append(" <div class=\"metric-value
  // score-").append(security.getRiskLevel()).append("\">");
  // html.append(security.getOverallScore()).append("/100</div>\n");
  // html.append(" </div>\n");
  //
  // html.append(" <div class=\"metric\">\n");
  // html.append(" <div class=\"metric-label\">Risk Level</div>\n");
  // html.append(" <div class=\"metric-value
  // score-").append(security.getRiskLevel()).append("\">");
  // html.append(security.getRiskLevel()).append("</div>\n");
  // html.append(" </div>\n");
  // }
  //
  // if (persona != null) {
  // html.append(" <div class=\"metric\">\n");
  // html.append(" <div class=\"metric-label\">Engagement</div>\n");
  // html.append(" <div
  // class=\"metric-value\">").append(persona.getEngagementLevel()).append("</div>\n");
  // html.append(" </div>\n");
  //
  // html.append(" <div class=\"metric\">\n");
  // html.append(" <div class=\"metric-label\">Confidence</div>\n");
  // html.append(" <div
  // class=\"metric-value\">").append(persona.getPersonaConfidence()).append("%</div>\n");
  // html.append(" </div>\n");
  // }
  //
  // html.append(" </div>\n");
  //
  // // Interests
  // if (persona != null && persona.getInterests() != null &&
  // !persona.getInterests().isEmpty()) {
  // html.append(" <div class=\"section\">\n");
  // html.append(" <div class=\"section-title\">Interests</div>\n");
  // html.append(" <div class=\"tag-container\">\n");
  // for (String interest : persona.getInterests()) {
  // html.append(" <span
  // class=\"tag\">").append(escapeHtml(interest)).append("</span>\n");
  // }
  // html.append(" </div>\n");
  // html.append(" </div>\n");
  // }
  //
  // // Marketing Recommendations
  // if (persona != null && persona.getMarketingRecommendations() != null &&
  // !persona.getMarketingRecommendations().isEmpty()) {
  // html.append(" <div class=\"section\">\n");
  // html.append(" <div class=\"section-title\">📈 Marketing
  // Recommendations</div>\n");
  // html.append(" <ul>\n");
  // for (String rec : persona.getMarketingRecommendations()) {
  // html.append(" <li>").append(escapeHtml(rec)).append("</li>\n");
  // }
  // html.append(" </ul>\n");
  // html.append(" </div>\n");
  // }
  //
  // // Security Recommendations
  // if (security != null && security.getRecommendations() != null &&
  // !security.getRecommendations().isEmpty()) {
  // html.append(" <div class=\"section\">\n");
  // html.append(" <div class=\"section-title\">🔒 Security
  // Recommendations</div>\n");
  // html.append(" <ul>\n");
  // for (String rec : security.getRecommendations()) {
  // html.append(" <li>").append(escapeHtml(rec)).append("</li>\n");
  // }
  // html.append(" </ul>\n");
  // html.append(" </div>\n");
  // }
  //
  // html.append(" </div>\n");
  // return html.toString();
  // }

  private String generateFooter(List<EmailIntelligenceProfile> profiles) {
    StringBuilder html = new StringBuilder();
    html.append("    </div>\n"); // Close container

    // Chart.js script for segment distribution

    html.append("    <script>\n");

    // === percentage plugin ===

    html.append(" const percentagePlugin = {\n");
    html.append(" id: 'percentagePlugin',\n");
    html.append(" afterDraw(chart) {\n");
    html.append(" const { ctx, data } = chart;\n");
    html.append(" const total = data.datasets[0].data.reduce((a, b) => a + b, 0);\n");
    html.append(" const meta = chart.getDatasetMeta(0);\n");
    html.append(" ctx.save();\n");
    html.append(" ctx.font = '14px sans-serif';\n");
    html.append(" ctx.fillStyle = '#333';\n");
    html.append(" ctx.textAlign = 'center';\n");
    html.append(" ctx.textBaseline = 'middle';\n");
    html.append(" meta.data.forEach((arc, index) => {\n");
    html.append(" const val = data.datasets[0].data[index];\n");
    html.append(" if (!val) return;\n");
    html.append(" const pct = ((val / total) * 100).toFixed(1) + '%';\n");
    html.append(" const pos = arc.tooltipPosition();\n");
    html.append(" ctx.fillText(pct, pos.x, pos.y);\n");
    html.append(" });\n");
    html.append(" ctx.restore();\n");
    html.append(" }\n");
    html.append(" };\n");

    // === chart ===
    html.append("        const ctx = document.getElementById('segmentChart').getContext('2d');\n");
    html.append("        new Chart(ctx, {\n");
    html.append("            type: 'doughnut',\n");
    html.append("            data: {\n");
    html.append("                labels: ").append(getSegmentLabels(profiles)).append(",\n");
    html.append("                datasets: [{\n");
    html.append("                    data: ").append(getSegmentData(profiles)).append(",\n");
    html.append(
        "                    backgroundColor: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#11998e', '#38ef7d', '#4facfe', '#00f2fe'],\n");
    html.append("                }]\n");
    html.append("            },\n");
    html.append("            options: {\n");
    html.append("                responsive: true,\n");
    html.append("                plugins: {\n");
    html.append("                    legend: { position: 'bottom' },\n");
    html.append("                    title: { display: true, text: 'Segment Distribution' }\n");
    html.append("                }\n");
    html.append("            }\n");
    html.append("        });\n");
    html.append("    </script>\n");
    html.append("</body>\n");
    html.append("</html>");

    return html.toString();
  }

  private String generateEmptyReport() {
    return "<!DOCTYPE html><html><head><title>No Data</title></head><body>" +
        "<h1>No email profiles to display</h1>" +
        "<p>Please analyze some emails first.</p></body></html>";
  }

  private String getSegmentLabels(List<EmailIntelligenceProfile> profiles) {
    Map<String, Long> segmentCounts = profiles.stream()
        .filter(p -> p.getPersonaScore() != null && p.getPersonaScore().getSegment() != null)
        .collect(Collectors.groupingBy(
            p -> p.getPersonaScore().getSegment(),
            Collectors.counting()));

    return "[" + String.join(", ", segmentCounts.keySet().stream()
        .map(s -> "'" + s + "'")
        .collect(Collectors.toList())) + "]";
  }

  private String getSegmentData(List<EmailIntelligenceProfile> profiles) {
    Map<String, Long> segmentCounts = profiles.stream()
        .filter(p -> p.getPersonaScore() != null && p.getPersonaScore().getSegment() != null)
        .collect(Collectors.groupingBy(
            p -> p.getPersonaScore().getSegment(),
            Collectors.counting()));

    return "[" + String.join(", ", segmentCounts.values().stream()
        .map(String::valueOf)
        .collect(Collectors.toList())) + "]";
  }

  private int calculateAvgQuality(List<EmailIntelligenceProfile> profiles) {
    return (int) profiles.stream()
        .filter(p -> p.getEmailMetrics() != null && p.getEmailMetrics().getEmailQualityScore() != null)
        .mapToInt(p -> p.getEmailMetrics().getEmailQualityScore())
        .average()
        .orElse(0);
  }

  private String escapeHtml(String text) {
    if (text == null)
      return "";
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;");
  }
}
