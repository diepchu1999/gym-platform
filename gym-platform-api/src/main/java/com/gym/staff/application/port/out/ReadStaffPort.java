package com.gym.staff.application.port.out;

import com.gym.shared.api.PageResponse;
import com.gym.staff.api.StaffRef;
import com.gym.staff.application.query.SearchStaffQuery;
import com.gym.staff.application.view.StaffListItem;
import com.gym.staff.domain.Staff;
import com.gym.staff.domain.StaffAssignment;

import java.util.List;
import java.util.Optional;

public interface ReadStaffPort {
    Optional<Staff> getByEmployeeCode(String employeeCode);

    Optional<Staff> getByUserAccountId(long userAccountId);

    PageResponse<StaffListItem> search(SearchStaffQuery query);

    List<StaffAssignment> listAssignments(long staffId);

    boolean existsById(long id);

    Optional<StaffRef> findRefById(long id);
}
