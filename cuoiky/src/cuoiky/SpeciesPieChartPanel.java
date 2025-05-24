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
import org.jfree.data.general.DefaultPieDataset;

public class SpeciesPieChartPanel extends JPanel {
    private ChartPanel chartPanelContainer;
    private PetDAO petDAO;

    public SpeciesPieChartPanel() {
        setLayout(new BorderLayout());
         Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            petDAO = new PetDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL cho biểu đồ Tỷ lệ Loài: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        if (petDAO == null) return;
        try {
            List<Pet> allPets = petDAO.getAllPet();
            if (allPets == null || allPets.isEmpty()) {
                chartPanelContainer.setChart(createEmptyPieChart("Không có dữ liệu thú cưng"));
                return;
            }
            Map<String, Integer> speciesCount = new HashMap<>();
            for (Pet pet : allPets) {
                if (pet != null && pet.getSpecies() != null && !pet.getSpecies().trim().isEmpty()) {
                    speciesCount.put(pet.getSpecies(), speciesCount.getOrDefault(pet.getSpecies(), 0) + 1);
                }
            }
            DefaultPieDataset dataset = new DefaultPieDataset();
             if (!speciesCount.isEmpty()) {
                for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
                    dataset.setValue(entry.getKey(), entry.getValue());
                }
             }
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Tỷ Lệ Các Loài Vật Nuôi", dataset, true, true, false);
            chartPanelContainer.setChart(pieChart);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu cho biểu đồ Tỷ lệ Loài: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            chartPanelContainer.setChart(createEmptyPieChart("Lỗi tải dữ liệu"));
        }
    }

    private JFreeChart createEmptyPieChart(String message) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(
            message, dataset, false, false, false);
        return chart;
    }
}