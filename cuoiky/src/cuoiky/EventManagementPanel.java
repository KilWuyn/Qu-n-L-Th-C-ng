package cuoiky;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent; // Thêm nếu cần (đã có lambda)
import java.awt.event.ActionListener; // Thêm nếu cần (đã có lambda)
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class EventManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private EventDAO eventDAO;
    private PetDAO petDAO; // Thêm PetDAO để kiểm tra Pet
    private JTextField searchField;
    private boolean isInitialized = false; // Cờ để kiểm tra khởi tạo thành công

    public EventManagementPanel() { 
        setLayout(new BorderLayout());
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            eventDAO = new EventDAO(conn);
            petDAO = new PetDAO(conn);
            isInitialized = true; // Đánh dấu khởi tạo thành công
        } catch (SQLException e) {
            e.printStackTrace();
            // Hiển thị lỗi ở đây và không khởi tạo UI nếu lỗi
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến cơ sở dữ liệu để quản lý sự kiện.\nChi tiết: " + e.getMessage(),
                    "Lỗi Kết Nối CSDL",
                    JOptionPane.ERROR_MESSAGE);
            // Không return ngay, để UI trống hoặc thông báo lỗi được hiển thị
            // Nếu muốn UI hoàn toàn không hiện gì, có thể setVisible(false) hoặc không add component
        }

        // Chỉ tạo UI nếu khởi tạo thành công
        if (isInitialized) {
            tableModel = new DefaultTableModel(new String[]{"Event ID", "Tên Thú Cưng", "Ngày", "Loại Sự Kiện", "Chi Tiết"}, 0);
            table = new JTable(tableModel);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton btnLoad = new JButton("Tải lại");
            JButton btnAdd = new JButton("Thêm");
            JButton btnEdit = new JButton("Sửa");
            JButton btnDelete = new JButton("Xóa");
            searchField = new JTextField(15);
            JButton btnSearch = new JButton("Tìm");

            buttonPanel.add(btnLoad);
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnDelete);
            buttonPanel.add(searchField);
            buttonPanel.add(btnSearch);
            add(buttonPanel, BorderLayout.SOUTH);

            btnLoad.addActionListener(e -> loadEvents());
            btnAdd.addActionListener(e -> addEvent());
            btnEdit.addActionListener(e -> editEvent());
            btnDelete.addActionListener(e -> deleteEvent());
            btnSearch.addActionListener(e -> searchEvents());

            loadEvents();
        } else {
            // Hiển thị một label thông báo lỗi nếu không khởi tạo được
            add(new javax.swing.JLabel("Không thể tải Event Panel do lỗi kết nối CSDL.", javax.swing.SwingConstants.CENTER), BorderLayout.CENTER);
        }
    }

    private void loadEvents() {
        if (!isInitialized) return; // Không làm gì nếu chưa khởi tạo thành công
        try {
            tableModel.setRowCount(0);
            List<Event> events = eventDAO.getAllEvents();
            if (events != null) {
                for (Event ev : events) {
                    tableModel.addRow(new Object[]{
                            ev.getEventId(), ev.getPetName(),
                            ev.getEventDate(), ev.getEventType(), ev.getEventDetails()
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu sự kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEvent() {
        if (!isInitialized) return;
        try {
            // Sử dụng petDAO đã được khởi tạo
            if (petDAO.getAllPet().isEmpty()) { // Giả sử PetDAO có getAllPets()
                JOptionPane.showMessageDialog(this, "Chưa có thú cưng nào trong hệ thống. Vui lòng thêm thú cưng trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } catch (SQLException e1) {
             e1.printStackTrace();
             JOptionPane.showMessageDialog(this,"Lỗi kiểm tra danh sách thú cưng: " + e1.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
        }

        String petName = JOptionPane.showInputDialog(this, "Tên Thú Cưng liên quan:");
        if (petName == null || petName.trim().isEmpty()) return;

        try {
            // Sử dụng petDAO đã được khởi tạo
            if (petDAO.getPetByName(petName) == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thú cưng với tên '" + petName + "'.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this,"Lỗi khi kiểm tra tên thú cưng: " + e1.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String eventType = JOptionPane.showInputDialog(this, "Loại Sự Kiện (ví dụ: vet, birthday, litter):");
        if (eventType == null || eventType.trim().isEmpty()) return;

        String dateStr = JOptionPane.showInputDialog(this, "Ngày Sự Kiện (YYYY-MM-DD):");
        if (dateStr == null || dateStr.trim().isEmpty()) return;

        String eventDetails = JOptionPane.showInputDialog(this, "Chi Tiết Sự Kiện:");

        try {
            LocalDate eventDate = LocalDate.parse(dateStr);
            Event newEvent = new Event(petName, eventDate, eventType, eventDetails);
            eventDAO.addEvent(newEvent);
            loadEvents();
            JOptionPane.showMessageDialog(this, "Đã thêm sự kiện thành công!");
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng nhập YYYY-MM-DD.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sự kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editEvent() {
        if (!isInitialized) return;
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sự kiện để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        Event eventToEdit;
        try {
            eventToEdit = eventDAO.getEventById(eventId);
            if (eventToEdit == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sự kiện để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                loadEvents();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin sự kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String petName = JOptionPane.showInputDialog(this, "Tên Thú Cưng mới:", eventToEdit.getPetName());
        if (petName == null) return; // Người dùng hủy
        // Nếu petName không rỗng, kiểm tra xem nó có tồn tại không
        if (!petName.trim().isEmpty()) {
            try {
                // Sử dụng petDAO đã được khởi tạo
                if (petDAO.getPetByName(petName) == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thú cưng với tên '" + petName + "' để liên kết.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this,"Lỗi khi kiểm tra tên thú cưng: " + e1.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // Nếu người dùng nhập petName rỗng, có thể mày muốn cho phép hoặc không
            // Hiện tại, nếu rỗng thì không kiểm tra, nhưng khóa ngoại có thể yêu cầu NOT NULL
            // Hoặc mày có thể muốn đặt petName thành null nếu cột pet_name trong DB cho phép NULL
            // petName = null; // Ví dụ
        }


        String dateStr = JOptionPane.showInputDialog(this, "Ngày Sự Kiện mới (YYYY-MM-DD):", eventToEdit.getEventDate().toString());
        if (dateStr == null) return;
        String eventType = JOptionPane.showInputDialog(this, "Loại Sự Kiện mới:", eventToEdit.getEventType());
        if (eventType == null) return;
        String eventDetails = JOptionPane.showInputDialog(this, "Chi Tiết Sự Kiện mới:", eventToEdit.getEventDetails());
        if (eventDetails == null) return; // Cho phép eventDetails là null hoặc rỗng


        try {
            LocalDate eventDate = LocalDate.parse(dateStr);
            Event updatedEvent = new Event(eventId, petName, eventDate, eventType, eventDetails);
            eventDAO.updateEvent(updatedEvent);
            loadEvents();
            JOptionPane.showMessageDialog(this, "Đã cập nhật sự kiện.");
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ.", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sự kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEvent() {
        if (!isInitialized) return;
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sự kiện để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sự kiện này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                eventDAO.deleteEvent(eventId);
                loadEvents();
                JOptionPane.showMessageDialog(this, "Đã xóa sự kiện.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sự kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchEvents() {
        if (!isInitialized) return;
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadEvents();
            return;
        }
        try {
            tableModel.setRowCount(0);
            List<Event> foundEvents = eventDAO.searchEvents(keyword);
            if (foundEvents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sự kiện nào khớp với '" + keyword + "'.", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Event ev : foundEvents) {
                    tableModel.addRow(new Object[]{
                            ev.getEventId(), ev.getPetName(),
                            ev.getEventDate(), ev.getEventType(), ev.getEventDetails()
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm sự kiện: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}