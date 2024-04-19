package com.group5.cpl.service.serviceImp;

import com.group5.cpl.exception.UserException;
import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.RoleEntity;
import com.group5.cpl.repository.AccountRepository;
import com.group5.cpl.repository.RoleRepository;
import com.group5.cpl.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    RoleRepository roleRepository;
    @Override
    public void accountByEmail(String email) {
        AccountEntity accountEntity = accountRepository.searchForAddEmployee(email);
        if(accountEntity == null){
            throw new UserException("No account found with this email!");
        }
        if (accountEntity.getRoleEntity().getName().equals("MANAGER")) {
            throw new UserException("You can not add this account!");
        }
        accountRepository.addEmployee(accountEntity.getAccount_id());
    }
}
