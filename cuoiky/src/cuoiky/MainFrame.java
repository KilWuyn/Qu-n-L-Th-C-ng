package cuoiky;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
// Không cần nếu constructor MainFrame không throws

public class MainFrame extends JFrame {

    public MainFrame() { // Bỏ throws SQLException
        setTitle("Hệ Thống Quản Lý Thú Cưng");
        setSize(1000, 750); // Tăng kích thước để dễ nhìn nhiều tab
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Các tab quản lý
        try {
            tabbedPane.addTab("Quản Lý Thú Cưng", new PetManagementPanel());
            tabbedPane.addTab("Quản Lý Sự Kiện", new EventManagementPanel());
        } catch (Exception e) { // Bắt Exception chung nếu constructor Panel ném lỗi
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng khi khởi tạo panel quản lý: " + e.getMessage(), "Lỗi Khởi Tạo", JOptionPane.ERROR_MESSAGE);
        }

        // Thêm các tab biểu đồ
        tabbedPane.addTab("Loài (Cột)", new SpeciesBarChartPanel());
        tabbedPane.addTab("Loài (Tròn)", new SpeciesPieChartPanel());
        tabbedPane.addTab("Loại Sự Kiện", new EventsByTypeBarChartPanel());
        tabbedPane.addTab("Năm Sinh (Đường)", new PetsByBirthYearLineChartPanel());
        tabbedPane.addTab("Loài & Giới Tính (Chồng)", new PetsBySpeciesAndSexStackedBarChartPanel());

        add(tabbedPane);
    }

}