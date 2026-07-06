package com.gym.membership.application.query;

import com.gym.membership.domain.PackageType;
import com.gym.shared.api.PageParams;
import com.gym.shared.api.Paged;
import com.gym.shared.api.QueryParams;
import com.gym.shared.validation.Enums;

public record SearchPackagePlansQuery(
        PackageType packageType,
        Boolean active,
        String keyword,
        int page,
        int size
) implements Paged {
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public static SearchPackagePlansQuery from(
            String packageType,
            Boolean active,
            String keyword,
            Integer page,
            Integer size
    ) {
        PageParams pageParams = PageParams.normalize(page, size, DEFAULT_SIZE, MAX_SIZE);
        return new SearchPackagePlansQuery(
                Enums.parseStrict(PackageType.class, "packageType", QueryParams.filterOrNull(packageType)),
                active,
                QueryParams.searchOrEmpty(keyword),
                pageParams.page(),
                pageParams.size()
        );
    }
}
