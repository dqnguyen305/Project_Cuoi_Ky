package ra.course.dao;

import ra.course.model.Admin;

public interface IAdminDAO {
    Admin loginAdmin(String username, String password);
}
