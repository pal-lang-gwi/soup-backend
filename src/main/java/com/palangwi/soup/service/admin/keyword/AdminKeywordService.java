package com.palangwi.soup.service.admin.keyword;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordRequestListDto;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.repository.admin.keyword.AdminKeywordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminKeywordService {

    private final AdminKeywordRepository adminKeywordRepository;

    public AdminKeywordResponseListDto getRequestedKeywords(Status status, Pageable pageable) {
        Page<Keyword> page;

        if (status == null) {
            page = adminKeywordRepository.findAll(pageable);
        } else {
            page = adminKeywordRepository.findByStatus(status, pageable);
        }

    }
}
