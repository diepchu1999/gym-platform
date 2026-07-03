package com.gym.staff.application.service;

import com.gym.shared.api.PageResponse;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import com.gym.staff.api.StaffDirectory;
import com.gym.staff.api.StaffRef;
import com.gym.staff.application.port.in.GetStaffUseCase;
import com.gym.staff.application.port.in.SearchStaffUseCase;
import com.gym.staff.application.port.out.ReadStaffPort;
import com.gym.staff.application.query.SearchStaffQuery;
import com.gym.staff.application.view.StaffDetail;
import com.gym.staff.application.view.StaffListItem;
import com.gym.staff.domain.Staff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class StaffQueryService implements SearchStaffUseCase, GetStaffUseCase, StaffDirectory {
    private final ReadStaffPort readStaffPort;
    private final StaffDetailAssembler detailAssembler;

    StaffQueryService(ReadStaffPort readStaffPort, StaffDetailAssembler detailAssembler) {
        this.readStaffPort = readStaffPort;
        this.detailAssembler = detailAssembler;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StaffListItem> handle(SearchStaffQuery query) {
        return readStaffPort.search(query);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffDetail handle(String employeeCode) {
        String normalizedCode = Validations.requireText(employeeCode, "employeeCode");
        Staff staff = readStaffPort.getByEmployeeCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Staff not found: " + normalizedCode));

        return detailAssembler.toDetail(staff, readStaffPort.listAssignments(staff.id()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return readStaffPort.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffRef> findRefById(long id) {
        return readStaffPort.findRefById(id);
    }
}
