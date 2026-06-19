package com.gym.branch.application.service;

import com.gym.branch.api.BranchRef;
import com.gym.branch.application.command.CreateBranchCommand;
import com.gym.branch.application.port.out.ReadBranchPort;
import com.gym.branch.application.port.out.WriteBranchPort;
import com.gym.branch.application.query.SearchBranchesQuery;
import com.gym.branch.application.view.BranchDetail;
import com.gym.branch.application.view.BranchListItem;
import com.gym.branch.application.view.BranchOption;
import com.gym.branch.domain.BranchStatus;
import com.gym.shared.api.PageResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BranchServiceTests {

    @Test
    void searchBranchesReturnsPagedResultFromReadPort() {
        FakeReadBranchPort readPort = new FakeReadBranchPort();
        BranchQueryService service = new BranchQueryService(readPort);

        SearchBranchesQuery query = SearchBranchesQuery.from("ACTIVE", "district 1", 1, 10);
        PageResponse<BranchListItem> result = service.handle(query);

        assertEquals(1, result.total());
        assertEquals(1, result.items().size());
        assertEquals("BR-D1", result.items().getFirst().code());
        assertEquals(query, readPort.lastSearchQuery);
    }

    @Test
    void createBranchInsertsThenReloadsByCode() {
        FakeReadBranchPort readPort = new FakeReadBranchPort();
        FakeWriteBranchPort writePort = new FakeWriteBranchPort();
        BranchCommandService service = new BranchCommandService(readPort, writePort);

        CreateBranchCommand command = CreateBranchCommand.from(
                "BR-D1",
                "District 1 Gym",
                "1 Le Loi",
                "District 1",
                null,
                "0901234567",
                null
        );

        BranchDetail created = service.handle(command);

        assertEquals("BR-D1", writePort.lastInserted.code());
        assertEquals(BranchStatus.ACTIVE, writePort.lastInserted.status());
        assertTrue(writePort.insertCalled);
        assertEquals("BR-D1", readPort.lastGetByCode);
        assertEquals("District 1 Gym", created.name());
        assertTrue(created.open24h());
    }

    private static final class FakeReadBranchPort implements ReadBranchPort {
        private SearchBranchesQuery lastSearchQuery;
        private String lastGetByCode;

        @Override
        public Optional<BranchDetail> getByCode(String code) {
            lastGetByCode = code;
            return Optional.of(branchDetail());
        }

        @Override
        public PageResponse<BranchListItem> search(SearchBranchesQuery query) {
            lastSearchQuery = query;
            return PageResponse.ofPageIndex(
                    List.of(new BranchListItem("BR-D1", "District 1 Gym", "Ho Chi Minh City", BranchStatus.ACTIVE, now())),
                    1,
                    query.pageIndex(),
                    query.size()
            );
        }

        @Override
        public List<BranchOption> listActive() {
            return List.of(new BranchOption("BR-D1", "District 1 Gym"));
        }

        @Override
        public boolean existsById(long id) {
            return id == 10L;
        }

        @Override
        public Optional<BranchRef> findRefById(long id) {
            return existsById(id) ? Optional.of(new BranchRef(id, "BR-D1", "District 1 Gym")) : Optional.empty();
        }

        private static BranchDetail branchDetail() {
            OffsetDateTime now = now();
            return new BranchDetail(
                    10L,
                    "BR-D1",
                    "District 1 Gym",
                    "1 Le Loi",
                    "District 1",
                    "Ho Chi Minh City",
                    "0901234567",
                    true,
                    BranchStatus.ACTIVE,
                    now,
                    now
            );
        }

        private static OffsetDateTime now() {
            return OffsetDateTime.parse("2026-06-18T10:00:00+07:00");
        }
    }

    private static final class FakeWriteBranchPort implements WriteBranchPort {
        private boolean insertCalled;
        private CreateBranchCommand lastInserted;

        @Override
        public long insert(CreateBranchCommand command) {
            insertCalled = true;
            lastInserted = command;
            return 10L;
        }
    }
}
