package cuoiky;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap; // Để sắp xếp năm

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PetsByBirthYearLineChartPanel extends JPanel {
    private ChartPanel chartPanelContainer; // Panel để chứa ChartPanel thực sự
    private PetDAO petDAO;
    private boolean isDaoInitialized = false;

    public PetsByBirthYearLineChartPanel() {
        setLayout(new BorderLayout());
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            petDAO = new PetDAO(conn);
            isDaoInitialized = true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL cho biểu đồ Năm Sinh: " + e.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            add(new javax.swing.JLabel("Không thể tải biểu đồ do lỗi kết nối CSDL.", javax.swing.SwingConstants.CENTER));
            // Không đóng conn ở đây vì có thể conn chưa được gán nếu DBConnection ném lỗi sớm
            return; // Không khởi tạo UI nếu DAO lỗi
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
        if (!isDaoInitialized || petDAO == null) {
            JOptionPane.showMessageDialog(this, "Đối tượng PetDAO chưa được khởi tạo đúng cách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            chartPanelContainer.setChart(createEmptyChart("Lỗi khởi tạo DAO"));
            return;
        }

        List<Pet> allPets;
        try {
            allPets = petDAO.getAllPet(); // Gọi phương thức lấy tất cả pet
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thú cưng từ CSDL: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            chartPanelContainer.setChart(createEmptyChart("Lỗi tải dữ liệu từ CSDL"));
            return;
        }

        if (allPets == null || allPets.isEmpty()) {
            System.err.println("Không có dữ liệu Pet để vẽ biểu đồ năm sinh.");
            chartPanelContainer.setChart(createEmptyChart("Không có dữ liệu thú cưng"));
            return;
        }

        // Dùng TreeMap để tự động sắp xếp các năm theo thứ tự tăng dần
        Map<Integer, Integer> petsByYear = new TreeMap<>();
        for (Pet pet : allPets) {
            if (pet != null && pet.getBirth() != null && !pet.getBirth().trim().isEmpty() && !pet.getBirth().equalsIgnoreCase("\\N")) {
                try {
                    String birthString = pet.getBirth(); 
                    // Kiểm tra độ dài chuỗi trước khi substring
                    if (birthString.length() >= 4) {
                        int year = Integer.parseInt(birthString.substring(0, 4)); // Lấy 4 ký tự đầu làm năm
                        petsByYear.put(year, petsByYear.getOrDefault(year, 0) + 1);
                    } else {
                         System.err.println("Định dạng ngày sinh không đủ dài cho thú cưng: " + pet.getName() + " - Ngày sinh: " + pet.getBirth());
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi chuyển đổi năm sinh (không phải số) cho thú cưng: " + pet.getName() + " - Ngày sinh: " + pet.getBirth());
                } catch (StringIndexOutOfBoundsException e) {
                     System.err.println("Lỗi định dạng ngày sinh (quá ngắn) cho thú cưng: " + pet.getName() + " - Ngày sinh: " + pet.getBirth());
                }
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (!petsByYear.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : petsByYear.entrySet()) {
                dataset.addValue(entry.getValue(), "Số thú cưng mới", entry.getKey().toString()); // Năm là category
            }
        } else {
            System.out.println("Không có dữ liệu năm sinh hợp lệ để vẽ biểu đồ.");
            // Dataset sẽ trống, biểu đồ sẽ hiển thị "No data available"
        }


        JFreeChart lineChart = ChartFactory.createLineChart(
                "Số Lượng Thú Cưng Mới Theo Năm Sinh", 
                "Năm Sinh",                          
                "Số Lượng",                           
                dataset,
                PlotOrientation.VERTICAL,
                true, 
                true, 
                false 
        );
        chartPanelContainer.setChart(lineChart);
    }

    private JFreeChart createEmptyChart(String message) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart(
            message, "Năm Sinh", "Số Lượng", dataset, PlotOrientation.VERTICAL, false, false, false);
        return chart;
    }
}