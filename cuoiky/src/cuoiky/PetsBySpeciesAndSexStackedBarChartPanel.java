package cuoiky;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList; // Thêm import này
import java.util.Collections; // Thêm import này
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PetsBySpeciesAndSexStackedBarChartPanel extends JPanel {
    private ChartPanel chartPanelContainer;
    private PetDAO petDAO;
    private boolean isDaoInitialized = false;

    public PetsBySpeciesAndSexStackedBarChartPanel() {
        setLayout(new BorderLayout());
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            petDAO = new PetDAO(conn);
            isDaoInitialized = true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL cho biểu đồ Loài & Giới tính: " + e.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            add(new javax.swing.JLabel("Không thể tải biểu đồ do lỗi kết nối CSDL.", javax.swing.SwingConstants.CENTER));
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
        if (!isDaoInitialized || petDAO == null) {
             JOptionPane.showMessageDialog(this, "Đối tượng PetDAO chưa được khởi tạo đúng cách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             chartPanelContainer.setChart(createEmptyChart("Lỗi khởi tạo DAO"));
            return;
        }

        List<Pet> allPets;
        try {
            allPets = petDAO.getAllPet();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thú cưng từ CSDL: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            chartPanelContainer.setChart(createEmptyChart("Lỗi tải dữ liệu từ CSDL"));
            return;
        }


        if (allPets == null || allPets.isEmpty()) {
            System.err.println("Không có dữ liệu Pet để vẽ biểu đồ loài và giới tính.");
            chartPanelContainer.setChart(createEmptyChart("Không có dữ liệu thú cưng"));
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Map<String, Integer>> speciesSexCount = new TreeMap<>(); // TreeMap để loài được sắp xếp

        final String MALE_STR = "Đực";
        final String FEMALE_STR = "Cái";
        final String UNKNOWN_SEX_STR = "Không rõ";

        for (Pet pet : allPets) {
            if (pet != null && pet.getSpecies() != null && !pet.getSpecies().trim().isEmpty()) {
                String species = pet.getSpecies();
                String sexKey;

                if (pet.getSex() == null || pet.getSex().trim().isEmpty() || pet.getSex().equalsIgnoreCase("\\N")) {
                    sexKey = UNKNOWN_SEX_STR;
                } else if (pet.getSex().equalsIgnoreCase("m")) {
                    sexKey = MALE_STR;
                } else if (pet.getSex().equalsIgnoreCase("f")) {
                    sexKey = FEMALE_STR;
                } else {
                    // Xử lý các giá trị sex không mong đợi, có thể gộp vào "Không rõ"
                    // Hoặc log ra để biết có dữ liệu lạ
                    System.err.println("Giá trị giới tính không xác định: '" + pet.getSex() + "' cho thú cưng: " + pet.getName());
                    sexKey = UNKNOWN_SEX_STR;
                }

                speciesSexCount.putIfAbsent(species, new HashMap<>());
                Map<String, Integer> sexCountForSpecies = speciesSexCount.get(species);
                sexCountForSpecies.put(sexKey, sexCountForSpecies.getOrDefault(sexKey, 0) + 1);
            }
        }

        // Các series (giới tính) cần được định nghĩa trước để đảm bảo thứ tự nhất quán trên biểu đồ
        List<String> sexSeries = new ArrayList<>();
        sexSeries.add(MALE_STR);    // Ưu tiên hiển thị Đực trước
        sexSeries.add(FEMALE_STR);  // Rồi đến Cái
        sexSeries.add(UNKNOWN_SEX_STR); // Cuối cùng là Không rõ

        if (!speciesSexCount.isEmpty()){
            for (String species : speciesSexCount.keySet()) { // Lặp qua các loài (đã được TreeMap sắp xếp)
                Map<String, Integer> sexCountMap = speciesSexCount.get(species);
                for (String sexKey : sexSeries) { // Lặp qua các series giới tính theo thứ tự đã định
                    dataset.addValue(sexCountMap.getOrDefault(sexKey, 0), sexKey, species);
                }
            }
        } else {
             System.out.println("Không có dữ liệu loài và giới tính hợp lệ để vẽ biểu đồ.");
        }


        JFreeChart stackedBarChart = ChartFactory.createStackedBarChart(
                "Số Lượng Vật Nuôi Theo Loài và Giới Tính",
                "Loài Vật Nuôi",
                "Số Lượng",
                dataset,
                PlotOrientation.VERTICAL,
                true, 
                true, 
                false 
        );
        chartPanelContainer.setChart(stackedBarChart);
    }

     private JFreeChart createEmptyChart(String message) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createStackedBarChart(
            message, "Loài", "Số Lượng", dataset, PlotOrientation.VERTICAL, false, false, false);
        return chart;
    }
}