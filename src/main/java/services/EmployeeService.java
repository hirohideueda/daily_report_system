package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import constants.JpaConst;
import models.Employee;
import models.validators.EmployeeValidator;
import utils.EncryptUtil;

//従業員テーブルの操作に関わる処理を行うクラス
public class EmployeeService extends ServiceBase {

    //指定されたページ数のデータを取得、EmployeeViewのリストで返却
    public List<EmployeeView> getPerPage(int page) {

        List<Employee> employees = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Employee.class).setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1)).setMaxResults(JpaConst.ROW_PER_PAGE).getResultList();

        return EmployeeConverter.toViewList(employees);

    }

    //従業員テーブルのデータ件数取得、返却
    public long countAll() {

        long empCount = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT, Long.class).getSingleResult();

        return empCount;

    }

    //社員番号、パスワードを条件に取得したデータをEmployeeViewのインスタンスで返却
    public EmployeeView findOne(String code, String plainPass, String pepper) {

        Employee e = null;

        try {
            String pass = EncryptUtil.getPasswordEncrypt(plainPass, pepper);

            e = em.createNamedQuery(JpaConst.Q_EMP_GET_BY_CODE_AND_PASS, Employee.class).setParameter(JpaConst.JPQL_PARM_CODE, code).setParameter(JpaConst.JPQL_PARM_PASSWORD, pass).getSingleResult();
        }catch(NoResultException ex){

        }
        return EmployeeConverter.toView(e);

    }

    //idを条件に取得したデータをEmployeeViewのインスタンスで返却
    public EmployeeView findOne(int id) {

        Employee e = findOneInternal(id);

        return EmployeeConverter.toView(e);
    }

    //社員番号を条件にデータ件数の取得、返却
    public long countByCode(String code) {

        long employees_count = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT_RESISTERED_BY_CODE, Long.class).setParameter(JpaConst.JPQL_PARM_CODE, code).getSingleResult();

        return employees_count;
    }

    //入力された登録内容でデータ作成、テーブルに登録
    public List<String> create(EmployeeView ev, String pepper){

        String pass = EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper);
        ev.setPassword(pass);

        LocalDateTime now = LocalDateTime.now();
        ev.setCreatedAt(now);
        ev.setUpdatedAt(now);

        List<String> errors = EmployeeValidator.validate(this, ev, true, true);

        if (errors.size() == 0) {
            create(ev);
        }

        return errors;
    }

    //入力された更新内容でデータ作成、テーブルを更新
    public List<String> update(EmployeeView ev, String pepper){

        EmployeeView savedEmp = findOne(ev.getId());

        boolean validateCode = false;

        if (!savedEmp.getCode().equals(ev.getCode())) {

            validateCode = true;

            savedEmp.setCode(ev.getCode());
        }

        boolean validatePass = false;

        if (ev.getPassword() != null && !ev.getPassword().equals("")) {

            validatePass = true;

            savedEmp.setPassword(EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper));
        }

        savedEmp.setName(ev.getName());
        savedEmp.setAdminFlag(ev.getAdminFlag());

        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);

        List<String> errors = EmployeeValidator.validate(this, savedEmp, validateCode, validatePass);

        if (errors.size() == 0) {
            update(savedEmp);
        }

        return errors;
    }

    //idを条件に従業員データを論理削除
    public void destroy(Integer id) {

        EmployeeView savedEmp = findOne(id);

        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);

        savedEmp.setDeleteFlag(JpaConst.EMP_DEL_TRUE);

        update(savedEmp);
    }

    //社員番号とパスワードを条件にデータが取得できるかどうかで認証結果を返却
    public Boolean validateLogin(String code, String plainPass, String pepper) {

        boolean isValidEmployee = false;

        if (code != null && !code.equals("") && plainPass != null && !plainPass.equals("")) {

            EmployeeView ev = findOne(code, plainPass, pepper);

            if (ev != null && ev.getId() != null) {
                isValidEmployee = true;
            }
        }
        return isValidEmployee;
    }

    //idを条件にデータを1件取得、Employeeのインスタンスで返却
    private Employee findOneInternal(int id) {

        Employee e = em.find(Employee.class, id);

        return e;
    }

    //従業員データを1件登録
    private void create(EmployeeView ev) {

        em.getTransaction().begin();
        em.persist(EmployeeConverter.toModel(ev));
        em.getTransaction().commit();

    }

    //従業員データを更新
    private void update(EmployeeView ev) {

        em.getTransaction().begin();
        Employee e = findOneInternal(ev.getId());
        EmployeeConverter.copyViewToModel(e, ev);
        em.getTransaction().commit();

    }
}
