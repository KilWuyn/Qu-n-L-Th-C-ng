package cuoiky;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.chart.plot.PlotOrientation;

public class EventsByTypeBarChartPanel extends JPanel {
 private ChartPanel chartPanelContainer;
 private EventDAO eventDAO;

 public EventsByTypeBarChartPanel() {
     setLayout(new BorderLayout());
     Connection conn = null;
     try {
         conn = DBConnection.getConnection();
         eventDAO = new EventDAO(conn);
     } catch (SQLException e) {
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL cho biểu đồ Loại Sự Kiện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
         add(new javax.swing.JLabel("Không thể tải biểu đồ do lỗi kết nối.", javax.swing.SwingConstants.CENTER));
         return;
     }

     this.chartPanelContainer = new ChartPanel(null);
     add(chartPanelContainer, BorderLayout.CENTER);

     JButton btnRefresh = new JButton("Tải lại biểu đồ");
     btnRefresh.addActionListener(e -> refreshChart());
     JPanel bottomPanel = new JPanel();
     bottomPanel.add(btnRefresh);
     add(bottomPanel, BorderLayout.SOUTH);

     refreshChart();
 }

 public void refreshChart() {
      if (eventDAO == null) return;
      try {
          List<Event> allEvents = eventDAO.getAllEvents();
          if (allEvents == null || allEvents.isEmpty()) {
              chartPanelContainer.setChart(createEmptyChart("Không có dữ liệu sự kiện"));
              return;
          }
          Map<String, Integer> eventTypeCount = new HashMap<>();
          for (Event event : allEvents) {
              if (event != null && event.getEventType() != null && !event.getEventType().trim().isEmpty()) {
                  eventTypeCount.put(event.getEventType(), eventTypeCount.getOrDefault(event.getEventType(), 0) + 1);
              }
          }
          DefaultCategoryDataset dataset = new DefaultCategoryDataset();
          if (!eventTypeCount.isEmpty()){
              for (Map.Entry<String, Integer> entry : eventTypeCount.entrySet()) {
                  dataset.addValue(entry.getValue(), "Số lượng", entry.getKey());
              }
          }
          JFreeChart barChart = ChartFactory.createBarChart(
                  "Số Lượng Sự Kiện Theo Loại", "Loại Sự Kiện", "Số Lượng",
                  dataset, PlotOrientation.VERTICAL, true, true, false);
          chartPanelContainer.setChart(barChart);
      } catch (SQLException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu cho biểu đồ Loại Sự Kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
          chartPanelContainer.setChart(createEmptyChart("Lỗi tải dữ liệu"));
      }
 }
  private JFreeChart createEmptyChart(String message) { 
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      JFreeChart chart = ChartFactory.createBarChart(
          message, "Loại Sự Kiện", "Số Lượng", dataset, PlotOrientation.VERTICAL, false, false, false);
      return chart;
  }
}