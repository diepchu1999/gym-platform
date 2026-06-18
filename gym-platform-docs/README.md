# GYM Platform — Documentation

Tài liệu chia 2 bản ngôn ngữ. Documentation is split into two language versions.

- 🇻🇳 **Tiếng Việt (canonical / nguồn chuẩn):** [`vi/`](vi/)
- 🇬🇧 **English (translation):** [`en/`](en/)

> Tiếng Việt là bản canonical: khi nội dung thay đổi, cập nhật `vi/` trước rồi đồng bộ `en/`.
> Vietnamese is canonical: update `vi/` first, then sync `en/`.
> Code identifiers, status enums, table/schema names stay in English in both versions.

## Cấu trúc / Structure

```text
gym-platform-docs/
├── vi/                      # Bản tiếng Việt (canonical)
│   ├── business/            # business-rules, domain-map, glossary, status-flow, BRD
│   ├── architecture/        # guidelines, architecture-overview, solution-architecture
│   │   └── data-model/      # README, module-schemas, p1..p9
│   ├── modules/             # member-kyc, checkin, booking-engine, ...
│   ├── decisions/           # ADR-0001 .. ADR-0012
│   └── dev-notes/
└── en/                      # English version (mirror of vi/)
```

## Trạng thái dịch / Translation status

| Section | vi/ | en/ |
|---|---|---|
| business (5) | ✅ | ✅ |
| architecture (guidelines + overview + solution) (10) | ✅ | ✅ |
| architecture/data-model (11) | ✅ | ✅ |
| modules (12) | ✅ | ✅ |
| decisions / ADR (12) | ✅ | ✅ |
| dev-notes (1) | ✅ | ✅ |

**Hoàn tất: 51 file mỗi bản (vi/ + en/).** Sơ đồ kiến trúc dùng chung ở [`diagrams/`](diagrams/).
