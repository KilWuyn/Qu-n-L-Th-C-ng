# Dự án Quản Lý Thú Cưng (Qu-n-L-Th-C-ng)
Đây là repository chứa dữ liệu mẫu cho một dự án quản lý thú cưng. Dữ liệu được trích xuất từ cơ sở dữ liệu `pet_db` sử dụng MySQL.

## Dữ liệu Cơ sở dữ liệu (MySQL)
Repository này chứa các file SQL riêng lẻ cho từng bảng trong cơ sở dữ liệu `pet_db`. Mỗi file chứa cả cấu trúc (lệnh `CREATE TABLE`) và dữ liệu (lệnh `INSERT INTO`) cho bảng tương ứng.

**Hệ quản trị CSDL:** MySQL
**Các file SQL cho từng bảng:**
`pet_db_event.sql`: Chứa dữ liệu cho bảng `event`.
`pet_db_pet.sql`: Chứa dữ liệu cho bảng `pet`.
`pet_db_users.sql`: Chứa dữ liệu cho bảng `users`.

### Các bảng chính trong `pet_db`:
`event`: Lưu trữ thông tin về các sự kiện liên quan đến thú cưng.
`pet`: Lưu trữ thông tin chi tiết về các thú cưng.
`users`: Lưu trữ thông tin người dùng (ví dụ: chủ sở hữu thú cưng).
*   **LƯU Ý QUAN TRỌNG VỀ BẢO MẬT:** Dữ liệu trong bảng `users` (file `pet_db_users.sql`), đặc biệt là cột mật khẩu, chỉ mang tính chất minh họa.
*   
## Mục đích dự án 
Dùng cho dự án học tập ứng dụng web/desktop quản lý thú cưng.

## Mô tả dự án
Đây là một dự án cơ sở dữ liệu mô phỏng một hệ thống quản lý thông tin thú cưng đơn giản.
Mục tiêu chính của dự án là phục vụ mục đích học tập về thiết kế cơ sở dữ liệu quan hệ và thực hành các truy vấn SQL.
Dự án sử dụng cơ sở dữ liệu MySQL để lưu trữ thông tin về:
**Người dùng/Chủ sở hữu:** Thông tin tài khoản của những người sở hữu thú cưng.
**Thú cưng:** Chi tiết về từng con thú cưng (tên, loài, giống, v.v.).
**Sự kiện:** Các sự kiện liên quan đến thú cưng (ví dụ: lịch tiêm phòng, lịch khám bệnh, ngày kỷ niệm).
Repository này cung cấp các file SQL cần thiết để bạn có thể tạo lại cấu trúc (schema) và dữ liệu mẫu cho cơ sở dữ liệu `pet_db` trên máy của mình.

### Cấu trúc Cơ sở dữ liệu `pet_db`
Cơ sở dữ liệu `pet_db` bao gồm các bảng chính sau:
1.  **`users`** (dữ liệu trong file `pet_db_users.sql`):
 Lưu trữ thông tin tài khoản người dùng hoặc chủ sở hữu thú cưng.
 Các cột chính (có thể có): `id`, `username`, `password`, [các cột khác nếu có].
 **LƯU Ý AN TOÀN:** Dữ liệu mật khẩu trong file SQL này chỉ mang tính chất minh họa và **không an toàn**. Trong một ứng dụng thực tế, mật khẩu phải luôn được hash.
2.  **`pet`** (dữ liệu trong file `pet_db_pet.sql`):
 Lưu trữ thông tin chi tiết về từng thú cưng.
 Các cột chính (có thể có): `id`, `name` (tên thú cưng), `species` (loài), `breed` (giống), `age` (tuổi), `owner_id` (ID của chủ sở hữu, liên kết với bảng `users`), [các cột khác nếu có].
3.  **`event`** (dữ liệu trong file `pet_db_event.sql`):
 Lưu trữ thông tin về các sự kiện liên quan đến thú cưng.
 Các cột chính (có thể có): `id`, `event_type` (loại sự kiện), `event_date` (ngày diễn ra), `description` (mô tả), `pet_id` (ID của thú cưng liên quan, liên kết với bảng `pet`), [các cột khác nếu có].
Dữ liệu được chia sẻ dưới dạng các file `.sql` riêng biệt cho từng bảng. Mỗi file chứa cả lệnh tạo bảng (`CREATE TABLE`) và lệnh chèn dữ liệu (`INSERT INTO`).

## Hướng dẫn sử dụng dự án (Khôi phục Cơ sở dữ liệu)
Để sử dụng dữ liệu từ dự án (tức là khôi phục cơ sở dữ liệu `pet_db` trên máy của bạn), bạn cần thực hiện các bước :

### Yêu cầu:
1.  **MySQL Server:** Đã được cài đặt và đang chạy trên máy của bạn.
2.  **Công cụ quản lý MySQL:**
    *   **MySQL Workbench** (khuyến nghị, trực quan và dễ sử dụng).
    *   Hoặc bạn có thể sử dụng command line `mysql`.

### Các bước thực hiện:
1.  **Tải các file SQL:**
    *   Clone repository này về máy của bạn:
        ```bash
        ```
    *   Hoặc tải trực tiếp các file `pet_db_event.sql`, `pet_db_pet.sql`, và `pet_db_users.sql` từ GitHub về một thư mục trên máy.

2.  **Tạo Schema (Database) `pet_db` (nếu chưa có):**
    *   **Sử dụng MySQL Workbench:**
        1.  Mở MySQL Workbench và kết nối đến MySQL Server của bạn.
        2.  Trong khung Navigator (bên trái), nhấp chuột phải vào một vùng trống dưới danh sách Schemas và chọn **"Create Schema..."**.
        3.  Đặt tên cho schema là `pet_db`.
        4.  Nhấn "Apply", sau đó "Apply" một lần nữa trong hộp thoại xác nhận để tạo schema.
    *   **Sử dụng Command Line `mysql`:**
        Mở terminal hoặc command prompt và chạy lệnh sau (thay `your_mysql_user` bằng tên người dùng MySQL của bạn):
        ```bash
        ```
        (Bạn sẽ được yêu cầu nhập mật khẩu MySQL).

    *   **Sử dụng MySQL Workbench:**
        1.  Mở MySQL Workbench.
        2.  Với **mỗi file SQL** theo thứ tự trên:
            a.  Chọn `File` -> `Open SQL Script...` từ menu.
            b.  Duyệt đến và chọn file SQL tương ứng (ví dụ: `pet_db_users.sql` trước tiên).
            c.  Trong cửa sổ SQL Editor vừa mở, đảm bảo rằng ở thanh công cụ phía trên, **Schema mặc định được chọn là `pet_db`** (schema bạn vừa tạo). Nếu không, bạn có thể thêm dòng `USE pet_db;` vào đầu script trong editor.
            d.  Nhấn vào biểu tượng **tia sét (Execute SQL Script)** để chạy toàn bộ script trong file đó.
            e.  Chờ quá trình thực thi hoàn tất.
        3.  Lặp lại cho các file SQL còn lại theo đúng thứ tự.

    *   **Sử dụng Command Line `mysql`:**
        Mở terminal hoặc command prompt, di chuyển đến thư mục chứa các file `.sql` đã tải về, và chạy các lệnh sau (thay `your_mysql_user` bằng tên người dùng MySQL của bạn):
        ```bash


4.  **Kiểm tra:**
    *   Sau khi import tất cả các file, trong MySQL Workbench, nhấp chuột phải vào schema `pet_db` trong Navigator và chọn "Refresh All".
    *   Mở rộng schema `pet_db`, sau đó mở rộng "Tables". Bạn sẽ thấy các bảng `users`, `pet`, và `event`.
    *   Bạn có thể nhấp chuột phải vào một bảng và chọn "Select Rows - Limit 1000" để xem dữ liệu mẫu.

## (Tùy chọn) Đóng góp
[Nếu bạn muốn người khác đóng góp, hãy ghi rõ cách thức ở đây, ví dụ:
*   Nếu bạn phát hiện lỗi hoặc có đề xuất cải thiện, vui lòng tạo một "Issue".
*   Nếu bạn muốn đóng góp mã nguồn hoặc dữ liệu, vui lòng tạo "Pull Request".
]

## (Tùy chọn) Liên hệ
*   Hoặc liên hệ qua [Email của bạn] / [Link mạng xã hội của bạn] (nếu muốn).
