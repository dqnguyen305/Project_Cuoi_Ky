package ra.course.business.impl;

import ra.course.business.IAdminService;
import ra.course.dao.IAdminDAO;
import ra.course.dao.impl.AdminDAOImpl;
import ra.course.model.Admin;

public class AdminServiceImpl implements IAdminService {
    private final IAdminDAO adminDAO = new AdminDAOImpl();

    @Override
    public Admin loginAdmin(String username, String password) {
        return adminDAO.loginAdmin(username, password);
    }
}