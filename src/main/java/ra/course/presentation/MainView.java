package ra.course.presentation;

import ra.course.business.IAdminService;
import ra.course.business.IStudentService;
import ra.course.business.impl.AdminServiceImpl;
import ra.course.business.impl.StudentServiceImpl;
import ra.course.model.Admin;
import ra.course.model.Student;

import java.util.Scanner;

public class MainView {
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
                case "1": handleAdminLogin(); break;
                case "2": handleStudentLogin(); break;
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
            System.out.print("Nhập tên đăng nhập: ");
            String username = scanner.nextLine().trim();
            System.out.print("Nhập mật khẩu: ");
            String password = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Lỗi: Không được để trống tài khoản/mật khẩu!");
                continue;
            }

            Admin admin = adminService.loginAdmin(username, password);
            if (admin != null) {
                System.out.println(" Đăng nhập Admin thành công! Chào mừng " + admin.getUsername());
                AdminView adminView = new AdminView(scanner);
                adminView.showAdminMenu();
                break;
            } else {
                System.out.println("Lỗi: Tài khoản hoặc mật khẩu Admin sai! Vui lòng nhập lại.");
            }
        }
    }

    private void handleStudentLogin() {
        while (true) {
            System.out.println("\n--- ĐĂNG NHẬP HỌC VIÊN ---");
            System.out.print("Nhập email học viên: ");
            String email = scanner.nextLine().trim();
            System.out.print("Nhập mật khẩu: ");
            String password = scanner.nextLine().trim();

            if (email.isEmpty() || password.isEmpty()) {
                System.out.println("Lỗi: Không được để trống email/mật khẩu!");
                continue;
            }

            Student student = studentService.loginStudent(email, password);
            if (student != null) {
                System.out.println(" Đăng nhập Học viên thành công! Chào mừng " + student.getName());
                StudentView studentView = new StudentView(scanner, student);
                studentView.showStudentMenu();
                break;
            } else {
                System.out.println("Lỗi: Email hoặc mật khẩu học viên sai! Vui lòng nhập lại.");
            }
        }
    }
}