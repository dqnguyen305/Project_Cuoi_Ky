package ra.course.presentation;

import ra.course.business.IAdminService;
import ra.course.business.IStudentService;
import ra.course.business.impl.AdminServiceImpl;
import ra.course.business.impl.StudentServiceImpl;
import ra.course.model.Admin;
import ra.course.model.Student;

import java.util.Scanner;

public class StudentView {
    private final Scanner scanner = new Scanner(System.in);
    private final IAdminService adminService = new AdminServiceImpl();
    private final IStudentService studentService = new StudentServiceImpl();

    public void showMainMenu() {
        while (true) {
            System.out.println("\n========== HỆ THỐNG QUẢN LÝ ĐÀO TẠO ==========");
            System.out.println("1. Đăng nhập với tư cách Quản trị viên");
            System.out.println("2. Đăng nhập với tư cách Học viên");
            System.out.println("3. Thoát");
            System.out.println("=============================================");
            System.out.print("Nhập lựa chọn: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleAdminLogin();
                    break;
                case "2":
                    handleStudentLogin();
                    break;
                case "3":
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống!");
                    System.exit(0);
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng nhập lại (1-3).");
            }
        }
    }

    private void handleAdminLogin() {
        while (true) {
            System.out.println("\n--- ĐĂNG NHẬP ADMIN ---");
            String username = "";
            String password = "";
            String errorMsg = "";

            while (true) {
                if (!errorMsg.isEmpty()) {
                    System.out.println(errorMsg);
                    errorMsg = "";
                }
                System.out.print("Nhập tên đăng nhập: ");
                username = scanner.nextLine().trim();
                if (username.isEmpty()) {
                    errorMsg = "Lỗi: Tên đăng nhập không được để trống!";
                    continue;
                }
                break;
            }

            while (true) {
                if (!errorMsg.isEmpty()) {
                    System.out.println(errorMsg);
                    errorMsg = "";
                }
                System.out.print("Nhập mật khẩu: ");
                password = scanner.nextLine().trim();
                if (password.isEmpty()) {
                    errorMsg = "Lỗi: Mật khẩu không được để trống!";
                    continue;
                }
                break;
            }

            Admin admin = adminService.loginAdmin(username, password);
            if (admin != null) {
                System.out.println(" Đăng nhập Admin thành công! Chào mừng " + admin.getUsername());
                showAdminMenu();
                break;
            } else {
                System.out.println("Lỗi: Tài khoản hoặc mật khẩu Admin sai! Vui lòng nhập lại.");
            }
        }
    }

    private void handleStudentLogin() {
        while (true) {
            System.out.println("\n--- ĐĂNG NHẬP HỌC VIÊN ---");
            String email = "";
            String password = "";
            String errorMsg = "";

            while (true) {
                if (!errorMsg.isEmpty()) {
                    System.out.println(errorMsg);
                    errorMsg = "";
                }
                System.out.print("Nhập email học viên: ");
                email = scanner.nextLine().trim();
                if (email.isEmpty()) {
                    errorMsg = "Lỗi: Email không được để trống!";
                    continue;
                }
                break;
            }

            while (true) {
                if (!errorMsg.isEmpty()) {
                    System.out.println(errorMsg);
                    errorMsg = "";
                }
                System.out.print("Nhập mật khẩu: ");
                password = scanner.nextLine().trim();
                if (password.isEmpty()) {
                    errorMsg = "Lỗi: Mật khẩu không được để trống!";
                    continue;
                }
                break;
            }

            Student student = studentService.loginStudent(email, password);
            if (student != null) {
                System.out.println(" Đăng nhập Học viên thành công! Chào mừng " + student.getName());
                showStudentMenu();
                break;
            } else {
                System.out.println("Lỗi: Email hoặc mật khẩu học viên sai! Vui lòng nhập lại.");
            }
        }
    }

    private void showAdminMenu() {
        System.out.println("\n========= MENU CHÍNH - ADMIN =========");
        System.out.println("1. Quản lý khóa học");
        System.out.println("2. Quản lý học viên");
        System.out.println("3. Quản lý đăng ký học");
        System.out.println("4. Thống kê học viên theo khóa học");
        System.out.println("5. Đăng xuất");
        System.out.println("======================================");
        System.out.println("\n>> Nhấn Enter để Đăng xuất và quay lại Màn hình chính...");
        scanner.nextLine();
    }

    private void showStudentMenu() {
        System.out.println("\n========= MENU CHÍNH - HỌC VIÊN =========");
        System.out.println("1. Xem danh sách khóa học");
        System.out.println("2. Đăng ký khóa học");
        System.out.println("3. Xem khóa học đã đăng ký");
        System.out.println("4. Hủy đăng ký (nếu trạng thái chưa bắt đầu)");
        System.out.println("5. Đổi mật khẩu");
        System.out.println("6. Đăng xuất");
        System.out.println("=========================================");
        System.out.println("\n>> Nhấn Enter để Đăng xuất và quay lại Màn hình chính...");
        scanner.nextLine();
    }
}