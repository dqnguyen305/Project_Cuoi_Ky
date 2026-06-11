package ra.course.presentation;

import ra.course.business.ICourseService;
import ra.course.business.IStudentService;
import ra.course.business.impl.CourseServiceImpl;
import ra.course.business.impl.StudentServiceImpl;
import ra.course.model.Course;
import ra.course.model.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AdminView {
    private final Scanner scanner;
    private final ICourseService courseService = new CourseServiceImpl();
    private final IStudentService studentService = new StudentServiceImpl();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public AdminView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showAdminMenu() {
        while (true) {
            System.out.println("\n========= MENU CHÍNH - ADMIN =========");
            System.out.println("1. Quản lý khóa học");
            System.out.println("2. Quản lý học viên");
            System.out.println("3. Quản lý đăng ký học");
            System.out.println("4. Thống kê học viên theo khóa học");
            System.out.println("5. Đăng xuất");
            System.out.println("======================================");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Lỗi: Lựa chọn không được để trống!");
                continue;
            }

            if (choice.equals("1")) {
                showCourseManagementMenu();
            } else if (choice.equals("2")) {
                showStudentManagementMenu();
            } else if (choice.equals("3")) {
                // Gọi hàm điều hướng quản lý đăng ký học thực tế
                showEnrollmentManagementMenu();
            } else if (choice.equals("4")) {
                System.out.println("Chức năng Thống kê đang phát triển, vui lòng chọn lại!");
            } else if (choice.equals("5")) {
                System.out.println("Đã đăng xuất tài khoản Admin.");
                break;
            } else {
                System.out.println("Lựa chọn sai, vui lòng nhập lại từ 1 đến 5.");
            }
        }
    }
    // Quản lý khóa học
    private void showCourseManagementMenu() {
        while (true) {
            System.out.println("\n--- 2.1 QUẢN LÝ KHÓA HỌC ---");
            System.out.println("1. Hiển thị danh sách khóa học");
            System.out.println("2. Thêm mới khóa học");
            System.out.println("3. Chỉnh sửa thông tin khóa học");
            System.out.println("4. Xóa khóa học");
            System.out.println("5. Tìm kiếm theo tên (tương đối)");
            System.out.println("6. Sắp xếp danh sách khóa học");
            System.out.println("7. Quay lại Menu Admin");
            System.out.print("Nhập lựa chọn: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Lỗi: Lựa chọn không được để trống!");
                continue;
            }

            switch (choice) {
                case "1": displayCourses(courseService.getAllCourses(null, "asc")); break;
                case "2": createCourse(); break;
                case "3": editCourse(); break;
                case "4": deleteCourse(); break;
                case "5": searchCourse(); break;
                case "6": sortCourses(); break;
                case "7": return;
                default: System.out.println("Lựa chọn sai, vui lòng nhập lại từ 1 đến 7.");
            }
        }
    }

    public void displayCourses(List<Course> list) {
        if (list.isEmpty()) { System.out.println("Danh sách khóa học trống!"); return; }
        System.out.println("\n" + "=".repeat(85));
        System.out.printf("| %-5s | %-25s | %-12s | %-20s | %-12s |\n", "ID", "Tên Khóa Học", "Thời Gian", "Giảng Viên", "Ngày Tạo");
        System.out.println("-".repeat(85));
        for (Course c : list) {
            String createDate = c.getCreate_at() != null ? sdf.format(c.getCreate_at()) : "N/A";
            System.out.printf("| %-5d | %-25s | %-7d giờ | %-20s | %-12s |\n", c.getId(), c.getName(), c.getDuration(), c.getInstructor(), createDate);
        }
        System.out.println("=".repeat(85));
    }

    private void createCourse() {
        System.out.println("\n--- THÊM MỚI KHÓA HỌC ---");
        String name, instructor; int duration = 0;
        while (true) {
            System.out.print("Nhập tên khóa học: "); name = scanner.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Lỗi: Tên không được để trống!"); continue; } break;
        }
        while (true) {
            System.out.print("Nhập thời lượng (giờ): ");
            String durInput = scanner.nextLine().trim();
            if (durInput.isEmpty()) { System.out.println("Lỗi: Thời lượng không được để trống!"); continue; }
            try {
                duration = Integer.parseInt(durInput);
                if (duration <= 0) { System.out.println("Lỗi: Thời lượng phải lớn hơn 0!"); continue; } break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: Định dạng giờ phải là số nguyên!"); }
        }
        while (true) {
            System.out.print("Nhập giảng viên: "); instructor = scanner.nextLine().trim();
            if (instructor.isEmpty()) { System.out.println("Lỗi: Tên giảng viên không được để trống!"); continue; } break;
        }
        Course c = new Course(); c.setName(name); c.setDuration(duration); c.setInstructor(instructor);
        if (courseService.addCourse(c)) System.out.println("Thêm mới khóa học thành công!");
        else System.out.println("Lỗi thêm mới thất bại.");
    }

    private void editCourse() {
        int id;
        while (true) {
            System.out.print("\nNhập ID khóa học cần sửa: ");
            String idInput = scanner.nextLine().trim();
            if (idInput.isEmpty()) { System.out.println("Lỗi: ID khóa học không được để trống!"); continue; }
            try {
                id = Integer.parseInt(idInput);
                break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: ID phải là số nguyên!"); }
        }

        int finalId = id;
        List<Course> allCourses = courseService.getAllCourses(null, "asc");
        Optional<Course> courseOpt = allCourses.stream().filter(c -> c.getId() == finalId).findFirst();

        if (!courseOpt.isPresent()) {
            System.out.println("Lỗi: Không tìm thấy khóa học nào có ID = " + id);
            return;
        }
        Course targetCourse = courseOpt.get();

        System.out.println("Tìm thấy khóa học: " + targetCourse.getName());

        String fieldChoice = "";
        while (true) {
            System.out.println("Chọn thuộc tính cần sửa:\n1. Sửa tên khóa học\n2. Sửa thời lượng học\n3. Sửa giảng viên hướng dẫn");
            System.out.print("Nhập lựa chọn (1-3): ");
            fieldChoice = scanner.nextLine().trim();
            if (fieldChoice.isEmpty()) { System.out.println("Lỗi: Lựa chọn không được để trống!"); continue; }
            if (!fieldChoice.equals("1") && !fieldChoice.equals("2") && !fieldChoice.equals("3")) {
                System.out.println("Lỗi: Vui lòng chọn đúng số từ 1 đến 3!");
                continue;
            }
            break;
        }

        switch (fieldChoice) {
            case "1":
                String newName = "";
                while (true) {
                    System.out.print("Nhập tên khóa học mới: ");
                    newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) { System.out.println("Lỗi: Tên khóa học mới không được để trống!"); continue; }
                    break;
                }
                if (courseService.updateCourseField(id, "name", newName)) System.out.println("Cập nhật tên thành công!");
                else System.out.println("Lỗi cập nhật."); break;
            case "2":
                int newDur = 0;
                while (true) {
                    System.out.print("Nhập thời lượng học mới (giờ): ");
                    String durInput = scanner.nextLine().trim();
                    if (durInput.isEmpty()) { System.out.println("Lỗi: Thời lượng học mới không được để trống!"); continue; }
                    try {
                        newDur = Integer.parseInt(durInput);
                        if (newDur <= 0) { System.out.println("Lỗi: Thời lượng phải lớn hơn 0!"); continue; }
                        break;
                    } catch (NumberFormatException e) { System.out.println("Lỗi: Thời lượng mới phải là số nguyên!"); }
                }
                if (courseService.updateCourseField(id, "duration", newDur)) System.out.println("Cập nhật thời lượng thành công!");
                else System.out.println("Cập nhật thất bại."); break;
            case "3":
                String newInst = "";
                while (true) {
                    System.out.print("Nhập tên giảng viên hướng dẫn mới: ");
                    newInst = scanner.nextLine().trim();
                    if (newInst.isEmpty()) { System.out.println("Lỗi: Tên giảng viên không được để trống!"); continue; }
                    break;
                }
                if (courseService.updateCourseField(id, "instructor", newInst)) System.out.println("Cập nhật giảng viên thành công!");
                else System.out.println("Lỗi cập nhật."); break;
        }
    }

    private void deleteCourse() {
        int id;
        while (true) {
            System.out.print("\nNhập ID khóa học muốn xóa: ");
            String idInput = scanner.nextLine().trim();
            if (idInput.isEmpty()) { System.out.println("Lỗi: ID khóa học không được để trống!"); continue; }
            try {
                id = Integer.parseInt(idInput);
                break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: ID phải là số!"); }
        }

        int finalId = id;
        List<Course> allCourses = courseService.getAllCourses(null, "asc");
        boolean exists = allCourses.stream().anyMatch(c -> c.getId() == finalId);
        if (!exists) {
            System.out.println("Lỗi: Không tìm thấy khóa học nào có ID = " + id);
            return;
        }

        while (true) {
            System.out.print("Bạn có chắc chắn muốn xóa khóa học này không? (Y/N): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.isEmpty()) { System.out.println("Lỗi: Vui lòng xác nhận Y hoặc N!"); continue; }
            if (confirm.equalsIgnoreCase("Y")) {
                if (courseService.deleteCourse(id)) System.out.println("Xóa khóa học thành công!");
                break;
            } else if (confirm.equalsIgnoreCase("N")) {
                System.out.println("Đã hủy bỏ thao tác xóa.");
                break;
            } else {
                System.out.println("Lỗi: Chỉ chấp nhận ký tự Y hoặc N.");
            }
        }
    }

    private void searchCourse() {
        String name = "";
        while (true) {
            System.out.print("\nNhập tên khóa học cần tìm: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Lỗi: Từ khóa tìm kiếm không được để trống!"); continue; }
            break;
        }
        displayCourses(courseService.searchCourseByName(name));
    }

    private void sortCourses() {
        String criteria = "";
        while (true) {
            System.out.println("\nChọn tiêu chí: 1. ID | 2. Tên");
            System.out.print("Nhập lựa chọn (1-2): ");
            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) { System.out.println("Lỗi: Tiêu chí sắp xếp không được để trống!"); continue; }
            if (!choice.equals("1") && !choice.equals("2")) { System.out.println("Lỗi: Chỉ nhập số 1 hoặc 2!"); continue; }
            criteria = choice.equals("2") ? "name" : "id";
            break;
        }

        String dir = "";
        while (true) {
            System.out.print("Hướng sắp xếp (1: Tăng | 2: Giảm): ");
            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) { System.out.println("Lỗi: Hướng sắp xếp không được để trống!"); continue; }
            if (!choice.equals("1") && !choice.equals("2")) { System.out.println("Lỗi: Chỉ nhập số 1 hoặc 2!"); continue; }
            dir = choice.equals("2") ? "desc" : "asc";
            break;
        }
        displayCourses(courseService.getAllCourses(criteria, dir));
    }
    // Quản lý học viên
    private void showStudentManagementMenu() {
        while (true) {
            System.out.println("\n--- 2.2 QUẢN LÝ HỌC VIÊN ---");
            System.out.println("1. Hiển thị danh sách học viên");
            System.out.println("2. Thêm mới học viên");
            System.out.println("3. Chỉnh sửa thông tin học viên");
            System.out.println("4. Xóa học viên");
            System.out.println("5. Tìm kiếm theo tên, email hoặc ID");
            System.out.println("6. Sắp xếp danh sách học viên");
            System.out.println("7. Quay lại Menu Admin");
            System.out.print("Nhập lựa chọn: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Lỗi: Lựa chọn không được để trống!");
                continue;
            }

            switch (choice) {
                case "1": displayStudents(studentService.getAllStudents(null, "asc")); break;
                case "2": createStudent(); break;
                case "3": editStudent(); break;
                case "4": deleteStudent(); break;
                case "5": searchStudent(); break;
                case "6": sortStudents(); break;
                case "7": return;
                default: System.out.println("Lựa chọn sai, vui lòng nhập lại từ 1 đến 7.");
            }
        }
    }

    private void displayStudents(List<Student> list) {
        if (list.isEmpty()) { System.out.println("Danh sách học viên trống!"); return; }
        System.out.println("\n" + "=".repeat(115));
        System.out.printf("| %-5s | %-20s | %-10s | %-12s | %-25s | %-12s | %-12s |\n", "ID", "Tên Học Viên", "Giới Tính", "Ngày Sinh", "Email", "Số ĐT", "Ngày Tạo");
        System.out.println("-".repeat(115));
        for (Student s : list) {
            String gender = s.isSex() ? "Nam" : "Nữ";
            String birthday = s.getDob() != null ? sdf.format(s.getDob()) : "N/A";
            String createDate = s.getCreate_at() != null ? sdf.format(s.getCreate_at()) : "N/A";
            System.out.printf("| %-5d | %-20s | %-10s | %-12s | %-25s | %-12s | %-12s |\n", s.getId(), s.getName(), gender, birthday, s.getEmail(), s.getPhone(), createDate);
        }
        System.out.println("=".repeat(115));
    }

    private void createStudent() {
        System.out.println("\n--- THÊM MỚI HỌC VIÊN ---");
        String name, email, phone, password; boolean sex = true; Date dob = null;
        while (true) {
            System.out.print("Nhập tên học viên: "); name = scanner.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Lỗi: Tên không được để trống!"); continue; } break;
        }
        while (true) {
            System.out.print("Giới tính (1: Nam | 2: Nữ): "); String g = scanner.nextLine().trim();
            if (g.isEmpty()) { System.out.println("Lỗi: Giới tính không được để trống!"); continue; }
            if (g.equals("1")) { sex = true; break; }
            else if (g.equals("2")) { sex = false; break; }
            else { System.out.println("Lỗi: Vui lòng nhập đúng 1 hoặc 2!"); }
        }
        while (true) {
            System.out.print("Nhập ngày sinh (dd/mm/yyyy): ");
            String dobInput = scanner.nextLine().trim();
            if (dobInput.isEmpty()) { System.out.println("Lỗi: Ngày sinh không được để trống!"); continue; }
            try { dob = sdf.parse(dobInput); break; }
            catch (ParseException e) { System.out.println("Lỗi: Sai định dạng ngày (Ví dụ: 30/05/2004)!"); }
        }
        while (true) {
            System.out.print("Nhập email học viên: "); email = scanner.nextLine().trim();
            if (email.isEmpty()) { System.out.println("Lỗi: Email không được để trống!"); continue; } break;
        }
        while (true) {
            System.out.print("Nhập số điện thoại: "); phone = scanner.nextLine().trim();
            if (phone.isEmpty()) { System.out.println("Lỗi: Số điện thoại không được để trống!"); continue; } break;
        }
        while (true) {
            System.out.print("Nhập mật khẩu: "); password = scanner.nextLine().trim();
            if (password.isEmpty()) { System.out.println("Lỗi: Mật khẩu không được để trống!"); continue; } break;
        }
        Student s = new Student();
        s.setName(name); s.setSex(sex); s.setDob(dob); s.setEmail(email); s.setPhone(phone); s.setPassword(password);

        if (studentService.addStudent(s)) System.out.println("Thêm mới học viên thành công!");
        else System.out.println("Lỗi thêm học viên thất bại (Có thể trùng email/số ĐT).");
    }

    private void editStudent() {
        int id;
        while (true) {
            System.out.print("\nNhập ID học viên cần sửa: ");
            String idInput = scanner.nextLine().trim();
            if (idInput.isEmpty()) { System.out.println("Lỗi: ID học viên không được để trống!"); continue; }
            try {
                id = Integer.parseInt(idInput);
                break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: ID không hợp lệ!"); }
        }

        int finalId = id;
        List<Student> allStudents = studentService.getAllStudents(null, "asc");
        Optional<Student> studentOpt = allStudents.stream().filter(s -> s.getId() == finalId).findFirst();

        if (!studentOpt.isPresent()) {
            System.out.println("Lỗi: Không tìm thấy học viên nào có ID = " + id + " trong hệ thống!");
            return;
        }
        Student targetStudent = studentOpt.get();

        System.out.println("Tìm thấy học viên: " + targetStudent.getName());

        String fieldChoice = "";
        while (true) {
            System.out.println("Chọn thuộc tính cần sửa:\n1. Sửa tên học viên\n2. Sửa giới tính\n3. Sửa ngày sinh\n4. Sửa số ĐT\n5. Sửa mật khẩu");
            System.out.print("Nhập lựa chọn (1-5): ");
            fieldChoice = scanner.nextLine().trim();
            if (fieldChoice.isEmpty()) { System.out.println("Lỗi: Lựa chọn thuộc tính không được để trống!"); continue; }
            if (Integer.parseInt(fieldChoice) < 1 || Integer.parseInt(fieldChoice) > 5) {
                System.out.println("Lỗi: Vui lòng chọn số từ 1 đến 5!");
                continue;
            }
            break;
        }

        switch (fieldChoice) {
            case "1":
                String newName = "";
                while (true) {
                    System.out.print("Nhập tên mới: ");
                    newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) { System.out.println("Lỗi: Tên học viên mới không được để trống!"); continue; }
                    break;
                }
                if (studentService.updateStudentField(id, "name", newName)) System.out.println("Cập nhật tên thành công!");
                else System.out.println("Thao tác thất bại."); break;
            case "2":
                boolean newSex = true;
                while (true) {
                    System.out.print("Chọn giới tính mới (1: Nam | 2: Nữ): ");
                    String g = scanner.nextLine().trim();
                    if (g.isEmpty()) { System.out.println("Lỗi: Giới tính không được để trống!"); continue; }
                    if (g.equals("1")) { newSex = true; break; }
                    else if (g.equals("2")) { newSex = false; break; }
                    else { System.out.println("Lỗi: Vui lòng nhập đúng số 1 hoặc 2!"); }
                }
                if (studentService.updateStudentField(id, "sex", newSex)) System.out.println("Cập nhật giới tính thành công!");
                else System.out.println("Thao tác thất bại."); break;
            case "3":
                Date newDob = null;
                while (true) {
                    System.out.print("Nhập ngày sinh mới (dd/mm/yyyy): ");
                    String dobInput = scanner.nextLine().trim();
                    if (dobInput.isEmpty()) { System.out.println("Lỗi: Ngày sinh không được để trống!"); continue; }
                    try {
                        newDob = sdf.parse(dobInput);
                        break;
                    } catch (ParseException e) { System.out.println("Lỗi: Sai định dạng ngày tháng (Ví dụ: 30/05/2004)!"); }
                }
                if (studentService.updateStudentField(id, "dob", newDob)) System.out.println("Cập nhật ngày sinh thành công!");
                else System.out.println("Thao tác thất bại."); break;
            case "4":
                String newPhone = "";
                while (true) {
                    System.out.print("Nhập số ĐT mới: ");
                    newPhone = scanner.nextLine().trim();
                    if (newPhone.isEmpty()) { System.out.println("Lỗi: Số điện thoại không được để trống!"); continue; }
                    break;
                }
                if (studentService.updateStudentField(id, "phone", newPhone)) System.out.println("Cập nhật số ĐT thành công!");
                else System.out.println("Thao tác thất bại."); break;
            case "5":
                String newPass = "";
                while (true) {
                    System.out.print("Nhập mật khẩu mới: ");
                    newPass = scanner.nextLine().trim();
                    if (newPass.isEmpty()) { System.out.println("Lỗi: Mật khẩu mới không được để trống!"); continue; }
                    break;
                }
                if (studentService.updateStudentField(id, "password", newPass)) System.out.println("Cập nhật mật khẩu thành công!");
                else System.out.println("Thao tác thất bại."); break;
        }
    }

    private void deleteStudent() {
        int id;
        while (true) {
            System.out.print("\nNhập ID học viên cần xóa: ");
            String idInput = scanner.nextLine().trim();
            if (idInput.isEmpty()) { System.out.println("Lỗi: ID học viên không được để trống!"); continue; }
            try {
                id = Integer.parseInt(idInput);
                break;
            } catch (NumberFormatException e) { System.out.println("Lỗi: ID phải là số!"); }
        }

        int finalId = id;
        List<Student> allStudents = studentService.getAllStudents(null, "asc");
        boolean exists = allStudents.stream().anyMatch(s -> s.getId() == finalId);
        if (!exists) {
            System.out.println("Lỗi: Không tìm thấy học viên nào có ID = " + finalId + " trong hệ thống!");
            return;
        }

        while (true) {
            System.out.print("Bạn có chắc chắn muốn xóa học viên này không? (Y/N): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.isEmpty()) { System.out.println("Lỗi: Vui lòng xác nhận Y hoặc N!"); continue; }
            if (confirm.equalsIgnoreCase("Y")) {
                if (studentService.deleteStudent(id)) System.out.println("Xóa học viên thành công!");
                break;
            } else if (confirm.equalsIgnoreCase("N")) {
                System.out.println("Đã hủy bỏ thao tác xóa.");
                break;
            } else {
                System.out.println("Lỗi: Chỉ chấp nhận ký tự Y hoặc N.");
            }
        }
    }

    private void searchStudent() {
        String keyword = "";
        while (true) {
            System.out.print("\nNhập từ khóa tìm kiếm (Tên/Email/ID): ");
            keyword = scanner.nextLine().trim();
            if (keyword.isEmpty()) { System.out.println("Lỗi: Từ khóa tìm kiếm không được để trống!"); continue; }
            break;
        }
        displayStudents(studentService.searchStudent(keyword));
    }

    private void sortStudents() {
        String criteria = "";
        while (true) {
            System.out.println("\nTiêu chí: 1. ID | 2. Tên học viên");
            System.out.print("Nhập lựa chọn (1-2): ");
            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) { System.out.println("Lỗi: Tiêu chí sắp xếp không được để trống!"); continue; }
            if (!choice.equals("1") && !choice.equals("2")) { System.out.println("Lỗi: Chỉ nhập số 1 hoặc 2!"); continue; }
            criteria = choice.equals("2") ? "name" : "id";
            break;
        }

        String dir = "";
        while (true) {
            System.out.print("Hướng (1: Tăng | 2: Giảm): ");
            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) { System.out.println("Lỗi: Hướng sắp xếp không được để trống!"); continue; }
            if (!choice.equals("1") && !choice.equals("2")) { System.out.println("Lỗi: Chỉ nhập số 1 hoặc 2!"); continue; }
            dir = choice.equals("2") ? "desc" : "asc";
            break;
        }
        displayStudents(studentService.getAllStudents(criteria, dir));
    }
    // Quản lý đăng ký học
    private void showEnrollmentManagementMenu() {
        while (true) {
            System.out.println("\n--- 2.3 Menu Quản lý đăng ký khóa học ---");
            System.out.println("1. Hiển thị học viên theo từng khóa học");
            System.out.println("2. Thêm học viên vào khóa học");
            System.out.println("3. Xóa học viên khỏi khóa học");
            System.out.println("4. Quay về menu chính");
            System.out.print("Nhập lựa chọn: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Lỗi: Lựa chọn không được để trống!");
                continue;
            }

            switch (choice) {
                case "1":
                    handleDisplayStudentsByCourse();
                    break;
                case "2":
                    handleAddStudentToCourse();
                    break;
                case "3":
                    handleRemoveStudentFromCourse();
                    break;
                case "4":
                    return; // Quay về menu chính Admin
                default:
                    System.out.println("Lựa chọn sai, vui lòng nhập lại từ 1 đến 4.");
            }
        }
    }

    private void handleDisplayStudentsByCourse() {
        System.out.println("\n--- HIỂN THỊ HỌC VIÊN THEO TỪNG KHÓA HỌC ---");
        // Gợi ý: Hiển thị nhanh danh sách khóa học hiện tại để Admin dễ nhìn ID
        System.out.println("Danh sách khóa học hiện có trên hệ thống:");
        displayCourses(courseService.getAllCourses(null, "asc"));

        System.out.print("\nNhập ID khóa học cần xem danh sách học viên: ");
        try {
            int courseId = Integer.parseInt(scanner.nextLine().trim());
            courseService.displayStudentsByCourse(courseId);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID khóa học phải là số nguyên!");
        }
    }

    private void handleAddStudentToCourse() {
        System.out.println("\n--- THÊM HỌC VIÊN VÀO KHÓA HỌC ---");
        try {
            System.out.print("Nhập ID Học Viên: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Nhập ID Khóa Học: ");
            int courseId = Integer.parseInt(scanner.nextLine().trim());

            if (courseService.addStudentToCourse(studentId, courseId)) {
                System.out.println(" Thêm học viên vào khóa học thành công (Trạng thái: Đã duyệt)!");
            } else {
                System.out.println("Lỗi: Không thể thêm học viên. Vui lòng kiểm tra lại ID!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID nhập vào phải là số nguyên!");
        }
    }

    private void handleRemoveStudentFromCourse() {
        System.out.println("\n--- XÓA HỌC VIÊN KHỎI KHÓA HỌC ---");
        try {
            System.out.print("Nhập ID Học Viên cần xóa: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Nhập ID Khóa Học: ");
            int courseId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Bạn có chắc chắn muốn xóa học viên này khỏi khóa học không? (Y/N): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("Y")) {
                if (courseService.removeStudentFromCourse(studentId, courseId)) {
                    System.out.println(" Đã xóa thành công học viên khỏi khóa học.");
                } else {
                    System.out.println("Lỗi: Xóa thất bại. Không tìm thấy thông tin đăng ký tương ứng!");
                }
            } else {
                System.out.println("Đã hủy bỏ thao tác xóa.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID nhập vào phải là số nguyên!");
        }
    }
}