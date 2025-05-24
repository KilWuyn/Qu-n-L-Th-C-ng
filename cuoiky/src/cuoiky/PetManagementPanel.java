package cuoiky;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField; 
import javax.swing.table.DefaultTableModel;

public class PetManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private PetDAO petDAO;
 

    public PetManagementPanel() throws SQLException {
        setLayout(new BorderLayout());
        Connection conn = null;
        conn = DBConnection.getConnection();
        petDAO = new PetDAO(conn);

        String[] columnNames = {"Tên Thú Cưng", "Chủ Sở Hữu", "Loài", "Giới Tính", "Ngày Sinh", "Ngày Mất"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnLoad = new JButton("Tải lại");
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        buttonPanel.add(btnLoad);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadPet());
        btnAdd.addActionListener(e -> addPet());
        btnEdit.addActionListener(e -> editPet());
        btnDelete.addActionListener(e -> deletePet());

        loadPet();
    }

    private void loadPet() {
        try {
            tableModel.setRowCount(0);
            List<Pet> pets = petDAO.getAllPet();
            if (pets != null) {
                for (Pet pet : pets) {
                    tableModel.addRow(new Object[]{
                            pet.getName(), pet.getOwner(), pet.getSpecies(),
                            pet.getSex(), pet.getBirth(), pet.getDeath()
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thú cưng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPet() {
        String name = JOptionPane.showInputDialog(this, "Tên thú cưng:");
        if (name == null || name.trim().isEmpty()) return;

        try {
            if (petDAO.getPetByName(name) != null) {
                JOptionPane.showMessageDialog(this, "Thú cưng với tên '" + name + "' đã tồn tại!", "Lỗi Trùng Tên", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra tên thú cưng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String owner = JOptionPane.showInputDialog(this, "Tên chủ:");
        String species = JOptionPane.showInputDialog(this, "Loài:");
        String sex = JOptionPane.showInputDialog(this, "Giới tính (m/f hoặc \\N nếu không có):");
        String birth = JOptionPane.showInputDialog(this, "Ngày sinh (YYYY-MM-DD hoặc \\N):");
        String death = JOptionPane.showInputDialog(this, "Ngày mất (YYYY-MM-DD hoặc \\N nếu còn sống):");

        Pet newPet = new Pet(name, owner, species, sex, birth, death);
        try {
            petDAO.addPet(newPet);
            loadPet();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm thú cưng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editPet() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thú cưng để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String currentName = (String) tableModel.getValueAt(selectedRow, 0);
        Pet petToEdit;
        try {
            petToEdit = petDAO.getPetByName(currentName);
            if (petToEdit == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thú cưng '" + currentName + "' trong CSDL để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                loadPet();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin thú cưng để sửa: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String owner = JOptionPane.showInputDialog(this, "Tên chủ mới:", petToEdit.getOwner());
        if (owner == null) return;
        String species = JOptionPane.showInputDialog(this, "Loài mới:", petToEdit.getSpecies());
        if (species == null) return;
        String sex = JOptionPane.showInputDialog(this, "Giới tính mới (m/f hoặc \\N):", petToEdit.getSex());
        if (sex == null) return;
        String birth = JOptionPane.showInputDialog(this, "Ngày sinh mới (YYYY-MM-DD hoặc \\N):", petToEdit.getBirth());
        if (birth == null) return;
        String death = JOptionPane.showInputDialog(this, "Ngày mất mới (YYYY-MM-DD hoặc \\N):", petToEdit.getDeath());
        if (death == null) return;

        Pet updatedPet = new Pet(currentName, owner, species, sex, birth, death);
        try {
            petDAO.updatePet(updatedPet);
            loadPet();
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin thú cưng '" + currentName + "'.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thú cưng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePet() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thú cưng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String petName = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa thú cưng '" + petName + "' không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                petDAO.deletePet(petName);
                loadPet();
                JOptionPane.showMessageDialog(this, "Đã xóa thú cưng '" + petName + "'.");
            } catch (SQLException e) {
                e.printStackTrace();
                if (e.getErrorCode() == 1451) { 
                     JOptionPane.showMessageDialog(this,
                         "Không thể xóa thú cưng '" + petName + "' vì có các sự kiện liên quan.\n" +
                         "Vui lòng xóa các sự kiện của thú cưng này trước.",
                         "Lỗi Ràng Buộc Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                } else {
                     JOptionPane.showMessageDialog(this, "Lỗi khi xóa thú cưng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}