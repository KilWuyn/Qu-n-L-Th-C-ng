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

public class SpeciesBarChartPanel extends JPanel {
    private ChartPanel chartPanelContainer; 
    private PetDAO petDAO;

    public SpeciesBarChartPanel() {
        setLayout(new BorderLayout());
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            petDAO = new PetDAO(conn); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL cho biểu đồ Loài: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            // Hiển thị panel trống hoặc thông báo lỗi
            add(new javax.swing.JLabel("Không thể tải biểu đồ do lỗi kết nối.", javax.swing.SwingConstants.CENTER));
            return;
        }

        this.chartPanelContainer = new ChartPanel(null); // Khởi tạo rỗng ban đầu
        add(chartPanelContainer, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Tải lại biểu đồ");
        btnRefresh.addActionListener(e -> refreshChart());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshChart(); // Tải và vẽ biểu đồ lần đầu
    }

    public void refreshChart() {
        if (petDAO == null) {
            JOptionPane.showMessageDialog(this, "Đối tượng DAO chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<Pet> allPets = petDAO.getAllPet(); // Hoặc getAllPets() tùy tên phương thức
            if (allPets == null || allPets.isEmpty()) {
                System.err.println("Không có dữ liệu Pet để vẽ biểu đồ loài (Cột).");
                chartPanelContainer.setChart(createEmptyChart("Không có dữ liệu thú cưng"));
                return;
            }

            Map<String, Integer> speciesCount = new HashMap<>();
            for (Pet pet : allPets) {
                if (pet != null && pet.getSpecies() != null && !pet.getSpecies().trim().isEmpty()) {
                    speciesCount.put(pet.getSpecies(), speciesCount.getOrDefault(pet.getSpecies(), 0) + 1);
                }
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            if (!speciesCount.isEmpty()) {
                for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
                    dataset.addValue(entry.getValue(), "Số lượng", entry.getKey());
                }
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Số Lượng Vật Nuôi Theo Loài", "Loài", "Số Lượng",
                    dataset, PlotOrientation.VERTICAL, true, true, false);
            chartPanelContainer.setChart(barChart); // Cập nhật biểu đồ trong ChartPanel

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu cho biểu đồ Loài: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            chartPanelContainer.setChart(createEmptyChart("Lỗi tải dữ liệu"));
        }
    }

    private JFreeChart createEmptyChart(String message) {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         // Để tạo một biểu đồ trống có thông báo
         JFreeChart chart = ChartFactory.createBarChart(
             message, // Tiêu đề là thông báo
             "Loài", "Số Lượng", dataset, PlotOrientation.VERTICAL, false, false, false);
         return chart;
     }
}