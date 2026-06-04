package ra.course.business;

import ra.course.model.Admin;

public interface IAdminService {
    Admin loginAdmin(String username, String password);
}
